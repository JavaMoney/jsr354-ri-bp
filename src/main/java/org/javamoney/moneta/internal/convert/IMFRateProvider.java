/*
 * Copyright (c) 2012, 2014, Credit Suisse (Anatole Tresch), Werner Keil and others by the @author tag.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.javamoney.moneta.internal.convert;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Currency;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import javax.money.CurrencyContextBuilder;
import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryException;
import javax.money.convert.ConversionContext;
import javax.money.convert.ConversionContextBuilder;
import javax.money.convert.ConversionQuery;
import javax.money.convert.ExchangeRate;
import javax.money.convert.ExchangeRateProvider;
import javax.money.convert.ProviderContext;
import javax.money.convert.ProviderContextBuilder;
import javax.money.convert.RateType;
import javax.money.spi.Bootstrap;

import org.javamoney.moneta.CurrencyUnitBuilder;
import org.javamoney.moneta.convert.ExchangeRateBuilder;
import org.javamoney.moneta.spi.AbstractRateProvider;
import org.javamoney.moneta.spi.DefaultNumberValue;
import org.javamoney.moneta.spi.LoaderService;
import org.javamoney.moneta.spi.LoaderService.LoaderListener;

/**
 * Implements a {@link ExchangeRateProvider} that loads the IMF conversion data.
 * In most cases this provider will provide chained rates, since IMF always is
 * converting from/to the IMF <i>SDR</i> currency unit.
 *
 * @author Anatole Tresch
 * @author Werner Keil
 */
public class IMFRateProvider extends AbstractRateProvider implements LoaderListener {

    /**
     * The data id used for the LoaderService.
     */
    private static final String DATA_ID = IMFRateProvider.class.getSimpleName();
    /**
     * The {@link ConversionContext} of this provider.
     */
    private static final ProviderContext CONTEXT = ProviderContextBuilder.of("IMF", RateType.DEFERRED)
            .set("providerDescription", "International Monetary Fond").set("days", 1).build();

    private static final CurrencyUnit SDR =
            CurrencyUnitBuilder.of("SDR", CurrencyContextBuilder.of(IMFRateProvider.class.getSimpleName()).build())
                    .setDefaultFractionDigits(3).build(true);

    private Map<CurrencyUnit, List<ExchangeRate>> currencyToSdr = new HashMap<>();

    private Map<CurrencyUnit, List<ExchangeRate>> sdrToCurrency = new HashMap<>();

    protected volatile String loadState;

    protected volatile CountDownLatch loadLock = new CountDownLatch(1);

    private static final Map<String, CurrencyUnit> currenciesByName = new HashMap<>();

    static {
        for (Currency currency : Currency.getAvailableCurrencies()) {
            currenciesByName.put(currency.getDisplayName(Locale.ENGLISH),
                    Monetary.getCurrency(currency.getCurrencyCode()));
        }
        // Additional IMF differing codes:
        // This mapping is required to fix data issues in the input stream, it has nothing to do with i18n
        currenciesByName.put("U.K. Pound Sterling", Monetary.getCurrency("GBP"));
        currenciesByName.put("U.S. Dollar", Monetary.getCurrency("USD"));
        currenciesByName.put("Bahrain Dinar", Monetary.getCurrency("BHD"));
        currenciesByName.put("Botswana Pula", Monetary.getCurrency("BWP"));
        currenciesByName.put("Czech Koruna", Monetary.getCurrency("CZK"));
        currenciesByName.put("Icelandic Krona", Monetary.getCurrency("ISK"));
        currenciesByName.put("Korean Won", Monetary.getCurrency("KRW"));
        currenciesByName.put("Rial Omani", Monetary.getCurrency("OMR"));
        currenciesByName.put("Nuevo Sol", Monetary.getCurrency("PEN"));
        currenciesByName.put("Qatar Riyal", Monetary.getCurrency("QAR"));
        currenciesByName.put("Saudi Arabian Riyal", Monetary.getCurrency("SAR"));
        currenciesByName.put("Sri Lanka Rupee", Monetary.getCurrency("LKR"));
        currenciesByName.put("Trinidad And Tobago Dollar", Monetary.getCurrency("TTD"));
        currenciesByName.put("U.A.E. Dirham", Monetary.getCurrency("AED"));
        currenciesByName.put("Peso Uruguayo", Monetary.getCurrency("UYU"));
        currenciesByName.put("Bolivar Fuerte", Monetary.getCurrency("VEF"));
    }

    public IMFRateProvider() {
        super(CONTEXT);
        LoaderService loader = Bootstrap.getService(LoaderService.class);
        loader.addLoaderListener(this, DATA_ID);
        try {
            loader.loadData(DATA_ID);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error loading initial data from IMF provider...", e);
        }
    }

    @Override
    public void newDataLoaded(String data, InputStream is) {
        try {
            int oldSize = this.sdrToCurrency.size();
            loadRatesTSV(is);
            int newSize = this.sdrToCurrency.size();
            loadState = "Loaded " + DATA_ID + " exchange rates for days:" + (newSize - oldSize);
            LOGGER.info(loadState);
            loadLock.countDown();
        } catch (Exception e) {
            loadState = "Last Error during data load: " + e.getMessage();
            throw new IllegalArgumentException("Failed to load IMF data provided.", e);
        }
    }

    @SuppressWarnings("unchecked")
    private void loadRatesTSV(InputStream inputStream) throws IOException, ParseException {
        Map<CurrencyUnit, List<ExchangeRate>> newCurrencyToSdr = new HashMap<>();
        Map<CurrencyUnit, List<ExchangeRate>> newSdrToCurrency = new HashMap<>();
        NumberFormat f = new DecimalFormat("#0.0000000000");
        f.setGroupingUsed(false);
        BufferedReader pr = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        String line = pr.readLine();
        if(line.contains("Request Rejected")){
            throw new IOException("Request has been rejected by IMF server.");
        }
        // int lineType = 0;
        boolean currencyToSdr = true;
        // SDRs per Currency unit (2)
        //
        // Currency January 31, 2013 January 30, 2013 January 29, 2013
        // January 28, 2013 January 25, 2013
        // Euro 0.8791080000 0.8789170000 0.8742470000 0.8752180000
        // 0.8768020000

        // Currency units per SDR(3)
        //
        // Currency January 31, 2013 January 30, 2013 January 29, 2013
        // January 28, 2013 January 25, 2013
        // Euro 1.137520 1.137760 1.143840 1.142570 1.140510
        List<LocalDate> timestamps = null;
        while (line!=null) {
            if (line.trim().isEmpty()) {
                line = pr.readLine();
                continue;
            }
            if (line.startsWith("SDRs per Currency unit")) {
                currencyToSdr = false;
                line = pr.readLine();
                continue;
            } else if (line.startsWith("Currency units per SDR")) {
                currencyToSdr = true;
                line = pr.readLine();
                continue;
            } else if (line.startsWith("Currency")) {
                timestamps = readTimestamps(line);
                line = pr.readLine();
                continue;
            }
            String[] parts = line.split("\\t");
            CurrencyUnit currency = currenciesByName.get(parts[0]);
            if (currency==null) {
                LOGGER.finest("Uninterpretable data from IMF data feed: " + parts[0]);
                line = pr.readLine();
                continue;
            }
            Double[] values = parseValues(parts);
            for (int i = 0; i < values.length; i++) {
                if (values[i]==null) {
                    continue;
                }
                LocalDate fromTS = timestamps != null ? timestamps.get(i) : null;
                if (fromTS == null) {
                    continue;
                }
                RateType rateType = RateType.HISTORIC;
                if (fromTS.equals(LocalDate.now())) {
                    rateType = RateType.DEFERRED;
                }
                if (currencyToSdr) { // Currency -> SDR
                    ExchangeRate rate = new ExchangeRateBuilder(
                            ConversionContextBuilder.create(CONTEXT, rateType).set(fromTS).build())
                            .setBase(currency).setTerm(SDR).setFactor(new DefaultNumberValue(1d / values[i])).build();
                    List<ExchangeRate> rates = newCurrencyToSdr.get(currency);
                    if(rates==null){
                        rates = new ArrayList<>(5);
                        newCurrencyToSdr.put(currency,rates);
                    }
                    rates.add(rate);
                } else { // SDR -> Currency
                    ExchangeRate rate = new ExchangeRateBuilder(
                            ConversionContextBuilder.create(CONTEXT, rateType).set(fromTS)
                                    .set("LocalTime",fromTS.toString()).build())
                                    .setBase(SDR).setTerm(currency)
                                    .setFactor(DefaultNumberValue.of(1d / values[i])).build();
                    List<ExchangeRate> rates = newSdrToCurrency.get(currency);
                    if(rates==null){
                        rates = new ArrayList<>(5);
                        newSdrToCurrency.put(currency,rates);
                    }
                    rates.add(rate);
                }
            }
            line = pr.readLine();
        }
        // Cast is save, since contained DefaultExchangeRate is Comparable!
        for(List<ExchangeRate> list:newSdrToCurrency.values()){
            Collections.sort(List.class.cast(list));
        }
        for(List<ExchangeRate> list:newCurrencyToSdr.values()){
            Collections.sort(List.class.cast(list));
        }
        this.sdrToCurrency = newSdrToCurrency;
        this.currencyToSdr = newCurrencyToSdr;
        for(Map.Entry<CurrencyUnit, List<ExchangeRate>> entry: this.sdrToCurrency.entrySet()){
            LOGGER.finest("SDR -> " + entry.getKey().getCurrencyCode() + ": " + entry.getValue());
        }
        for(Map.Entry<CurrencyUnit, List<ExchangeRate>> entry: this.currencyToSdr.entrySet()){
            LOGGER.finest(entry.getKey().getCurrencyCode() + " -> SDR: " + entry.getValue());
        }
    }

    private Double[] parseValues(String[] parts) throws ParseException {

		ArrayList<Double> result = new ArrayList<>();
		int index = 0;
		for (String part : parts) {
			if(index == 0) {
				index++;
				continue;
			}
			if (part.isEmpty() || "NA".equals(part)) {
				index++;
				result.add(null);
				continue;
			}
			index++;
			result.add(Double.valueOf(part.trim().replace(",", "")));
		}
		return result.toArray(new Double[parts.length - 1]);
	}

    private List<LocalDate> readTimestamps(String line) throws ParseException {
        // Currency May 01, 2013 April 30, 2013 April 29, 2013 April 26, 2013
        // April 25, 2013
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH);
        @SuppressWarnings("Annotator") String[] parts = line.split("\\\t");
        List<LocalDate> dates = new ArrayList<>(parts.length);
        for (int i = 1; i < parts.length; i++) {
            Calendar date = GregorianCalendar.getInstance();
            date.setTime(sdf.parse(parts[i]));
            dates.add(LocalDate.from(date));
        }
        return dates;
    }

    @Override
    public ExchangeRate getExchangeRate(ConversionQuery conversionQuery) {
        try {
            if (loadLock.await(30, TimeUnit.SECONDS)) {
                if (currencyToSdr.isEmpty()) {
                    return null;
                }
                if (!isAvailable(conversionQuery)) {
                    return null;
                }
                CurrencyUnit base = conversionQuery.getBaseCurrency();
                CurrencyUnit term = conversionQuery.getCurrency();
                Calendar timestamp = conversionQuery.get(Calendar.class);
                if (timestamp == null) {
                    timestamp = conversionQuery.get(GregorianCalendar.class);
                }
                ExchangeRate rate1;
                ExchangeRate rate2;
                LocalDate localDate;
                if (timestamp == null) {
                    localDate = LocalDate.yesterday();
                    rate1 = lookupRate(currencyToSdr.get(base), localDate);
                    rate2 = lookupRate(sdrToCurrency.get(term), localDate);
                    if(rate1==null || rate2==null){
                        localDate = LocalDate.beforeDays(2);
                    }
                    rate1 = lookupRate(currencyToSdr.get(base), localDate);
                    rate2 = lookupRate(sdrToCurrency.get(term), localDate);
                    if(rate1==null || rate2==null){
                        localDate = LocalDate.beforeDays(3);
                        rate1 = lookupRate(currencyToSdr.get(base), localDate);
                        rate2 = lookupRate(sdrToCurrency.get(term), localDate);
                    }
                }
                else{
                    localDate = LocalDate.from(timestamp);
                    rate1 = lookupRate(currencyToSdr.get(base), localDate);
                    rate2 = lookupRate(sdrToCurrency.get(term), localDate);
                }
                if(rate1==null || rate2==null){
                    return null;
                }
                if (base.equals(SDR)) {
                    return rate2;
                } else if (term.equals(SDR)) {
                    return rate1;
                }
                ExchangeRateBuilder builder =
                        new ExchangeRateBuilder(ConversionContext.of(CONTEXT.getProviderName(), RateType.HISTORIC));
                builder.setBase(base);
                builder.setTerm(term);
                builder.setFactor(multiply(rate1.getFactor(), rate2.getFactor()));
                builder.setRateChain(rate1, rate2);
                return builder.build();
            }else{
                // Lets wait for a successful load only once, then answer requests as data is present.
                loadLock.countDown();
                throw new MonetaryException("Failed to load currency conversion data: " + loadState);
            }
        }
        catch(InterruptedException e){
            throw new MonetaryException("Failed to load currency conversion data: Load task has been interrupted.", e);
        }
    }

    private ExchangeRate lookupRate(List<ExchangeRate> list, LocalDate localDate) {
        if (list==null) {
            return null;
        }
        for (ExchangeRate rate : list) {
            if (localDate==null) {
                localDate = LocalDate.now();
            }
            if (rate!=null) {
                return rate;
            }
        }
        return null;
    }

}

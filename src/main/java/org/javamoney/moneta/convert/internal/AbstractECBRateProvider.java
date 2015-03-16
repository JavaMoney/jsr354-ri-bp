/**
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
package org.javamoney.moneta.convert.internal;

import java.io.InputStream;
import java.math.MathContext;
import java.net.MalformedURLException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import org.javamoney.bp.CurrencyUnit;
import org.javamoney.bp.MonetaryCurrencies;
import org.javamoney.bp.convert.ConversionContextBuilder;
import org.javamoney.bp.convert.ConversionQuery;
import org.javamoney.bp.convert.CurrencyConversionException;
import org.javamoney.bp.convert.ExchangeRate;
import org.javamoney.bp.convert.ProviderContext;
import org.javamoney.bp.convert.RateType;
import org.javamoney.bp.spi.Bootstrap;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.javamoney.moneta.ExchangeRateBuilder;
import org.javamoney.moneta.spi.AbstractRateProvider;
import org.javamoney.moneta.spi.DefaultNumberValue;
import org.javamoney.moneta.spi.LoaderService;
import org.javamoney.moneta.spi.LoaderService.LoaderListener;

/**
 * Base to all Europe Central Bank implementation.
 *
 * @author otaviojava
 */
abstract class AbstractECBRateProvider extends AbstractRateProvider implements
        LoaderListener {

    static final String BASE_CURRENCY_CODE = "EUR";

    /**
     * Base currency of the loaded rates is always EUR.
     */
    public static final CurrencyUnit BASE_CURRENCY = MonetaryCurrencies.getCurrency(BASE_CURRENCY_CODE);

    /**
     * Historic exchange rates, rate timestamp as UTC long.
     */
    protected final Map<LocalDate, Map<String, ExchangeRate>> rates = new ConcurrentHashMap<>();
    /**
     * Parser factory.
     */
    private SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();

    public AbstractECBRateProvider(ProviderContext context) throws MalformedURLException {
        super(context);
        saxParserFactory.setNamespaceAware(false);
        saxParserFactory.setValidating(false);
        LoaderService loader = Bootstrap.getService(LoaderService.class);
        loader.addLoaderListener(this, getDataId());
        loader.loadDataAsync(getDataId());
    }

    public abstract String getDataId();

    @Override
    public void newDataLoaded(String data, InputStream is) {
        final int oldSize = this.rates.size();
        try {
            SAXParser parser = saxParserFactory.newSAXParser();
            parser.parse(is, new ECBRateReader(rates, getContext()));
        } catch (Exception e) {
            LOGGER.log(Level.FINEST, "Error during data load.", e);
        }
        int newSize = this.rates.size();
        LOGGER.info("Loaded " + getDataId() + " exchange rates for days:" + (newSize - oldSize));
    }


    protected LocalDate[] getTargetDates(ConversionQuery query){
        if (rates.isEmpty()) {
            return new LocalDate[0];
        }
        LocalDate date;
        Calendar cal = query.get(GregorianCalendar.class);
        if(cal==null){
            cal = query.get(Calendar.class);
        }
        if(cal==null){
            date = LocalDate.now();
        }
        else{
            date = LocalDate.from(cal);
        }
        return new LocalDate[]{date, date.minusDays(1), date.minusDays(2), date.minusDays(3)};
    }

    @Override
    public ExchangeRate getExchangeRate(ConversionQuery query) {
        Objects.requireNonNull(query);
        LocalDate selectedDate = null;
        Map<String, ExchangeRate> targets = null;
        for(LocalDate date: getTargetDates(query)){
            targets = this.rates.get(date);
            if(targets!=null){
                selectedDate = date;
                break;
            }
        }
        if (targets==null) {
            return null;
        }
        ExchangeRateBuilder builder = getBuilder(query, selectedDate);
        ExchangeRate sourceRate = targets.get(query.getBaseCurrency()
                .getCurrencyCode());
        ExchangeRate target = targets
                .get(query.getCurrency().getCurrencyCode());
        return createExchangeRate(query, builder, sourceRate, target);
    }

    private ExchangeRate createExchangeRate(ConversionQuery query,
                                            ExchangeRateBuilder builder, ExchangeRate sourceRate,
                                            ExchangeRate target) {

        if (areBothBaseCurrencies(query)) {
            builder.setFactor(DefaultNumberValue.ONE);
            return builder.build();
        } else if (BASE_CURRENCY_CODE.equals(query.getCurrency().getCurrencyCode())) {
            if (sourceRate==null) {
                return null;
            }
            return reverse(sourceRate);
        } else if (BASE_CURRENCY_CODE.equals(query.getBaseCurrency()
                .getCurrencyCode())) {
            return target;
        } else {
            // Get Conversion base as derived rate: base -> EUR -> term
            ExchangeRate rate1 = getExchangeRate(
                    query.toBuilder().setTermCurrency(MonetaryCurrencies.getCurrency(BASE_CURRENCY_CODE)).build());
            ExchangeRate rate2 = getExchangeRate(
                    query.toBuilder().setBaseCurrency(MonetaryCurrencies.getCurrency(BASE_CURRENCY_CODE))
                            .setTermCurrency(query.getCurrency()).build());
            if (rate1!=null && rate2!=null) {
                builder.setFactor(multiply(rate1.getFactor(), rate2.getFactor()));
                builder.setRateChain(rate1, rate2);
                return builder.build();
            }
            throw new CurrencyConversionException(query.getBaseCurrency(),
                    query.getCurrency(), sourceRate.getContext());
        }
    }

    private boolean areBothBaseCurrencies(ConversionQuery query) {
        return BASE_CURRENCY_CODE.equals(query.getBaseCurrency().getCurrencyCode()) &&
                BASE_CURRENCY_CODE.equals(query.getCurrency().getCurrencyCode());
    }


    private ExchangeRateBuilder getBuilder(ConversionQuery query, LocalDate localDate) {
        ExchangeRateBuilder builder = new ExchangeRateBuilder(
                ConversionContextBuilder.create(getContext(), RateType.HISTORIC)
                        .set(localDate).set("LocalDate", localDate.toString()).build());
        builder.setBase(query.getBaseCurrency());
        builder.setTerm(query.getCurrency());
        return builder;
    }

    private ExchangeRate reverse(ExchangeRate rate) {
        if (rate==null) {
            throw new IllegalArgumentException("Rate null is not reversible.");
        }
        return new ExchangeRateBuilder(rate).setRate(rate).setBase(rate.getCurrency()).setTerm(rate.getBaseCurrency())
                .setFactor(divide(DefaultNumberValue.ONE, rate.getFactor(), MathContext.DECIMAL64)).build();
    }

}
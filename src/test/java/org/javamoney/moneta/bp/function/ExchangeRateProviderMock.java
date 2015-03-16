package org.javamoney.moneta.bp.function;

import org.javamoney.moneta.bp.spi.base.BaseExchangeRateProvider;

import org.javamoney.bp.CurrencyUnit;
import org.javamoney.bp.convert.ConversionQuery;
import org.javamoney.bp.convert.CurrencyConversion;
import org.javamoney.bp.convert.ExchangeRate;
import org.javamoney.bp.convert.ProviderContext;

/**
 * This class mock the exchange rate to test some {@link MonetaryFunctions} that
 * needs an exchange provider
 *
 * @author otaviojava
 */
class ExchangeRateProviderMock extends BaseExchangeRateProvider {

    @Override
    public ProviderContext getContext() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ExchangeRate getExchangeRate(ConversionQuery conversionQuery) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public CurrencyConversion getCurrencyConversion(
            ConversionQuery conversionQuery) {
        CurrencyUnit currencyUnit = conversionQuery.get("Query.termCurrency",
                CurrencyUnit.class);
        return new CurrencyConversionMock(currencyUnit);
    }

}

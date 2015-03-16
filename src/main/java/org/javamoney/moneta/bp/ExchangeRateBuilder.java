package org.javamoney.moneta.bp;

import org.javamoney.bp.api.CurrencyUnit;
import org.javamoney.bp.api.NumberValue;
import org.javamoney.bp.api.convert.ConversionContext;
import org.javamoney.bp.api.convert.ExchangeRate;
import org.javamoney.bp.api.convert.RateType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Builder for creating new instances of {@link org.javamoney.bp.api.convert.ExchangeRate}. Note that
 * instances of this class are not thread-safe.
 *
 * @author Anatole Tresch
 * @author Werner Keil
 */
public class ExchangeRateBuilder {

    /**
     * The {@link org.javamoney.bp.api.convert.ConversionContext}.
     */
    ConversionContext conversionContext;
    /**
     * The base (source) currency.
     */
    CurrencyUnit base;
    /**
     * The term (target) currency.
     */
    CurrencyUnit term;
    /**
     * The conversion factor.
     */
    NumberValue factor;
    /**
     * The chain of invovled rates.
     */
    List<ExchangeRate> rateChain = new ArrayList<>();

    /**
     * Sets the exchange rate type
     *
     * @param rateType the {@link org.javamoney.bp.api.convert.RateType} contained
     */
    public ExchangeRateBuilder(String provider, RateType rateType) {
        this(ConversionContext.of(provider, rateType));
    }

    /**
     * Sets the exchange rate type
     *
     * @param context the {@link org.javamoney.bp.api.convert.ConversionContext} to be applied
     */
    public ExchangeRateBuilder(ConversionContext context) {
        setContext(context);
    }

    /**
     * Sets the exchange rate type
     *
     * @param rate the {@link org.javamoney.bp.api.convert.ExchangeRate} to be applied
     */
    public ExchangeRateBuilder(ExchangeRate rate) {
        setContext(rate.getContext());
        setFactor(rate.getFactor());
        setTerm(rate.getCurrency());
        setBase(rate.getBaseCurrency());
        setRateChain(rate.getExchangeRateChain());
    }

    /**
     * Sets the base {@link org.javamoney.bp.api.CurrencyUnit}
     *
     * @param base to base (source) {@link org.javamoney.bp.api.CurrencyUnit} to be applied
     * @return the builder instance
     */
    public ExchangeRateBuilder setBase(CurrencyUnit base) {
        this.base = base;
        return this;
    }

    /**
     * Sets the terminating (target) {@link org.javamoney.bp.api.CurrencyUnit}
     *
     * @param term to terminating {@link org.javamoney.bp.api.CurrencyUnit} to be applied
     * @return the builder instance
     */
    public ExchangeRateBuilder setTerm(CurrencyUnit term) {
        this.term = term;
        return this;
    }

    /**
     * Sets the {@link org.javamoney.bp.api.convert.ExchangeRate} chain.
     *
     * @param exchangeRates the {@link org.javamoney.bp.api.convert.ExchangeRate} chain to be applied
     * @return the builder instance
     */
    public ExchangeRateBuilder setRateChain(ExchangeRate... exchangeRates) {
        this.rateChain.clear();
        if (exchangeRates!=null) {
            this.rateChain.addAll(Arrays.asList(exchangeRates.clone()));
        }
        return this;
    }

    /**
     * Sets the {@link org.javamoney.bp.api.convert.ExchangeRate} chain.
     *
     * @param exchangeRates the {@link org.javamoney.bp.api.convert.ExchangeRate} chain to be applied
     * @return the builder instance
     */
    public ExchangeRateBuilder setRateChain(List<ExchangeRate> exchangeRates) {
        this.rateChain.clear();
        if (exchangeRates!=null) {
            this.rateChain.addAll(exchangeRates);
        }
        return this;
    }


    /**
     * Sets the conversion factor, as the factor
     * {@code base * factor = target}.
     *
     * @param factor the factor.
     * @return The builder instance.
     */
    public ExchangeRateBuilder setFactor(NumberValue factor) {
        this.factor = factor;
        return this;
    }

    /**
     * Sets the provider to be applied.
     *
     * @param conversionContext the {@link org.javamoney.bp.api.convert.ConversionContext}, not null.
     * @return The builder.
     */
    public ExchangeRateBuilder setContext(ConversionContext conversionContext) {
        Objects.requireNonNull(conversionContext);
        this.conversionContext = conversionContext;
        return this;
    }

    /**
     * Builds a new instance of {@link org.javamoney.bp.api.convert.ExchangeRate}.
     *
     * @return a new instance of {@link org.javamoney.bp.api.convert.ExchangeRate}.
     * @throws IllegalArgumentException if the rate could not be built.
     */
    public ExchangeRate build() {
        return new DefaultExchangeRate(this);
    }

    /**
     * Initialize the {@link ExchangeRateBuilder} with an {@link org.javamoney.bp.api.convert.ExchangeRate}. This is
     * useful for creating a new rate, reusing some properties from an
     * existing one.
     *
     * @param rate the base rate
     * @return the Builder, for chaining.
     */
    public ExchangeRateBuilder setRate(ExchangeRate rate) {
        this.base = rate.getBaseCurrency();
        this.term = rate.getCurrency();
        this.conversionContext = rate.getContext();
        this.factor = rate.getFactor();
        this.rateChain = rate.getExchangeRateChain();
        this.term = rate.getCurrency();
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("org.javamoney.moneta.ExchangeRateBuilder: ");
        sb.append("[conversionContext").append(conversionContext).append(',');
        sb.append("base").append(base).append(',');
        sb.append("term").append(term).append(',');
        sb.append("factor").append(factor).append(',');
        sb.append("rateChain").append(rateChain).append(']');
        return sb.toString();
    }
}

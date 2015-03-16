/*
 * CREDIT SUISSE IS WILLING TO LICENSE THIS SPECIFICATION TO YOU ONLY UPON THE
 * CONDITION THAT YOU ACCEPT ALL OF THE TERMS CONTAINED IN THIS AGREEMENT.
 * PLEASE READ THE TERMS AND CONDITIONS OF THIS AGREEMENT CAREFULLY. BY
 * DOWNLOADING THIS SPECIFICATION, YOU ACCEPT THE TERMS AND CONDITIONS OF THE
 * AGREEMENT. IF YOU ARE NOT WILLING TO BE BOUND BY IT, SELECT THE "DECLINE"
 * BUTTON AT THE BOTTOM OF THIS PAGE. Specification: JSR-354 Money and Currency
 * API ("Specification") Copyright (c) 2012-2014, Credit Suisse All rights
 * reserved.
 */
package org.javamoney.moneta.spi.base;

import org.javamoney.bp.CurrencyUnit;
import org.javamoney.bp.MonetaryCurrencies;
import org.javamoney.bp.convert.ConversionQuery;
import org.javamoney.bp.convert.ConversionQueryBuilder;
import org.javamoney.bp.convert.CurrencyConversion;
import org.javamoney.bp.convert.ExchangeRate;
import org.javamoney.bp.convert.ExchangeRateProvider;
import java.util.Objects;

/**
 * This interface defines access to the exchange rates provided by a provider.
 * The provider and its capabilities can be defined in arbitrary detail by the
 * corresponding {@link org.javamoney.bp.convert.ProviderContext}.
 * Instances of this class must only provide conversion data for exact one provider, identified by
 * {@link org.javamoney.bp.convert.ProviderContext#getProviderName()}.
 *
 * When accessing ExchangeRateProvider instances or {@link org.javamoney.bp.convert.CurrencyConversion} instances from the
 * {@link org.javamoney.bp.convert.MonetaryConversions}
 * in many cases a chain of providers will be returned. It is the reponsibility of the implementation code assembling
 * the chain to
 * establish a well defined coordination mechanism for evaluating the correct result. By default the first provider
 * in the chain that returns a non null result determines the final result of a call. Nevertheless adapting the
 * {@link org.javamoney.bp.spi.MonetaryConversionsSingletonSpi} allows
 * to implement also alternate strategies, e.g. honoring different priorities of providers as well.
 * <p>
 * Implementations of this interface are required to be thread save.
 * <p>
 * Implementations of this class must neither be immutable nor serializable.
 *
 * @author Anatole Tresch
 * @author Werner Keil
 */
public abstract class BaseExchangeRateProvider implements ExchangeRateProvider{

    /**
     * Checks if an {@link org.javamoney.bp.convert.ExchangeRate} between two {@link org.javamoney.bp.CurrencyUnit} is
     * available from this provider. This method should check, if a given rate
     * is <i>currently</i> defined.
     *
     * @param conversionQuery the required {@link org.javamoney.bp.convert.ConversionQuery}, not {@code null}
     * @return {@code true}, if such an {@link org.javamoney.bp.convert.ExchangeRate} is currently
     * defined.
     */
    public boolean isAvailable(ConversionQuery conversionQuery){
        Objects.requireNonNull(conversionQuery);
        try{
            return conversionQuery.getProviderNames().isEmpty() ||
                    conversionQuery.getProviderNames().contains(getContext().getProviderName());
        }
        catch(Exception e){
            return false;
        }
    }


    /**
     * Access a {@link org.javamoney.bp.convert.ExchangeRate} using the given currencies. The
     * {@link org.javamoney.bp.convert.ExchangeRate} may be, depending on the data provider, eal-time or
     * deferred. This method should return the rate that is <i>currently</i>
     * valid.
     *
     * @param base base {@link org.javamoney.bp.CurrencyUnit}, not {@code null}
     * @param term term {@link org.javamoney.bp.CurrencyUnit}, not {@code null}
     * @throws org.javamoney.bp.convert.CurrencyConversionException If no such rate is available.
     */
    public ExchangeRate getExchangeRate(CurrencyUnit base, CurrencyUnit term){
        Objects.requireNonNull(base, "Base Currency is null");
        Objects.requireNonNull(term, "Term Currency is null");
        return getExchangeRate(ConversionQueryBuilder.of().setBaseCurrency(base).setTermCurrency(term).build());
    }

    /**
     * Access a {@link org.javamoney.bp.convert.CurrencyConversion} that can be applied as a
     * {@link org.javamoney.bp.MonetaryOperator} to an amount.
     *
     * @param term term {@link org.javamoney.bp.CurrencyUnit}, not {@code null}
     * @return a new instance of a corresponding {@link org.javamoney.bp.convert.CurrencyConversion},
     * never {@code null}.
     */
    public CurrencyConversion getCurrencyConversion(CurrencyUnit term){
        return getCurrencyConversion(ConversionQueryBuilder.of().setTermCurrency(term).build());
    }

    /**
     * Checks if an {@link org.javamoney.bp.convert.ExchangeRate} between two {@link org.javamoney.bp.CurrencyUnit} is
     * available from this provider. This method should check, if a given rate
     * is <i>currently</i> defined.
     *
     * @param base the base {@link org.javamoney.bp.CurrencyUnit}
     * @param term the term {@link org.javamoney.bp.CurrencyUnit}
     * @return {@code true}, if such an {@link org.javamoney.bp.convert.ExchangeRate} is currently
     * defined.
     */
    public boolean isAvailable(CurrencyUnit base, CurrencyUnit term){
        return isAvailable(ConversionQueryBuilder.of().setBaseCurrency(base).setTermCurrency(term).build());
    }


    /**
     * Checks if an {@link org.javamoney.bp.convert.ExchangeRate} between two {@link org.javamoney.bp.CurrencyUnit} is
     * available from this provider. This method should check, if a given rate
     * is <i>currently</i> defined.
     *
     * @param baseCode the base currency code
     * @param termCode the terminal/target currency code
     * @return {@code true}, if such an {@link org.javamoney.bp.convert.ExchangeRate} is currently
     * defined.
     * @throws org.javamoney.bp.MonetaryException if one of the currency codes passed is not valid.
     */
    public boolean isAvailable(String baseCode, String termCode){
        return isAvailable(MonetaryCurrencies.getCurrency(baseCode), MonetaryCurrencies.getCurrency(termCode));
    }


    /**
     * Access a {@link org.javamoney.bp.convert.ExchangeRate} using the given currencies. The
     * {@link org.javamoney.bp.convert.ExchangeRate} may be, depending on the data provider, eal-time or
     * deferred. This method should return the rate that is <i>currently</i>
     * valid.
     *
     * @param baseCode base currency code, not {@code null}
     * @param termCode term/target currency code, not {@code null}
     * @return the matching {@link org.javamoney.bp.convert.ExchangeRate}.
     * @throws org.javamoney.bp.convert.CurrencyConversionException If no such rate is available.
     * @throws org.javamoney.bp.MonetaryException           if one of the currency codes passed is not valid.
     */
    public ExchangeRate getExchangeRate(String baseCode, String termCode){
        return getExchangeRate(MonetaryCurrencies.getCurrency(baseCode), MonetaryCurrencies.getCurrency(termCode));
    }


    /**
     * The method reverses the {@link org.javamoney.bp.convert.ExchangeRate} to a rate mapping from term
     * to base {@link org.javamoney.bp.CurrencyUnit}. Hereby the factor must <b>not</b> be
     * recalculated as {@code 1/oldFactor}, since typically reverse rates are
     * not symmetric in most cases.
     *
     * @return the matching reversed {@link org.javamoney.bp.convert.ExchangeRate}, or {@code null}, if
     * the rate cannot be reversed.
     */
    public ExchangeRate getReversed(ExchangeRate rate){
        ConversionQuery reverseQuery = rate.getContext().toQueryBuilder().setBaseCurrency(rate.getCurrency())
                .setTermCurrency(rate.getBaseCurrency()).build();
        if(isAvailable(reverseQuery)){
            return getExchangeRate(reverseQuery);
        }
        return null;
    }


    /**
     * Access a {@link org.javamoney.bp.convert.CurrencyConversion} that can be applied as a
     * {@link org.javamoney.bp.MonetaryOperator} to an amount.
     *
     * @param termCode terminal/target currency code, not {@code null}
     * @return a new instance of a corresponding {@link org.javamoney.bp.convert.CurrencyConversion},
     * never {@code null}.
     * @throws org.javamoney.bp.MonetaryException if one of the currency codes passed is not valid.
     */
    public CurrencyConversion getCurrencyConversion(String termCode){
        return getCurrencyConversion(MonetaryCurrencies.getCurrency(termCode));
    }

}

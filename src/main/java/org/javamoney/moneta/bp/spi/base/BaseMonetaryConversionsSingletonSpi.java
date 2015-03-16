/*
 * CREDIT SUISSE IS WILLING TO LICENSE THIS SPECIFICATION TO YOU ONLY UPON THE CONDITION THAT YOU
 * ACCEPT ALL OF THE TERMS CONTAINED IN THIS AGREEMENT. PLEASE READ THE TERMS AND CONDITIONS OF THIS
 * AGREEMENT CAREFULLY. BY DOWNLOADING THIS SPECIFICATION, YOU ACCEPT THE TERMS AND CONDITIONS OF
 * THE AGREEMENT. IF YOU ARE NOT WILLING TO BE BOUND BY IT, SELECT THE "DECLINE" BUTTON AT THE
 * BOTTOM OF THIS PAGE. Specification: JSR-354 Money and Currency API ("Specification") Copyright
 * (c) 2012-2015, Credit Suisse All rights reserved.
 */
package org.javamoney.moneta.bp.spi.base;

import org.javamoney.bp.api.CurrencyUnit;
import org.javamoney.bp.api.MonetaryException;
import org.javamoney.bp.api.convert.ConversionQuery;
import org.javamoney.bp.api.convert.ConversionQueryBuilder;
import org.javamoney.bp.api.convert.CurrencyConversion;
import org.javamoney.bp.api.convert.ExchangeRateProvider;
import org.javamoney.bp.api.spi.MonetaryConversionsSingletonSpi;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * SPI (conversoin) that implements the functionalities provided by the
 * {@code MonetaryConversions} singleton accessor. It should be registered as a
 * service using the JDK {@code ServiceLoader}. Hereby only one instance can be
 * registered at a time.
 * <p>
 * This interface is designed to support also contextual behaviour, e.g. in Java
 * EE containers each application may provide its own
 * {@link org.javamoney.bp.api.convert.ExchangeRateProvider} instances, e.g. by registering them as CDI
 * beans. An EE container can register an according
 * {@link BaseMonetaryConversionsSingletonSpi} that manages the different application
 * contexts transparently. In a SE environment this class is expected to behave
 * like an ordinary singleton, loading its SPIs from the {@link java.util.ServiceLoader}.
 * <p>
 * Instances of this class must be thread safe. It is not a requirement that
 * they are serializable.
 * <p>
 * Only one instance can be registered using the {@link java.util.ServiceLoader}. When
 * registering multiple instances the {@link org.javamoney.bp.api.convert.MonetaryConversions} accessor will
 * not work.
 *
 * @author Anatole Tresch
 * @author Werner Keil
 */
public abstract class BaseMonetaryConversionsSingletonSpi implements MonetaryConversionsSingletonSpi{

    /**
     * Allows to quickly check, if a {@link org.javamoney.bp.api.convert.ExchangeRateProvider} is accessible for the given
     * {@link org.javamoney.bp.api.convert.ConversionQuery}.
     *
     * @param conversionQuery the {@link org.javamoney.bp.api.convert.ConversionQuery} determining the type of conversion
     *                        required, not null.
     * @return {@code true}, if such a conversion is supported, meaning an according
     * {@link org.javamoney.bp.api.convert.ExchangeRateProvider} can be
     * accessed.
     * @see #getExchangeRateProvider(org.javamoney.bp.api.convert.ConversionQuery)
     * @see #getExchangeRateProvider(String...)}
     */
    public boolean isExchangeRateProviderAvailable(ConversionQuery conversionQuery) {
        try {
            return getExchangeRateProvider(conversionQuery) != null;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Allows to quickly check, if a {@link org.javamoney.bp.api.convert.CurrencyConversion} is accessible for the given
     * {@link org.javamoney.bp.api.convert.ConversionQuery}.
     *
     * @param conversionQuery the {@link org.javamoney.bp.api.convert.ConversionQuery} determining the type of conversion
     *                        required, not null.
     * @return {@code true}, if such a conversion is supported, meaning an according
     * {@link org.javamoney.bp.api.convert.CurrencyConversion} can be
     * accessed.
     * @see #getConversion(org.javamoney.bp.api.convert.ConversionQuery)
     * @see #getConversion(org.javamoney.bp.api.CurrencyUnit, String...)}
     */
    public boolean isConversionAvailable(ConversionQuery conversionQuery) {
        try {
            return getConversion(conversionQuery) != null;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Allows to quickly check, if a {@link org.javamoney.bp.api.convert.CurrencyConversion} is accessible for the given
     * {@link org.javamoney.bp.api.convert.ConversionQuery}.
     *
     * @param termCurrency the terminating/target currency unit, not null.
     * @param providers    the provider names defines a corresponding
     *                     prpovider chain that must be encapsulated by the resulting {@link javax
     *                     .money.convert.CurrencyConversion}. By default the provider
     *                     chain as defined by #getDefaultProviderChain will be used.
     * @return {@code true}, if such a conversion is supported, meaning an according
     * {@link org.javamoney.bp.api.convert.CurrencyConversion} can be
     * accessed.
     * @see #getConversion(org.javamoney.bp.api.convert.ConversionQuery)
     * @see #getConversion(org.javamoney.bp.api.CurrencyUnit, String...)}
     */
    public boolean isConversionAvailable(CurrencyUnit termCurrency, String... providers) {
        return isConversionAvailable(
                ConversionQueryBuilder.of().setTermCurrency(termCurrency).setProviderNames(providers).build());
    }

    /**
     * Access the current registered {@link org.javamoney.bp.api.convert.ExchangeRateProvider} instances. If no provider
     * names are passed ALL current registered providers are returned in undefined order.
     *
     * @param providers the provider names of hte providers to be accessed
     * @return the list of providers, in the same order as requested.
     * @throws org.javamoney.bp.api.MonetaryException if a provider could not be resolved.
     */
    public List<ExchangeRateProvider> getExchangeRateProviders(String... providers) {
        List<ExchangeRateProvider> provInstances = new ArrayList<>();
        Collection<String> providerNames = Arrays.asList(providers);
        if (providerNames.isEmpty()) {
            providerNames = getProviderNames();
        }
        for (String provName : providerNames) {
            ExchangeRateProvider provider = getExchangeRateProvider(provName);
            if(provider==null){
                throw new MonetaryException("Unsupported conversion/rate provider: " + provName);
            }
            provInstances.add(provider);
        }
        return provInstances;
    }

    /**
     * Access a compound instance of an {@link org.javamoney.bp.api.convert.ExchangeRateProvider} based on the given provider chain.
     *
     * @param providers the {@link org.javamoney.bp.api.convert.ConversionQuery} provider names defines a corresponding
     *                  provider chain that must be
     *                  encapsulated by the resulting {@link org.javamoney.bp.api.convert.ExchangeRateProvider}. By default
     *                  the default
     *                  provider changes as defined in #getDefaultProviderChain will be used.
     * @return an {@link org.javamoney.bp.api.convert.ExchangeRateProvider} built up with the given sub
     * providers, never {@code null}.
     * @throws org.javamoney.bp.api.MonetaryException if a provider listed could not be found.
     * @see #getProviderNames()
     * @see #isExchangeRateProviderAvailable(org.javamoney.bp.api.convert.ConversionQuery)
     */
    public ExchangeRateProvider getExchangeRateProvider(String... providers) {
        return getExchangeRateProvider(ConversionQueryBuilder.of().setProviderNames(providers).build());
    }

    /**
     * Access an instance of {@link org.javamoney.bp.api.convert.CurrencyConversion}.
     *
     * @param conversionQuery the {@link org.javamoney.bp.api.convert.ConversionQuery} determining the type of conversion
     *                        required, not null.
     * @return the corresponding conversion, not null.
     * @throws org.javamoney.bp.api.MonetaryException if no matching conversion could be found.
     * @see #isConversionAvailable(org.javamoney.bp.api.convert.ConversionQuery)
     */
    public CurrencyConversion getConversion(ConversionQuery conversionQuery) {
        return getExchangeRateProvider(conversionQuery).getCurrencyConversion(
                Objects.requireNonNull(conversionQuery.getCurrency(), "Terminating Currency is required.")
        );
    }

    /**
     * Access an instance of {@link org.javamoney.bp.api.convert.CurrencyConversion}.
     *
     * @param termCurrency the terminating/target currency unit, not null.
     * @param providers    the {@link org.javamoney.bp.api.convert.ConversionQuery} provider names defines a corresponding
     *                     provider chain that must be encapsulated by the resulting {@link javax
     *                     .money.convert.CurrencyConversion}. By default the default
     *                     provider chain as defined by #getDefaultProviderChain will be used.
     * @return the corresponding conversion, not null.
     * @throws org.javamoney.bp.api.MonetaryException if no matching conversion could be found.
     * @see #isConversionAvailable(org.javamoney.bp.api.convert.ConversionQuery)
     */
    public CurrencyConversion getConversion(CurrencyUnit termCurrency, String... providers) {
        return getConversion(ConversionQueryBuilder.of().setTermCurrency(termCurrency).setProviderNames(providers).build());
    }

}

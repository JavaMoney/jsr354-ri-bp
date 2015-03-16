/*
 * CREDIT SUISSE IS WILLING TO LICENSE THIS SPECIFICATION TO YOU ONLY UPON THE CONDITION THAT YOU
 * ACCEPT ALL OF THE TERMS CONTAINED IN THIS AGREEMENT. PLEASE READ THE TERMS AND CONDITIONS OF THIS
 * AGREEMENT CAREFULLY. BY DOWNLOADING THIS SPECIFICATION, YOU ACCEPT THE TERMS AND CONDITIONS OF
 * THE AGREEMENT. IF YOU ARE NOT WILLING TO BE BOUND BY IT, SELECT THE "DECLINE" BUTTON AT THE
 * BOTTOM OF THIS PAGE. Specification: JSR-354 Money and Currency API ("Specification") Copyright
 * (c) 2012-2013, Credit Suisse All rights reserved.
 */
package org.javamoney.moneta.bp.spi.base;

import org.javamoney.bp.api.MonetaryException;
import org.javamoney.bp.api.format.AmountFormatQuery;
import org.javamoney.bp.api.format.AmountFormatQueryBuilder;
import org.javamoney.bp.api.format.MonetaryAmountFormat;
import org.javamoney.bp.api.spi.MonetaryFormatsSingletonSpi;
import java.util.Collection;
import java.util.Locale;

/**
 * This interface models the singleton functionality of {@link org.javamoney.bp.api.format.MonetaryFormats}.
 * <p>
 * Implementations of this interface must be thread-safe.
 *
 * @author Anatole Tresch
 * @author Werner Keil
 */
public abstract class BaseMonetaryFormatsSingletonSpi implements MonetaryFormatsSingletonSpi{

    /**
     * Access an {@link org.javamoney.bp.api.format.MonetaryAmountFormat} given a {@link org.javamoney.bp.api.format
     * .AmountFormatQuery}.
     *
     * @param formatQuery The format query defining the requirements of the formatter.
     * @return the corresponding {@link org.javamoney.bp.api.format.MonetaryAmountFormat}
     * @throws org.javamoney.bp.api.MonetaryException if no registered {@link org.javamoney.bp.api.spi
     *                                       .MonetaryAmountFormatProviderSpi} can provide a
     *                                       corresponding {@link org.javamoney.bp.api.format.MonetaryAmountFormat} instance.
     */
    public MonetaryAmountFormat getAmountFormat(AmountFormatQuery formatQuery) {
        Collection<MonetaryAmountFormat> formats = getAmountFormats(formatQuery);
        if (formats.isEmpty()) {
            throw new MonetaryException("No MonetaryAmountFormat for AmountFormatQuery " + formatQuery);
        }
        return formats.iterator().next();
    }

    /**
     * Checks if a {@link org.javamoney.bp.api.format.MonetaryAmountFormat} is available given a {@link org.javamoney.bp.api.format
     * .AmountFormatQuery}.
     *
     * @param formatQuery The format query defining the requirements of the formatter.
     * @return true, if a t least one {@link org.javamoney.bp.api.format.MonetaryAmountFormat} is matching the query.
     */
    public boolean isAvailable(AmountFormatQuery formatQuery) {
        return !getAmountFormats(formatQuery).isEmpty();
    }

    /**
     * Checks if a {@link org.javamoney.bp.api.format.MonetaryAmountFormat} is available given a {@link org.javamoney.bp.api.format
     * .AmountFormatQuery}.
     *
     * @param locale    the target {@link java.util.Locale}, not {@code null}.
     * @param providers The (optional) providers to be used, ordered correspondingly.
     * @return true, if a t least one {@link org.javamoney.bp.api.format.MonetaryAmountFormat} is matching the query.
     */
    public boolean isAvailable(Locale locale, String... providers) {
        return isAvailable(AmountFormatQuery.of(locale, providers));
    }

    /**
     * Access the default {@link org.javamoney.bp.api.format.MonetaryAmountFormat} given a {@link java.util.Locale}.
     *
     * @param locale    the target {@link java.util.Locale}, not {@code null}.
     * @param providers The (optional) providers to be used, oredered correspondingly.
     * @return the matching {@link org.javamoney.bp.api.format.MonetaryAmountFormat}
     * @throws org.javamoney.bp.api.MonetaryException if no registered {@link org.javamoney.bp.api.spi.MonetaryAmountFormatProviderSpi} can provide a
     *                           corresponding {@link org.javamoney.bp.api.format.MonetaryAmountFormat} instance.
     */
    public MonetaryAmountFormat getAmountFormat(Locale locale, String... providers) {
        return getAmountFormat(AmountFormatQueryBuilder.of(locale).setProviderNames(providers).build());
    }

    /**
     * Access the default {@link org.javamoney.bp.api.format.MonetaryAmountFormat} given a {@link java.util.Locale}.
     *
     * @param formatName the target format name, not {@code null}.
     * @param providers  The (optional) providers to be used, ordered correspondingly.
     * @return the matching {@link org.javamoney.bp.api.format.MonetaryAmountFormat}
     * @throws org.javamoney.bp.api.MonetaryException if no registered {@link org.javamoney.bp.api.spi.MonetaryAmountFormatProviderSpi} can provide a
     *                           corresponding {@link org.javamoney.bp.api.format.MonetaryAmountFormat} instance.
     */
    public MonetaryAmountFormat getAmountFormat(String formatName, String... providers) {
        return getAmountFormat(AmountFormatQueryBuilder.of(formatName).setProviderNames(providers).build());
    }

}

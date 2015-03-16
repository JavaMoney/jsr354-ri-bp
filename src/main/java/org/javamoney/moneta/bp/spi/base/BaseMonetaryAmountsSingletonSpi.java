/*
 * CREDIT SUISSE IS WILLING TO LICENSE THIS SPECIFICATION TO YOU ONLY UPON THE CONDITION THAT YOU
 * ACCEPT ALL OF THE TERMS CONTAINED IN THIS AGREEMENT. PLEASE READ THE TERMS AND CONDITIONS OF THIS
 * AGREEMENT CAREFULLY. BY DOWNLOADING THIS SPECIFICATION, YOU ACCEPT THE TERMS AND CONDITIONS OF
 * THE AGREEMENT. IF YOU ARE NOT WILLING TO BE BOUND BY IT, SELECT THE "DECLINE" BUTTON AT THE
 * BOTTOM OF THIS PAGE. Specification: JSR-354 Money and Currency API ("Specification") Copyright
 * (c) 2012-2013, Credit Suisse All rights reserved.
 */
package org.javamoney.moneta.bp.spi.base;

import org.javamoney.bp.api.MonetaryAmount;
import org.javamoney.bp.api.MonetaryAmountFactory;
import org.javamoney.bp.api.spi.MonetaryAmountsSingletonSpi;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * SPI (core) for the backing implementation of the {@link org.javamoney.bp.api.MonetaryAmounts} singleton. It
 * should load and manage (including contextual behavior), if needed) the different registered
 * {@link org.javamoney.bp.api.MonetaryAmountFactory} instances.
 *
 * @author Anatole Tresch
 */
public abstract class BaseMonetaryAmountsSingletonSpi implements MonetaryAmountsSingletonSpi{

    /**
     * Access the default {@link org.javamoney.bp.api.MonetaryAmountFactory}.
     *
     * @return a the default {@link org.javamoney.bp.api.MonetaryAmount} type corresponding, never {@code null}.
     * @throws org.javamoney.bp.api.MonetaryException if no {@link org.javamoney.bp.api.spi.MonetaryAmountFactoryProviderSpi} is available, or no
     *                           {@link org.javamoney.bp.api.spi.MonetaryAmountFactoryProviderSpi} targeting the configured default
     *                           {@link org.javamoney.bp.api.MonetaryAmount} type.
     * @see org.javamoney.bp.api.MonetaryAmounts#getDefaultAmountType()
     */
    public MonetaryAmountFactory<?> getDefaultAmountFactory(){
        return getAmountFactory(getDefaultAmountType());
    }

    /**
     * Get the currently registered {@link org.javamoney.bp.api.MonetaryAmount} implementation classes.
     *
     * @return the {@link java.util.Set} if registered {@link org.javamoney.bp.api.MonetaryAmount} implementations, never
     * {@code null}.
     */
    public Collection<MonetaryAmountFactory<?>> getAmountFactories(){
        List<MonetaryAmountFactory<?>> factories = new ArrayList<>();
        for(Class<? extends MonetaryAmount> type : getAmountTypes()){
            factories.add(getAmountFactory(type));
        }
        return factories;
    }

}
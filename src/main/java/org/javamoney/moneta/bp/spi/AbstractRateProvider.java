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
package org.javamoney.moneta.bp.spi;

import org.javamoney.bp.api.convert.ConversionContext;
import org.javamoney.bp.api.convert.ConversionQuery;
import org.javamoney.bp.api.convert.CurrencyConversion;
import org.javamoney.bp.api.convert.ExchangeRate;
import org.javamoney.bp.api.convert.ProviderContext;
import org.javamoney.bp.api.convert.RateType;
import org.javamoney.moneta.bp.spi.base.BaseExchangeRateProvider;

import org.javamoney.bp.api.NumberValue;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Calendar;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Abstract base class for {@link org.javamoney.bp.api.convert.ExchangeRateProvider} implementations.
 *
 * @author Anatole Tresch
 * @author Werner Keil
 */
public abstract class AbstractRateProvider extends BaseExchangeRateProvider {

    /**
     * The logger used.
     */
    protected final Logger LOGGER = Logger.getLogger(getClass().getName());

    /**
     * The {@link org.javamoney.bp.api.convert.ConversionContext} of this provider.
     */
    private ProviderContext providerContext;

    /**
     * Constructor.
     *
     * @param providerContext the {@link ProviderContext}, not null.
     */
    public AbstractRateProvider(ProviderContext providerContext) {
        Objects.requireNonNull(providerContext);
        this.providerContext = providerContext;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.javamoney.bp.api.convert.spi.ExchangeRateProviderSpi#getExchangeRateType
     * ()
     */
    @Override
    public ProviderContext getContext() {
        return providerContext;
    }

    @Override
    public abstract ExchangeRate getExchangeRate(ConversionQuery conversionQuery);

    @Override
    public CurrencyConversion getCurrencyConversion(ConversionQuery conversionQuery) {
        if (getContext().getRateTypes().size() == 1) {
            return new LazyBoundCurrencyConversion(conversionQuery, this, ConversionContext
                    .of(getContext().getProviderName(), getContext().getRateTypes().iterator().next()));
        }
        return new LazyBoundCurrencyConversion(conversionQuery, this,
                ConversionContext.of(getContext().getProviderName(), RateType.ANY));
    }


    /**
     * A protected helper method to multiply 2 {@link NumberValue} types.<br>
     * If either of the values is <code>null</code> an {@link ArithmeticException} is thrown.
     *
     * @param multiplicand the first value to be multiplied
     * @param multiplier   the second value to be multiplied
     * @return the result of the multiplication as {@link NumberValue}
     */
    protected static NumberValue multiply(NumberValue multiplicand, NumberValue multiplier) {
        if (multiplicand==null) {
            throw new ArithmeticException("The multiplicand cannot be null");
        }
        if (multiplier==null) {
            throw new ArithmeticException("The multiplier cannot be null");
        }
        return new DefaultNumberValue(
                multiplicand.numberValueExact(BigDecimal.class).multiply(multiplier.numberValue(BigDecimal.class)));
    }

    /**
     * A protected helper method to divide 2 {@link NumberValue} types.<br>
     * If either of the values is <code>null</code> an {@link ArithmeticException} is thrown.
     *
     * @param dividend the first value to be divided
     * @param divisor  the value to be divided by
     * @return the result of the division as {@link NumberValue}
     */
    protected static NumberValue divide(NumberValue dividend, NumberValue divisor) {
        if (dividend==null) {
            throw new ArithmeticException("The dividend cannot be null");
        }
        if (divisor==null) {
            throw new ArithmeticException("The divisor cannot be null");
        }
        return new DefaultNumberValue(
                dividend.numberValueExact(BigDecimal.class).divide(divisor.numberValue(BigDecimal.class)));
    }

    /**
     * A protected helper method to divide 2 {@link NumberValue} types.<br>
     * If either of the values is <code>null</code> an {@link ArithmeticException} is thrown.
     *
     * @param dividend the first value to be divided
     * @param divisor  the value to be divided by
     * @param context  the {@link MathContext} to use
     * @return the result of the division as {@link NumberValue}
     */
    protected static NumberValue divide(NumberValue dividend, NumberValue divisor, MathContext context) {
        if (dividend==null) {
            throw new ArithmeticException("The dividend cannot be null");
        }
        if (divisor==null) {
            throw new ArithmeticException("The divisor cannot be null");
        }
        return new DefaultNumberValue(
                dividend.numberValueExact(BigDecimal.class).divide(divisor.numberValue(BigDecimal.class), context));
    }

    protected String formatLocalDate(Calendar calendar){
        return calendar.get(Calendar.YEAR) + "-" + calendar.get(Calendar.MONTH) +
                "-" + calendar.get(Calendar.DAY_OF_MONTH);
    }
}

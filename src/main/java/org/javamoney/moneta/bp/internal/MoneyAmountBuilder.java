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
package org.javamoney.moneta.bp.internal;

import java.math.RoundingMode;

import org.javamoney.bp.api.CurrencyUnit;
import org.javamoney.bp.api.MonetaryContext;
import org.javamoney.bp.api.MonetaryContextBuilder;
import org.javamoney.bp.api.NumberValue;

import org.javamoney.moneta.bp.Money;
import org.javamoney.moneta.bp.spi.AbstractAmountBuilder;

/**
 * Implementation of {@link org.javamoney.bp.api.MonetaryAmountFactory} creating instances of {@link Money}.
 *
 * @author Anatole Tresch
 */
public class MoneyAmountBuilder extends AbstractAmountBuilder<Money> {

    static final MonetaryContext DEFAULT_CONTEXT =
            MonetaryContextBuilder.of(Money.class).set(64).setMaxScale(63).set(RoundingMode.HALF_EVEN).build();
    static final MonetaryContext MAX_CONTEXT =
            MonetaryContextBuilder.of(Money.class).setPrecision(0).setMaxScale(-1).set(RoundingMode.HALF_EVEN).build();

    @Override
    protected Money create(Number number, CurrencyUnit currency, MonetaryContext monetaryContext) {
        return Money.of(number, currency, MonetaryContext.from(monetaryContext, Money.class));
    }

    @Override
    public NumberValue getMaxNumber() {
        return null;
    }

    @Override
    public NumberValue getMinNumber() {
        return null;
    }

    @Override
    public Class<Money> getAmountType() {
        return Money.class;
    }

    @Override
    protected MonetaryContext loadDefaultMonetaryContext() {
        return DEFAULT_CONTEXT;
    }

    @Override
    protected MonetaryContext loadMaxMonetaryContext() {
        return MAX_CONTEXT;
    }

}

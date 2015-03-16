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
package org.javamoney.moneta;

import org.javamoney.moneta.spi.base.BaseMonetaryAmountFormat;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Objects;

import org.javamoney.bp.CurrencyUnit;
import org.javamoney.bp.MonetaryAmount;
import org.javamoney.bp.MonetaryCurrencies;
import org.javamoney.bp.format.AmountFormatContext;
import org.javamoney.bp.format.MonetaryParseException;

/**
 * class to format and parse a text string such as 'EUR 25.25' or vice versa.
 *
 * @author otaviojava
 */
class ToStringMonetaryAmountFormat extends BaseMonetaryAmountFormat {

    private ToStringMonetaryAmountFormatStyle style;

    private ToStringMonetaryAmountFormat(ToStringMonetaryAmountFormatStyle style) {
        this.style = Objects.requireNonNull(style);
    }

    public static ToStringMonetaryAmountFormat of(
            ToStringMonetaryAmountFormatStyle style) {
        return new ToStringMonetaryAmountFormat(style);
    }

    @Override
    public String queryFrom(MonetaryAmount amount) {
        if (amount==null) {
            return null;
        }
        return amount.toString();
    }

    @Override
    public AmountFormatContext getContext() {
        throw new UnsupportedOperationException(
                "ToStringMonetaryAmountFormat does not support getContext()");
    }

    @Override
    public void print(Appendable appendable, MonetaryAmount amount)
            throws IOException {
        appendable.append(amount == null ? "null" : amount.toString());

    }

    @Override
    public MonetaryAmount parse(CharSequence text)
            throws MonetaryParseException {
        ParserMonetaryAmount amount = parserMonetaryAmount(text);
        return style.to(amount);
    }

    private ParserMonetaryAmount parserMonetaryAmount(CharSequence text) {
        String[] array = Objects.requireNonNull(text).toString().split(" ");
        CurrencyUnit currencyUnit = MonetaryCurrencies.getCurrency(array[0]);
        BigDecimal number = new BigDecimal(array[1]);
        return new ParserMonetaryAmount(currencyUnit, number);
    }

    private static class ParserMonetaryAmount {
        public ParserMonetaryAmount(CurrencyUnit currencyUnit, BigDecimal number) {
            this.currencyUnit = currencyUnit;
            this.number = number;
        }

        private CurrencyUnit currencyUnit;
        private BigDecimal number;
    }

    /**
     * indicates with implementation will used to format or parser in
     * ToStringMonetaryAmountFormat
     */
    enum ToStringMonetaryAmountFormatStyle {
        MONEY {
            @Override
            MonetaryAmount to(ParserMonetaryAmount amount) {
                return Money.of(amount.number, amount.currencyUnit);
            }
        },
        FAST_MONEY {
            @Override
            MonetaryAmount to(ParserMonetaryAmount amount) {
                return FastMoney.of(amount.number, amount.currencyUnit);
            }
        },
        ROUNDED_MONEY {
            @Override
            MonetaryAmount to(ParserMonetaryAmount amount) {
                return RoundedMoney.of(amount.number, amount.currencyUnit);
            }
        };

        abstract MonetaryAmount to(ParserMonetaryAmount amount);
    }

}

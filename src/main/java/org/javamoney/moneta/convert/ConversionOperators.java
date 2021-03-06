/*
 * Copyright (c) 2012, 2015, Anatole Tresch, Werner Keil and others by the @author tag.
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
package org.javamoney.moneta.convert;

import java.util.Objects;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.MonetaryOperator;

import org.javamoney.moneta.convert.ExchangeCurrencyOperator;

/**
 * This singleton class provides access to the predefined monetary functions.
 *
 * The class is thread-safe, which is also true for all functions returned by
 * this class.
 * <pre>
 * {@code
 * 	MonetaryAmount money = Money.parse("EUR 2.35");
 *  MonetaryAmount result = operator.apply(money);
 * }
 * </pre>
 * Or using:
 * <pre>
 * {@code
 * 	MonetaryAmount money = Money.parse("EUR 2.35");
 *  MonetaryAmount result = money.with(operator);
 * }
 * </pre>
 * @see MonetaryAmount#with(MonetaryOperator)
 * @see MonetaryOperator
 * @see MonetaryOperator#apply(MonetaryAmount)
 * @author Werner Keil
 * @since 1.0.1
 */
public final class ConversionOperators {
 
    private ConversionOperators() {
    }

	/**
	 * Do exchange of currency, in other words, create the monetary amount with the
	 * same value but with currency different.
	 *
	 * For example, 'EUR 2.35', using the currency 'USD' as exchange parameter, will return 'USD 2.35',
	 * and 'BHD -1.345', using the currency 'USD' as exchange parameter, will return 'BHD -1.345'.
	 *
	 *<pre>
	 *{@code
	 *Currency real = Monetary.getCurrency("BRL");
	 *MonetaryAmount money = Money.parse("EUR 2.355");
	 *MonetaryAmount result = ConversionOperators.exchangeCurrency(real).apply(money);//BRL 2.355
	 *}
	 *</pre>
	 * @param currencyUnit the currency
	 * @return the major part as {@link MonetaryOperator}
	 */
	public static MonetaryOperator exchange(CurrencyUnit currencyUnit){
		return new ExchangeCurrencyOperator(Objects.requireNonNull(currencyUnit));
	}
}

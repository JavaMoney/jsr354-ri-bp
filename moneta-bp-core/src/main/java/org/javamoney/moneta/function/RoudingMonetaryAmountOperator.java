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
package org.javamoney.moneta.function;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.MonetaryOperator;

class RoudingMonetaryAmountOperator implements MonetaryOperator {

	static final RoundingMode DEFAULT_ROUDING_MONETARY_AMOUNT = RoundingMode.HALF_EVEN;

	private final RoundingMode roundingMode;

	private final Integer scaleOptional;

	public RoudingMonetaryAmountOperator() {
		this.roundingMode = DEFAULT_ROUDING_MONETARY_AMOUNT;
		scaleOptional = null;
	}

	public RoudingMonetaryAmountOperator(RoundingMode roundingMode) {
		this.roundingMode = roundingMode;
		scaleOptional = null;
	}

	public RoudingMonetaryAmountOperator(RoundingMode roundingMode, int scale) {
		this.roundingMode = roundingMode;
		this.scaleOptional = scale;

	}

	@Override
	public MonetaryAmount apply(MonetaryAmount amount) {
		Objects.requireNonNull(amount, "Amount required.");
		CurrencyUnit currency = amount.getCurrency();
		int scale = scaleOptional == null ? currency.getDefaultFractionDigits(): scaleOptional;
		BigDecimal value = amount.getNumber().numberValue(BigDecimal.class).setScale(scale, roundingMode);
		return amount.getFactory().setNumber(value).create();
	}

}

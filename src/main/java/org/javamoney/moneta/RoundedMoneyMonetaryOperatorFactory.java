/*
  Copyright (c) 2012, 2015, Credit Suisse (Anatole Tresch), Werner Keil and others by the @author tag.

  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy of
  the License at

  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
  License for the specific language governing permissions and limitations under
  the License.
 */
package org.javamoney.moneta;

import java.math.MathContext;
import java.math.RoundingMode;

import javax.money.Monetary;
import javax.money.MonetaryContext;
import javax.money.MonetaryOperator;
import javax.money.RoundingQueryBuilder;

/**
 * Factory of default {@link RoundedMoney}
 * @see RoundedMoney
 * @see RoundedMoney#divide(double)
 * @author Otavio Santana
 */
enum RoundedMoneyMonetaryOperatorFactory {

INSTANCE;

	private static final int SCALE_DEFAULT = 2;
	public static final String SCALE_KEY = "scale";

	MonetaryOperator getDefaultMonetaryOperator(MonetaryOperator rounding,
			MonetaryContext context) {

		if (rounding!=null) {
			return rounding;
		}
		if (context!=null) {
			return createUsingMonetaryContext(context);
		} else {
			return Monetary.getDefaultRounding();
		}
	}

	private MonetaryOperator createUsingMonetaryContext(
			MonetaryContext context) {

		MathContext mathContext = context.get(MathContext.class);
		int scale = SCALE_DEFAULT;
		if(context.getInt(SCALE_KEY)!=null){
			scale = context.getInt(SCALE_KEY);
		}
		if (mathContext!=null) {
			return Monetary.getRounding(RoundingQueryBuilder.of().set(mathContext)
					.setScale(scale).build());
		}

		RoundingMode roundingMode = context.get(RoundingMode.class);
		if (roundingMode!=null) {
			return Monetary.getRounding(RoundingQueryBuilder.of().set(roundingMode)
					.setScale(scale).build());
		} else {
			return Monetary.getDefaultRounding();
		}
	}
}

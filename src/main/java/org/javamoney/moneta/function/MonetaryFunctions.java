/*
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

import java.util.Comparator;
import java.util.Objects;

import javax.money.MonetaryAmount;
import javax.money.MonetaryException;
import javax.money.convert.CurrencyConversion;
import javax.money.convert.ExchangeRate;
import javax.money.convert.ExchangeRateProvider;

import org.javamoney.moneta.spi.MoneyUtils;

/**
 * This singleton class provides access to the predefined monetary functions.
 *
 * @author otaviojava
 * @author anatole
 */
public final class MonetaryFunctions {

	  private static final Comparator<MonetaryAmount> NUMBER_COMPARATOR = new Comparator<MonetaryAmount>() {
	        @Override
	        public int compare(MonetaryAmount o1, MonetaryAmount o2) {
	            return o1.getNumber().compareTo(o2.getNumber());
	        }
	    };

	    private static final Comparator<MonetaryAmount> CURRENCY_COMPARATOR = new Comparator<MonetaryAmount>() {
	        @Override
	        public int compare(MonetaryAmount o1, MonetaryAmount o2) {
	            return o1.getCurrency().compareTo(o2.getCurrency());
	        }
	    };

	    /**
	     * Get a comparator for sorting CurrencyUnits ascending.
	     *
	     * @return the Comparator to sort by CurrencyUnit in ascending order, not null.
	     */
	    public static Comparator<MonetaryAmount> sortCurrencyUnit(){
	        return CURRENCY_COMPARATOR;
	    }

		/**
		 * comparator to sort the {@link MonetaryAmount} considering the
		 * {@link ExchangeRate}
		 * @param provider the rate provider to be used.
		 * @return the sort of {@link MonetaryAmount} using {@link ExchangeRate}
		 */
		public static Comparator<? super MonetaryAmount> sortValuable(
	            final ExchangeRateProvider provider) {
	        return new Comparator<MonetaryAmount>() {
	            @Override
	            public int compare(MonetaryAmount m1, MonetaryAmount m2) {
	                CurrencyConversion conversor = provider.getCurrencyConversion(m1
	                        .getCurrency());
	                return m1.compareTo(conversor.apply(m2));
	            }
	        };
		}

		/**
		 * comparator to sort the {@link MonetaryAmount} considering the
		 * {@link ExchangeRate}
		 * @param provider the rate provider to be used.
		 * @return the sort of {@link MonetaryAmount} using {@link ExchangeRate}
		 * @deprecated call #sortValuable instead of.
		 */
		@Deprecated
		public static Comparator<? super MonetaryAmount> sortValiable(final ExchangeRateProvider provider) {
			return sortValuable(provider);
		}


		/**
		 * Descending order of
		 * {@link MonetaryFunctions#sortValuable(ExchangeRateProvider)}
		 * @param provider the rate provider to be used.
		 * @return the Descending order of
		 *         {@link MonetaryFunctions#sortValuable(ExchangeRateProvider)}
		 */
		public static Comparator<? super MonetaryAmount> sortValuableDesc(
	            final ExchangeRateProvider provider) {
			return new Comparator<MonetaryAmount>() {
	            @Override
	            public int compare(MonetaryAmount o1, MonetaryAmount o2) {
	                return sortValuable(provider).compare(o1, o2) * -1;
	            }
	        };
		}

		/**
		 * Descending order of
		 * {@link MonetaryFunctions#sortValuable(ExchangeRateProvider)}
		 * @param provider the rate provider to be used.
		 * @return the Descending order of
		 *         {@link MonetaryFunctions#sortValuable(ExchangeRateProvider)}
		 * @deprecated Use #sortValiableDesc instead of.
		 */
		@Deprecated
		public static Comparator<? super MonetaryAmount> sortValiableDesc(
				final ExchangeRateProvider provider) {
			return new Comparator<MonetaryAmount>() {
				@Override
				public int compare(MonetaryAmount o1, MonetaryAmount o2) {
					return sortValuable(provider).compare(o1, o2) * -1;
				}
			};
		}

	    /**
	     * Get a comparator for sorting CurrencyUnits descending.
	     * @return the Comparator to sort by CurrencyUnit in descending order, not null.
	     */
	    public static Comparator<MonetaryAmount> sortCurrencyUnitDesc(){
	        return new Comparator<MonetaryAmount>() {
	            @Override
	            public int compare(MonetaryAmount o1, MonetaryAmount o2) {
	                return sortCurrencyUnit().compare(o1, o2) * -1;
	            }
	        };
	    }

	    /**
	     * Get a comparator for sorting amount by number value ascending.
	     * @return the Comparator to sort by number in ascending way, not null.
	     */
	    public static Comparator<MonetaryAmount> sortNumber(){
	        return NUMBER_COMPARATOR;
	    }

	    /**
	     * Get a comparator for sorting amount by number value descending.
	     * @return the Comparator to sort by number in descending way, not null.
	     */
	    public static Comparator<MonetaryAmount> sortNumberDesc(){
	        return new Comparator<MonetaryAmount>() {
	            @Override
	            public int compare(MonetaryAmount o1, MonetaryAmount o2) {
	                return sortNumber().compare(o1, o2) * -1;
	            }
	        };
	    }

	    /**
	     * Adds two monetary together
	     * @param a the first operand
	     * @param b the second operand
	     * @return the sum of {@code a} and {@code b}
	     * @throws NullPointerException if a o b be null
	     * @throws MonetaryException    if a and b have different currency
	     */
	    public static MonetaryAmount sum(MonetaryAmount a, MonetaryAmount b){
	        MoneyUtils.checkAmountParameter(Objects.requireNonNull(a), Objects.requireNonNull(b.getCurrency()));
	        return a.add(b);
	    }

	    /**
	     * Returns the smaller of two {@code MonetaryAmount} values. If the arguments
	     * have the same value, the result is that same value.
	     * @param a an argument.
	     * @param b another argument.
	     * @return the smaller of {@code a} and {@code b}.
	     */
		static MonetaryAmount min(MonetaryAmount a, MonetaryAmount b) {
	        MoneyUtils.checkAmountParameter(Objects.requireNonNull(a), Objects.requireNonNull(b.getCurrency()));
	        return a.isLessThan(b) ? a : b;
	    }

	    /**
	     * Returns the greater of two {@code MonetaryAmount} values. If the
	     * arguments have the same value, the result is that same value.
	     * @param a an argument.
	     * @param b another argument.
	     * @return the larger of {@code a} and {@code b}.
	     */
		static MonetaryAmount max(MonetaryAmount a, MonetaryAmount b) {
	        MoneyUtils.checkAmountParameter(Objects.requireNonNull(a), Objects.requireNonNull(b.getCurrency()));
	        return a.isGreaterThan(b) ? a : b;
	    }



}
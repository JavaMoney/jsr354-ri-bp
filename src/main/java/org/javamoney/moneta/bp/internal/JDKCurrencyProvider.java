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

import org.javamoney.moneta.bp.spi.base.BaseCurrencyProviderSpi;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.javamoney.bp.api.CurrencyQuery;
import org.javamoney.bp.api.CurrencyUnit;

/**
 * Default implementation of a {@link CurrencyUnit} based on the using the JDK's
 * {@link Currency}.
 * 
 * @version 0.5.1
 * @author Anatole Tresch
 * @author Werner Keil
 */
public class JDKCurrencyProvider extends BaseCurrencyProviderSpi {

	/** Internal shared cache of {@link org.javamoney.bp.api.CurrencyUnit} instances. */
	private static final Map<String, CurrencyUnit> CACHED = new HashMap<>();

	public JDKCurrencyProvider() {
		for (Currency jdkCurrency : Currency.getAvailableCurrencies()) {
			CurrencyUnit cu = new JDKCurrencyAdapter(jdkCurrency);
			CACHED.put(cu.getCurrencyCode(), cu);
		}
	}

    @Override
    public String getProviderName(){
        return "default";
    }

    /**
     * Return a {@link CurrencyUnit} instances matching the given
     * {@link org.javamoney.bp.api.CurrencyContext}.
     *
     * @param currencyQuery the {@link org.javamoney.bp.api.CurrencyContext} containing the parameters determining the query. not null.
     * @return the corresponding {@link CurrencyUnit}, or null, if no such unit
     * is provided by this provider.
     */
    public Set<CurrencyUnit> getCurrencies(CurrencyQuery currencyQuery){
        Set<CurrencyUnit> result = new HashSet<>();
        if(!currencyQuery.getCurrencyCodes().isEmpty()) {
            for (String code : currencyQuery.getCurrencyCodes()) {
                CurrencyUnit cu = CACHED.get(code);
                if (cu != null) {
                    result.add(cu);
                }
            }
            return result;
        }
        if(!currencyQuery.getCountries().isEmpty()) {
            for (Locale country : currencyQuery.getCountries()) {
                CurrencyUnit cu = getCurrencyUnit(country);
                if (cu != null) {
                    result.add(cu);
                }
            }
            return result;
        }
        result.addAll(CACHED.values());
        return result;
    }

    private CurrencyUnit getCurrencyUnit(Locale locale) {
		Currency cur;
		try {
			cur = Currency.getInstance(locale);
			if (cur!=null) {
				return CACHED.get(cur.getCurrencyCode());
			}
		} catch (Exception e) {
			if (Logger.getLogger(getClass().getName()).isLoggable(Level.FINEST)) {
				Logger.getLogger(getClass().getName()).finest(
						"No currency for locale found: " + locale);
			}
		}
		return null;
	}

}

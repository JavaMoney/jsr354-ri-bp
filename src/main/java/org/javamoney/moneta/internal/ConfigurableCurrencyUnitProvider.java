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
package org.javamoney.moneta.internal;

import org.javamoney.moneta.spi.base.BaseCurrencyProviderSpi;

import javax.money.CurrencyQuery;
import javax.money.CurrencyUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class provides a programmatic singleton for globally registering new {@link java.util.Currency}  into the
 * {@link javax.money.Monetary} singleton either by currency code, locale, or both.
 */
public class ConfigurableCurrencyUnitProvider extends BaseCurrencyProviderSpi {
    /**
     * The currency units, identified by currency code.
     */
    private static final Map<String, CurrencyUnit> currencyUnits = new ConcurrentHashMap<>();
    /**
     * The currency units, identified by numeric code.
     */
    private static final Map<Integer, CurrencyUnit> currencyUnitsByNumericCode = new ConcurrentHashMap<>();
    /**
     * The currency units identified by Locale.
     */
    private static final Map<Locale, CurrencyUnit> currencyUnitsByLocale = new ConcurrentHashMap<>();


    /**
     * Return a {@link CurrencyUnit} instances matching the given
     * {@link javax.money.CurrencyContext}.
     *
     * @param currencyQuery the {@link javax.money.CurrencyQuery} containing the parameters determining the query. not null.
     * @return the corresponding {@link CurrencyUnit}, or null, if no such unit
     * is provided by this provider.
     */
    public Set<CurrencyUnit> getCurrencies(CurrencyQuery currencyQuery) {
        Set<CurrencyUnit> result = new HashSet<>(currencyUnits.size());
        if (!currencyQuery.getCurrencyCodes().isEmpty()) {
            for (String code : currencyQuery.getCurrencyCodes()) {
                CurrencyUnit cu = currencyUnits.get(code);
                if (cu != null) {
                    result.add(cu);
                }
            }
            return result;
        }
        if (!currencyQuery.getCountries().isEmpty()) {
            for (Locale locale : currencyQuery.getCountries()) {
                CurrencyUnit cu = currencyUnitsByLocale.get(locale);
                if (cu != null) {
                    result.add(cu);
                }
            }
            return result;
        }
        if (!currencyQuery.getNumericCodes().isEmpty()) {
            for (Integer numericCode : currencyQuery.getNumericCodes()) {
                CurrencyUnit cu = currencyUnitsByNumericCode.get(numericCode);
                if (cu != null) {
                    result.add(cu);
                }
            }
            return result;
        }
        result.addAll(currencyUnits.values());
        return result;
    }

    /**
     * Registers a new currency unit under its currency code and potentially numeric code.
     *
     * @param currencyUnit the new currency to be registered, not null.
     * @return any unit instance registered previously by this instance, or null.
     */
    public static CurrencyUnit registerCurrencyUnit(CurrencyUnit currencyUnit) {
        Objects.requireNonNull(currencyUnit);
        CurrencyUnit registered = ConfigurableCurrencyUnitProvider.currencyUnits.put(currencyUnit.getCurrencyCode(), currencyUnit);
        int numericCode = currencyUnit.getNumericCode();
        if (numericCode != -1) {
            ConfigurableCurrencyUnitProvider.currencyUnitsByNumericCode.put(numericCode, currencyUnit);
        }
        return registered;
    }

    /**
     * Registers a new currency unit under the given Locale.
     *
     * @param currencyUnit the new currency to be registered, not null.
     * @param locale       the Locale, not null.
     * @return any unit instance registered previously by this instance, or null.
     */
    public static CurrencyUnit registerCurrencyUnit(CurrencyUnit currencyUnit, Locale locale) {
        Objects.requireNonNull(locale);
        Objects.requireNonNull(currencyUnit);
        return ConfigurableCurrencyUnitProvider.currencyUnitsByLocale.put(locale, currencyUnit);
    }

    /**
     * Removes a CurrencyUnit.
     *
     * @param currencyCode the currency code, not null.
     * @return any unit instance removed, or null.
     */
    public static CurrencyUnit removeCurrencyUnit(String currencyCode) {
        Objects.requireNonNull(currencyCode);
        CurrencyUnit removed = ConfigurableCurrencyUnitProvider.currencyUnits.remove(currencyCode);
        if (removed != null) {
            int numericCode = removed.getNumericCode();
            if (numericCode != -1) {
                ConfigurableCurrencyUnitProvider.currencyUnitsByNumericCode.remove(numericCode);
            }
        }
        return removed;
    }

    /**
     * Removes a CurrencyUnit.
     *
     * @param locale the Locale, not null.
     * @return any unit instance removed, or null.
     */
    public static CurrencyUnit removeCurrencyUnit(Locale locale) {
        Objects.requireNonNull(locale);
        return ConfigurableCurrencyUnitProvider.currencyUnitsByLocale.remove(locale);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ConfigurableCurrencyUnitProvider [currencyUnits=" + currencyUnits
                + ", currencyUnitsByNumericCode=" + currencyUnitsByNumericCode
                + ", currencyUnitsByLocale=" + currencyUnitsByLocale + ']';
    }

}

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
package org.javamoney.moneta.bp.function;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.javamoney.bp.CurrencyUnit;

/**
 * This map is decorator of HashMap that returns an empty Summary when there
 * isn't currency in get's method
 *
 * @author otaviojava
 */
class MonetarySummaryMap implements
        Map<CurrencyUnit, MonetarySummaryStatistics> {

    private Map<CurrencyUnit, MonetarySummaryStatistics> map = new HashMap<>();

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public MonetarySummaryStatistics get(Object key) {
        if (CurrencyUnit.class.isInstance(key)) {
            CurrencyUnit unit = CurrencyUnit.class.cast(key);
            MonetarySummaryStatistics stats = map.get(key);
            if(stats==null){
                stats = new DefaultMonetarySummaryStatistics(unit);
                map.put(unit, stats);
            }
            return stats;
        }
        return map.get(key);
    }

    @Override
    public MonetarySummaryStatistics put(CurrencyUnit key,
                                         MonetarySummaryStatistics value) {
        return map.put(key, value);
    }

    @Override
    public MonetarySummaryStatistics remove(Object key) {
        return map.remove(key);
    }

    @Override
    public void putAll(
            Map<? extends CurrencyUnit, ? extends MonetarySummaryStatistics> m) {
        map.putAll(m);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Set<CurrencyUnit> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<MonetarySummaryStatistics> values() {
        return map.values();
    }

    @Override
    public Set<java.util.Map.Entry<CurrencyUnit, MonetarySummaryStatistics>> entrySet() {
        return map.entrySet();
    }

    @Override
    public boolean equals(Object obj) {
        if (MonetarySummaryMap.class.isInstance(obj)) {
            MonetarySummaryMap other = MonetarySummaryMap.class.cast(obj);
            return map.equals(other.map);
        }
        return false;
    }

    public MonetarySummaryStatistics putIfAbsent(CurrencyUnit key,
                                                 MonetarySummaryStatistics value) {
        MonetarySummaryStatistics v = map.get(key);
        if (v==null) {
            v = put(key, value);
        }
        return v;
    }

    @Override
    public int hashCode() {
        return map.hashCode();
    }

    @Override
    public String toString() {
        return "MonetarySummaryMap: " + map.toString();
    }
}

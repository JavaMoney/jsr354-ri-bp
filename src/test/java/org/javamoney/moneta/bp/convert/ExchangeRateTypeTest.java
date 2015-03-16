/**
 * Copyright (c) 2012, 2015, Credit Suisse (Anatole Tresch), Werner Keil and others by the @author tag.
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
package org.javamoney.moneta.bp.convert;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import org.javamoney.bp.api.convert.ExchangeRateProvider;
import org.javamoney.bp.api.convert.MonetaryConversions;

import org.javamoney.moneta.bp.ExchangeRateType;
import org.javamoney.moneta.bp.internal.convert.ECBCurrentRateProvider;
import org.javamoney.moneta.bp.internal.convert.ECBHistoric90RateProvider;
import org.javamoney.moneta.bp.internal.convert.ECBHistoricRateProvider;
import org.javamoney.moneta.bp.internal.convert.IMFRateProvider;
import org.javamoney.moneta.bp.internal.convert.IdentityRateProvider;
import org.testng.annotations.Test;

public class ExchangeRateTypeTest {

    @Test
    public void shouldReturnsECBCurrentRateProvider() {
        ExchangeRateProvider prov = MonetaryConversions
                .getExchangeRateProvider(ExchangeRateType.ECB);
        assertNotNull(prov);
        assertEquals(ECBCurrentRateProvider.class, prov.getClass());
    }

    @Test
    public void shouldReturnsECBHistoricRateProvider() {
        ExchangeRateProvider prov = MonetaryConversions
                .getExchangeRateProvider(ExchangeRateType.ECB_HIST);
        assertNotNull(prov);
        assertEquals(ECBHistoricRateProvider.class, prov.getClass());
    }

    @Test
    public void shouldReturnsECBHistoric90RateProvider() {
        ExchangeRateProvider prov = MonetaryConversions
                .getExchangeRateProvider(ExchangeRateType.ECB_HIST90);
        assertNotNull(prov);
        assertEquals(ECBHistoric90RateProvider.class, prov.getClass());
    }

    @Test
    public void shouldReturnsIMFRateProvider() {
        ExchangeRateProvider prov = MonetaryConversions
                .getExchangeRateProvider(ExchangeRateType.IMF);
        assertNotNull(prov);
        assertEquals(IMFRateProvider.class, prov.getClass());
    }

    @Test
    public void shouldReturnsIdentityRateProvider() {
        ExchangeRateProvider prov = MonetaryConversions
                .getExchangeRateProvider(ExchangeRateType.IDENTITY);
        assertNotNull(prov);
        assertEquals(IdentityRateProvider.class, prov.getClass());
    }

}

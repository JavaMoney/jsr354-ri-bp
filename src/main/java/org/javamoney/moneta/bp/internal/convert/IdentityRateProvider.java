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
package org.javamoney.moneta.bp.internal.convert;

import java.math.BigDecimal;
import java.net.MalformedURLException;

import org.javamoney.bp.convert.ConversionQuery;
import org.javamoney.bp.convert.ExchangeRate;
import org.javamoney.bp.convert.ProviderContext;
import org.javamoney.bp.convert.ProviderContextBuilder;
import org.javamoney.bp.convert.RateType;
import org.javamoney.moneta.bp.ExchangeRateBuilder;
import org.javamoney.moneta.bp.spi.AbstractRateProvider;
import org.javamoney.moneta.bp.spi.DefaultNumberValue;

/**
 * This class implements an {@link org.javamoney.bp.convert.ExchangeRateProvider} that provides exchange rate with factor
 * one for identical base/term currencies.
 *
 * @author Anatole Tresch
 * @author Werner Keil
 */
public class IdentityRateProvider extends AbstractRateProvider {

    /**
     * The {@link org.javamoney.bp.convert.ConversionContext} of this provider.
     */
    private static final ProviderContext CONTEXT =
            ProviderContextBuilder.of("IDENT", RateType.OTHER).set("providerDescription", "Identitiy Provider").build();

    /**
     * Constructor, also loads initial data.
     *
     * @throws java.net.MalformedURLException
     */
    public IdentityRateProvider() throws MalformedURLException {
        super(CONTEXT);
    }

    /**
     * Check if this provider can provide a rate, which is only the case if base and term are equal.
     *
     * @param conversionQuery the required {@link ConversionQuery}, not {@code null}
     * @return true, if the contained base and term currencies are known to this provider.
     */
    public boolean isAvailable(ConversionQuery conversionQuery) {
        return conversionQuery.getBaseCurrency().getCurrencyCode()
                .equals(conversionQuery.getCurrency().getCurrencyCode());
    }

    public ExchangeRate getExchangeRate(ConversionQuery query) {
        if (query.getBaseCurrency().getCurrencyCode().equals(query.getCurrency().getCurrencyCode())) {
            ExchangeRateBuilder builder = new ExchangeRateBuilder(getContext().getProviderName(), RateType.OTHER)
                    .setBase(query.getBaseCurrency());
            builder.setTerm(query.getCurrency());
            builder.setFactor(DefaultNumberValue.of(BigDecimal.ONE));
            return builder.build();
        }
        return null;
    }

    /*
     * (non-Javadoc)
	 *
	 * @see
	 * org.javamoney.bp.convert.ExchangeRateProvider#getReversed(org.javamoney.bp.convert
	 * .ExchangeRate)
	 */
    @Override
    public ExchangeRate getReversed(ExchangeRate rate) {
        if (rate.getContext().getProviderName().equals(CONTEXT.getProviderName())) {
            return new ExchangeRateBuilder(rate.getContext()).setTerm(rate.getBaseCurrency())
                    .setBase(rate.getCurrency()).setFactor(new DefaultNumberValue(BigDecimal.ONE)).build();
        }
        return null;
    }

}
package org.javamoney.moneta;

import javax.money.convert.ExchangeRateProviderSupplier;

/**
 * <p>
 * This enum contains all implementation of moneta. Using this enum will easier
 * to choose an available implementation.
 * </p>
 * <code>ExchangeRateProvider provider = MonetaryConversions.getExchangeRateProvider(ExchangeRateType.ECB);<code>
 *
 * @author otaviojava
 */
public enum ExchangeRateType implements ExchangeRateProviderSupplier {
    /**
     * Exchange rate to the European Central Bank. Uses the
     * {@link org.javamoney.moneta.internal.convert.ECBCurrentRateProvider} implementation.
     */
    ECB("ECB", "Exchange rate to the European Central Bank."),
    /**
     * Exchange rate to the International Monetary Fond. Uses the
     * {@link org.javamoney.moneta.internal.convert.IMFRateProvider} implementation.
     */
    IMF("IMF", "Exchange rate to the International Monetary Fond."),
    /**
     * Exchange rate to European Central Bank (last 90 days). Uses the
     * {@link org.javamoney.moneta.internal.convert.ECBHistoric90RateProvider} implementation.
     */
    ECB_HIST90("ECB-HIST90",
            "Exchange rate to European Central Bank (last 90 days)."),
    /**
     * Uses the {@link org.javamoney.moneta.internal.convert.ECBHistoricRateProvider} implementation.
     */
    ECB_HIST(
            "ECB-HIST",
            "Exchange rate to the European Central Bank that loads all data up to 1999 into its historic data cache."),
    /**
     * Uses the {@link org.javamoney.moneta.internal.convert.IdentityRateProvider} implementation.
     */
    IDENTITY(
            "IDENT",
            "Exchange rate rate with factor one for identical base/term currencies");

    private final String type;

    private final String description;

    ExchangeRateType(String type, String description) {
        this.type = type;
        this.description = description;
    }

    @Override
    public String get() {
        return type;
    }

    @Override
    public String getDescription() {
        return description;
    }

}

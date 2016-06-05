/*
 * CREDIT SUISSE IS WILLING TO LICENSE THIS SPECIFICATION TO YOU ONLY UPON THE CONDITION THAT YOU
 * ACCEPT ALL OF THE TERMS CONTAINED IN THIS AGREEMENT. PLEASE READ THE TERMS AND CONDITIONS OF THIS
 * AGREEMENT CAREFULLY. BY DOWNLOADING THIS SPECIFICATION, YOU ACCEPT THE TERMS AND CONDITIONS OF
 * THE AGREEMENT. IF YOU ARE NOT WILLING TO BE BOUND BY IT, SELECT THE "DECLINE" BUTTON AT THE
 * BOTTOM OF THIS PAGE. Specification: JSR-354 Money and Currency API ("Specification") Copyright
 * (c) 2012-2015, Credit Suisse All rights reserved.
 */
package org.javamoney.moneta.function;

import javax.money.CurrencyUnit;
import javax.money.Monetary;

/**
 * Test utility class for testing streams.
 */
public final class StreamFactory {

	static final CurrencyUnit EURO = Monetary.getCurrency("EUR");
	static final CurrencyUnit DOLLAR = Monetary.getCurrency("USD");
	static final CurrencyUnit BRAZILIAN_REAL = Monetary
			.getCurrency("BRL");

}

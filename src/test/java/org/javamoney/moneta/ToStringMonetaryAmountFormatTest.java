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
package org.javamoney.moneta;

import static org.testng.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.Locale;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.money.format.AmountFormatQueryBuilder;
import javax.money.format.MonetaryAmountFormat;
import javax.money.format.MonetaryFormats;

import org.javamoney.moneta.format.CurrencyStyle;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class ToStringMonetaryAmountFormatTest {
	private static final CurrencyUnit BRAZILIAN_REAL = Monetary
			.getCurrency("BRL");

	private MonetaryAmount money;
	private MonetaryAmount fastMoney;
	private MonetaryAmount roundedMoney;



	@BeforeTest
	public void init() {
		money = Money.of(BigDecimal.TEN, BRAZILIAN_REAL);
		fastMoney = FastMoney.of(BigDecimal.TEN, BRAZILIAN_REAL);
		roundedMoney = RoundedMoney.of(BigDecimal.TEN, BRAZILIAN_REAL);
	}

	@Test(expectedExceptions = NullPointerException.class)
	public void shouldRunNPE() {
		ToStringMonetaryAmountFormat format = ToStringMonetaryAmountFormat
				.of(ToStringMonetaryAmountFormat.ToStringMonetaryAmountFormatStyle.FAST_MONEY);
		format.parse(null);
	}

	@Test(expectedExceptions = javax.money.format.MonetaryParseException.class)
	public void shouldRunNumberFormatException() {
		ToStringMonetaryAmountFormat format = ToStringMonetaryAmountFormat
				.of(ToStringMonetaryAmountFormat.ToStringMonetaryAmountFormatStyle.FAST_MONEY);
		format.parse("BRL 23AD");
	}

	@Test(expectedExceptions = javax.money.format.MonetaryParseException.class)
	public void shouldRunUnknownCurrencyException() {
		ToStringMonetaryAmountFormat format = ToStringMonetaryAmountFormat
				.of(ToStringMonetaryAmountFormat.ToStringMonetaryAmountFormatStyle.FAST_MONEY);
		format.parse("AXD 23");
	}

	@Test
	public void parserMoneyTest() {
		executeTest(money, fastMoney, roundedMoney,
				ToStringMonetaryAmountFormat.ToStringMonetaryAmountFormatStyle.MONEY);
	}

	@Test
	public void parserFastMoneyTest() {
		executeTest(fastMoney, money, roundedMoney,
				ToStringMonetaryAmountFormat.ToStringMonetaryAmountFormatStyle.FAST_MONEY);
	}

	@Test
	public void parserRoundedMoneyTest() {
		executeTest(roundedMoney, fastMoney, money,
				ToStringMonetaryAmountFormat.ToStringMonetaryAmountFormatStyle.ROUNDED_MONEY);
	}

	/**
	 * Test related to {@see https://java.net/jira/browse/JAVAMONEY-151}.
	 */
	@Test
	public void testBulgarianLev(){
		MonetaryAmount money = Money.of(1123000.50, "BGN");
		Locale locale = new Locale("", "BG");
		MonetaryAmountFormat format = MonetaryFormats.getAmountFormat(
				AmountFormatQueryBuilder.of(locale).set(CurrencyStyle.SYMBOL)
						.build());
		assertEquals(format.format(money), "1 123 000,50 лв");

		format = MonetaryFormats.getAmountFormat(
				AmountFormatQueryBuilder.of(locale).set(CurrencyStyle.CODE)
						.build());
		assertEquals(format.format(money), "1 123 000,50 BGN");
	}

	private void executeTest(MonetaryAmount expectedMoney, MonetaryAmount a,
			MonetaryAmount b, ToStringMonetaryAmountFormat.ToStringMonetaryAmountFormatStyle type) {

		MonetaryAmount parserAResult = parser(a, type);
		MonetaryAmount parserBResult = parser(b, type);

		assertEquals(parserAResult, expectedMoney);
		assertEquals(parserBResult, expectedMoney);
		assertEquals(parserBResult, parserAResult);
	}

	private MonetaryAmount parser(MonetaryAmount a,
			ToStringMonetaryAmountFormat.ToStringMonetaryAmountFormatStyle style) {
		return ToStringMonetaryAmountFormat.of(style).parse(a.toString());
	}
}

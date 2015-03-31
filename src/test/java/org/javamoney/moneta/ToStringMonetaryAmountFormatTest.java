package org.javamoney.moneta;

import static org.testng.Assert.assertEquals;

import java.math.BigDecimal;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.money.UnknownCurrencyException;

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

	@Test(expectedExceptions = NumberFormatException.class)
	public void shouldRunNumberFormatException() {
		ToStringMonetaryAmountFormat format = ToStringMonetaryAmountFormat
				.of(ToStringMonetaryAmountFormat.ToStringMonetaryAmountFormatStyle.FAST_MONEY);
		format.parse("BRL 23AD");
	}

	@Test(expectedExceptions = UnknownCurrencyException.class)
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

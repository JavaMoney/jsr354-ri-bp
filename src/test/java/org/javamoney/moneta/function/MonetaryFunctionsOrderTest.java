package org.javamoney.moneta.function;

import javax.money.convert.ExchangeRateProvider;

import org.testng.annotations.Test;

public class MonetaryFunctionsOrderTest {

    private ExchangeRateProvider provider;

    @Test
    public void init() {
        provider = new ExchangeRateProviderMock();
    }

//    @Test
//    public void sortCurrencyUnitTest() {
//        MonetaryAmount money = currencies().sorted(sortCurrencyUnit())
//                .findFirst().get();
//        Assert.assertEquals(BRAZILIAN_REAL, money.getCurrency());
//    }
//
//    @Test
//    public void sortCurrencyUnitDescTest() {
//        MonetaryAmount money = currencies().sorted(sortCurrencyUnitDesc())
//                .findFirst().get();
//        Assert.assertEquals(DOLLAR, money.getCurrency());
//    }
//
//    @Test
//    public void sortorderNumberTest() {
//        MonetaryAmount money = currencies().sorted(sortNumber())
//                .findFirst().get();
//        Assert.assertEquals(BigDecimal.ZERO, money.getNumber().numberValue(BigDecimal.class));
//    }
//
//    @Test
//    public void sortorderNumberDescTest() {
//        MonetaryAmount money = currencies().sorted(sortNumberDesc())
//                .findFirst().get();
//        Assert.assertEquals(BigDecimal.TEN, money.getNumber().numberValue(BigDecimal.class));
//    }
//
//    @Test
//    public void sortCurrencyUnitAndNumberTest() {
//        MonetaryAmount money = currencies().sorted(sortCurrencyUnit().thenComparing(sortNumber()))
//                .findFirst().get();
//
//        Assert.assertEquals(BRAZILIAN_REAL, money.getCurrency());
//        Assert.assertEquals(BigDecimal.ZERO, money.getNumber().numberValue(BigDecimal.class));
//    }
//
//    @Test
//    public void shouldExecuteValiableOrder() {
//
//        Stream<MonetaryAmount> stream = Stream.of(Money.of(7, EURO),
//                Money.of(9, BRAZILIAN_REAL), Money.of(8, DOLLAR));
//        List<MonetaryAmount> list = stream.sorted(
//                MonetaryFunctions.sortValiable(provider)).collect(
//                Collectors.toList());
//
//        Assert.assertEquals(Money.of(9, BRAZILIAN_REAL), list.get(0));
//        Assert.assertEquals(Money.of(8, DOLLAR), list.get(1));
//        Assert.assertEquals(Money.of(7, EURO), list.get(2));
//    }
//
//    @Test
//    public void shouldExecuteValiableOrderDesc() {
//
//        Stream<MonetaryAmount> stream = Stream.of(Money.of(7, EURO),
//                Money.of(9, BRAZILIAN_REAL), Money.of(8, DOLLAR));
//        List<MonetaryAmount> list = stream.sorted(
//                MonetaryFunctions.sortValiableDesc(provider)).collect(
//                Collectors.toList());
//
//        Assert.assertEquals(Money.of(7, EURO), list.get(0));
//        Assert.assertEquals(Money.of(8, DOLLAR), list.get(1));
//        Assert.assertEquals(Money.of(9, BRAZILIAN_REAL), list.get(2));
//
//    }
}

package org.javamoney.moneta.convert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import javax.money.CurrencyUnit;
import javax.money.convert.ConversionQuery;
import javax.money.convert.ConversionQueryBuilder;

import org.javamoney.moneta.internal.convert.LocalDate;

public class HistoricConversionQueryBuilder {
	private final ConversionQueryBuilder conversionQueryBuilder;

	private HistoricConversionQueryBuilder(ConversionQueryBuilder conversionQuery) {
		this.conversionQueryBuilder = conversionQuery;
	}

	/**
	 *Create a {@link HistoricConversionQueryBuilder} from currency
	 * @param currencyUnit to be used in term currency.
	 * @return a HistoricConversionQuery from currency
	 * @throws NullPointerException when currency is null
	 */
	public static HistoricConversionQueryBuilder of(CurrencyUnit currencyUnit) {
		Objects.requireNonNull(currencyUnit, "Currency is required");
		return new HistoricConversionQueryBuilder(ConversionQueryBuilder.of()
                .setTermCurrency(currencyUnit));
	}

	/**
	 * Set a specify day on {@link HistoricConversionQueryBuilder}
	 * @param localDate
	 * @return this
	 * @throws NullPointerException when {@link LocalDate} is null
	 */
	public HistoricConversionQueryWithDayBuilder withDay(LocalDate localDate) {
		Objects.requireNonNull(localDate);
		conversionQueryBuilder.set(LocalDate.class, localDate);

		return new HistoricConversionQueryWithDayBuilder(conversionQueryBuilder);
	}

	/**
	 *Set days on {@link HistoricConversionQueryBuilder} to be used on ExchangeRateProvider,
	 *these parameters will sort to most recent to be more priority than other.
	 * @param localDates
	 * @return this
	 * @throws IllegalArgumentException when is empty or the parameter has an null value
	 */
	@SafeVarargs
	public final HistoricConversionQueryWithDayBuilder withDays(LocalDate... localDates) {
		Objects.requireNonNull(localDates);
		if(localDates.length == 0) {
			throw new IllegalArgumentException("LocalDates are required");
		}
		for(LocalDate localDate: localDates) {
			if(localDate == null) {
				throw new IllegalArgumentException("LocalDates cannot be null");
			}
		}
		Comparator<LocalDate> comparatorReverserd = new Comparator<LocalDate>() {

			@Override
			public int compare(LocalDate o1, LocalDate o2) {
				return o2.compareTo(o1);
			}
		};
		List<LocalDate> list = Arrays.asList(localDates);
		Collections.sort(list, comparatorReverserd);
		LocalDate[] sortedDates = list.toArray(new LocalDate[list.size()]);
		conversionQueryBuilder.set(LocalDate[].class, sortedDates);

		return new HistoricConversionQueryWithDayBuilder(conversionQueryBuilder);
	}

	/**
	 *Set days on {@link HistoricConversionQueryBuilder} to be used on ExchangeRateProvider,
	 *these parameters, different of  {@link HistoricConversionQueryBuilder#withDays(LocalDate...)}, consider the order already defined.
	 * @param localDates
	 * @return this
	 * @throws IllegalArgumentException when is empty or the parameter has an null value
	 */
	@SafeVarargs
	public final HistoricConversionQueryWithDayBuilder withDaysPriorityDefined(LocalDate... localDates) {
		Objects.requireNonNull(localDates);
		if(localDates.length == 0) {
			throw new IllegalArgumentException("LocalDates are required");
		}

		for(LocalDate localDate: localDates) {
			if(localDate == null) {
				throw new IllegalArgumentException("LocalDates cannot be null");
			}
		}
		conversionQueryBuilder.set(LocalDate[].class, localDates);

		return new HistoricConversionQueryWithDayBuilder(conversionQueryBuilder);
	}

	/**
	 * Set the period of days on {@link HistoricConversionQueryBuilder}
	 *  to be used on ExchangeRateProvider,
	 * @param begin
	 * @param end
	 * @return this;
	 * <p>Example:</p>
	 * <pre>
	 * {@code
	 *LocalDate today = LocalDate.parse("2015-04-03");
	 *LocalDate yesterday = today.minusDays(1);
	 *LocalDate tomorrow = today.plusDays(1);
	 *ConversionQuery query = HistoricConversionQueryBuilder.of(real).onDaysBetween(yesterday, tomorrow).build();//the query with new LocalDate[] {tomorrow, today, yesterday}
	 * }
	 * </pre>
	 * @throws NullPointerException when either begin or end is null
	 * @throws IllegalArgumentException when the begin is bigger than end
	 */
	public final HistoricConversionQueryWithDayBuilder withDaysBetween(LocalDate begin, LocalDate end) {
		Objects.requireNonNull(begin);
		Objects.requireNonNull(end);
		if(end.before(begin)) {
			throw new IllegalArgumentException("The end period should be bigger than the begin period.");
		}

		long startTime = begin.toCalendar().getTime().getTime();
		long endTime = end.toCalendar().getTime().getTime();
		long diffTime = endTime - startTime;
		int diffDays = (int)diffTime / (1000 * 60 * 60 * 24);

		List<LocalDate> dates = new ArrayList<>();
		for(int index = diffDays; index >= 0; index--) {
			dates.add(begin.minusDays(-index));
		}
		conversionQueryBuilder.set(LocalDate[].class, dates.toArray(new LocalDate[dates.size()]));

		return new HistoricConversionQueryWithDayBuilder(conversionQueryBuilder);
	}

	/**
	 * Create the {@link ConversionQuery} just with {@link CurrencyUnit}, to term currency, already defined.
	 * @return the conversion query
	 */
	public ConversionQuery build() {
		return conversionQueryBuilder.build();
	}

	@Override
	public String toString() {
	    StringBuilder sb = new StringBuilder();
	    sb.append(HistoricConversionQueryBuilder.class.getName())
	    .append('{').append(" conversionQueryBuilder: ")
	    .append(conversionQueryBuilder).append('}');
		return sb.toString();
	}

	public class HistoricConversionQueryWithDayBuilder {

		private final ConversionQueryBuilder conversionQueryBuilder;

		HistoricConversionQueryWithDayBuilder(
				ConversionQueryBuilder conversionQueryBuilder) {
			this.conversionQueryBuilder = conversionQueryBuilder;
		}

		/**
		 * Create the {@link ConversionQuery} with {@link LocalDate} and {@link CurrencyUnit} to term currency already defined.
		 * @return the conversion query
		 */
		public ConversionQuery build() {
			return conversionQueryBuilder.build();
		}

		@Override
		public String toString() {
		    StringBuilder sb = new StringBuilder();
		    sb.append(HistoricConversionQueryWithDayBuilder.class.getName())
		    .append('{').append(" conversionQueryBuilder: ")
		    .append(conversionQueryBuilder).append('}');
			return sb.toString();
		}

	}
}

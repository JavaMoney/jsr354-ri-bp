/*
 * Copyright (c) 2012, 2018, Anatole Tresch, Werner Keil and others by the @author tag.
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
package org.javamoney.moneta.internal.convert;

import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Class to model a local date without timezone info.
  */
public final class LocalDate implements Comparable<LocalDate>, Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 8427707792648468834L;
	final int year;
    final int month;
    final int dayOfMonth;

    LocalDate(int year, int month, int dayOfMonth){
    this.year = year;
        this.month = month;
        this.dayOfMonth = dayOfMonth;
    }

    static LocalDate now(){
        Calendar cal = GregorianCalendar.getInstance();
        return from(cal);
    }

    static LocalDate yesterday(){
        return beforeDays(1);
    }

    static LocalDate beforeDays(int days){
        Calendar cal = GregorianCalendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, days*-1);
        return from(cal);
    }

    public LocalDate minusDays(int days){
        Calendar cal = toCalendar();
        cal.add(Calendar.DAY_OF_YEAR, days*-1);
        return from(cal);
    }

    /**
     * Create a new (local/default Locale based) GregorianCalendar instance.
     * @return a new (local/default Locale based) GregorianCalendar instance, not null.
     */
    public Calendar toCalendar() {
        return new GregorianCalendar(year, month-1, dayOfMonth);
    }

    /**
     * Cerates a new instance from the given Calendar.
     * @param cal the Calendar, not null.
     * @return the corresponding LocalDate instance, never null.
     */
    public static LocalDate from(Calendar cal) {
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH)+1;
        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        return new LocalDate(year, month, dayOfMonth);
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public boolean before(LocalDate localDate){
        return compareTo(localDate)<0;
    }

    public boolean after(LocalDate localDate){
        return compareTo(localDate)>0;
    }

    @Override
    public int compareTo(LocalDate o) {
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LocalDate)) return false;

        LocalDate localDate = (LocalDate) o;

        return dayOfMonth == localDate.dayOfMonth && month == localDate.month && year == localDate.year;

    }

    @Override
    public int hashCode() {
        int result = year;
        result = 31 * result + month;
        result = 31 * result + dayOfMonth;
        return result;
    }

    @Override
    public String toString() {
        return "LocalDate{" +
                "year=" + year +
                ", month=" + month +
                ", dayOfMonth=" + dayOfMonth +
                '}';
    }


}

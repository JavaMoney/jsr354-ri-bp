package org.javamoney.moneta.convert.internal;

import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Class to model a local date without timezone info.
 * @deprecated Do not use this class in your code, it will be removed/replaced with Java 8 by java.time.
 */
@Deprecated
public final class LocalDate implements Comparable<LocalDate>, Serializable{
    int year;
    int month;
    int dayOfMonth;

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

        if (dayOfMonth != localDate.dayOfMonth) return false;
        if (month != localDate.month) return false;
        if (year != localDate.year) return false;

        return true;
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

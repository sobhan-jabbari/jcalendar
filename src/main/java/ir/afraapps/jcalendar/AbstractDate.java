package ir.afraapps.jcalendar;

import java.util.Locale;

import androidx.annotation.NonNull;


public abstract class AbstractDate {

    static final String[] weekdayName = {"", "یکشنبه", "دوشنبه",
            "سه شنبه", "چهارشنبه", "پنجشنبه", "جمعه", "شنبه"};

    static final String[] weekdayShortName = {"ش", "ی", "د", "س", "چ", "پ", "ج"};

    public abstract String[] getMonthsList();


    public void setDate(int year, int month, int day) {
        setYear(year);
        setMonth(month);
        setDayOfMonth(day);
    }


    public abstract int getYear();


    public abstract void setYear(int year);


    public abstract int getMonth();


    public abstract void setMonth(int month);


    public abstract String getMonthName();


    public abstract int getDayOfMonth();


    public abstract void setDayOfMonth(int day);


    public abstract int getDayOfWeek();


    public abstract int getDayOfYear();


    public abstract int getWeekOfYear();


    public abstract int getWeekOfMonth();


    public abstract String getDayOfWeekName();


    public abstract String getDayOfWeekNameShort();


    public abstract long toJdn();


    /**
     * Returns a string specifying the event of this date, or null if there are
     * no events for this year.
     */
    public abstract String getEvent();


    public abstract boolean isLeapYear();


    @NonNull
    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "%s %s %s",
                getDayOfMonth(),
                getMonthName(),
                getYear());
    }


    public String toStringShort() {
        return String.format(Locale.ENGLISH, "%s %s",
                getDayOfMonth(),
                getMonthName());
    }

    public String toStringMonthYeay() {
        return String.format(Locale.ENGLISH, "%s %s",
                getYear(),
                getMonthName());
    }

    public String toStringByWeekName() {
        return String.format(Locale.ENGLISH, "%s %s %s %s",
                getDayOfWeekName(),
                getDayOfMonth(),
                getMonthName(),
                getYear());
    }

}

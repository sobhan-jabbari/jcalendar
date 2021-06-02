package ir.afraapps.bcalendar;

import androidx.annotation.NonNull;

import java.util.Calendar;
import java.util.Locale;


/**
 * @author Ali Jabbari
 */
public class CivilDate extends AbstractDate {

    private static final String[] monthName = {"", "January", "February", "March",
            "April", "May", "June", "July", "August", "September", "October",
            "November", "December"};

    private static final String[] monthPersianName = {"", "ژانویه", "فوریه", "مارس",
            "آوریل", "مه", "ژوئن", "ژوئیه", "اوت", "سپتامبر", "اکتبر",
            "نوامبر", "دسامبر"};

    private static final int[] daysInMonth = {0, 31, 28, 31, 30, 31, 30, 31,
            31, 30, 31, 30, 31};
    private int year;
    private int month;
    private int day;

    public CivilDate() {
        this(Calendar.getInstance());
    }


    public CivilDate(Calendar calendar) {
        this.year = calendar.get(Calendar.YEAR);
        this.month = calendar.get(Calendar.MONTH) + 1;
        this.day = calendar.get(Calendar.DAY_OF_MONTH);
    }


    public CivilDate(int year, int month, int day) {
        setYear(year);
        setMonth(month);
        setDayOfMonth(day);
    }

    @Override
    public String[] getMonthsList() {
        return monthName;
    }

    @Override
    public int getDayOfMonth() {
        return day;
    }

    @Override
    public void setDayOfMonth(int day) {
        if (day < 1)
            throw new DayOutOfRangeException("day " + day + " is out of range! for month: " + month);

        if (month != 2 && day > daysInMonth[month])
            throw new DayOutOfRangeException("day " + day + " is out of range! for month: " + month);

        if (month == 2 && isLeapYear() && day > 29)
            throw new DayOutOfRangeException("day " + day + " is out of range! for month: " + month);

        if (month == 2 && (!isLeapYear()) && day > 28)
            throw new DayOutOfRangeException("day " + day + " is out of range! for month: " + month);

        // TODO check for the case of leap year for February
        this.day = day;
    }

    public int getDaysInMonth(int month) {
        return daysInMonth[month];
    }


    @Override
    public int getDayOfWeek() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.DAY_OF_MONTH, day);
        return cal.get(Calendar.DAY_OF_WEEK);
    }

    @Override
    public int getDayOfYear() {
        throw new RuntimeException("not implemented yet!");
    }

    @Override
    public String getEvent() {
        throw new RuntimeException("not implemented yet!");
    }

    @Override
    public int getMonth() {
        return month;
    }

    @Override
    public void setMonth(int month) {
        if (month < 1 || month > 12)
            throw new MonthOutOfRangeException("month " + month
                    + " is out of range!");

        this.month = month;

        if (day != 0) {
            setDayOfMonth(day);
        }
    }

    @Override
    public String getMonthName() {
        return monthName[getMonth()];
    }


    public String getMonthPersianName() {
        return monthPersianName[getMonth()];
    }

    @Override
    public int getWeekOfMonth() {
        throw new RuntimeException("not implemented yet!");
    }

    @Override
    public int getWeekOfYear() {
        throw new RuntimeException("not implemented yet!");
    }

    @Override
    public int getYear() {
        return year;
    }

    @Override
    public void setYear(int year) {
        if (year == 0)
            throw new YearOutOfRangeException("Year 0 is invalid!");

        this.year = year;
    }

    @Override
    public boolean isLeapYear() {
        return (year % 4 == 0) && (year % 100 != 0 || year % 400 == 0);
    }


    @Override
    public String getDayOfWeekName() {
        return weekdayName[getDayOfWeek()];
    }


    @Override
    public String getDayOfWeekNameShort() {
        return weekdayShortName[getDayOfWeek()];
    }


    public PersianDate toPersianDate() {
        return PersianDate.fromJdn(toJdn());
    }

    public IslamicDate toIslamicDate(int method, int offset) {
        return IslamicDate.fromJdn(toJdn(), method, offset);
    }

    public Calendar toCalendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    public long toCalendarTime() {
        return toCalendar().getTimeInMillis();
    }


    @Override
    public long toJdn() {
        long jdn = (year + (month - 8) / 6 + 100100) * 1461 / 4 + (153 * ((month + 9) % 12) + 2) / 5 + day - 34840408;
        jdn = jdn - (year + 100100 + (month - 8) / 6) / 100 * 3 / 4 + 752;
        return jdn;
    }


    public static CivilDate fromJdn(long jdn) {
        long j = 4 * jdn + 139361631;
        j = j + (((((4 * jdn + 183187720) / 146097) * 3) / 4) * 4 - 3908);
        long i = ((j % 1461) / 4) * 5 + 308;
        int day = (int) ((i % 153) / 5 + 1);
        int month = (int) (((i / 153) % 12) + 1);
        int year = (int) (j / 1461 - 100100 + (8 - month) / 6);
        return new CivilDate(year, month, day);
    }


    public static CivilDate fromCalendarTime(long milliseconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);
        return new CivilDate(calendar);
    }

    public boolean isToday() {
        CivilDate now = new CivilDate();
        return now.getYear() == getYear()
                && now.getMonth() == getMonth()
                && now.getDayOfMonth() == getDayOfMonth();
    }


    @Override
    public CivilDate clone() {
        try {
            super.clone();
        } catch (CloneNotSupportedException e) {
            //
        }
        return new CivilDate(this.getYear(), this.getMonth(), this.getDayOfMonth());
    }


    @Override
    public boolean equals(Object obj) {
        return obj instanceof CivilDate
                && this.getDayOfMonth() == ((CivilDate) obj).getDayOfMonth()
                && this.getMonth() == ((CivilDate) obj).getMonth()
                && this.getYear() == ((CivilDate) obj).getYear();
    }

    @NonNull
    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "%s %s %s",
                getDayOfMonth(),
                getMonthPersianName(),
                getYear());
    }


    @Override
    public String toStringShort() {
        return String.format(Locale.ENGLISH, "%s %s",
                getDayOfMonth(),
                getMonthPersianName());
    }

    @Override
    public String toStringMonthYeay() {
        return String.format(Locale.ENGLISH, "%s %s",
                getYear(),
                getMonthPersianName());
    }

    @Override
    public String toStringByWeekName() {
        return String.format(Locale.ENGLISH, "%s %s %s %s",
                getDayOfWeekName(),
                getDayOfMonth(),
                getMonthPersianName(),
                getYear());
    }
}

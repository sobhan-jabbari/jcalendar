package ir.afraapps.bcalendar;

import android.util.SparseArray;

import java.util.Calendar;


/**
 * @author Ali Jabbari
 */

public final class DateConverter {

    static final SparseArray<long[]> yearsMonthsInJd = new SparseArray();

    static int supportedYearsStart;
    static long[] yearsStartJd;
    static long jdSupportEnd;
    static long jdSupportStart = 2453766;


    // ۱-محرم، ۲-صفر، ۳-ربیع الاول، ۴-ربیع الثانی، ۵-جمادی الاول، ۶-جمادی الثانی،
    // ۷-رجب، ۸-شعبان، ۹-رمضان، ۱۰-شوال، ۱۱-ذی‌قعده، ۱۲-ذی‌حجه
    static {

        int[] hijriMonths = {
                1427, 30, 29, 29, 30, 29, 30, 30, 30, 30, 29, 29, 30,
                1428, 29, 30, 29, 29, 29, 30, 30, 29, 30, 30, 30, 29,
                1429, 30, 29, 30, 29, 29, 29, 30, 30, 29, 30, 30, 29,
                1430, 30, 30, 29, 29, 30, 29, 30, 29, 29, 30, 30, 29,
                1431, 30, 30, 29, 30, 29, 30, 29, 30, 29, 29, 30, 29,
                1432, 30, 30, 29, 30, 30, 30, 29, 29, 30, 29, 30, 29,
                1433, 29, 30, 29, 30, 30, 30, 29, 30, 29, 30, 29, 30,
                1434, 29, 29, 30, 29, 30, 30, 29, 30, 30, 29, 30, 29,
                1435, 29, 30, 29, 30, 29, 30, 29, 30, 30, 30, 29, 30,
                1436, 29, 30, 29, 29, 30, 29, 30, 29, 30, 29, 30, 30,
                1437, 29, 30, 30, 29, 30, 29, 29, 30, 29, 29, 30, 30,
                1438, 29, 30, 30, 30, 29, 30, 29, 29, 30, 29, 29, 30,
                1439, 29, 30, 30, 30, 30, 29, 30, 29, 29, 30, 29, 29,
                1440, 30, 29, 30, 30, 30, 29, 30, 30, 29, 29, 30, 29,
                1441, 29, 30, 29, 30, 30, 29, 30, 30, 29, 30, 29, 30,
                1442, 29, 29, 30, 29, 30, 29, 30,/**/30, 30, 29, 30, 29
        };

        int years = (int) Math.ceil(((float) hijriMonths.length) / 13);
        yearsStartJd = new long[years];
        supportedYearsStart = hijriMonths[0];
        long jd = jdSupportStart;
        for (int y = 0; y < years; ++y) {
            int year = hijriMonths[y * 13];

            yearsStartJd[y] = jd;
            long[] months = new long[12];
            for (int m = 1; m < 13 && y * 13 + m < hijriMonths.length; ++m) {
                months[m - 1] = jd;
                jd += hijriMonths[y * 13 + m];
            }
            yearsMonthsInJd.put(year, months);
        }
        jdSupportEnd = jd;

    }


    static int search(long[] array, long r) {
        int i = 0;
        while (i < array.length && array[i] < r) ++i;
        return i;
    }


    private static boolean isLipYearInShamsi(int year) {
        int y;
        if (year > 0)
            y = year - 474;
        else
            y = 473;
        return (((((y % 2820) + 474) + 38) * 682) % 2816) < 682;
    }


    public static int countDaysToDate(PersianDate date) {
        if (date == null) return 0;
        PersianDate now = new PersianDate();
        int[] monthsList = new int[]{0, 31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29};
        int baseYear = now.getYear();
        int baseMonth = now.getMonth();
        int baseDay = now.getDayOfMonth();
        int year = date.getYear();
        int month = date.getMonth();
        int day = date.getDayOfMonth();

        int yearDiff = year - baseYear;

        int resultOfYearDiff = 0;
        int resultOfMonthsDiff = 0;
        int resultOfBaseMonthsDiff = 0;

        if (yearDiff > 0) {
            for (int j = 0; j < yearDiff; j++) {
                resultOfYearDiff += 365;

                if (isLipYearInShamsi(baseYear)) {
                    resultOfYearDiff++;
                }
                baseYear++;
            }
        }

        for (int i = 0; i < month; i++) {
            resultOfMonthsDiff += monthsList[i];
        }

        for (int i = 0; i < baseMonth; i++) {
            resultOfBaseMonthsDiff += monthsList[i];
        }

        int days1 = resultOfYearDiff + resultOfMonthsDiff + (day - 1);
        int days2 = resultOfBaseMonthsDiff + (baseDay - 1);

        return days1 - days2;
    }


    public static long getRoundDate(long date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 1);

        return calendar.getTimeInMillis();
    }


    public static long persianToCalendarRound(PersianDate persian) {
        Calendar calendar = persian.toCalendar();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return getRoundDate(calendar.getTimeInMillis());
    }


    private static double visibility(long n, int offset) {

        // parameters for Makkah: for a new moon to be visible after sunset on
        // a the same day in which it started, it has to have started before
        // (SUNSET-MINAGE)-TIMZ=3 A.M. local time.
        final float TIMZ = 3f, MINAGE = 13.5f, SUNSET = 19.5f, // approximate
                TIMDIF = (SUNSET - MINAGE);

        double jd = tmoonphase(n, 0);
        long d = (long) Math.floor(jd);

        double tf = (jd - d);

        if (tf <= 0.5) // new moon starts in the afternoon
            return (jd + 1f) - offset;
        else { // new moon starts before noon
            tf = (tf - 0.5) * 24 + TIMZ; // local time
            if (tf > TIMDIF)
                return (jd + 1d) - offset; // age at sunset < min for visiblity
            else
                return jd - offset;
        }

    }


    private static double tmoonphase(long n, int nph) {

        final double RPD = (1.74532925199433E-02); // radians per degree
        // (pi/180)

        double xtra = 0;

        double k = n + nph / 4d;
        double T = k / 1236.85;
        double t2 = T * T;
        double t3 = t2 * T;
        double jd = 2415020.75933 + 29.53058868 * k - 0.0001178 * t2
                - 0.000000155 * t3 + 0.00033
                * Math.sin(RPD * (166.56 + 132.87 * T - 0.009173 * t2));

        // Sun's mean anomaly
        double sa = RPD
                * (359.2242 + 29.10535608 * k - 0.0000333 * t2 - 0.00000347 * t3);

        // Moon's mean anomaly
        double ma = RPD
                * (306.0253 + 385.81691806 * k + 0.0107306 * t2 + 0.00001236 * t3);

        // Moon's argument of latitude
        double tf = RPD
                * 2d
                * (21.2964 + 390.67050646 * k - 0.0016528 * t2 - 0.00000239 * t3);

        // should reduce to interval 0-1.0 before calculating further
        switch (nph) {
            case 0:
            case 2:
                xtra = (0.1734 - 0.000393 * T) * Math.sin(sa) + 0.0021
                        * Math.sin(sa * 2) - 0.4068 * Math.sin(ma) + 0.0161
                        * Math.sin(2 * ma) - 0.0004 * Math.sin(3 * ma) + 0.0104
                        * Math.sin(tf) - 0.0051 * Math.sin(sa + ma) - 0.0074
                        * Math.sin(sa - ma) + 0.0004 * Math.sin(tf + sa) - 0.0004
                        * Math.sin(tf - sa) - 0.0006 * Math.sin(tf + ma) + 0.001
                        * Math.sin(tf - ma) + 0.0005 * Math.sin(sa + 2 * ma);
                break;
            case 1:
            case 3:
                xtra = (0.1721 - 0.0004 * T) * Math.sin(sa) + 0.0021
                        * Math.sin(sa * 2) - 0.628 * Math.sin(ma) + 0.0089
                        * Math.sin(2 * ma) - 0.0004 * Math.sin(3 * ma) + 0.0079
                        * Math.sin(tf) - 0.0119 * Math.sin(sa + ma) - 0.0047
                        * Math.sin(sa - ma) + 0.0003 * Math.sin(tf + sa) - 0.0004
                        * Math.sin(tf - sa) - 0.0006 * Math.sin(tf + ma) + 0.0021
                        * Math.sin(tf - ma) + 0.0003 * Math.sin(sa + 2 * ma)
                        + 0.0004 * Math.sin(sa - 2 * ma) - 0.0003
                        * Math.sin(2 * sa + ma);
                if (nph == 1)
                    xtra = xtra + 0.0028 - 0.0004 * Math.cos(sa) + 0.0003
                            * Math.cos(ma);
                else
                    xtra = xtra - 0.0028 + 0.0004 * Math.cos(sa) - 0.0003
                            * Math.cos(ma);

                break;
            default:
                return 0;
        }
        // convert from Ephemeris Time (ET) to (approximate)Universal Time (UT)
        return jd + xtra - (0.41 + 1.2053 * T + 0.4992 * t2) / 1440;
    }


}

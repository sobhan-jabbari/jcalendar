package ir.afraapps.bcalendar;

import java.util.Calendar;


/**
 * @author Ali Jabbari
 */
public class IslamicDate extends AbstractDate {

  public static final int TUG = 0;
  public static final int PREVALENT = 1;

  private static final long JDN_UTG_SUPPORT_START = 2455892;

  private static final String[] monthName = {"", "محرم", "صفر",
    "ربيع‌الاول", "ربيع‌الثاني", "جمادي‌الاول", "جمادي‌الثاني", "رجب",
    "شعبان", "رمضان", "شوال", "ذي‌القعده", "ذي‌الحجه"};


  @Override
  public String[] getMonthsList() {
    return monthName;
  }

  private int day;
  private int month;
  private int year;
  private int method;
  private int offset;


  public IslamicDate(int year, int month, int day) {
    setYear(year);
    setMonth(month);
    setDayOfMonth(day);
  }


  public IslamicDate(int year, int month, int day, int method, int offset) {
    setMethod(method);
    setOffset(offset);
    setYear(year);
    setMonth(month);
    setDayOfMonth(day);
  }


  @Override
  public int getDayOfMonth() {
    return day;
  }


  @Override
  public int getDayOfWeek() {
    return toCalendar().get(Calendar.DAY_OF_WEEK);
  }


  @Override
  public int getMonth() {
    return month;
  }


  @Override
  public String getMonthName() {
    return monthName[month];
  }


  public static String getMonthName(int month) {
    return monthName[month];
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

  public CivilDate toCivilDate() {
    return CivilDate.fromJdn(toJdn());
  }

  public Calendar toCalendar() {
    return toCivilDate().toCalendar();
  }

  public long toCalendarTime() {
    return toCalendar().getTimeInMillis();
  }


  @Override
  public long toJdn() {
    if (method == IslamicDate.TUG) {
      if (DateConverter.yearsMonthsInJd == null || DateConverter.yearsMonthsInJd.get(year) == null)
        return -1;
      long calculatedDay = DateConverter.yearsMonthsInJd.get(year)[month - 1];
      if (calculatedDay == 0)
        return -1;
      return calculatedDay + day;

    } else {
      int NMONTHS = (1405 * 12 + 1);

      int islamicYear = year;

      if (islamicYear < 0)
        islamicYear++;

      long k = month + islamicYear * 12 - NMONTHS; // number of months since 1/1/1405
      return (long) Math.floor(visibility(k + 1048, offset) + day + 0.5);
    }
  }


  public static IslamicDate fromJdn(long jdn, int method, int offset) {
    if (method == IslamicDate.TUG) {
      if (jdn < DateConverter.jdSupportStart || jdn >= DateConverter.jdSupportEnd || DateConverter.yearsStartJd == null)
        return null;

      int yearIndex = DateConverter.search(DateConverter.yearsStartJd, jdn);
      int year = yearIndex + DateConverter.supportedYearsStart - 1;
      long[] yearMonths = DateConverter.yearsMonthsInJd.get(year);
      if (yearMonths == null) {
        return null;
      }
      int month = DateConverter.search(yearMonths, jdn);
      if (yearMonths[month - 1] == 0) {
        return null;
      }
      int day = (int) (jdn - yearMonths[month - 1]);
      return new IslamicDate(year, month, day, method, offset);

    } else {
      CivilDate civil = CivilDate.fromJdn(jdn);
      int year = civil.getYear();
      int month = civil.getMonth();
      int day = civil.getDayOfMonth();

      long k = (long) Math.floor(0.6 + (year + (month % 2 == 0 ? month : month - 1) / 12d
        + day / 365f - 1900) * 12.3685);

      double mjd;
      do {
        mjd = visibility(k, offset);
        k = k - 1;
      } while (mjd > (jdn - 0.5));

      k = k + 1;
      long hm = k - 1048;

      year = 1405 + (int) (hm / 12);
      month = (int) (hm % 12) + 1;

      if (hm != 0 && month <= 0) {
        month = month + 12;
        year = year - 1;
      }

      if (year <= 0)
        year = year - 1;

      day = (int) Math.floor(jdn - mjd + 0.5);

      return new IslamicDate(year, month, day, method, offset);
    }

  }


  public static IslamicDate fromCalendar(Calendar calendar, int method, int offset) {
    return new CivilDate(calendar).toIslamicDate(method, offset);
  }

  public static IslamicDate fromCalendarTime(long milliseconds, int method, int offset) {
    return CivilDate.fromCalendarTime(milliseconds).toIslamicDate(method, offset);
  }


  @Override
  public void setDayOfMonth(int day) {
    // TODO This check is not very exact! But it's not worth of it
    // to compute the number of days in this month exactly
    if (day < 1 || day > 30)
      throw new DayOutOfRangeException("day " + day + " is out of range!");

    this.day = day;
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
  public void setYear(int year) {
    if (year == 0)
      throw new YearOutOfRangeException("Year 0 is invalid!");

    this.year = year;
  }


  @Override
  public String getEvent() {
    throw new RuntimeException("not implemented yet!");
  }


  @Override
  public int getDayOfYear() {
    throw new RuntimeException("not implemented yet!");
  }


  @Override
  public int getWeekOfMonth() {
    throw new RuntimeException("not implemented yet!");
  }


  @Override
  public boolean isLeapYear() {
    throw new RuntimeException("not implemented yet!");
  }


  public int getMethod() {
    return method;
  }


  public void setMethod(int method) {
    this.method = method;
  }


  public int getOffset() {
    return offset;
  }

  public void setOffset(int offset) {
    this.offset = offset;
  }

  public boolean isTUG() {
    return method == TUG;
  }


  public boolean isToday() {
    return this.equals(IslamicDate.fromJdn(new CivilDate().toJdn(), method, offset));
  }


  @Override
  public IslamicDate clone() {
    try {
      super.clone();
    } catch (CloneNotSupportedException e) {
      //
    }
    return new IslamicDate(getYear(), getMonth(), getDayOfMonth());
  }


  public boolean equals(IslamicDate islamicDate) {
    if (this.getDayOfMonth() == islamicDate.getDayOfMonth()
      && this.getMonth() == islamicDate.getMonth()
      && this.getYear() == islamicDate.getYear())
      return true;
    return false;
  }


  @Override
  public boolean equals(Object obj) {
    return obj instanceof IslamicDate &&
      getYear() == ((IslamicDate) obj).getYear() &&
      getMonth() == ((IslamicDate) obj).getMonth() &&
      getDayOfMonth() == ((IslamicDate) obj).getDayOfMonth();
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


  public interface BeginPersianDateCallback {
    PersianDate callBeginPersianDate(IslamicDate islamicDate);
  }

}

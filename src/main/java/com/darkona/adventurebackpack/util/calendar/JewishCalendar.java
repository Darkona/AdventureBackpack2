package com.darkona.adventurebackpack.util.calendar;

/**
 * Created on 22/12/2014
 *
 * @author Javier Darkona
 */
public class JewishCalendar
{

    public static boolean isHannukah(int year, int month, int day)
    {

        CalendarDate gregorianDate = new CalendarDate(day, month, year);
        CalendarImpl kosher = new CalendarImpl();
        int absolutedate = kosher.absoluteFromGregorianDate(gregorianDate);
        CalendarDate jewishDate = kosher.jewishDateFromAbsolute(absolutedate);
        int hebDay = jewishDate.getDay();
        int hebMonth = jewishDate.getMonth();
        int hebYear = jewishDate.getYear();

        boolean result = false;
        if (hebMonth == 9)
        {
            if (hebDay >= 25 && hebDay <= 29) result = true;
        }
        if (kosher.getLastDayOfJewishMonth(9, hebYear) == 30)
        {

            if (hebDay == 30 && hebMonth == 9)
            {
                result = true;
            }
            if (hebMonth == 10 && (hebDay == 1 || hebDay == 2)) result = true;

        }
        if (kosher.getLastDayOfJewishMonth(9, hebYear) == 29)
        {
            if (hebMonth == 10 && (hebDay == 1 || hebDay == 2 || hebDay == 3)) result = true;
        }
        return result;
    }

}

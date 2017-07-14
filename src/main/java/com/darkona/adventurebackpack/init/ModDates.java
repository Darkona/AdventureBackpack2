package com.darkona.adventurebackpack.init;

import java.util.Calendar;

/**
 * Created by Ugachaga on 06.07.2017.
 */
public class ModDates
{
    private static String holiday;

    private static int year = Calendar.getInstance().get(Calendar.YEAR);
    private static int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
    private static int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

    public static void init()
    {
        holiday = setHoliday();
    }

    public static String getHoliday()
    {
        return holiday;
    }

    private static String setHoliday()
    {
        // here and below commented lines: textures are missing.
        /*if (ChineseCalendar.isChineseNewYear(year, month, day))
        {
            return "ChinaNewYear";
        }
        if (JewishCalendar.isHannukah(year, month, day))
        {
            return "Hannukah";
        }
        if (month == calculateEaster(year)[0] && day == calculateEaster(year)[1])
        {
            return "Easter";
        }*/

        String result = "Standard";
        if (month == 1)
        {
            if (day == 1) result = "NewYear";
            if (day == 28) result = "Shuttle"; //Challenger
        }
        if (month == 2)
        {
            if (day == 1) result = "Shuttle"; //Columbia
            if (day == 14) result = "Valentines";
            //if (day == 23) result = "Fatherland";
        }
        if (month == 3)
        {
            if (day == 17) result = "Patrick";
        }
        if (month == 4)
        {
            if (day == 1) result = "Fools";
            //if (day == 25) result = "Italy";
        }
        if (month == 5)
        {
            //if (day == 8 || day == 9 || day == 10) result = "Liberation";
        }
        if (month == 6)
        {

        }
        if (month == 7)
        {
            if (day == 4) result = "USA";
            if (day == 24) result = "Bolivar";
            //if (day == 14) result = "Bastille";
        }
        if (month == 8)
        {

        }
        if (month == 9)
        {
            //if (day == 19) result = "Pirate";
        }
        if (month == 10)
        {
            //if (day == 3) result = "Germany";
            //if (day == 12) result = "Columbus";
            //if (day == 31) result = "Halloween";
        }
        if (month == 11)
        {
            //if (day == 2) result = "Muertos";
        }
        if (month == 12)
        {
            if (day >= 22 && day <= 26) result = "Christmas";
            if (day == 31) result = "NewYear";
        }
        //LogHelper.info("Today is: " + day + "/" + month + "/" + year + ". Which means today is: " + result);
        return result;
    }

    private static int[] calculateEaster(int year)
    {
        int a = year % 19,
                b = year / 100,
                c = year % 100,
                d = b / 4,
                e = b % 4,
                g = (8 * b + 13) / 25,
                h = (19 * a + b - d - g + 15) % 30,
                j = c / 4,
                k = c % 4,
                m = (a + 11 * h) / 319,
                r = (2 * e + 2 * j - k - h + m + 32) % 7,
                n = (h - m + r + 90) / 25,
                p = (h - m + r + n + 19) % 32;

        return new int[]{n, p};
    }
}
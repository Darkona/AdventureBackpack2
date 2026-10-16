package com.darkona.adventurebackpack.util;

import java.util.function.Predicate;

public final class HolidayTexture
{
    public static final String STANDARD = "Standard";

    private HolidayTexture() {}

    public static String resolve(String holiday, Predicate<String> textureExists)
    {
        if (holiday == null || holiday.isEmpty() || STANDARD.equals(holiday)) return STANDARD;
        return textureExists.test(holiday) ? holiday : STANDARD;
    }
}

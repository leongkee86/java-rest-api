package com.demo.rest_api.utils;

import java.util.concurrent.ThreadLocalRandom;

public class EnumHelper
{
    public static <T extends Enum<?>> T getRandomEnum( Class<T> enumClass )
    {
        T[] enumConstants = enumClass.getEnumConstants();
        int randomIndex = ThreadLocalRandom.current().nextInt( enumConstants.length );
        return enumConstants[ randomIndex ];
    }
}

/*
 * ****************************************************************************
 * File: EnumHelper.java
 * Author: Lim Leong Kee
 * Email: leongkee86@gmail.com
 * Last Modified Date: 19 August 2025
 * 
 * Description:
 * This class provides a set of helper methods for working with enums.
 * ****************************************************************************
 */

package com.demo.rest_api.utils;

import java.util.concurrent.ThreadLocalRandom;

public class EnumHelper
{
    /**
     * Returns a random value from the specified enum type.
     *
     * @param enumClass The class object of the enum type.
     * @param <T> The type of the enum, which must be an enum type.
     * @return A random enum constant from the specified enum type.
     * @throws NullPointerException If the enumClass is null.
     * @throws IllegalArgumentException If the class is not an enum type.
     */
    public static <T extends Enum<?>> T getRandomEnum( Class<T> enumClass )
    {
        T[] enumConstants = enumClass.getEnumConstants();
        int randomIndex = ThreadLocalRandom.current().nextInt( enumConstants.length );
        return enumConstants[ randomIndex ];
    }
}

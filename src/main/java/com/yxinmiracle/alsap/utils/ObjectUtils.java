package com.yxinmiracle.alsap.utils;

import java.lang.reflect.Field;
import java.util.Arrays;

public class ObjectUtils {

    /**
     * Checks if all fields in any given object are null.
     *
     * @param object the object to check
     * @return true if all fields are null, false otherwise
     */
    public static boolean areAllFieldsNull(Object object) {
        if (object == null) {
            return true;
        }
        return Arrays.stream(object.getClass().getDeclaredFields())
                .allMatch(field -> {
                    try {
                        field.setAccessible(true); // You might want to set accessible to true to reach private fields
                        return field.get(object) == null;
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("Access exception while checking field values", e);
                    }
                });
    }
}

package cc.invictusgames.ilib.utils;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;

/**
 * credit: https://github.com/sim0n/Nemesis/blob/main/src/main/java/dev/sim0n/anticheat/util/ReflectionUtil.java
 */
public class ReflectionUtil {

    /**
     * Gets a field in {@param clazz}
     * @param clazz The class owning the class
     * @param fieldName The name of the field to get
     * @return The field
     */
    public static Field getField(Class<?> clazz, String fieldName) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);

            return field;
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException(clazz.getSimpleName() + ":" + fieldName);
        }
    }

    /**
     * Gets the field value of {@param field}
     * @param field The field to get
     * @param instance The object instance (null if static)
     * @return The field value
     */
    public static <T> T getFieldValue(Field field, Object instance) {
        try {
            return (T) field.get(instance);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Sets the value of {@param field} to {@param value}
     * @param field The field to set the value of
     * @param instance The object instance (null if static)
     * @param value The value to set {@param field} to
     */
    public static void setFieldValue(Field field, Object instance, Object value) {
        try {
            field.set(instance, value);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException();
        }
    }

    public static <T extends AccessibleObject> T setAccessible(T object) {
        object.setAccessible(true);
        return object;
    }
}

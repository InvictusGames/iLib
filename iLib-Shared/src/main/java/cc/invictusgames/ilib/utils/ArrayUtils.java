package cc.invictusgames.ilib.utils;

import java.util.List;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 09.02.2020 / 01:14
 * iLib / cc.invictusgames.ilib.utils
 */

public class ArrayUtils<T> {

    public boolean contains(T[] array, T element) {
        for (T t : array) {
            if (t.equals(element)) {
                return true;
            }
        }
        return false;
    }

    public static boolean containsIgnoreCase(String[] array, String element) {
        for (String s : array) {
            if (s.equalsIgnoreCase(element)) {
                return true;
            }
        }
        return false;
    }

    public static boolean containsIgnoreCase(List<String> array, String element) {
        return containsIgnoreCase(array.toArray(new String[0]), element);
    }
}

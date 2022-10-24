package cc.invictusgames.ilib.utils;

import java.util.regex.Pattern;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 18.02.2020 / 20:34
 * iLib / cc.invictusgames.ilib.utils
 */

public class UUIDUtils {

    private static Pattern UUID_PATTERN = Pattern.compile("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[34][0-9a-fA-F]{3}-[89ab][0" +
            "-9a-fA-F]{3}-[0-9a-fA-F]{12}");

    public static boolean isUUID(String string) {
        return UUID_PATTERN.matcher(string).find();
    }
}

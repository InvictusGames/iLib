package cc.invictusgames.ilib.utils;

public class EntityUtils {

    private static int fakeEntityId = -1;

    public static int fakeEntityId() {
        return fakeEntityId--;
    }

}

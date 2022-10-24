package cc.invictusgames.ilib.utils.logging;


import java.util.logging.Logger;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 21.02.2021 / 14:06
 * iLib / cc.invictusgames.ilib.utils.logging
 */

public interface LogFactory {

    Logger newLogger(String name);

    default Logger newLogger(Class<?> clazz) {
        return newLogger(clazz.getSimpleName());
    }

}

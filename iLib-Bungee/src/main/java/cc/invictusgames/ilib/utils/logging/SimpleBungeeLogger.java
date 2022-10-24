package cc.invictusgames.ilib.utils.logging;


import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginLogger;

import java.util.logging.LogRecord;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 05.03.2021 / 06:59
 * iLib / cc.invictusgames.ilib.utils.logging
 */

public class SimpleBungeeLogger extends PluginLogger {

    private final String name;

    protected SimpleBungeeLogger(Plugin parent, String name) {
        super(parent);
        this.name = "[" + name + "] ";
    }

    @Override
    public void log(LogRecord record) {
        record.setMessage(name + record.getMessage());
        super.log(record);
    }

    public static SimpleBungeeLogger getLogger(Plugin parent, String name) {
        return new SimpleBungeeLogger(parent, name);
    }

}

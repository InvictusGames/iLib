package cc.invictusgames.ilib.utils.logging;

import cc.invictusgames.ilib.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginLogger;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 05.03.2021 / 06:59
 * iLib / cc.invictusgames.ilib.utils.logging
 */

public class SimpleBukkitLogger extends PluginLogger {

    public static final Map<UUID, Integer> LOG_LEVEL_MAP = new HashMap<>();

    private final String name;
    private final String pluginName;

    protected SimpleBukkitLogger(Plugin parent, String name) {
        super(parent);
        this.setLevel(Level.ALL);
        this.name = name;
        this.pluginName = parent.getDescription().getPrefix() != null
                ? parent.getDescription().getPrefix()
                : parent.getDescription().getName();
    }

    @Override
    public void log(LogRecord record) {
        StringBuilder builder = new StringBuilder()
                .append(CC.DGRAY).append("[").append(CC.RED).append(pluginName).append(CC.DGRAY).append("]");

        if (name != null)
            builder.append(" [").append(CC.RED).append(name).append(CC.DGRAY).append("]");

        String color;

        if (record.getLevel().intValue() >= Level.WARNING.intValue())
            color = CC.RED;
        else if (record.getLevel().intValue() >= Level.INFO.intValue())
            color = CC.YELLOW;
        else if (record.getLevel().intValue() >= Level.CONFIG.intValue())
            color = CC.BLUE;
        else color = CC.GREEN;

        builder.append(" [").append(color)
                .append(record.getLevel() == Level.CONFIG ? "DEBUG" : record.getLevel().getName())
                .append(CC.DGRAY).append("]")
                .append(CC.WHITE).append(" ").append(record.getMessage());

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("ilib.logging")
                    && record.getLevel().intValue() >= LOG_LEVEL_MAP.getOrDefault(player.getUniqueId(),
                    Level.WARNING.intValue()))
                player.sendMessage(builder.toString());
        }

        record.setMessage((name != null ? "[" + name + "] " : "") + record.getMessage());

        if (record.getLevel() == Level.CONFIG)
            record.setLevel(Level.INFO);

        super.log(record);
    }

    public static SimpleBukkitLogger getLogger(Plugin parent, String name) {
        return new SimpleBukkitLogger(parent, name);
    }

}

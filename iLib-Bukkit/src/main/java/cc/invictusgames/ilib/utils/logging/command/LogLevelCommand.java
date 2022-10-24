package cc.invictusgames.ilib.utils.logging.command;

import cc.invictusgames.ilib.ILib;
import cc.invictusgames.ilib.command.annotation.Command;
import cc.invictusgames.ilib.command.annotation.Param;
import cc.invictusgames.ilib.utils.CC;
import cc.invictusgames.ilib.utils.logging.SimpleBukkitLogger;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.util.logging.Level;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 18.06.2021 / 18:31
 * iLib / cc.invictusgames.ilib.utils.logging
 */

@RequiredArgsConstructor
public class LogLevelCommand {

    private final ILib instance;

    @Command(names = {"loglevel set"},
             permission = "ilib.logging",
             description = "Set someones logging level",
             async = true)
    public boolean logLevelSet(Player sender, @Param(name = "level") String levelInput) {
        try {
            Integer.parseInt(levelInput);
            sender.sendMessage(CC.RED + "Cannot input integer as level.");
            return false;
        } catch (NumberFormatException e) {
        }

        Level level = Level.parse(levelInput);
        instance.getRedisService().executeBackendCommand(redis -> {
            redis.set("logging:" + sender.getUniqueId().toString() + ":level", String.valueOf(level.intValue()));
            SimpleBukkitLogger.LOG_LEVEL_MAP.put(sender.getUniqueId(), level.intValue());
            return null;
        });

        sender.sendMessage(CC.format("&eYour log level was set to &9%s&e.", level.getName()));
        return true;
    }

}

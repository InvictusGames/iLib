package cc.invictusgames.ilib.combatlogger.command;

import cc.invictusgames.ilib.combatlogger.CombatLogger;
import cc.invictusgames.ilib.combatlogger.event.CombatLoggerDespawnEvent;
import cc.invictusgames.ilib.command.annotation.Command;
import cc.invictusgames.ilib.command.annotation.Param;
import cc.invictusgames.ilib.utils.CC;
import cc.invictusgames.ilib.uuid.UUIDCache;
import org.bukkit.command.CommandSender;

import java.util.UUID;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 05.07.2020 / 02:10
 * iLib / cc.invictusgames.ilib.combatlogger.command
 */

public class DespawnCombatLoggerCommand {

    @Command(names = "despawncombatlogger", permission = "combatlogger.despawn",
             description = "Despawn the combat logger of a player")
    public boolean despawn(CommandSender sender, @Param(name = "uuid") UUID uuid) {
        CombatLogger logger = CombatLogger.getLoggerMap().getOrDefault(uuid, null);
        if (logger == null) {
            sender.sendMessage(CC.RED + "There is no combat logger belonging to " + CC.YELLOW + UUIDCache.getName(uuid) + CC.RED + ".");
            return false;
        }

        CombatLogger.getLoggerMap().remove(logger.getPlayerUuid());
        CombatLogger.getLoggerMap().remove(logger.getSpawnedEntity().getUniqueId());
        logger.despawn(CombatLoggerDespawnEvent.Cause.COMMAND);
        sender.sendMessage(CC.GOLD + "Successfully despawned the "
                + CC.WHITE + logger.getLoggerType().getName()
                + CC.GOLD + " of " + CC.WHITE + UUIDCache.getName(uuid) + CC.GOLD + ".");
        return true;
    }

}

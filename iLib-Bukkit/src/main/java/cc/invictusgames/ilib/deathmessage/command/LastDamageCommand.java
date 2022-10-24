package cc.invictusgames.ilib.deathmessage.command;

import cc.invictusgames.ilib.command.annotation.Command;
import cc.invictusgames.ilib.command.annotation.Param;
import cc.invictusgames.ilib.deathmessage.DeathMessageService;
import cc.invictusgames.ilib.deathmessage.damage.Damage;
import cc.invictusgames.ilib.utils.CC;
import cc.invictusgames.ilib.uuid.UUIDCache;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.UUID;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 09.07.2020 / 22:38
 * iLib / cc.invictusgames.ilib.deathmessage.command
 */

public class LastDamageCommand {

    @Command(names = {"lastdamage"}, permission = "ilib.command.lastdamage",
             description = "Check the last damage a player has taken")
    public boolean lastDamage(CommandSender sender, @Param(name = "player") UUID player) {
        List<Damage> damages = DeathMessageService.getDamage(player);
        if (damages.isEmpty()) {
            sender.sendMessage(CC.YELLOW + UUIDCache.getName(player) + CC.RED + " does not have any damage stored.");
            return false;
        }

        sender.sendMessage(CC.GOLD + "Displaying " + CC.WHITE + damages.size()
                + CC.GOLD + (damages.size() == 1 ? " entry." : " entries."));
        damages.forEach(damage -> sender.sendMessage(CC.GRAY + " - " + CC.GOLD + damage.getClass().getSimpleName()
                + (damage.getExtraInformation().isEmpty() ? "" : CC.GRAY + " " + damage.getExtraInformation())
                + ": " + CC.WHITE + damage.getDamage()));
        return true;
    }

}

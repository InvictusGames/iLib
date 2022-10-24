package cc.invictusgames.ilib.utils;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 01.04.2020 / 19:09
 * iLib / cc.invictusgames.ilib.utils
 */

public class Debugger {

    public static boolean enabled = true;

    public static void debug(String message) {
        if (enabled) {
            Bukkit.broadcastMessage(CC.RED + "[Debug] " + CC.WHITE + message);
        }
    }

    public static void debug(CommandSender sender, String message) {
        if (enabled) {
            sender.sendMessage(CC.RED + "[Debug] " + CC.WHITE + message);
        }
    }

}

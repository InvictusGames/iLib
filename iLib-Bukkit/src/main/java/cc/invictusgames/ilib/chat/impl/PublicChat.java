package cc.invictusgames.ilib.chat.impl;

import cc.invictusgames.ilib.chat.ChatChannel;
import cc.invictusgames.ilib.playersetting.impl.ILibSettings;
import cc.invictusgames.ilib.utils.CC;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 17.11.2020 / 20:26
 * iLib / cc.invictusgames.ilib.chat
 */

public class PublicChat extends ChatChannel {

    public PublicChat() {
        super("public",
                CC.RED + "Public",
                null,
                Arrays.asList("pc", "p", "pub", "global", "g", "gc"),
                '!',
                0);
    }

    @Override
    public boolean onChat(Player player, String message) {
        if (!ILibSettings.GLOBAL_CHAT.get(player)) {
            player.sendMessage(CC.RED + "You have the global chat disabled.");
            return false;
        }

        return true;
    }

    @Override
    public String getFormat(Player player, CommandSender sender) {
        if (sender instanceof Player && !ILibSettings.GLOBAL_CHAT.get((Player) sender))
            return null;

        return "<%1$s" + ChatColor.WHITE + "> %2$s";
    }
}

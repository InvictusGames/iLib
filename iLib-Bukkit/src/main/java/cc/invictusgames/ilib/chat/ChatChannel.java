package cc.invictusgames.ilib.chat;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 17.11.2020 / 20:21
 * iLib / cc.invictusgames.ilib.chat
 */

@Getter
public abstract class ChatChannel {

    private final String name;
    private final String displayName;
    private final String permission;
    private final List<String> aliases;
    private final char prefix;
    private final int priority;

    public ChatChannel(String name, String displayName, String permission, List<String> aliases,
                       char prefix, int priority) {
        this.name = name.toLowerCase();
        this.displayName = displayName;
        this.permission = permission;
        this.aliases = aliases;
        this.prefix = prefix;
        this.priority = priority;
    }

    public abstract String getFormat(Player player, CommandSender target);

    public abstract boolean onChat(Player player, String message);

    public void chat(Player player, String message) {
        if (!onChat(player, message))
            return;

        String format = getFormat(player, Bukkit.getConsoleSender());
        if (format != null)
            Bukkit.getConsoleSender().sendMessage(String.format(
                    format,
                    ChatService.getPrefixGetter().apply(player, Bukkit.getConsoleSender()),
                    message)
            );

        for (Player current : Bukkit.getOnlinePlayers()) {
            format = getFormat(player, current);
            if (format != null)
                current.sendMessage(String.format(
                        format,
                        ChatService.getPrefixGetter().apply(player, current),
                        message)
                );
        }
    }

    public boolean canAccess(CommandSender sender) {
        return permission == null || sender.hasPermission(permission);
    }

}

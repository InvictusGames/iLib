package cc.invictusgames.ilib.chat;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 17.11.2020 / 20:58
 * iLib / cc.invictusgames.ilib.chat
 */

public class ChatListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerLogin(AsyncPlayerPreLoginEvent event) {
        if (event.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED)
            return;

        ChatService.loadChatChannel(event.getUniqueId());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (ChatService.fromPlayer(event.getPlayer()) == null)
            ChatService.setChatChannel(
                    event.getPlayer(),
                    ChatService.getDefaultChannelProvider().getDefaultChannel(event.getPlayer()),
                    true
            );
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event)  {
        ChatService.removePlayer(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent event) {
        if (event.isCancelled())
            return;

        Player player = event.getPlayer();
        String message = event.getMessage();
        ChatChannel channel = ChatService.fromPlayer(player);

        ChatChannel fromPrefix = ChatService.fromPrefix(message.charAt(0));
        if (message.length() > 1
                && fromPrefix != null
                && (fromPrefix.canAccess(player))) {
            channel = fromPrefix;
            message = message.substring(1).trim();
        }

        if (message.isEmpty())
            return;

        if (!channel.canAccess(player)) {
            channel = ChatService.getDefaultChannelProvider().getDefaultChannel(player);
            ChatService.setChatChannel(player, channel, false);
        }

        event.setCancelled(true);
        channel.chat(player, message);
    }

}

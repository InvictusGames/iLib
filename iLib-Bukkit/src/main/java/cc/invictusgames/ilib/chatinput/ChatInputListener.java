package cc.invictusgames.ilib.chatinput;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ChatInputListener implements Listener {


    @EventHandler(priority = EventPriority.HIGH)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        ChatInput<?> input = ChatInput.getInput(player);
        if (input == null)
            return;

        event.setCancelled(true);
        input.handle(player, event.getMessage());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        ChatInput.clear(event.getPlayer().getUniqueId());
    }

}

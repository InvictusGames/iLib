package cc.invictusgames.ilib.chatinputold.listener;

import cc.invictusgames.ilib.chatinputold.ChatInput;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.server.ServerCommandEvent;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 02.03.2020 / 17:33
 * iLib / cc.invictusgames.ilib.chatinput
 */

public class ChatInputListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        if (ChatInput.getInputs().containsKey(event.getPlayer())) {
            if (ChatInput.getInputs().get(event.getPlayer()).callback(event.getMessage())) {
                ChatInput.getInputs().remove(event.getPlayer());
            }
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onServerCommandEvent(ServerCommandEvent event) {
        if (ChatInput.getInputs().containsKey(event.getSender())) {
            if (ChatInput.getInputs().get(event.getSender()).callback(event.getCommand())) {
                ChatInput.getInputs().remove(event.getSender());
            }
        }
    }

}

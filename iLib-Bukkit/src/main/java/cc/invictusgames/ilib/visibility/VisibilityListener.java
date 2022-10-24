package cc.invictusgames.ilib.visibility;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 24.10.2020 / 06:50
 * iLib / cc.invictusgames.ilib.visibility
 */

public class VisibilityListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        VisibilityService.update(event.getPlayer());
    }

}

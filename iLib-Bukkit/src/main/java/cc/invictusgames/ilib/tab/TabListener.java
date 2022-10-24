package cc.invictusgames.ilib.tab;

import cc.invictusgames.ilib.ILibBukkitPlugin;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public class TabListener implements Listener {

    private final TabService tabService;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTaskLater(ILibBukkitPlugin.getInstance(), () -> {
            if (player != null && player.isOnline())
                tabService.getTabs().put(player.getUniqueId(), new PlayerTab(tabService, player));
        }, 10L);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        tabService.getTabs().remove(event.getPlayer().getUniqueId());
    }

}

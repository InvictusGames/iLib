package cc.invictusgames.ilib.scoreboard;

import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 21.10.2020 / 08:51
 * iLib / cc.invictusgames.ilib.scoreboard
 */

@RequiredArgsConstructor
public class ScoreboardListener implements Listener {

    private final ScoreboardService service;

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        ScoreboardService.SCOREBOARDS.put(event.getPlayer().getUniqueId(), new PlayerScoreboard(service,
                event.getPlayer().getUniqueId()));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        ScoreboardService.SCOREBOARDS.remove(event.getPlayer().getUniqueId());
    }

}

package cc.invictusgames.ilib.uuid;

import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 25.10.2020 / 05:09
 * iLib / cc.invictusgames.ilib.uuid
 */

@RequiredArgsConstructor
public class UUIDCacheListener implements Listener {

    private final UUIDCache uuidCache;

    @EventHandler
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        if ((UUIDCache.getUuid(event.getName()) == null) || (UUIDCache.getName(event.getUniqueId()) == null)) {
            uuidCache.update(event.getUniqueId(), event.getName(), false);
            return;
        }

        if (!UUIDCache.getName(event.getUniqueId()).equals(event.getName())) {
            uuidCache.update(event.getUniqueId(), event.getName(), false);
        }
    }

}

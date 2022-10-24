package cc.invictusgames.ilib.uuid;

import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 25.10.2020 / 05:09
 * iLib / cc.invictusgames.ilib.uuid
 */

@RequiredArgsConstructor
public class UUIDCacheListener implements Listener {

    private final UUIDCache uuidCache;

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerPreLogin(LoginEvent event) {
        if ((UUIDCache.getUuid(event.getConnection().getName()) == null)
                || (UUIDCache.getName(event.getConnection().getUniqueId()) == null)) {
            uuidCache.update(event.getConnection().getUniqueId(), event.getConnection().getName(), true);
            return;
        }

        if (!UUIDCache.getName(event.getConnection().getUniqueId()).equals(event.getConnection().getName())) {
            uuidCache.update(event.getConnection().getUniqueId(), event.getConnection().getName(), true);
        }
    }

}

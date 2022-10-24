package cc.invictusgames.ilib.utils.logging.listener;

import cc.invictusgames.ilib.ILib;
import cc.invictusgames.ilib.utils.logging.SimpleBukkitLogger;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.UUID;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 18.06.2021 / 18:30
 * iLib / cc.invictusgames.ilib.utils.logging
 */

@RequiredArgsConstructor
public class LogLevelListener implements Listener {

    private final ILib instance;

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();
        String key = "logging:" + uuid.toString() + ":level";
        instance.getRedisService().executeBackendCommand(redis -> {
            if (redis.exists(key))
                SimpleBukkitLogger.LOG_LEVEL_MAP.put(uuid, Integer.valueOf(redis.get(key)));
            return null;
        });
    }

}

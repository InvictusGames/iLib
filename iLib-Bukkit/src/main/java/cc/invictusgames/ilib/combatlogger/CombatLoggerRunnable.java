package cc.invictusgames.ilib.combatlogger;

import cc.invictusgames.ilib.combatlogger.event.CombatLoggerDespawnEvent;
import lombok.RequiredArgsConstructor;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 05.07.2020 / 01:27
 * iLib / cc.invictusgames.ilib.combatlogger
 */

@RequiredArgsConstructor
public class CombatLoggerRunnable extends BukkitRunnable {

    private final CombatLogger logger;

    @Override
    public void run() {
        logger.despawn(CombatLoggerDespawnEvent.Cause.TIME);
    }
}

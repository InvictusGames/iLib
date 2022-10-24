package cc.invictusgames.ilib.combatlogger.event;


import cc.invictusgames.ilib.combatlogger.CombatLogger;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 05.07.2020 / 01:20
 * iLib / cc.invictusgames.ilib.combatlogger
 */

public class CombatLoggerDeathEvent extends Event {
    private static final HandlerList handlerList = new HandlerList();

    private final CombatLogger logger;
    private final Player killer;

    public CombatLoggerDeathEvent(CombatLogger logger, Player killer) {
        this.logger = logger;
        this.killer = killer;
    }

    public CombatLogger getLogger() {
        return logger;
    }

    public Player getKiller() {
        return killer;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

}

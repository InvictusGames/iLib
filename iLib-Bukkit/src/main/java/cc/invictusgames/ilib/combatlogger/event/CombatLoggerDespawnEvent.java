package cc.invictusgames.ilib.combatlogger.event;


import cc.invictusgames.ilib.combatlogger.CombatLogger;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 05.07.2020 / 01:20
 * iLib / cc.invictusgames.ilib.combatlogger
 */

public class CombatLoggerDespawnEvent extends Event {
    private static final HandlerList handlerList = new HandlerList();

    private final CombatLogger logger;
    private final Cause cause;

    public CombatLoggerDespawnEvent(CombatLogger logger, Cause cause) {
        this.logger = logger;
        this.cause = cause;
    }

    public CombatLogger getLogger() {
        return logger;
    }

    public Cause getCause() {
        return cause;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public enum Cause {
        JOIN, TIME, COMMAND, DISABLED
    }

}

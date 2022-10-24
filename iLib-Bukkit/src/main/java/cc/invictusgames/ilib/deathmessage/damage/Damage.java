package cc.invictusgames.ilib.deathmessage.damage;

import cc.invictusgames.ilib.deathmessage.DeathMessageService;
import cc.invictusgames.ilib.utils.CC;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 02.07.2020 / 21:24
 * iLib / cc.invictusgames.ilib.deathmessage.damage
 */

@Getter
public abstract class Damage {

    @Setter
    @Getter
    protected static String color = CC.YELLOW;

    private final UUID damaged;
    private final double damage;
    private final long time;

    public Damage(UUID damaged, double damage) {
        this.damaged = damaged;
        this.damage = damage;
        this.time = System.currentTimeMillis();
    }

    public long getTimeAgo() {
        return System.currentTimeMillis() - time;
    }

    public abstract String getDeathMessage(UUID checkFor);

    public abstract String getExtraInformation();

    public static String formatName(UUID player) {
        return DeathMessageService.getSettings().formatName(player);
    }

    public static String formatName(UUID player, UUID checkFor) {
        return DeathMessageService.getSettings().formatName(player, checkFor);
    }
}

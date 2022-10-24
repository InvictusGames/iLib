package cc.invictusgames.ilib.deathmessage.damage;

import lombok.Getter;

import java.util.UUID;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 02.07.2020 / 21:30
 * iLib / cc.invictusgames.ilib.deathmessage.damage
 */

@Getter
public abstract class PlayerDamage extends Damage {

    private final UUID damager;

    public PlayerDamage(UUID damaged, double damage, UUID damager) {
        super(damaged, damage);
        this.damager = damager;
    }
}

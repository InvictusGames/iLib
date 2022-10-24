package cc.invictusgames.ilib.deathmessage.damage;

import cc.invictusgames.ilib.utils.CC;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.EntityType;

import java.util.UUID;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 02.07.2020 / 21:30
 * iLib / cc.invictusgames.ilib.deathmessage.damage
 */

@Getter
public abstract class MobDamage extends Damage {

    @Setter
    protected static String entityColor = CC.RED;

    private final EntityType entityType;

    public MobDamage(UUID damaged, double damage, EntityType entityType) {
        super(damaged, damage);
        this.entityType = entityType;
    }
}

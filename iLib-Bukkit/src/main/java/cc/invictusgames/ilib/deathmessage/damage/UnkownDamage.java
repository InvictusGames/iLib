package cc.invictusgames.ilib.deathmessage.damage;

import cc.invictusgames.ilib.utils.CC;

import java.util.UUID;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 02.07.2020 / 21:32
 * iLib / cc.invictusgames.ilib.deathmessage.damage
 */

public class UnkownDamage extends Damage {

    public UnkownDamage(UUID damaged, double damage) {
        super(damaged, damage);
    }

    @Override
    public String getDeathMessage(UUID checkFor) {
        return Damage.formatName(getDamaged(), checkFor) + color + " died.";
    }

    @Override
    public String getExtraInformation() {
        return "";
    }


}

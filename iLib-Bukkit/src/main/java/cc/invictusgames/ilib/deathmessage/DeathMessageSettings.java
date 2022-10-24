package cc.invictusgames.ilib.deathmessage;

import cc.invictusgames.ilib.utils.CC;
import cc.invictusgames.ilib.uuid.UUIDCache;
import org.bukkit.Bukkit;

import java.util.UUID;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 02.07.2020 / 21:18
 * iLib / cc.invictusgames.ilib.deathmessage
 */

public interface DeathMessageSettings {

    DeathMessageSettings DEFAULT = new DeathMessageSettings() {
        @Override
        public String formatName(UUID player, UUID checkFor) {
            return (player == checkFor ? CC.GREEN : CC.RED) +
                    (Bukkit.getPlayer(player) != null ? Bukkit.getPlayer(player).getName() : UUIDCache.getName(player));
        }

        @Override
        public boolean shouldShow(UUID player, UUID killer, UUID checkFor) {
            return true;
        }
    };

    String formatName(UUID player, UUID checkFor);

    boolean shouldShow(UUID player, UUID killer, UUID checkFor);

    default String formatName(UUID player) {
        return formatName(player, null);
    }

    default boolean hideWeapons() {
        return false;
    }

    default boolean coloredWeaponNames() {
        return false;
    }
}

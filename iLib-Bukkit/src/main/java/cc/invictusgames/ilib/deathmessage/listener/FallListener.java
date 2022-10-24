package cc.invictusgames.ilib.deathmessage.listener;

import cc.invictusgames.ilib.deathmessage.DeathMessageService;
import cc.invictusgames.ilib.deathmessage.damage.Damage;
import cc.invictusgames.ilib.deathmessage.damage.PlayerDamage;
import cc.invictusgames.ilib.utils.CC;
import cc.invictusgames.ilib.uuid.UUIDCache;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 02.07.2020 / 21:50
 * iLib / cc.invictusgames.ilib.deathmessage.listener
 */

public class FallListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player) || event.isCancelled()) {
            return;
        }

        if (event.getCause() != EntityDamageEvent.DamageCause.FALL) {
            return;
        }

        Player player = (Player) event.getEntity();
        PlayerDamage playerDamage = DeathMessageService.getLastPlayerDamage(player.getUniqueId());

        if (playerDamage != null && playerDamage.getTimeAgo() <= TimeUnit.SECONDS.toMillis(30)) {
            DeathMessageService.addDamage(player.getUniqueId(), new PlayerFallDamage(
                    player.getUniqueId(),
                    event.getDamage(),
                    playerDamage.getDamager()
            ));
        } else {
            DeathMessageService.addDamage(player.getUniqueId(), new FallDamage(player.getUniqueId(),
                    event.getDamage()));
        }
    }

    public static class FallDamage extends Damage {

        public FallDamage(UUID damaged, double damage) {
            super(damaged, damage);
        }

        @Override
        public String getDeathMessage(UUID checkFor) {
            return Damage.formatName(getDamaged(), checkFor) + color + " hit the ground too hard.";
        }

        @Override
        public String getExtraInformation() {
            return "";
        }
    }

    public static class PlayerFallDamage extends PlayerDamage {

        public PlayerFallDamage(UUID damaged, double damage, UUID damager) {
            super(damaged, damage, damager);
        }

        @Override
        public String getDeathMessage(UUID checkFor) {
            return Damage.formatName(getDamaged(), checkFor) + color + " hit the ground too hard thanks to "
                    + Damage.formatName(getDamager(), checkFor) + color + ".";
        }

        @Override
        public String getExtraInformation() {
            return "(damager=" + UUIDCache.getName(getDamager()) + ")";
        }
    }
}

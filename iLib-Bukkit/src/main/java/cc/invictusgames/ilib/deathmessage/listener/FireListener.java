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
 * 02.07.2020 / 22:11
 * iLib / cc.invictusgames.ilib.deathmessage.listener
 */

public class FireListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player) || event.isCancelled()) {
            return;
        }

        if (event.getCause() != EntityDamageEvent.DamageCause.FIRE_TICK
                && event.getCause() != EntityDamageEvent.DamageCause.FIRE
                && event.getCause() != EntityDamageEvent.DamageCause.LAVA) {
            return;
        }

        Player player = (Player) event.getEntity();
        PlayerDamage playerDamage = DeathMessageService.getLastPlayerDamage(player.getUniqueId());

        if (playerDamage != null && playerDamage.getTimeAgo() <= TimeUnit.SECONDS.toMillis(30)) {
            DeathMessageService.addDamage(player.getUniqueId(), new PlayerFireDamage(
                    player.getUniqueId(),
                    event.getDamage(),
                    playerDamage.getDamager(),
                    event.getCause() == EntityDamageEvent.DamageCause.LAVA
            ));
        } else {
            DeathMessageService.addDamage(player.getUniqueId(), new FireDamage(
                    player.getUniqueId(),
                    event.getDamage(),
                    event.getCause() == EntityDamageEvent.DamageCause.LAVA
            ));
        }
    }

    public static class FireDamage extends Damage {

        private final boolean lava;

        public FireDamage(UUID damaged, double damage, boolean lava) {
            super(damaged, damage);
            this.lava = lava;
        }

        @Override
        public String getDeathMessage(UUID checkFor) {
            return Damage.formatName(getDamaged(), checkFor) + color +
                    (lava ? " tried to swim in lava." : " burned to death.");
        }

        @Override
        public String getExtraInformation() {
            return "(lava=" + lava + ")";
        }
    }

    public static class PlayerFireDamage extends PlayerDamage {

        private final boolean lava;

        public PlayerFireDamage(UUID damaged, double damage, UUID damager, boolean lava) {
            super(damaged, damage, damager);
            this.lava = lava;
        }

        @Override
        public String getDeathMessage(UUID checkFor) {
            return Damage.formatName(getDamaged(), checkFor)
                    + color + (lava ? " took a hot bath" : " burned to death")
                    + " thanks to "
                    + Damage.formatName(getDamager(), checkFor) + color + ".";
        }

        @Override
        public String getExtraInformation() {
            return "(damager=" + UUIDCache.getName(getDamager()) + ", lava=" + lava + ")";
        }
    }

}

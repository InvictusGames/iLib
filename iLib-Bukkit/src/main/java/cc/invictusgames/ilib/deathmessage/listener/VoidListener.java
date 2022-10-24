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
 * 02.07.2020 / 22:06
 * iLib / cc.invictusgames.ilib.deathmessage.listener
 */

public class VoidListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player) || event.isCancelled()) {
            return;
        }

        if (event.getCause() != EntityDamageEvent.DamageCause.VOID) {
            return;
        }

        Player player = (Player) event.getEntity();
        PlayerDamage playerDamage = DeathMessageService.getLastPlayerDamage(player.getUniqueId());

        if (playerDamage != null && playerDamage.getTimeAgo() <= TimeUnit.SECONDS.toMillis(30)) {
            DeathMessageService.addDamage(player.getUniqueId(), new PlayerVoidDamage(
                    player.getUniqueId(),
                    event.getDamage(),
                    playerDamage.getDamager()
            ));
        } else {
            DeathMessageService.addDamage(player.getUniqueId(), new VoidDamage(player.getUniqueId(),
                    event.getDamage()));
        }
    }

    public static class VoidDamage extends Damage {

        public VoidDamage(UUID damaged, double damage) {
            super(damaged, damage);
        }

        @Override
        public String getDeathMessage(UUID checkFor) {
            return Damage.formatName(getDamaged(), checkFor) + color + " fell into the void.";
        }

        @Override
        public String getExtraInformation() {
            return "";
        }
    }

    public static class PlayerVoidDamage extends PlayerDamage {

        public PlayerVoidDamage(UUID damaged, double damage, UUID damager) {
            super(damaged, damage, damager);
        }

        @Override
        public String getDeathMessage(UUID checkFor) {
            return Damage.formatName(getDamaged(), checkFor) + color + " fell into the void thanks to "
                    + Damage.formatName(getDamager(), checkFor) + color + ".";
        }

        @Override
        public String getExtraInformation() {
            return "(damager=" + UUIDCache.getName(getDamager()) + ")";
        }
    }
}

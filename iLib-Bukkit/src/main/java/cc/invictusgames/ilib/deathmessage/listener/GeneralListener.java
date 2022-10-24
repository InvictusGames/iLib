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
 * 02.07.2020 / 22:40
 * iLib / cc.invictusgames.ilib.deathmessage.listener
 */

public class GeneralListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player) || event.isCancelled()) {
            return;
        }

        Player player = (Player) event.getEntity();
        String message;
        switch (event.getCause()) {
            case SUFFOCATION:
                message = "suffocated";
                break;
            case DROWNING:
                message = "drowned";
                break;
            case STARVATION:
                message = "starved to death";
                break;
            case POISON:
                message = "was poisoned";
                break;
            case WITHER:
                message = "withered away";
                break;
            case CONTACT:
                message = "was pricked to death";
                break;
            case FALLING_BLOCK:
                message = "got hit by a block";
                break;
            case ENTITY_EXPLOSION:
            case BLOCK_EXPLOSION:
                message = "blew up";
                break;
            default:
                return;
        }

        PlayerDamage playerDamage = DeathMessageService.getLastPlayerDamage(player.getUniqueId());
        if (playerDamage != null && playerDamage.getTimeAgo() <= TimeUnit.SECONDS.toMillis(30)) {
            DeathMessageService.addDamage(player.getUniqueId(), new PlayerGeneralDamage(
                    player.getUniqueId(),
                    event.getDamage(),
                    playerDamage.getDamager(),
                    message
            ));
        } else {
            DeathMessageService.addDamage(player.getUniqueId(), new GeneralDamage(player.getUniqueId(),
                    event.getDamage(), message));
        }
    }

    public static class GeneralDamage extends Damage {

        private final String message;

        public GeneralDamage(UUID damaged, double damage, String message) {
            super(damaged, damage);
            this.message = message;
        }

        @Override
        public String getDeathMessage(UUID checkFor) {
            return Damage.formatName(getDamaged(), checkFor)
                    + color + " " + message + color + ".";
        }

        @Override
        public String getExtraInformation() {
            return "(message=" + message + ")";
        }
    }

    public static class PlayerGeneralDamage extends PlayerDamage {

        private final String message;

        public PlayerGeneralDamage(UUID damaged, double damage, UUID damager, String message) {
            super(damaged, damage, damager);
            this.message = message;
        }

        @Override
        public String getDeathMessage(UUID checkFor) {
            return Damage.formatName(getDamaged(), checkFor)
                    + color + " " + message + color + " while fighting "
                    + Damage.formatName(getDamager(), checkFor) + color + ".";
        }

        @Override
        public String getExtraInformation() {
            return "(damager=" + UUIDCache.getName(getDamager()) + ", message=" + message + ")";
        }
    }

}

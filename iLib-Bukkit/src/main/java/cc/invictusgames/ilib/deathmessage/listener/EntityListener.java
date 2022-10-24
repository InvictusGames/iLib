package cc.invictusgames.ilib.deathmessage.listener;

import cc.invictusgames.ilib.deathmessage.DeathMessageService;
import cc.invictusgames.ilib.deathmessage.damage.Damage;
import cc.invictusgames.ilib.deathmessage.damage.MobDamage;
import cc.invictusgames.ilib.utils.CC;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.UUID;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 02.07.2020 / 22:17
 * iLib / cc.invictusgames.ilib.deathmessage.listener
 */

public class EntityListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || event.isCancelled()) {
            return;
        }

        Player player = (Player) event.getEntity();

        if (!(event.getDamager() instanceof LivingEntity) || event.getDamager() instanceof Player) {
            return;
        }

        DeathMessageService.addDamage(player.getUniqueId(), new EntityDamage(
                player.getUniqueId(),
                event.getDamage(),
                event.getDamager()
        ));
    }

    public static class EntityDamage extends MobDamage {

        public EntityDamage(UUID damaged, double damage, Entity entity) {
            super(damaged, damage, entity.getType());
        }

        @Override
        public String getDeathMessage(UUID checkFor) {
            return Damage.formatName(getDamaged(), checkFor) + color + " was slain by "
                    + (getEntityType().getName() == null ? "an unkown entity."
                    : "a " + entityColor + getEntityType().getName() + color + ".");
        }

        @Override
        public String getExtraInformation() {
            return "(entity=" + getEntityType().getName() + ")";
        }
    }
}

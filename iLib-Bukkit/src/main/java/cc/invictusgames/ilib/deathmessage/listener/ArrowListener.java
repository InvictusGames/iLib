package cc.invictusgames.ilib.deathmessage.listener;

import cc.invictusgames.ilib.ILibBukkitPlugin;
import cc.invictusgames.ilib.deathmessage.DeathMessageService;
import cc.invictusgames.ilib.deathmessage.damage.Damage;
import cc.invictusgames.ilib.deathmessage.damage.MobDamage;
import cc.invictusgames.ilib.deathmessage.damage.PlayerDamage;
import cc.invictusgames.ilib.utils.CC;
import cc.invictusgames.ilib.uuid.UUIDCache;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.UUID;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 02.07.2020 / 22:26
 * iLib / cc.invictusgames.ilib.deathmessage.listener
 */

public class ArrowListener implements Listener {

    @EventHandler
    public void onEntityShootBowEvent(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player) {
            event.getProjectile().setMetadata("ShotFromLocation",
                    new FixedMetadataValue(ILibBukkitPlugin.getInstance(), event.getProjectile().getLocation()));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || event.isCancelled()) {
            return;
        }

        Player player = (Player) event.getEntity();

        if (!(event.getDamager() instanceof Arrow)) {
            return;
        }

        Arrow arrow = (Arrow) event.getDamager();
        if (arrow.getShooter() == null) {
            DeathMessageService.addDamage(player.getUniqueId(), new ArrowDamage(player.getUniqueId(),
                    event.getDamage()));
            return;
        }

        if (arrow.getShooter() instanceof Player) {
            Player damager = (Player) arrow.getShooter();
            for (MetadataValue value : arrow.getMetadata("ShotFromLocation")) {
                Location from = (Location) value.value();
                double distance = from.distance(player.getLocation());
                DeathMessageService.addDamage(player.getUniqueId(), new PlayerArrowDamage(
                        player.getUniqueId(),
                        event.getDamage(),
                        damager.getUniqueId(),
                        distance
                ));
            }
            return;
        }

        if (arrow.getShooter() instanceof Entity) {
            Entity damager = (Entity) arrow.getShooter();
            DeathMessageService.addDamage(player.getUniqueId(), new EntityArrowDamage(player.getUniqueId(),
                    event.getDamage(), damager.getType()));
            return;
        }

        DeathMessageService.addDamage(player.getUniqueId(), new ArrowDamage(player.getUniqueId(), event.getDamage()));
    }

    public static class ArrowDamage extends Damage {

        public ArrowDamage(UUID damaged, double damage) {
            super(damaged, damage);
        }

        @Override
        public String getDeathMessage(UUID checkFor) {
            return Damage.formatName(getDamaged(), checkFor) + color + " was shot.";
        }

        @Override
        public String getExtraInformation() {
            return "";
        }
    }

    public static class EntityArrowDamage extends MobDamage {

        public EntityArrowDamage(UUID damaged, double damage, EntityType entityType) {
            super(damaged, damage, entityType);
        }

        @Override
        public String getDeathMessage(UUID checkFor) {
            return Damage.formatName(getDamaged(), checkFor) + color + " was shot by "
                    + (getEntityType().getName() == null ? "an unkown entity."
                    : "a " + entityColor + getEntityType().getName() + color + ".");
        }

        @Override
        public String getExtraInformation() {
            return "(entity=" + getEntityType().getName() + ")";
        }
    }

    public static class PlayerArrowDamage extends PlayerDamage {

        @Setter
        protected static String distanceColor = CC.BLUE;

        private final double distance;

        public PlayerArrowDamage(UUID damaged, double damage, UUID damager, double distance) {
            super(damaged, damage, damager);
            this.distance = distance;
        }

        @Override
        public String getDeathMessage(UUID checkFor) {
            return Damage.formatName(getDamaged(), checkFor) + color + " was shot by "
                    + Damage.formatName(getDamager(), checkFor)
                    + color + " from " + distanceColor + (int) distance + " blocks" + color + ".";
        }

        @Override
        public String getExtraInformation() {
            return "(damager=" + UUIDCache.getName(getDamager()) + ", distance=" + (int) distance + ")";
        }
    }

}

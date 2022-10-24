package cc.invictusgames.ilib.deathmessage.listener;

import cc.invictusgames.ilib.deathmessage.DeathMessageService;
import cc.invictusgames.ilib.deathmessage.damage.Damage;
import cc.invictusgames.ilib.deathmessage.damage.PlayerDamage;
import cc.invictusgames.ilib.deathmessage.killmessage.KillMessage;
import cc.invictusgames.ilib.playersetting.impl.ILibSettings;
import cc.invictusgames.ilib.utils.CC;
import cc.invictusgames.ilib.utils.ItemUtils;
import cc.invictusgames.ilib.uuid.UUIDCache;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.UUID;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 02.07.2020 / 21:37
 * iLib / cc.invictusgames.ilib.deathmessage.listener
 */

public class PvPListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || event.isCancelled()) {
            return;
        }

        Player player = (Player) event.getEntity();
        if (event.getDamager() instanceof Player) {
            DeathMessageService.addDamage(player.getUniqueId(), new PvPDamage(
                    player.getUniqueId(),
                    event.getDamage(),
                    (Player) event.getDamager()
            ));
            return;
        }

        if (event.getDamager() instanceof Projectile) {
            Projectile projectile = (Projectile) event.getDamager();
            if (!(projectile.getShooter() instanceof Player) || projectile instanceof Arrow) {
                return;
            }

            DeathMessageService.addDamage(player.getUniqueId(), new PvPDamage(
                    player.getUniqueId(),
                    event.getDamage(),
                    (Player) projectile.getShooter()
            ));
        }
    }

    public static class PvPDamage extends PlayerDamage {

        @Setter
        protected static String itemColor = CC.RED;

        private final String itemName;

        public PvPDamage(UUID damaged, double damage, Player damager) {
            super(damaged, damage, damager.getUniqueId());

            if (damager.getItemInHand() == null || damager.getItemInHand().getType().equals(Material.AIR)) {
                this.itemName = "their fists";
            } else {
                if (DeathMessageService.getSettings().coloredWeaponNames()) {
                    this.itemName = ItemUtils.getName(damager.getItemInHand());
                } else {
                    this.itemName = CC.strip(ItemUtils.getName(damager.getItemInHand()));
                }
            }
        }

        @Override
        public String getDeathMessage(UUID checkFor) {
            /*return Damage.formatName(getDamaged(), checkFor) +
                    color + " was slain by " +
                    Damage.formatName(getDamager(), checkFor)
                    + color + (DeathMessageService.getSettings().hideWeapons() ? ""
                    : " using " + itemColor + itemName.trim())
                    + color + ".";*/
            KillMessage killMessage = KillMessage.DEFAULT;
            Player player = Bukkit.getPlayer(getDamager());
            if (player != null)
                killMessage = ILibSettings.KILL_MESSAGE.get(player);

            return killMessage.formatPvP(
                    Damage.formatName(getDamaged(), checkFor),
                    Damage.formatName(getDamager(), checkFor)
            ) + color + (DeathMessageService.getSettings().hideWeapons()
                    ? "" : " using " + itemColor + itemName.trim())
                    + color + ".";
        }

        @Override
        public String getExtraInformation() {
            return "(damager=" + UUIDCache.getName(getDamager()) + ", item=" + CC.strip(itemName) + ")";
        }
    }

}

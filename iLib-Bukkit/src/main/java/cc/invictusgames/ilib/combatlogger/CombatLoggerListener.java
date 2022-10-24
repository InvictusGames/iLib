package cc.invictusgames.ilib.combatlogger;

import cc.invictusgames.ilib.combatlogger.event.CombatLoggerDespawnEvent;
import cc.invictusgames.ilib.utils.CC;
import cc.invictusgames.ilib.utils.ChatMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.metadata.MetadataValue;

import java.util.Arrays;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 05.07.2020 / 01:37
 * iLib / cc.invictusgames.ilib.combatlogger
 */

public class CombatLoggerListener implements Listener {

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        CombatLogger logger = CombatLogger.getLoggerMap().get(player.getUniqueId());
        if (logger == null || logger.getSpawnedEntity().isDead() || !logger.getSpawnedEntity().isValid()) {
            return;
        }

        player.setHealth(logger.getSpawnedEntity().getHealth());
        player.teleport(logger.getSpawnedEntity());
        player.setFallDistance(logger.getSpawnedEntity().getFallDistance());
        player.setRemainingAir(logger.getSpawnedEntity().getRemainingAir());
        logger.despawn(CombatLoggerDespawnEvent.Cause.JOIN);
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        if (Arrays.stream(event.getChunk().getEntities())
                .anyMatch(entity -> entity.hasMetadata(CombatLogger.METADATA)))
            event.setCancelled(true);
    }

    @EventHandler
    public void onEntityPortal(EntityPortalEvent event) {
        if (event.getEntity().hasMetadata(CombatLogger.METADATA))
            event.setCancelled(true);
    }

    @EventHandler
    public void onEntityInteract(EntityInteractEvent event) {
        if (event.getEntity().hasMetadata(CombatLogger.METADATA))
            event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (Bukkit.getPluginManager().getPlugin("Invictus").isEnabled())
            return;

        if (event.getRightClicked().hasMetadata(CombatLogger.METADATA)) {
            event.setCancelled(true);

            if (event.getPlayer().hasPermission("combatlogger.despawn")) {
                for (MetadataValue value : event.getRightClicked().getMetadata("CombatLogger")) {
                    ChatMessage message = new ChatMessage("Click to here despawn this ").color(ChatColor.GOLD);
                    message.add(event.getRightClicked().getType().getName())
                            .runCommand("/despawncombatlogger " + value.asString())
                            .hoverText(CC.YELLOW + "Click here to despawn.")
                            .color(ChatColor.WHITE);
                    message.add(".").color(ChatColor.GOLD);
                    message.send(event.getPlayer());
                }
            }
        }
    }

}

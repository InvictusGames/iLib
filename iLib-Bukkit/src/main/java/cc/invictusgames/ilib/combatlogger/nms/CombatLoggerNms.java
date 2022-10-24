package cc.invictusgames.ilib.combatlogger.nms;

import cc.invictusgames.ilib.combatlogger.CombatLogger;
import cc.invictusgames.ilib.combatlogger.event.CombatLoggerDeathEvent;
import cc.invictusgames.ilib.utils.callback.TypeCallable;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_8_R3.event.CraftEventFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 07.03.2021 / 06:37
 * iLib / cc.invictusgames.ilib.combatlogger.nms
 */

public class CombatLoggerNms {

    private static final Function<Double, @Nullable Double> DAMAGE_FUNCTION = (d -> 0D);

    public static boolean damageEntity(Entity entity, PlayerNmsResult result, DamageSource damagesource, float damage) {
        if (result == null)
            return true;

        EntityPlayer entityPlayer = result.getEntityPlayer();
        if (entityPlayer == null)
            return true;

        entityPlayer.setPosition(entity.locX, entity.locY, entity.locZ);
        EntityDamageEvent event = CraftEventFactory.handleLivingEntityDamageEvent(
                entityPlayer,
                damagesource,
                damage,
                0D,
                0D,
                0D,
                0D,
                0D,
                0D,
                DAMAGE_FUNCTION::apply,
                DAMAGE_FUNCTION::apply,
                DAMAGE_FUNCTION::apply,
                DAMAGE_FUNCTION::apply,
                DAMAGE_FUNCTION::apply,
                DAMAGE_FUNCTION::apply
        );

        return !event.isCancelled();
    }

    public static void die(EntityLiving entity, CombatTracker combatTracker,
                           PlayerNmsResult result, DamageSource damageSource, TypeCallable<DamageSource> callable) {
        if (result == null)
            return;

        Player player = result.getPlayer();
        List<ItemStack> drops = new ArrayList<>();
        boolean keepInventory = entity.world.getGameRules().getBoolean("keepInventory");

        if (!keepInventory)
            drops.addAll(Stream.of(player.getInventory().getContents(), player.getInventory().getArmorContents())
                    .flatMap(Stream::of)
                    .filter(item -> item != null && item.getType() != Material.AIR)
                    .collect(Collectors.toList()));

        String deathMessage = combatTracker.b().c();
        EntityPlayer entityPlayer = result.getEntityPlayer();
        entityPlayer.combatTracker = combatTracker;

        PlayerDeathEvent deathEvent = CraftEventFactory.callPlayerDeathEvent(entityPlayer, drops, deathMessage,
                keepInventory);
        deathMessage = deathEvent.getDeathMessage();
        if (deathMessage != null && !deathMessage.isEmpty())
            Bukkit.broadcastMessage(deathMessage);

        callable.callback(damageSource);

        CombatLoggerDeathEvent loggerDeathEvent = new CombatLoggerDeathEvent(
                CombatLogger.getLoggerMap().get(entity.getUniqueID()),
                ((CraftLivingEntity) entity.getBukkitEntity()).getKiller());
        Bukkit.getPluginManager().callEvent(loggerDeathEvent);

        CombatLogger.getLoggerMap().remove(player.getUniqueId());
        CombatLogger.getLoggerMap().remove(entity.getUniqueID());

        if (!deathEvent.getKeepInventory()) {
            player.getInventory().setContents(new ItemStack[36]);
            player.getInventory().setArmorContents(new ItemStack[4]);
        }

        entityPlayer.setLocation(entity.locX, entity.locY, entity.locZ, entity.yaw, entity.pitch);
        entityPlayer.setHealth(0f);
        player.saveData();
        ((WorldServer) entity.world).getTracker().untrackEntity(entity);
        if (!player.isOnline())
            ((WorldServer) entityPlayer.world).getTracker().untrackEntity(entityPlayer);
    }

}

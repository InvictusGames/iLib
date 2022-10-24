package cc.invictusgames.ilib.hologram.listener;

import cc.invictusgames.ilib.ILibBukkit;
import cc.invictusgames.ilib.ILibBukkitPlugin;
import cc.invictusgames.ilib.hologram.Hologram;
import cc.invictusgames.ilib.hologram.HologramService;
import cc.invictusgames.ilib.hologram.clickhandler.HologramClickHandler;
import cc.invictusgames.ilib.protocol.TinyProtocol;
import cc.invictusgames.ilib.timebased.TimeBasedContainer;
import cc.invictusgames.ilib.utils.ReflectionUtil;
import cc.invictusgames.marvelspigot.handler.SimpleMovementHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.world.WorldLoadEvent;

import java.lang.reflect.Field;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class HologramListener implements Listener, SimpleMovementHandler, TinyProtocol {

    private static final Field USE_ENTITY_A = ReflectionUtil.getField(PacketPlayInUseEntity.class, "a");

    private final ILibBukkit instance;

    private final TimeBasedContainer<UUID> clickCooldown = new TimeBasedContainer<>(500, TimeUnit.MILLISECONDS);

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTaskLater(ILibBukkitPlugin.getInstance(), () -> {
            for (Hologram hologram : HologramService.getHolograms()) {
                if (hologram.isInDistance(player))
                    hologram.update(player);
            }
        }, 20L);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        for (Hologram hologram : HologramService.getHolograms()) {
            if (hologram.isInDistance(player))
                hologram.spawn(player);
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTaskLater(ILibBukkitPlugin.getInstance(), () -> {
            for (Hologram hologram : HologramService.getHolograms()) {
                if (hologram.isInDistance(player))
                    hologram.update(player);
            }
        }, 10L);
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        instance.getHologramService().loadWorld(event.getWorld());
    }

    @Override
    public boolean handleUpdateLocation(Player player, Location from, Location to) {
        if (from.getBlockX() == to.getBlockX() && from.getBlockZ() == to.getBlockZ())
            return false;

        for (Hologram hologram : HologramService.getHolograms()) {
            if (!hologram.getCurrentWatchers().contains(player.getUniqueId()) && hologram.isInDistance(player)) {
                hologram.spawn(player);
                return false;
            }

            if (hologram.getCurrentWatchers().contains(player.getUniqueId()) && !hologram.isInDistance(player))
                hologram.destroy(player);
        }

        return false;
    }

    @Override
    public Packet handleIncomingPacket(Player player, Packet packet, ChannelHandlerContext ctx) {
        if (!(packet instanceof PacketPlayInUseEntity))
            return packet;

        PacketPlayInUseEntity useEntity = (PacketPlayInUseEntity) packet;
        int id = ReflectionUtil.getFieldValue(USE_ENTITY_A, useEntity);

        for (Hologram hologram : HologramService.getHolograms()) {
            if (!hologram.isEntityId(player, id))
                continue;

            if (hologram.getClickHandler() == null)
                continue;

            if (clickCooldown.contains(player.getUniqueId()))
                continue;

            hologram.getClickHandler().click(
                    player,
                    hologram,
                    //HologramClickHandler.ClickType.RIGHT_CLICK
                    useEntity.getAction() == PacketPlayInUseEntity.EnumEntityUseAction.ATTACK
                            ? HologramClickHandler.ClickType.LEFT_CLICK
                            : HologramClickHandler.ClickType.RIGHT_CLICK
            );
            clickCooldown.add(player.getUniqueId());
            return null;
        }

        return packet;
    }

    @Override
    public Packet handleOutgoingPacket(Player player, Packet packet, ChannelHandlerContext ctx) {
        return packet;
    }
}

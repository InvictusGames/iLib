package cc.invictusgames.ilib.hologram.packet.impl;

import cc.invictusgames.ilib.hologram.HologramLine;
import cc.invictusgames.ilib.hologram.packet.HologramPacketProvider;
import cc.invictusgames.ilib.placeholder.PlaceholderService;
import cc.invictusgames.ilib.utils.EntityUtils;
import cc.invictusgames.ilib.utils.ReflectionUtil;
import net.minecraft.server.v1_8_R3.DataWatcher;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class Hologram1_8Provider implements HologramPacketProvider {

    @Override
    public void sendSpawnPackets(Player player, Location location, HologramLine line) {
        if (line.getArmorStandId() == 0)
            line.setArmorStandId(EntityUtils.fakeEntityId());

        PacketPlayOutSpawnEntityLiving armorStand = new PacketPlayOutSpawnEntityLiving();
        DataWatcher dataWatcher = new DataWatcher(null);
        ReflectionUtil.setFieldValue(Hologram1_7Provider.LIVING_A, armorStand, line.getArmorStandId());
        ReflectionUtil.setFieldValue(Hologram1_7Provider.LIVING_B, armorStand, 30);
        ReflectionUtil.setFieldValue(Hologram1_7Provider.LIVING_C, armorStand, (int) (location.getX() * 32));
        ReflectionUtil.setFieldValue(Hologram1_7Provider.LIVING_D, armorStand, (int) ((location.getY() - 2.0) * 32.0));
        ReflectionUtil.setFieldValue(Hologram1_7Provider.LIVING_E, armorStand, (int) (location.getZ() * 32));
        ReflectionUtil.setFieldValue(Hologram1_7Provider.LIVING_L, armorStand, dataWatcher);

        dataWatcher.a(0, (byte) 0x20);
        dataWatcher.a(1, (short) 300);
        dataWatcher.a(2, PlaceholderService.replace(player, line.getText()));
        dataWatcher.a(3, (byte) 1);
        dataWatcher.a(4, (byte) 1);
        dataWatcher.a(5, (byte) 1);

        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(armorStand);
    }

    @Override
    public void updateExistingEntity(Player player, Location location, HologramLine line) {
        if (line.getArmorStandId() == 0) {
            sendSpawnPackets(player, location, line);
            return;
        }

        DataWatcher dataWatcher = new DataWatcher(null);
        dataWatcher.a(2, PlaceholderService.replace(player, line.getText()));

        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(
                new PacketPlayOutEntityMetadata(line.getArmorStandId(), dataWatcher, true)
        );
    }
}

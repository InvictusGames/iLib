package cc.invictusgames.ilib.hologram.packet.impl;

import cc.invictusgames.ilib.hologram.HologramLine;
import cc.invictusgames.ilib.hologram.packet.HologramPacketProvider;
import cc.invictusgames.ilib.placeholder.PlaceholderService;
import cc.invictusgames.ilib.utils.EntityUtils;
import cc.invictusgames.ilib.utils.ReflectionUtil;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

public class Hologram1_7Provider implements HologramPacketProvider {

    private static final Field ENTITY_A = ReflectionUtil.getField(PacketPlayOutSpawnEntity.class, "a");
    private static final Field ENTITY_B = ReflectionUtil.getField(PacketPlayOutSpawnEntity.class, "b");
    private static final Field ENTITY_C = ReflectionUtil.getField(PacketPlayOutSpawnEntity.class, "c");
    private static final Field ENTITY_D = ReflectionUtil.getField(PacketPlayOutSpawnEntity.class, "d");
    private static final Field ENTITY_J = ReflectionUtil.getField(PacketPlayOutSpawnEntity.class, "j");

    public static final Field LIVING_A = ReflectionUtil.getField(PacketPlayOutSpawnEntityLiving.class, "a");
    public static final Field LIVING_B = ReflectionUtil.getField(PacketPlayOutSpawnEntityLiving.class, "b");
    public static final Field LIVING_C = ReflectionUtil.getField(PacketPlayOutSpawnEntityLiving.class, "c");
    public static final Field LIVING_D = ReflectionUtil.getField(PacketPlayOutSpawnEntityLiving.class, "d");
    public static final Field LIVING_E = ReflectionUtil.getField(PacketPlayOutSpawnEntityLiving.class, "e");
    public static final Field LIVING_L = ReflectionUtil.getField(PacketPlayOutSpawnEntityLiving.class, "l");

    private static final Field ATTACH_B = ReflectionUtil.getField(PacketPlayOutAttachEntity.class, "b");
    private static final Field ATTACH_C = ReflectionUtil.getField(PacketPlayOutAttachEntity.class, "c");

    public static final Field METADATA_A = ReflectionUtil.getField(PacketPlayOutEntityMetadata.class, "a");
    public static final Field METADATA_B = ReflectionUtil.getField(PacketPlayOutEntityMetadata.class, "b");

    @Override
    public void sendSpawnPackets(Player player, Location location, HologramLine line) {
        if (line.getWitherId() == 0)
            line.setWitherId(EntityUtils.fakeEntityId());
        if (line.getHorseId() == 0)
            line.setHorseId(EntityUtils.fakeEntityId());

        PacketPlayOutSpawnEntity skull = new PacketPlayOutSpawnEntity();
        ReflectionUtil.setFieldValue(ENTITY_A, skull, line.getWitherId());
        ReflectionUtil.setFieldValue(ENTITY_B, skull, (int) (location.getX() * 32));
        ReflectionUtil.setFieldValue(ENTITY_C, skull, MathHelper.floor((location.getY() + 55.0) * 32.0D));
        ReflectionUtil.setFieldValue(ENTITY_D, skull, (int) (location.getZ() * 32));
        ReflectionUtil.setFieldValue(ENTITY_J, skull, 66);

        PacketPlayOutSpawnEntityLiving horse = new PacketPlayOutSpawnEntityLiving();
        DataWatcher dataWatcher = new DataWatcher(null);
        ReflectionUtil.setFieldValue(LIVING_A, horse, line.getHorseId());
        ReflectionUtil.setFieldValue(LIVING_B, horse, 100);
        ReflectionUtil.setFieldValue(LIVING_C, horse, (int) (location.getX() * 32));
        ReflectionUtil.setFieldValue(LIVING_D, horse, MathHelper.floor((location.getY() - 55.0) * 32.0D));
        ReflectionUtil.setFieldValue(LIVING_E, horse, (int) (location.getZ() * 32));
        dataWatcher.a(0, (byte) 0);
        dataWatcher.a(1, (short) 300);
        dataWatcher.a(2, PlaceholderService.replace(player, line.getText()));
        dataWatcher.a(3, (byte) 1);
        dataWatcher.a(4, (byte) 1);
        dataWatcher.a(5, (byte) 1);
        dataWatcher.a(12, -1700000);

        ReflectionUtil.setFieldValue(LIVING_L, horse, dataWatcher);

        PacketPlayOutAttachEntity attach = new PacketPlayOutAttachEntity();
        ReflectionUtil.setFieldValue(ATTACH_B, attach, line.getHorseId());
        ReflectionUtil.setFieldValue(ATTACH_C, attach, line.getWitherId());

        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(horse);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(skull);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(attach);
    }

    @Override
    public void updateExistingEntity(Player player, Location location, HologramLine line) {
        if (line.getWitherId() == 0 || line.getHorseId() == 0) {
            sendSpawnPackets(player, location, line);
            return;
        }

        PacketPlayOutEntityMetadata metadata = new PacketPlayOutEntityMetadata();
        ReflectionUtil.setFieldValue(METADATA_A, metadata, line.getHorseId());

        DataWatcher dataWatcher = new DataWatcher(null);
        dataWatcher.a(10, PlaceholderService.replace(player, line.getText()));

        ReflectionUtil.setFieldValue(METADATA_B, metadata, dataWatcher.c());

        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(metadata);
    }

}

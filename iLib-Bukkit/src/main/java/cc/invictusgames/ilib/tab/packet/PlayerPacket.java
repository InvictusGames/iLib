package cc.invictusgames.ilib.tab.packet;

import cc.invictusgames.ilib.ILibBukkitPlugin;
import cc.invictusgames.ilib.protocol.TinyProtocol;
import cc.invictusgames.ilib.utils.ReflectionUtil;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.UUID;

public class PlayerPacket implements TinyProtocol {

    private static final Field UUID_FIELD = ReflectionUtil.getField(PacketPlayOutNamedEntitySpawn.class, "b");

    @Override
    public Packet handleIncomingPacket(Player player, Packet packet, ChannelHandlerContext ctx) {
        return packet;
    }

    @Override
    public Packet handleOutgoingPacket(Player player, Packet packet, ChannelHandlerContext ctx) {
        if (packet instanceof PacketPlayOutPlayerInfo) {
            PacketPlayOutPlayerInfo packetPlayOutPlayerInfo = (PacketPlayOutPlayerInfo) packet;
            for (PacketPlayOutPlayerInfo.PlayerInfoData data : packetPlayOutPlayerInfo.getData()) {
                Player target = Bukkit.getPlayer(data.a().getId());

                System.out.println("ok");

                // Send the player, so they are not invisible and then hide them 5 ticks later.
                if (target != null && packetPlayOutPlayerInfo.getAction() != PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER) {
                    EntityPlayer entityTarget = ((CraftPlayer) target).getHandle();
                    entityTarget.playerConnection.sendPacket(new PacketPlayOutPlayerInfo(
                            PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, entityTarget));
                    Bukkit.getScheduler().runTaskLater(ILibBukkitPlugin.getInstance(), ()
                            -> entityTarget.playerConnection.sendPacket(new PacketPlayOutPlayerInfo(
                            PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, entityTarget)), 10L);
                    return packet;
                }
            }

        } else if (packet instanceof PacketPlayOutNamedEntitySpawn) {
            PacketPlayOutNamedEntitySpawn packetPlayOutNamedEntitySpawn = (PacketPlayOutNamedEntitySpawn) packet;
            Player target = Bukkit.getPlayer((UUID) ReflectionUtil.getFieldValue(UUID_FIELD, packetPlayOutNamedEntitySpawn));

            // Add the player, so they are not invisible and then the listener above will handle the hiding.
            if (target != null) {
                EntityPlayer entityTarget = ((CraftPlayer) target).getHandle();
                entityTarget.playerConnection.sendPacket(
                        new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entityTarget));
                return packet;
            }
        }
        return packet;
    }
}

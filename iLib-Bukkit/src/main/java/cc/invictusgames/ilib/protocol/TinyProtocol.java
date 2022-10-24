package cc.invictusgames.ilib.protocol;

import io.netty.channel.ChannelHandlerContext;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.entity.Player;

public interface TinyProtocol {

    int DEFAULT_PRIORITY = 5;

    default int getProtocolPriority() {
        return DEFAULT_PRIORITY;
    }

    Packet<?> handleIncomingPacket(Player player, Packet<?> packet, ChannelHandlerContext ctx);

    Packet<?> handleOutgoingPacket(Player player, Packet<?> packet, ChannelHandlerContext ctx);

}

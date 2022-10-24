package cc.invictusgames.ilib.protocol;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class PacketHandler extends ChannelDuplexHandler {

    public static final String HANDLER_NAME = "tiny_protocol_handler";

    private final Player player;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Packet packet = (Packet) msg;
        for (TinyProtocol interceptor : ProtocolService.getTinyProtocols()) {
            packet = interceptor.handleIncomingPacket(player, packet, ctx);
            if (packet == null)
                return;
        }

        if (packet != null)
            super.channelRead(ctx, packet);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        Packet packet = (Packet) msg;
        for (TinyProtocol interceptor : ProtocolService.getTinyProtocols()) {
            packet = interceptor.handleOutgoingPacket(player, packet, ctx);
            if (packet == null)
                return;
        }

        if (packet != null)
            super.write(ctx, packet, promise);
    }
}

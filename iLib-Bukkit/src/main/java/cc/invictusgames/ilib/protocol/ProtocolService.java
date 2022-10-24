package cc.invictusgames.ilib.protocol;

import cc.invictusgames.ilib.utils.logging.SimpleBukkitLogger;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProtocolService {

    private static final List<TinyProtocol> TINY_PROTOCOLS = new ArrayList<>();

    private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();

    public static List<TinyProtocol> getTinyProtocols() {
        return Collections.unmodifiableList(TINY_PROTOCOLS);
    }

    public static void registerTinyProtocol(JavaPlugin plugin, TinyProtocol protocol) {
        TINY_PROTOCOLS.add(protocol);
        TINY_PROTOCOLS.sort(Comparator.comparingInt(TinyProtocol::getProtocolPriority).reversed());

        SimpleBukkitLogger logger = SimpleBukkitLogger.getLogger(plugin, "TinyProtocol");
        logger.info(String.format("Registered tiny protocol %s with priority %d",
                protocol.getClass().getSimpleName(), protocol.getProtocolPriority()));
    }

    public static void unregisterTinyProtocol(JavaPlugin plugin, TinyProtocol protocol) {
        TINY_PROTOCOLS.remove(protocol);

        SimpleBukkitLogger logger = SimpleBukkitLogger.getLogger(plugin, "TinyProtocol");
        logger.info(String.format("Removed tiny protocol %s", protocol.getClass().getSimpleName()));
    }

    protected static void injectPlayer(Player player) {
        EXECUTOR.submit(() -> {
            PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;
            Channel channel = playerConnection.networkManager.channel;
            ChannelPipeline pipeline = channel.pipeline();
            pipeline.addBefore("packet_handler", PacketHandler.HANDLER_NAME, new PacketHandler(player));
        });
    }

    protected static void uninjectPlayer(Player player) {
        EXECUTOR.submit(() -> {
            PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;
            if (playerConnection == null || playerConnection.isDisconnected())
                return;

            Channel channel = playerConnection.networkManager.channel;
            ChannelPipeline pipeline = channel.pipeline();

            channel.eventLoop().execute(() -> {
                if (pipeline.get(PacketHandler.HANDLER_NAME) != null)
                    pipeline.remove(PacketHandler.HANDLER_NAME);
            });
        });
    }

}

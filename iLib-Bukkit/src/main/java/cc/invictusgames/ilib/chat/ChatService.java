package cc.invictusgames.ilib.chat;

import cc.invictusgames.ilib.ILib;
import cc.invictusgames.ilib.ILibBukkit;
import cc.invictusgames.ilib.chat.impl.PublicChat;
import cc.invictusgames.ilib.utils.CC;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.BiFunction;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 17.11.2020 / 20:24
 * iLib / cc.invictusgames.ilib.chat
 */

public class ChatService {

    @Getter
    @Setter
    private static BiFunction<Player, CommandSender, String> prefixGetter = (player, sender) -> player.getDisplayName();

    @Getter
    @Setter
    private static DefaultChannelProvider defaultChannelProvider = DefaultChannelProvider.DEFAULT;

    @Getter
    @Setter
    private static ChatChannel defaultChannel = new PublicChat();

    private static final Map<UUID, ChatChannel> playerChatChannels = new HashMap<>();
    private static final Map<String, ChatChannel> chatChannels = new HashMap<>();

    public static void registerChatChannel(ChatChannel channel) {
        chatChannels.put(channel.getName().toLowerCase(), channel);
    }

    public static ChatChannel fromPlayer(Player player) {
        ChatChannel chatChannel = playerChatChannels.get(player.getUniqueId());
        if (chatChannel == null) {
            chatChannel = defaultChannelProvider.getDefaultChannel(player);
            setChatChannel(player, chatChannel, true);
        }

        return chatChannel;
    }

    public static ChatChannel fromName(String name) {
        ChatChannel channel = chatChannels.get(name.toLowerCase());
        if (channel == null) {
            for (ChatChannel chatChannel : chatChannels.values()) {
                if (chatChannel.getName().equalsIgnoreCase(name)
                        || chatChannel.getAliases().contains(name.toLowerCase()))
                    return chatChannel;
            }
        }
        return channel;
    }

    public static ChatChannel fromPrefix(char prefix) {
        for (ChatChannel chatChannel : chatChannels.values()) {
            if (chatChannel.getPrefix() == prefix)
                return chatChannel;
        }

        return null;
    }

    public static void setChatChannel(Player player, ChatChannel channel, boolean silent) {
        if (channel.getPriority() >= 0)
            ILib.getInstance().getRedisService().executeBackendCommand(redis ->
                    redis.hset("ChatChannel" + (channel instanceof LocalChatChannel
                                    ? ":" + ILibBukkit.getServerNameGetter().apply(null) : ""),
                            player.getUniqueId().toString(), channel.getName().toLowerCase()));

        if (!(channel instanceof LocalChatChannel) && channel.getPriority() >= 0)
            ILib.getInstance().getRedisService().executeBackendCommand(redis ->
                    redis.hdel("ChatChannel:" + ILibBukkit.getServerNameGetter().apply(null),
                            player.getUniqueId().toString()));

        if (channel.getPriority() >= 0)
            loadChatChannel(player.getUniqueId());
        else playerChatChannels.put(player.getUniqueId(), channel);

        if (!silent)
            player.sendMessage(CC.YELLOW + "You are now talking in "
                    + channel.getDisplayName().toLowerCase() + CC.YELLOW + " chat.");
    }

    public static void loadChatChannel(UUID uuid) {
        playerChatChannels.put(uuid,
                ILib.getInstance().getRedisService().executeBackendCommand(redis -> {
            ChatChannel globalChannel = null;
            ChatChannel localChannel = null;

            if (redis.hexists("ChatChannel", uuid.toString()))
                globalChannel = fromName(redis.hget("ChatChannel", uuid.toString()));

            if (redis.hexists("ChatChannel:" + ILibBukkit.getServerNameGetter().apply(null),
                    uuid.toString()))
                localChannel = fromName(redis.hget("ChatChannel:" + ILibBukkit.getServerNameGetter().apply(null),
                        uuid.toString()));

            if (globalChannel == null || (localChannel != null
                    && localChannel.getPriority() >= globalChannel.getPriority()))
                globalChannel = localChannel;

            return globalChannel;
        }));
    }

    public static void removePlayer(Player player) {
        playerChatChannels.remove(player.getUniqueId());
    }

    public static List<ChatChannel> getChannels() {
        ArrayList<ChatChannel> chatChannels = new ArrayList<>(ChatService.chatChannels.values());
        Comparator<ChatChannel> comparator = Comparator.comparingInt(ChatChannel::getPriority).reversed();
        chatChannels.sort(comparator);
        return chatChannels;
    }


}

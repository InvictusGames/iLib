package cc.invictusgames.ilib.chat.command;

import cc.invictusgames.ilib.chat.ChatChannel;
import cc.invictusgames.ilib.chat.ChatService;
import cc.invictusgames.ilib.command.CommandService;
import cc.invictusgames.ilib.command.annotation.Command;
import cc.invictusgames.ilib.command.annotation.Param;
import cc.invictusgames.ilib.utils.CC;
import org.bukkit.entity.Player;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 17.11.2020 / 20:42
 * iLib / cc.invictusgames.ilib.chat.command
 */

public class ChatCommand {

    @Command(names = {"chat"}, description = "Change your chat channel", playerOnly = true, async = true)
    public boolean chat(Player sender, @Param(name = "channel", defaultValue = "@list") String channel) {
        if (channel.equals("@list")) {
            sender.sendMessage(CC.SMALL_CHAT_BAR);
            sender.sendMessage(CC.RED + CC.BOLD + "Chat Channels");
            sender.sendMessage(CC.format("&eYou are currently talking in %s &echat.",
                    ChatService.fromPlayer(sender).getDisplayName().toLowerCase()));
            sender.sendMessage(" ");
            for (ChatChannel chatChannel : ChatService.getChannels()) {
                if (chatChannel.canAccess(sender))
                    sender.sendMessage(CC.format(
                            " - %s &f - &c/%s [message] &e(or prefix with &c%s&e)",
                            chatChannel.getDisplayName(),
                            chatChannel.getAliases().size() >= 1 ? chatChannel.getAliases().get(0) :
                                    chatChannel.getName(),
                            chatChannel.getPrefix()
                    ));
            }
            sender.sendMessage(CC.SMALL_CHAT_BAR);
            return true;
        }

        ChatChannel chatChannel = CommandService.getParameter(ChatChannel.class).parse(sender, channel);
        if (chatChannel != null)
            ChatService.setChatChannel(sender, chatChannel, false);
        return true;
    }

    @Command(names = {"publicchat", "pc", "globalchat", "gc"}, description = "Send a message in public chat",
             playerOnly = true, async = true)
    public boolean publicChat(Player sender, @Param(name = "message", wildcard = true) String message) {
        ChatService.getDefaultChannelProvider().getDefaultChannel(sender).chat(sender, message);
        return true;
    }

}




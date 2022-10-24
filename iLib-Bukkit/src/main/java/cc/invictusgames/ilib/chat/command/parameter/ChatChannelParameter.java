package cc.invictusgames.ilib.chat.command.parameter;

import cc.invictusgames.ilib.chat.ChatChannel;
import cc.invictusgames.ilib.chat.ChatService;
import cc.invictusgames.ilib.command.parameter.ParameterType;
import cc.invictusgames.ilib.utils.CC;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 17.11.2020 / 20:42
 * iLib / cc.invictusgames.ilib.chat.command.parameter
 */

public class ChatChannelParameter implements ParameterType<ChatChannel> {

    @Override
    public ChatChannel parse(CommandSender sender, String source) {
        ChatChannel channel = ChatService.fromName(source);

        if (channel == null || (!channel.canAccess(sender))) {
            sender.sendMessage(CC.RED + "Chat Channel " + CC.YELLOW + source + CC.RED + " not found.");
            return null;
        }

        return channel;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, List<String> flags) {
        List<String> completions = new ArrayList<>();
        for (ChatChannel channel : ChatService.getChannels()) {
            if (channel.canAccess(sender))
                completions.add(channel.getName());
        }
        return completions;
    }
}

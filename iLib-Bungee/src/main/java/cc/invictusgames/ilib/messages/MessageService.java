package cc.invictusgames.ilib.messages;

import net.md_5.bungee.api.CommandSender;

import java.util.List;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 07.01.2020 / 22:22
 * iLib / cc.invictusgames.ilib.messages
 */

public interface MessageService {
    void loadMessages();

    String formatMessage(String key, Object... args);

    List<String> formatMessages(String key, Object... args);

    void sendMessage(CommandSender sender, String key, Object... args);
}

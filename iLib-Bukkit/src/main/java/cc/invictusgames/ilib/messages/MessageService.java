package cc.invictusgames.ilib.messages;

import org.bukkit.command.CommandSender;

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

package cc.invictusgames.ilib.chatinputold;

import cc.invictusgames.ilib.utils.callback.ReturnCallable;
import lombok.Getter;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 02.03.2020 / 17:28
 * iLib / cc.invictusgames.ilib.chatinput
 */

public class ChatInput {

    @Getter private static Map<CommandSender, ReturnCallable<String, Boolean>> inputs = new HashMap<>();
    private CommandSender sender;
    private ReturnCallable<String, Boolean> callable;

    public ChatInput(CommandSender sender, ReturnCallable<String, Boolean> callable) {
        this.sender = sender;
        this.callable = callable;
        inputs.put(sender, callable);
    }

    public void resend() {
        inputs.put(sender, callable);
    }

}

package cc.invictusgames.ilib.command.parameter;

import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 15.01.2020 / 22:20
 * iLib / cc.invictusgames.ilib.commandapi.parameter
 */

public interface ParameterType<T> {

    T parse(CommandSender sender, String source);

    List<String> tabComplete(CommandSender sender, List<String> flags);

}

package cc.invictusgames.ilib.command.parameter.defaults;

import cc.invictusgames.ilib.command.parameter.ParameterType;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 15.01.2020 / 22:41
 * iLib / cc.invictusgames.ilib.commandapi.parameter.defaults
 */

public class StringParameter implements ParameterType<String> {

    @Override
    public String parse(CommandSender sender, String source) {
        return source;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, List<String> flags) {
        return new ArrayList<>();
    }
}

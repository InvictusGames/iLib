package cc.invictusgames.ilib.command.parameter.defaults;

import cc.invictusgames.ilib.command.parameter.ParameterType;
import cc.invictusgames.ilib.utils.CC;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 15.01.2020 / 22:22
 * iLib / cc.invictusgames.ilib.commandapi.parameter.defaults
 */

public class BooleanParameter implements ParameterType<Boolean> {

    private final Map<String, Boolean> map = new HashMap<String, Boolean>() {{
        put("true", true);
        put("on", true);
        put("yes", true);
        put("enabled", true);
        put("false", false);
        put("off", false);
        put("no", false);
        put("disabled", false);
    }};

    @Override
    public Boolean parse(CommandSender sender, String source) {
        if (!map.containsKey(source)) {
            sender.sendMessage(CC.YELLOW + source + CC.RED + " is not a valid boolean.");
            return null;
        }
        return this.map.get(source.toLowerCase());
    }

    @Override
    public List<String> tabComplete(CommandSender sender, List<String> flags) {
        return new ArrayList<>(this.map.keySet());
    }
}

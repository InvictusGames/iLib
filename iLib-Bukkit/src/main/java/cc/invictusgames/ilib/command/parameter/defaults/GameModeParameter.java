package cc.invictusgames.ilib.command.parameter.defaults;

import cc.invictusgames.ilib.command.parameter.ParameterType;
import cc.invictusgames.ilib.utils.CC;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 03.06.2020 / 19:00
 * iLib / cc.invictusgames.ilib.commandapi.parameter.defaults
 */

public class GameModeParameter implements ParameterType<GameMode> {

    private final Map<String, GameMode> map = new HashMap<String, GameMode>() {{
        put("survival", GameMode.SURVIVAL);
        put("s", GameMode.SURVIVAL);
        put("0", GameMode.SURVIVAL);
        put("creative", GameMode.CREATIVE);
        put("c", GameMode.CREATIVE);
        put("1", GameMode.CREATIVE);
        put("adventure", GameMode.ADVENTURE);
        put("a", GameMode.ADVENTURE);
        put("2", GameMode.ADVENTURE);
        put("spectator", GameMode.SPECTATOR);
        put("spec", GameMode.SPECTATOR);
        put("sp", GameMode.SPECTATOR);
        put("3", GameMode.SPECTATOR);
    }};

    @Override
    public GameMode parse(CommandSender sender, String source) {
        if ((source.equals("@toggle")) && (sender instanceof Player)) {
            return ((Player) sender).getGameMode().equals(GameMode.CREATIVE) ? GameMode.SURVIVAL : GameMode.CREATIVE;
        }
        if (!map.containsKey(source.toLowerCase())) {
            sender.sendMessage(CC.RED + "GameMode " + CC.YELLOW + source + CC.RED + " not found.");
            return null;
        }

        return map.get(source.toLowerCase());
    }

    @Override
    public List<String> tabComplete(CommandSender sender, List<String> flags) {
        return new ArrayList<>(map.keySet());
    }
}

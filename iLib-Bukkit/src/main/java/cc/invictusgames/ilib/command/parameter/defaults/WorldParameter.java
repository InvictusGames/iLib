package cc.invictusgames.ilib.command.parameter.defaults;

import cc.invictusgames.ilib.command.parameter.ParameterType;
import cc.invictusgames.ilib.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 15.01.2020 / 22:43
 * iLib / cc.invictusgames.ilib.commandapi.parameter.defaults
 */

public class WorldParameter implements ParameterType<World> {

    @Override
    public World parse(CommandSender sender, String source) {
        World world = Bukkit.getWorld(source);
        if (world == null) {
            sender.sendMessage(CC.RED + "World " + CC.YELLOW + source + CC.RED + " not found.");
            return null;
        }
        return world;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, List<String> flags) {
        List<String> completions = new ArrayList<>();
        for (World world : Bukkit.getWorlds()) {
            completions.add(world.getName());
        }
        return completions;
    }
}

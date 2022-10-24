package cc.invictusgames.ilib.command.parameter.defaults;

import cc.invictusgames.ilib.command.parameter.ParameterType;
import cc.invictusgames.ilib.utils.CC;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 23.10.2020 / 08:33
 * iLib / cc.invictusgames.ilib.command.parameter.defaults
 */

public class EntityTypeParameter implements ParameterType<EntityType> {

    @Override
    public EntityType parse(CommandSender sender, String source) {
        EntityType parsed = null;
        for (EntityType type : EntityType.values()) {
            if (type.name().equalsIgnoreCase(source)) {
                parsed = type;
                break;
            }

            if (type.getName() != null && type.getName().equalsIgnoreCase(source)) {
                parsed = type;
                break;
            }

            if (String.valueOf(type.getTypeId()).equalsIgnoreCase(source)) {
                parsed = type;
                break;
            }
        }

        if (parsed == null)
            sender.sendMessage(CC.RED + "Entity " + CC.YELLOW + source + CC.RED + " not found.");

        return parsed;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, List<String> flags) {
        List<String> completions = new ArrayList<>();
        for (EntityType type : EntityType.values()) {
            completions.add(type.name().toLowerCase());
        }
        return completions;
    }
}

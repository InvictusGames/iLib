package cc.invictusgames.ilib.command.parameter.defaults;

import cc.invictusgames.ilib.command.parameter.ParameterType;
import cc.invictusgames.ilib.utils.CC;
import cc.invictusgames.ilib.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 03.06.2020 / 18:47
 * iLib / cc.invictusgames.ilib.commandapi.parameter.defaults
 */

public class ItemStackParameter implements ParameterType<ItemStack> {

    @Override
    public ItemStack parse(CommandSender sender, String source) {
        ItemStack item = ItemUtils.get(source);
        if (item == null) {
            sender.sendMessage(CC.RED + "Item " + CC.YELLOW + source + CC.RED + " not found.");
            return null;
        }
        return item;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, List<String> flags) {
        List<String> completions = new ArrayList<>();
        for (Material value : Material.values()) {
            completions.add(value.name().toLowerCase());
        }
        return completions;
    }
}

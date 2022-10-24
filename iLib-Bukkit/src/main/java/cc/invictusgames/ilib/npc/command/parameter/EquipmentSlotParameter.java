package cc.invictusgames.ilib.npc.command.parameter;

import cc.invictusgames.ilib.command.parameter.ParameterType;
import cc.invictusgames.ilib.npc.equipment.EquipmentSlot;
import cc.invictusgames.ilib.utils.CC;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class EquipmentSlotParameter implements ParameterType<EquipmentSlot> {

    @Override
    public EquipmentSlot parse(CommandSender sender, String source) {
        EquipmentSlot slot = null;
        try {
            slot = EquipmentSlot.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException ignored) { }

        if (slot == null)
            sender.sendMessage(CC.format(
                    "&cSlot &e%s &cnot found. Available: &e%s",
                    source,
                    StringUtils.join(EquipmentSlot.values(), CC.RED + ", " + CC.YELLOW)
            ));

        return slot;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, List<String> flags) {
        List<String> completions = new ArrayList<>();
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            completions.add(slot.name().toLowerCase());
        }
        return completions;
    }
}

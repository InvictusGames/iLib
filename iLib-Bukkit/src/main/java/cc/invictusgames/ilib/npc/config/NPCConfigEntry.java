package cc.invictusgames.ilib.npc.config;

import cc.invictusgames.ilib.configuration.StaticConfiguration;
import cc.invictusgames.ilib.configuration.defaults.LocationConfig;
import cc.invictusgames.ilib.npc.equipment.EquipmentSlot;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
public class NPCConfigEntry implements StaticConfiguration {

    private LocationConfig location;
    private String name;
    private String displayName;
    private String texture;
    private String signature;
    private String command;
    private boolean consoleCommand;
    private Map<EquipmentSlot, ItemStack> equipment;

}

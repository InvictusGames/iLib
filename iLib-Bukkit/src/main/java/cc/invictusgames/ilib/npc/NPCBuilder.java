package cc.invictusgames.ilib.npc;

import cc.invictusgames.ilib.npc.clickhandler.NPCClickHandler;
import cc.invictusgames.ilib.npc.equipment.EquipmentSlot;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

@Getter(AccessLevel.PROTECTED)
public class NPCBuilder {

    private String displayName;
    private Location location;
    private String command;
    private boolean consoleCommand = false;
    private NPCClickHandler clickHandler;
    private String skinName;
    private String[] skin = null;
    private Map<EquipmentSlot, ItemStack> equipment = new HashMap<>();

    public NPCBuilder at(Location location) {
        this.location = location;
        return this;
    }

    public NPCBuilder displayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public NPCBuilder command(String command) {
        this.command = command;
        this.consoleCommand = false;
        return this;
    }

    public NPCBuilder consoleCommand(String command) {
        this.command = command;
        this.consoleCommand = true;
        return this;
    }

    public NPCBuilder clickHandler(NPCClickHandler clickHandler) {
        this.clickHandler = clickHandler;
        return this;
    }

    public NPCBuilder skinName(String skinName) {
        this.skinName = skinName;
        this.skin = null;
        return this;
    }

    public NPCBuilder skinTexture(String texture, String signature) {
        this.skin = new String[] {texture, signature};
        this.skinName = null;
        return this;
    }

    public NPCBuilder withEquipment(EquipmentSlot slot, ItemStack item) {
        equipment.put(slot, item);
        return this;
    }

    public NPC buildAndSpawn() {
        return new NPC(this);
    }

}

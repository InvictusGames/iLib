package cc.invictusgames.ilib.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.enchantments.Enchantment;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 03.06.2020 / 18:55
 * iLib / cc.invictusgames.ilib.utils
 */

@Getter
@RequiredArgsConstructor
public enum EnchantmentWrapper {

    PROTECTION_ENVIRONMENTAL("Protection", new String[]{"p", "prot"}),
    PROTECTION_FIRE("Fire Protection", new String[]{"fp", "fprot", "fireprot", "firep"}),
    PROTECTION_FALL("Feather Falling", new String[]{"ff", "featherf", "ffalling"}),
    PROTECTION_EXPLOSIONS("Blast Protection", new String[]{"explosionprotection", "explosionprotection", "bprotection"
            , "blastprotect", "pe", "bp"}),
    PROTECTION_PROJECTILE("Projectile Protection", new String[]{"pp", "projprot", "projprotection", "projp", "pprot"}),
    THORNS("Thorns", new String[0]),
    DURABILITY("Unbreaking", new String[]{"unbr", "unb", "dur", "dura"}),
    DAMAGE_ALL("Sharpness", new String[]{"s", "sharp", "dmg"}),
    DAMAGE_UNDEAD("Smite", new String[]{"du", "dz"}),
    DAMAGE_ARTHROPODS("Bane of Arthropods", new String[]{"bane", "ardmg", "baneofarthropod", "arthrop", "arthropod",
            "dar", "dspider"}),
    KNOCKBACK("Knockback", new String[]{"k", "knock", "kb"}),
    FIRE_ASPECT("Fire Aspect", new String[]{"fire", "fa"}),
    OXYGEN("Respiration", new String[]{"breathing", "o", "breath"}),
    WATER_WORKER("Aqua Affinity", new String[]{"aa"}),
    LOOT_BONUS_MOBS("Looting", new String[]{"moblooting", "ml", "loot"}),
    LOOT_BONUS_BLOCKS("Fortune", new String[]{"fort", "lbm", "bl"}),
    DIG_SPEED("Efficiency", new String[]{"e", "eff", "digspeed", "ds"}),
    SILK_TOUCH("Silk Touch", new String[]{"silk", "sk"}),
    ARROW_DAMAGE("Power", new String[]{"apower", "adamage", "admg"}),
    ARROW_KNOCKBACK("Punch", new String[]{"akb", "arrowkb", "aknockback"}),
    ARROW_FIRE("Flame", new String[]{"afire"}),
    ARROW_INFINITE("Infinity", new String[]{"infinitearrows", "inf", "infarrows", "unlimitedarrows", "ai", "uarrows",
            "unlimited"}),
    LUCK("Luck of the Sea", new String[]{"rodluck", "luckofsea", "los", "lots"}),
    LURE("Lure", new String[]{"rodlure"}),
    DEPTH_STRIDER("Depth Strider", new String[]{"depthstrider", "depth"});

    private final String fancyName;
    private final String[] parse;

    public Enchantment toBukkitEnchant() {
        return Enchantment.getByName(this.name());
    }

    public static EnchantmentWrapper fromString(String input) {
        for (EnchantmentWrapper value : values()) {
            for (String s : value.getParse()) {
                if (s.equalsIgnoreCase(input)) {
                    return value;
                }
            }
            if (value.toBukkitEnchant().getName().replace("_", "").equalsIgnoreCase(input)) {
                return value;
            }
            if (value.toBukkitEnchant().getName().equalsIgnoreCase(input)) {
                return value;
            }
            if (value.getFancyName().replace(" ", "").equalsIgnoreCase(input)) {
                return value;
            }
            if (value.getFancyName().replace(" ", "_").equalsIgnoreCase(input)) {
                return value;
            }
            if (value.getFancyName().equalsIgnoreCase(input)) {
                return value;
            }
        }
        return null;
    }

}

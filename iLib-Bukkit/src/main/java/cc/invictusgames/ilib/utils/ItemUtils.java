package cc.invictusgames.ilib.utils;

import cc.invictusgames.ilib.ILib;
import cc.invictusgames.ilib.ILibBukkitPlugin;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 17.04.2020 / 15:24
 * iLib / cc.invictusgames.ilib.utils
 */

public class ItemUtils {

    public static final List<Material> HELMET_MATERIALS = Arrays.asList(Material.DIAMOND_HELMET, Material.IRON_HELMET
            , Material.CHAINMAIL_HELMET, Material.GOLD_HELMET, Material.LEATHER_HELMET);
    public static final List<Material> CHESTPLATE_MATERIALS = Arrays.asList(Material.DIAMOND_CHESTPLATE,
            Material.IRON_CHESTPLATE, Material.CHAINMAIL_CHESTPLATE, Material.GOLD_CHESTPLATE,
            Material.LEATHER_CHESTPLATE);
    public static final List<Material> LEGGINS_MATERIALS = Arrays.asList(Material.DIAMOND_LEGGINGS,
            Material.IRON_LEGGINGS, Material.CHAINMAIL_LEGGINGS, Material.GOLD_LEGGINGS, Material.LEATHER_LEGGINGS);
    public static final List<Material> BOOTS_MATERIALS = Arrays.asList(Material.DIAMOND_BOOTS, Material.IRON_BOOTS,
            Material.CHAINMAIL_BOOTS, Material.GOLD_BOOTS, Material.LEATHER_BOOTS);

    private static final Map<String, MaterialData> NAME_MAP = new HashMap<>();

    public static void loadItems() {
        NAME_MAP.clear();

        for (String line : readLines()) {
            String[] parts = line.split(",");
            NAME_MAP.put(parts[0], new MaterialData(Material.getMaterial(Integer.parseInt(parts[1])),
                    (byte) Short.parseShort(parts[2])));
        }
    }

    private static List<String> readLines() {
        try {
            File file = new File(ILib.getInstance().getMainConfig().getItemsPath());
            if (!file.exists()) {
                if (!file.createNewFile()) {
                    return new ArrayList<>();
                }
                FileUtils.copyInputStreamToFile(ILibBukkitPlugin.getInstance().getResource("items.csv"), file);
                return IOUtils.readLines(ILibBukkitPlugin.getInstance().getResource("items.csv"));
            }
            return IOUtils.readLines(new FileInputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static ItemStack get(String input) {
        input = input.toLowerCase().replace(" ", "");
        try {
            return new ItemStack(Material.getMaterial(Integer.parseInt(input)));
        } catch (NumberFormatException | NullPointerException ignored) {
        }

        if (input.contains(":")) {
            short subId;
            try {
                subId = Short.parseShort(input.split(":")[1]);
            } catch (NumberFormatException e) {
                return null;
            }

            int material;
            try {
                material = Integer.parseInt(input.split(":")[0]);
            } catch (NumberFormatException e) {
                return null;
            }

            return new ItemStack(Material.getMaterial(material), 1, subId);
        } else {
            if (!NAME_MAP.containsKey(input)) {
                if (Material.getMaterial(input.toUpperCase()) != null)
                    return new ItemStack(Material.getMaterial(input.toUpperCase()));

                return null;
            }
            return NAME_MAP.get(input).toItemStack();
        }
    }

    public static boolean isHelmet(Material material) {
        return HELMET_MATERIALS.contains(material);
    }

    public static boolean isHelmet(ItemStack itemStack) {
        return isHelmet(itemStack.getType());
    }

    public static boolean isChestplate(Material material) {
        return CHESTPLATE_MATERIALS.contains(material);
    }

    public static boolean isChestplate(ItemStack itemStack) {
        return isChestplate(itemStack.getType());
    }

    public static boolean isLeggings(Material material) {
        return LEGGINS_MATERIALS.contains(material);
    }

    public static boolean isLeggings(ItemStack itemStack) {
        return isLeggings(itemStack.getType());
    }

    public static boolean isBoots(Material material) {
        return BOOTS_MATERIALS.contains(material);
    }

    public static boolean isBoots(ItemStack itemStack) {
        return isBoots(itemStack.getType());
    }

    public static String getName(ItemStack item) {
        if (item == null)
            return "null";

        net.minecraft.server.v1_8_R3.ItemStack nmsCopy = CraftItemStack.asNMSCopy(item);
        if (nmsCopy == null) {
            return WordUtils.capitalizeFully(item.getType().name().replace("_", " "));
        }
        return nmsCopy.getName();
    }
}

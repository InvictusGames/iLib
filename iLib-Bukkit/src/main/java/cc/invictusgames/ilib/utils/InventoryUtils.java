package cc.invictusgames.ilib.utils;

import cc.invictusgames.ilib.ILibBukkitPlugin;
import cc.invictusgames.ilib.builder.ItemBuilder;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagList;
import net.minecraft.server.v1_8_R3.NBTTagString;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Objects;
import java.util.UUID;

public class InventoryUtils {

    public static ItemStack[] fixInventoryOrder(ItemStack[] source) {
        ItemStack[] fixed = new ItemStack[36];

        System.arraycopy(source, 0, fixed, 27, 9);
        System.arraycopy(source, 9, fixed, 0, 27);

        return fixed;
    }

    public static ItemStack createTexturedSkull(String texture, String signatrue) {
        net.minecraft.server.v1_8_R3.ItemStack nmsStack =
                CraftItemStack.asNMSCopy(new ItemBuilder(Material.SKULL_ITEM, 3).build());

        NBTTagCompound compound = nmsStack.getTag();
        if (compound == null) {
            compound = new NBTTagCompound();
            nmsStack.setTag(compound);
            compound = nmsStack.getTag();
        }

        NBTTagCompound skullOwner = new NBTTagCompound();
        skullOwner.set("Id", new NBTTagString(UUID.randomUUID().toString()));

        NBTTagList textures = new NBTTagList();
        NBTTagCompound value = new NBTTagCompound();
        value.set("Value", new NBTTagString(texture));
        /*NBTTagCompound signature = new NBTTagCompound();
        signature.set("Signature", new NBTTagString(signatrue));
        textures.add(signature);*/
        textures.add(value);

        NBTTagCompound properties = new NBTTagCompound();
        properties.set("textures", textures);
        skullOwner.set("Properties", properties);

        compound.set("SkullOwner", skullOwner);
        nmsStack.setTag(compound);

        return CraftItemStack.asBukkitCopy(nmsStack);
    }

    public static String inventoryToBase64(ItemStack[] items) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            dataOutput.writeInt(items.length);

            for (ItemStack item : items) {
                dataOutput.writeObject(item);
                dataOutput.writeBoolean(item instanceof CraftItemStack);
            }

            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static ItemStack[] inventoryFromBase64(String input) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(input));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack[] items = new ItemStack[dataInput.readInt()];

            for (int i = 0; i < items.length; i++) {
                items[i] = (ItemStack) dataInput.readObject();
                if (dataInput.readBoolean())
                    items[i] = CraftItemStack.asCraftCopy(items[i]);
            }
            dataInput.close();
            return items;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String itemToBase64(ItemStack itemStack) {
        return inventoryToBase64(new ItemStack[]{itemStack});
    }

    public static ItemStack itemFromBase64(String input) {
        return Objects.requireNonNull(inventoryFromBase64(input))[0];
    }

    public static void removeCrafting(Material material) {
        Iterator<Recipe> iterator = ILibBukkitPlugin.getInstance().getServer().recipeIterator();

        while (iterator.hasNext()) {
            Recipe recipe = iterator.next();

            if (recipe != null && recipe.getResult().getType() == material) {
                iterator.remove();
            }
        }
    }

}

package cc.invictusgames.ilib.utils;

import net.minecraft.server.v1_8_R3.NBTBase;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 09.03.2021 / 01:37
 * iLib / cc.invictusgames.ilib.utils
 */

public class ItemNbtUtil {

    public static void remove(ItemStack item, String key) {
        CraftItemStack craftItem = (CraftItemStack) item;
        net.minecraft.server.v1_8_R3.ItemStack stack = craftItem.handle;

        if (stack.tag != null)
            stack.tag.remove(key);
    }

    public static void set(ItemStack item, String key, String value) {
        CraftItemStack craftItem = (CraftItemStack) item;
        net.minecraft.server.v1_8_R3.ItemStack stack = craftItem.handle;

        NBTTagCompound tag;
        if (stack.tag != null)
            tag = stack.tag;
        else tag = new NBTTagCompound();

        tag.setString(key, value);
        stack.tag = tag;
    }

    public static void set(ItemStack item, String key, double value) {
        CraftItemStack craftItem = (CraftItemStack) item;
        net.minecraft.server.v1_8_R3.ItemStack stack = craftItem.handle;

        NBTTagCompound tag;
        if (stack.tag != null)
            tag = stack.tag;
        else tag = new NBTTagCompound();

        tag.setDouble(key, value);
        stack.tag = tag;
    }

    public static void set(ItemStack item, String key, int value) {
        CraftItemStack craftItem = (CraftItemStack) item;
        net.minecraft.server.v1_8_R3.ItemStack stack = craftItem.handle;

        NBTTagCompound tag;
        if (stack.tag != null)
            tag = stack.tag;
        else tag = new NBTTagCompound();

        tag.setInt(key, value);
        stack.tag = tag;
    }

    public static void set(ItemStack item, String key, boolean value) {
        CraftItemStack craftItem = (CraftItemStack) item;
        net.minecraft.server.v1_8_R3.ItemStack stack = craftItem.handle;

        NBTTagCompound tag;
        if (stack.tag != null)
            tag = stack.tag;
        else tag = new NBTTagCompound();

        tag.setBoolean(key, value);
        stack.tag = tag;
    }

    public static void set(ItemStack item, String key, long value) {
        CraftItemStack craftItem = (CraftItemStack) item;
        net.minecraft.server.v1_8_R3.ItemStack stack = craftItem.handle;

        NBTTagCompound tag;
        if (stack.tag != null)
            tag = stack.tag;
        else tag = new NBTTagCompound();

        tag.setLong(key, value);
        stack.tag = tag;
    }

    public static void set(ItemStack item, String key, short value) {
        CraftItemStack craftItem = (CraftItemStack) item;
        net.minecraft.server.v1_8_R3.ItemStack stack = craftItem.handle;

        NBTTagCompound tag;
        if (stack.tag != null)
            tag = stack.tag;
        else tag = new NBTTagCompound();

        tag.setShort(key, value);
        stack.tag = tag;
    }

    public static void set(ItemStack item, String key, byte value) {
        CraftItemStack craftItem = (CraftItemStack) item;
        net.minecraft.server.v1_8_R3.ItemStack stack = craftItem.handle;

        NBTTagCompound tag;
        if (stack.tag != null)
            tag = stack.tag;
        else tag = new NBTTagCompound();

        tag.setByte(key, value);
        stack.tag = tag;
    }

    public static void set(ItemStack item, String key, byte[] value) {
        CraftItemStack craftItem = (CraftItemStack) item;
        net.minecraft.server.v1_8_R3.ItemStack stack = craftItem.handle;

        NBTTagCompound tag;
        if (stack.tag != null)
            tag = stack.tag;
        else tag = new NBTTagCompound();

        tag.setByteArray(key, value);
        stack.tag = tag;
    }

    public static void set(ItemStack item, String key, int[] value) {
        CraftItemStack craftItem = (CraftItemStack) item;
        net.minecraft.server.v1_8_R3.ItemStack stack = craftItem.handle;

        NBTTagCompound tag;
        if (stack.tag != null)
            tag = stack.tag;
        else tag = new NBTTagCompound();

        tag.setIntArray(key, value);
        stack.tag = tag;
    }

    public static void set(ItemStack item, String key, float value) {
        CraftItemStack craftItem = (CraftItemStack) item;
        net.minecraft.server.v1_8_R3.ItemStack stack = craftItem.handle;

        NBTTagCompound tag;
        if (stack.tag != null)
            tag = stack.tag;
        else tag = new NBTTagCompound();

        tag.setFloat(key, value);
        stack.tag = tag;
    }

    public static void set(ItemStack item, String key, NBTBase value) {
        CraftItemStack craftItem = (CraftItemStack) item;
        net.minecraft.server.v1_8_R3.ItemStack stack = craftItem.handle;

        NBTTagCompound tag;
        if (stack.tag != null)
            tag = stack.tag;
        else tag = new NBTTagCompound();

        tag.set(key, value);
        stack.tag = tag;
    }

    public static String getString(ItemStack item, String key) {
        CraftItemStack craftItem = (CraftItemStack) item;
        net.minecraft.server.v1_8_R3.ItemStack stack = craftItem.handle;

        if (stack.tag == null)
            return null;

        NBTTagCompound tag = stack.tag;
        return tag.getString(key);
    }

    public static double getDouble(ItemStack item, String key) {
        CraftItemStack craftItem = (CraftItemStack) item;
        net.minecraft.server.v1_8_R3.ItemStack stack = craftItem.handle;

        if (stack.tag == null)
            return 0.0D;

        NBTTagCompound tag = stack.tag;
        return tag.getDouble(key);
    }

    public static int getInt(ItemStack item, String key) {
        CraftItemStack craftItem = (CraftItemStack) item;
        net.minecraft.server.v1_8_R3.ItemStack stack = craftItem.handle;

        if (stack.tag == null)
            return 0;

        NBTTagCompound tag = stack.tag;
        return tag.getInt(key);
    }

    public static boolean getBoolean(ItemStack item, String key) {
        CraftItemStack craftItem = (CraftItemStack) item;
        net.minecraft.server.v1_8_R3.ItemStack stack = craftItem.handle;

        if (stack.tag == null)
            return false;

        NBTTagCompound tag = stack.tag;
        return tag.getBoolean(key);
    }

    public static long getLong(ItemStack item, String key) {
        CraftItemStack craftItem = (CraftItemStack) item;
        net.minecraft.server.v1_8_R3.ItemStack stack = craftItem.handle;

        if (stack.tag == null)
            return 0L;

        NBTTagCompound tag = stack.tag;
        return tag.getLong(key);
    }

    public static short getShort(ItemStack item, String key) {
        CraftItemStack craftItem = (CraftItemStack) item;
        net.minecraft.server.v1_8_R3.ItemStack stack = craftItem.handle;

        if (stack.tag == null)
            return 0;

        NBTTagCompound tag = stack.tag;
        return tag.getShort(key);
    }

    public static byte getByte(ItemStack item, String key) {
        CraftItemStack craftItem = (CraftItemStack) item;
        net.minecraft.server.v1_8_R3.ItemStack stack = craftItem.handle;

        if (stack.tag == null)
            return 0;

        NBTTagCompound tag = stack.tag;
        return tag.getByte(key);
    }

    public static byte[] getByteArray(ItemStack item, String key) {
        CraftItemStack craftItem = (CraftItemStack) item;
        net.minecraft.server.v1_8_R3.ItemStack stack = craftItem.handle;

        if (stack.tag == null)
            return new byte[0];

        NBTTagCompound tag = stack.tag;
        return tag.getByteArray(key);
    }

    public static int[] getIntArray(ItemStack item, String key) {
        CraftItemStack craftItem = (CraftItemStack) item;
        net.minecraft.server.v1_8_R3.ItemStack stack = craftItem.handle;

        if (stack.tag == null)
            return new int[0];

        NBTTagCompound tag = stack.tag;
        return tag.getIntArray(key);
    }

    public static float getFloat(ItemStack item, String key) {
        CraftItemStack craftItem = (CraftItemStack) item;
        net.minecraft.server.v1_8_R3.ItemStack stack = craftItem.handle;

        if (stack.tag == null)
            return 0.0F;

        NBTTagCompound tag = stack.tag;
        return tag.getFloat(key);
    }

    public static NBTBase getBase(ItemStack item, String key) {
        CraftItemStack craftItem = (CraftItemStack) item;
        net.minecraft.server.v1_8_R3.ItemStack stack = craftItem.handle;

        if (stack.tag == null)
            return null;

        NBTTagCompound tag = stack.tag;
        return tag.get(key);
    }

}

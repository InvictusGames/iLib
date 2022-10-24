package cc.invictusgames.ilib.builder;

import cc.invictusgames.ilib.utils.ReflectionUtil;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author langgezockt (langgezockt@gmail.com)
 * 06.12.2018 / 18:26
 * BuilderAPI / de.langgezockt.BuilderAPI
 */

public class ItemBuilder implements Cloneable {

    public static final Enchantment GLOW = new EnchantmentWrapper(-1) {
        @Override
        public String getName() {
            return "glow";
        }

        @Override
        public int getMaxLevel() {
            return 1;
        }

        @Override
        public int getStartLevel() {
            return 1;
        }

        @Override
        public EnchantmentTarget getItemTarget() {
            return EnchantmentTarget.ALL;
        }

        @Override
        public boolean conflictsWith(Enchantment other) {
            return false;
        }

        @Override
        public boolean canEnchantItem(ItemStack item) {
            return true;
        }
    };

    static {
        Field field = ReflectionUtil.getField(Enchantment.class, "acceptingNew");
        ReflectionUtil.setFieldValue(field, null, true);
        Enchantment.registerEnchantment(GLOW);
        ReflectionUtil.setFieldValue(field, null, false);
    }

    private final ItemStack itemStack;
    private final ItemMeta itemMeta;

    public ItemBuilder(Material material) {
        this.itemStack = new ItemStack(material);
        this.itemMeta = itemStack.getItemMeta();
    }

    public ItemBuilder(Material material, short subID) {
        this.itemStack = new ItemStack(material, 1, subID);
        this.itemMeta = itemStack.getItemMeta();
    }

    public ItemBuilder(Material material, int subID) {
        this.itemStack = new ItemStack(material, 1, (short) subID);
        this.itemMeta = itemStack.getItemMeta();
    }

    public ItemBuilder(ItemStack itemStack) {
        this.itemStack = itemStack;
        this.itemMeta = itemStack.getItemMeta();
    }

    public ItemBuilder setDisplayName(String name) {
        this.itemMeta.setDisplayName(name);
        return this;
    }

    public ItemBuilder setLore(String... lore) {
        this.itemMeta.setLore(Arrays.asList(lore));
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        this.itemMeta.setLore(lore);
        return this;
    }

    public ItemBuilder addToLore(String... entries) {
        List<String> lore = itemMeta.hasLore() ? itemMeta.getLore() : new ArrayList<>();
        lore.addAll(Arrays.asList(entries));
        itemMeta.setLore(lore);
        return this;
    }

    public ItemBuilder addEnchantment(Enchantment enchantment, int level) {
        this.itemMeta.addEnchant(enchantment, level, true);
        return this;
    }

    public ItemBuilder storeEnchantment(Enchantment enchantment, int level) {
        if (this.itemMeta instanceof EnchantmentStorageMeta)
            ((EnchantmentStorageMeta) this.itemMeta).addStoredEnchant(enchantment, level, true);
        return this;
    }

    public ItemBuilder glowing(boolean glowing) {
        if (glowing)
            itemMeta.addEnchant(GLOW, 1, true);
        else itemMeta.removeEnchant(GLOW);
        return this;
    }

    public ItemBuilder setAmount(int amount) {
        this.itemStack.setAmount(amount);
        return this;
    }

    public ItemBuilder setUnbreakable(Boolean unbreakable) {
        this.itemMeta.spigot().setUnbreakable(unbreakable);
        return this;
    }

    public ItemBuilder setSkullOwner(String owner) {
        if (this.itemMeta instanceof SkullMeta)
            ((SkullMeta) this.itemMeta).setOwner(owner);
        return this;
    }

    public ItemBuilder setArmorColor(Color color) {
        if (this.itemMeta instanceof LeatherArmorMeta)
            ((LeatherArmorMeta) this.itemMeta).setColor(color);
        return this;
    }

    public ItemBuilder setData(short data) {
        this.itemStack.setDurability(data);
        return this;
    }

    public ItemBuilder setData(int data) {
        this.itemStack.setDurability((short) data);
        return this;
    }

    public ItemBuilder setBookAuthor(String author) {
        if (this.itemMeta instanceof BookMeta)
            ((BookMeta) this.itemMeta).setAuthor(author);
        return this;
    }

    public ItemBuilder setBookTitle(String title) {
        if (this.itemMeta instanceof BookMeta)
            ((BookMeta) this.itemMeta).setTitle(title);
        return this;
    }

    public ItemBuilder setBookPages(String... pages) {
        if (this.itemMeta instanceof BookMeta)
            ((BookMeta) this.itemMeta).setPages(pages);
        return this;
    }

    public ItemBuilder setBookPages(List<String> pages) {
        if (this.itemMeta instanceof BookMeta)
            ((BookMeta) this.itemMeta).setPages(pages);
        return this;
    }

    public ItemBuilder addFlag(ItemFlag... flags) {
        this.itemMeta.addItemFlags(flags);
        return this;
    }

    public ItemStack build() {
        this.itemStack.setItemMeta(this.itemMeta);
        return itemStack.clone();
    }

    public ItemBuilder clone() {
        return new ItemBuilder(build());
    }
}

package cc.invictusgames.ilib.menu.hotbaritem;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 11.02.2020 / 23:48
 * iLib / cc.invictusgames.ilib.menu.hotbaritem
 */

public abstract class HotbarItem {

    public static final ConcurrentHashMap<UUID, ConcurrentHashMap<String, HotbarItem>> HOTBAR_ITEMS = new ConcurrentHashMap<>();

    public HotbarItem(Player player) {
        if (!HotbarItem.HOTBAR_ITEMS.containsKey(player.getUniqueId())) {
            HotbarItem.HOTBAR_ITEMS.put(player.getUniqueId(), new ConcurrentHashMap<>());
        }

        HotbarItem.HOTBAR_ITEMS.get(player.getUniqueId()).put(this.getClass().getSimpleName(), this);
    }

    public HotbarItem(Player player, String id) {
        if (!HotbarItem.HOTBAR_ITEMS.containsKey(player.getUniqueId())) {
            HotbarItem.HOTBAR_ITEMS.put(player.getUniqueId(), new ConcurrentHashMap<>());
        }

        HotbarItem.HOTBAR_ITEMS.get(player.getUniqueId()).put(id, this);
    }

    public abstract ItemStack getItem();

    public abstract void click(Action action, Block block);

    public abstract void clickEntity(Entity entity);

    public boolean hasCoolDown() {
        return true;
    }

    public static void unregisterItem(Player player, String id) {
        if (!HotbarItem.HOTBAR_ITEMS.containsKey(player.getUniqueId())) {
            return;
        }

        HotbarItem.HOTBAR_ITEMS.get(player.getUniqueId()).remove(id);
    }

    public static void unregisterItem(Player player, Class<? extends HotbarItem> clazz) {
        unregisterItem(player, clazz.getSimpleName());
    }

}

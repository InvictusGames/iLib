package cc.invictusgames.ilib.menu;

import cc.invictusgames.ilib.ILibBukkitPlugin;
import cc.invictusgames.ilib.builder.ItemBuilder;
import cc.invictusgames.ilib.menu.fill.FillTemplate;
import cc.invictusgames.ilib.menu.fill.IMenuFiller;
import cc.invictusgames.ilib.menu.page.PagedMenu;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutWindowItems;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author langgezockt (langgezockt@gmail.com)
 * 17.06.2019 / 20:05
 * iUtils / cc.invictusgames.iutils.utils.menu
 */

public abstract class Menu {

    @Getter
    private static Map<Player, Menu> openedMenus = new HashMap<>();

    private static Method openInventoryMethod;

    private Map<Integer, Button> buttons = new HashMap<>();
    private Inventory inventory;

    @Setter
    private boolean cancelIncomingUpdates = false;

    @Getter
    @Setter
    private BukkitTask updateRunnable;

    public abstract String getTitle(Player player);

    public abstract Map<Integer, Button> getButtons(Player player);

    public int calculateSize(Map<Integer, Button> buttons) {
        int highest = 0;

        for (int buttonValue : buttons.keySet()) {
            if (buttonValue > highest) {
                highest = buttonValue;
            }
        }

        return (int) (Math.ceil((highest + 1) / 9D) * 9D);
    }

    public void openMenu(Player player) {
        this.buttons = this.getButtons(player);
        int size = this.getSize() == -1 ? this.calculateSize(buttons) : this.getSize();
        boolean update = false;

        String title = this.getTitle(player);
        if (title.length() > 32) {
            title = title.substring(0, 32);
        }

        Inventory inventory = Bukkit.createInventory(player, size, title);
        Menu previousMenu = openedMenus.get(player);

        /*if (player.getOpenInventory().getTopInventory() != null) {
            if (previousMenu != null) {
                previousMenu.setCancelIncomingUpdates(true);
                if (previousMenu.getUpdateRunnable() != null) {
                    previousMenu.getUpdateRunnable().cancel();
                }
            }
            int previousSize = player.getOpenInventory().getTopInventory().getSize();
            if ((previousSize == size) && (!title.equals(player.getOpenInventory().getTopInventory().getTitle()))) {
                updateInventory(player, title, null);
                inventory = player.getOpenInventory().getTopInventory();
                update = true;
            }

        }*/

        if (player.getOpenInventory().getTopInventory() != null) {
            if (previousMenu != null) {
                previousMenu.setCancelIncomingUpdates(true);
                if (previousMenu.getUpdateRunnable() != null)
                    previousMenu.getUpdateRunnable().cancel();
            }

            int previousSize = player.getOpenInventory().getTopInventory().getSize();
            String previousTitle = player.getOpenInventory().getTopInventory().getTitle();
            if (previousSize == size && previousTitle.equalsIgnoreCase(title)) {
                inventory = player.getOpenInventory().getTopInventory();
                update = true;
            }
        }

        if (getMenuFiller() != null)
            getMenuFiller().fill(this, player, buttons, size);

        for (Map.Entry<Integer, Button> buttonEntry : buttons.entrySet())
            inventory.setItem(buttonEntry.getKey(), buttonEntry.getValue().getItem(player));

        for (int i = 0; i < inventory.getContents().length; i++) {
            if ((buttons.get(i) == null) && (inventory.getItem(i) != null) && (inventory.getItem(i).getType() != Material.AIR)) {
                inventory.setItem(i, new ItemBuilder(Material.AIR).build());
            }
        }

        this.inventory = inventory;

        if (update) {
            player.updateInventory();
        } else {
            player.openInventory(this.inventory);
        }
        this.startUpdateTask(player, this instanceof PagedMenu);
        this.onOpen(player);
        openedMenus.put(player, this);
        cancelIncomingUpdates = false;
    }

    public void onOpen(Player player) {

    }

    public void onClose(Player player) {

    }

    public boolean isAutoUpdate() {
        return true;
    }

    public boolean isClickUpdate() {
        return false;
    }

    public int getSize() {
        return -1;
    }

    public FillTemplate getFillTemplate() {
        return null;
    }

    public IMenuFiller getMenuFiller() {
        return getFillTemplate() == null ? null : getFillTemplate().getMenuFiller();
    }

    public ItemStack getPlaceholderItem(Player player) {
        return Button.createPlaceholder().getItem(player);
    }

    public boolean cancelLowerClicks() {
        return true;
    }

    public boolean cancelClicks() {
        return true;
    }

    private void updateInventory(Player player, String title, List<net.minecraft.server.v1_8_R3.ItemStack> items) {
        EntityPlayer ep = ((CraftPlayer) player).getHandle();
        if (items != null) {
            PacketPlayOutWindowItems itemsPacket = new PacketPlayOutWindowItems(ep.activeContainer.windowId, items);
            ep.playerConnection.sendPacket(itemsPacket);
        }

        ep.updateInventory(ep.activeContainer);
    }

    private static Method getOpenInventoryMethod() {
        if (Menu.openInventoryMethod == null) {
            try {
                (Menu.openInventoryMethod = CraftHumanEntity.class.getDeclaredMethod("openCustomInventory",
                        Inventory.class, EntityPlayer.class, Integer.TYPE)).setAccessible(true);
            } catch (NoSuchMethodException ex) {
                ex.printStackTrace();
            }
        }
        return Menu.openInventoryMethod;
    }

    public void startUpdateTask(Player player, boolean pagedMenu) {
        if (!this.isAutoUpdate()) {
            return;
        }

        if (this.updateRunnable != null) {
            return;
        }

        this.updateRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                if ((player == null) || (!player.isOnline())) {
                    this.cancel();
                    return;
                }

                updateInventory(player, pagedMenu);
            }
        }.runTaskTimerAsynchronously(ILibBukkitPlugin.getInstance(), 20L, 20L);
    }

    public void updateInventory(Player player, boolean pagedMenu) {
        if (cancelIncomingUpdates) {
            return;
        }
        buttons = getButtons(player);
        int size = getSize() == -1 ? calculateSize(buttons) : getSize();

        if (getMenuFiller() != null)
            getMenuFiller().fill(this, player, buttons, size);

        for (Map.Entry<Integer, Button> buttonEntry : buttons.entrySet())
            inventory.setItem(buttonEntry.getKey(), buttonEntry.getValue().getItem(player));

        for (int i = 0; i < inventory.getContents().length; i++) {
            if ((buttons.get(i) == null) && (inventory.getItem(i) != null) && (inventory.getItem(i).getType() != Material.AIR)) {
                inventory.setItem(i, new ItemBuilder(Material.AIR).build());
            }
        }
        player.getOpenInventory().getTopInventory().setContents(inventory.getContents());
    }

    public int getSlot(int row, int slot) {
        return 9 * row + slot;
    }

}

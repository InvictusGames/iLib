package cc.invictusgames.ilib.menu.listener;

import cc.invictusgames.ilib.menu.Button;
import cc.invictusgames.ilib.menu.Menu;
import cc.invictusgames.ilib.menu.hotbaritem.HotbarItem;
import cc.invictusgames.ilib.menu.page.PagedMenu;
import cc.invictusgames.ilib.timebased.TimeBasedContainer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author langgezockt (langgezockt@gmail.com)
 * 22.06.2019 / 11:51
 * iUtils / cc.invictusgames.iutils.utils.menu
 */

public class MenuListener implements Listener {

    private final TimeBasedContainer<UUID> clickCooldown = new TimeBasedContainer<>(500, TimeUnit.MILLISECONDS);
    private final TimeBasedContainer<UUID> entityCooldown = new TimeBasedContainer<>(500, TimeUnit.MILLISECONDS);

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Menu menu = Menu.getOpenedMenus().get(player);

        if (menu == null) {
            return;
        }

        if (event.getClickedInventory() == null) {
            return;
        }

        if (event.getClickedInventory() != player.getOpenInventory().getTopInventory()) {
            if (menu.cancelLowerClicks())
                event.setCancelled(true);
            return;
        }

        if (menu.cancelClicks())
            event.setCancelled(true);

        int slot = event.getSlot();
        Map<Integer, Button> buttons = menu.getButtons(player);

        if (buttons.containsKey(slot)) {
            Button button = buttons.get(slot);
            button.click(player, slot, event.getClick(), event.getHotbarButton());
            event.setCancelled(button.isCancelClick());
            Button.ButtonClickSound sound = button.getClickSound(player);
            if (sound != null) {
                player.playSound(player.getLocation(), sound.getSound(), sound.getVolume(), sound.getPitch());
            }
            if (menu.isClickUpdate()) {
                menu.updateInventory(player, menu instanceof PagedMenu);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        Menu menu = Menu.getOpenedMenus().get(player);

        if (menu == null) {
            return;
        }

        menu.setCancelIncomingUpdates(true);
        menu.onClose(player);
        if (menu.getUpdateRunnable() != null) {
            menu.getUpdateRunnable().cancel();
            menu.setUpdateRunnable(null);
        }
        Menu.getOpenedMenus().remove(player);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Menu menu = Menu.getOpenedMenus().get(player);

        if (menu == null) {
            return;
        }

        menu.onClose(player);
        menu.getUpdateRunnable().cancel();
        menu.setUpdateRunnable(null);
        Menu.getOpenedMenus().remove(player);
        HotbarItem.HOTBAR_ITEMS.remove(player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Map<String, HotbarItem> items = HotbarItem.HOTBAR_ITEMS.getOrDefault(player.getUniqueId(),
                new ConcurrentHashMap<>());
        if ((event.getItem() == null) || (event.getItem().getType() == Material.AIR)) {
            return;
        }

        if (event.getAction() == Action.PHYSICAL) {
            return;
        }

        for (HotbarItem item : items.values()) {
            if (item.getItem().isSimilar(event.getItem())) {
                event.setCancelled(true);

                if (clickCooldown.contains(player.getUniqueId()))
                    continue;

                item.click(event.getAction(), event.getClickedBlock());

                if (item.hasCoolDown())
                    clickCooldown.add(player.getUniqueId());
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Map<String, HotbarItem> items = HotbarItem.HOTBAR_ITEMS.getOrDefault(player.getUniqueId(),
                new ConcurrentHashMap<>());
        if ((event.getPlayer().getItemInHand() == null) || (event.getPlayer().getItemInHand().getType() == Material.AIR)) {
            return;
        }

        for (HotbarItem item : items.values()) {
            if (item.getItem().isSimilar(player.getItemInHand())) {
                event.setCancelled(true);

                if (entityCooldown.contains(player.getUniqueId()))
                    continue;

                item.clickEntity(event.getRightClicked());

                if (item.hasCoolDown())
                    entityCooldown.add(player.getUniqueId());
            }
        }
    }

}

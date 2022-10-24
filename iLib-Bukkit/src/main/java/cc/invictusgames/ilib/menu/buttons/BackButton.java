package cc.invictusgames.ilib.menu.buttons;

import cc.invictusgames.ilib.builder.ItemBuilder;
import cc.invictusgames.ilib.menu.Button;
import cc.invictusgames.ilib.menu.Menu;
import cc.invictusgames.ilib.utils.CC;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

/**
 * @author langgezockt (langgezockt@gmail.com)
 * 09.11.2019 / 15:05
 * iLib / cc.invictusgames.ilib.menu.buttons
 */

public class BackButton extends Button {

    private Menu menu;

    public BackButton(Menu menu) {
        this.menu = menu;
    }

    @Override
    public ItemStack getItem(Player player) {
        /*List<String> lore = new ArrayList<>();
        if (menu instanceof PagedMenu) {
            lore.add(CC.GRAY + "To: " + ((PagedMenu) menu).getRawTitle(player));
        } else {
            lore.add(CC.GRAY + "To: " + menu.getTitle(player));
        }*/
        return new ItemBuilder(Material.BED).setDisplayName(CC.RED + CC.BOLD + "Go Back").build();
    }

    @Override
    public void click(Player player, int slot, ClickType clickType, int hotbarButton) {
        menu.openMenu(player);
    }
}

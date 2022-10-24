package cc.invictusgames.ilib.menu.buttons;

import cc.invictusgames.ilib.builder.ItemBuilder;
import cc.invictusgames.ilib.menu.Button;
import cc.invictusgames.ilib.utils.CC;
import cc.invictusgames.ilib.utils.callback.TypeCallable;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 30.12.2019 / 04:32
 * iLib / cc.invictusgames.ilib.menu.buttons
 */

public class ConfirmationButton extends Button {

    private boolean bool;
    private TypeCallable<Boolean> callable;
    private String name;

    public ConfirmationButton(boolean bool, TypeCallable<Boolean> callable) {
        this.bool = bool;
        this.callable = callable;
    }

    public ConfirmationButton(boolean bool, String name, TypeCallable<Boolean> callable) {
        this.bool = bool;
        this.callable = callable;
        this.name = name;
    }

    @Override
    public ItemStack getItem(Player player) {
        return new ItemBuilder(Material.WOOL, (short) (bool ? 5 : 14))
                .setDisplayName(bool ? CC.GREEN + CC.BOLD + name : CC.RED + CC.BOLD + name)
                .build();
    }

    @Override
    public void click(Player player, int slot, ClickType clickType, int hotbarButton) {
        player.closeInventory();
        callable.callback(bool);
    }
}

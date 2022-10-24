package cc.invictusgames.ilib.deathmessage.killmessage;

import cc.invictusgames.ilib.builder.ItemBuilder;
import cc.invictusgames.ilib.menu.Button;
import cc.invictusgames.ilib.menu.Menu;
import cc.invictusgames.ilib.menu.fill.FillTemplate;
import cc.invictusgames.ilib.playersetting.impl.ILibSettings;
import cc.invictusgames.ilib.utils.CC;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class KillMessageMenu extends Menu {
    @Override
    public String getTitle(Player player) {
        return "Kill Messages";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        int row = 1;
        int slot = 1;

        for (KillMessage killMessage : KillMessage.values()) {
            if (slot == 8) {
                row++;
                slot = 1;
            }

            buttons.put(
                    getSlot(row, slot++),
                    new KillMessageButton(killMessage)
            );
        }

        // this is very ugly, but has to be done because otherwise the menu will miss it's last border row
        buttons.put(getSlot(++row, 8), Button.createPlaceholder(getPlaceholderItem(player)));

        return buttons;
    }

    @Override
    public boolean isClickUpdate() {
        return true;
    }

    @Override
    public FillTemplate getFillTemplate() {
        return FillTemplate.BORDER;
    }

    @RequiredArgsConstructor
    public class KillMessageButton extends Button {

        private final KillMessage killMessage;

        @Override
        public ItemStack getItem(Player player) {
            KillMessage active = ILibSettings.KILL_MESSAGE.get(player);
            ItemBuilder builder = new ItemBuilder(Material.NAME_TAG)
                    .setDisplayName((active == this.killMessage ? CC.GREEN : CC.RED)
                            + CC.BOLD + this.killMessage.getDisplay());

            builder.addToLore(
                    " ",
                    this.killMessage.formatPvP(
                            CC.translate("&cPlayer&4[0]"),
                            CC.format("&a%s&4[0]", player.getName())
                    ),
                    " "
            );

            if (!this.killMessage.canAccess(player))
                builder.addToLore(CC.RED + CC.BOLD + "You don't own this kill message.");
            else if (active == killMessage)
                builder.addToLore(CC.RED + "This kill message is equipped.");
            else builder.addToLore(CC.GREEN + "Click to equip this kill message.");

            if (player.isOp())
                builder.addToLore(
                        " ",
                        CC.DGRAY + killMessage.getPermission()
                );

            return builder.build();
        }

        @Override
        public void click(Player player, int slot, ClickType clickType, int hotbarButton) {
            if (!killMessage.canAccess(player))
                return;

            KillMessage active = ILibSettings.KILL_MESSAGE.get(player);
            if (active == killMessage)
                return;

            ILibSettings.KILL_MESSAGE.set(player, killMessage);
        }
    }
}

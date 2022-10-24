package cc.invictusgames.ilib.playersetting.menu;

import cc.invictusgames.ilib.menu.Button;
import cc.invictusgames.ilib.menu.Menu;
import cc.invictusgames.ilib.menu.fill.FillTemplate;
import cc.invictusgames.ilib.playersetting.PlayerSetting;
import cc.invictusgames.ilib.playersetting.PlayerSettingService;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class SettingsMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return "Settings";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        List<PlayerSetting> remaining = new ArrayList<>();
        List<PlayerSetting> completeRows = new ArrayList<>();

        int i = 0;
        for (PlayerSetting setting : PlayerSettingService.getAllSettings()) {
            if (!setting.canUpdate(player))
                continue;

            if (i >= 4) {
                i = 0;
                completeRows.addAll(remaining);
                remaining.clear();
            }

            remaining.add(setting);
            i++;
        }

        int slot = 1;
        int row = 0;
        for (PlayerSetting setting : completeRows) {
            if (slot > 8) {
                slot = 1;
                row++;
            }

            buttons.put(getSlot(row, slot), new SettingButton(setting));
            slot += 2;
        }

        if (!completeRows.isEmpty())
            row++;
        int[] slots = getSlots(remaining.size());
        int j = 0;
        for (PlayerSetting setting : remaining) {
            buttons.put(getSlot(row, slots[j++]), new SettingButton(setting));
        }

        return buttons;
    }

    @Override
    public FillTemplate getFillTemplate() {
        return FillTemplate.FILL;
    }

    @Override
    public boolean isClickUpdate() {
        return true;
    }

    private int[] getSlots(int size) {
        switch (size) {
            case 1: {
                return new int[] { 4 };
            }

            case 2: {
                return new int[] { 3, 5 };
            }

            case 3: {
                return new int[] { 2, 4, 6};
            }

            default: {
                return new int[] { 1, 3, 5, 7};
            }
        }
    };

    @RequiredArgsConstructor
    public class SettingButton extends Button {

        private final PlayerSetting setting;

        @Override
        public ItemStack getItem(Player player) {
            return setting.getIcon(player);
        }

        @Override
        public void click(Player player, int slot, ClickType clickType, int hotbarButton) {
            if (!setting.canUpdate(player))
                return;

           setting.click(player, clickType);
        }
    }

}

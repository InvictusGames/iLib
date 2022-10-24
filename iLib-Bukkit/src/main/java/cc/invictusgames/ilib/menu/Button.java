package cc.invictusgames.ilib.menu;

import cc.invictusgames.ilib.builder.ItemBuilder;
import lombok.Data;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

/**
 * @author langgezockt (langgezockt@gmail.com)
 * 17.06.2019 / 20:16
 * iUtils / cc.invictusgames.iutils.utils.menu
 */

public abstract class Button {

    public abstract ItemStack getItem(Player player);

    public void click(Player player, int slot, ClickType clickType, int hotbarButton) {

    }

    public ButtonClickSound getClickSound(Player player) {
        return null;
    }

    public boolean isCancelClick() {
        return true;
    }

    public static Button createPlaceholder() {
        return createPlaceholder(" ", Material.STAINED_GLASS_PANE, (short) 15);
    }

    public static Button createPlaceholder(Material material, short subId) {
        return createPlaceholder(" ", material, subId);
    }

    public static Button createPlaceholder(String displayName, Material material, short subId) {
        return new Button() {
            @Override
            public ItemStack getItem(Player player) {
                return new ItemBuilder(material, subId)
                        .setDisplayName(displayName)
                        .build();
            }
        };
    }

    public static Button createPlaceholder(ItemStack item) {
        return new Button() {
            @Override
            public ItemStack getItem(Player player) {
                return item;
            }
        };
    }

    @Data
    public class ButtonClickSound {

        public ButtonClickSound(Sound sound) {
            this(sound, 1.0F, 1.0F);
        }

        public ButtonClickSound(Sound sound, float volume, float pitch) {
            this.sound = sound;
            this.volume = volume;
            this.pitch = pitch;
        }

        private Sound sound;
        private float volume = 1F;
        private float pitch = 1F;

    }

}

package cc.invictusgames.ilib.playersetting.impl;

import cc.invictusgames.ilib.builder.ItemBuilder;
import cc.invictusgames.ilib.deathmessage.killmessage.KillMessage;
import cc.invictusgames.ilib.deathmessage.killmessage.KillMessageMenu;
import cc.invictusgames.ilib.playersetting.PlayerSetting;
import cc.invictusgames.ilib.playersetting.PlayerSettingProvider;
import cc.invictusgames.ilib.utils.AdminBypass;
import cc.invictusgames.ilib.utils.CC;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ILibSettings implements PlayerSettingProvider {

    public static final BooleanSetting GLOBAL_CHAT = new BooleanSetting("ilib", "global_chat") {
        @Override
        public String getDisplayName() {
            return "Global Chat";
        }

        @Override
        public String getEnabledText() {
            return "Global chat is shown";
        }

        @Override
        public String getDisabledText() {
            return "Global chat is hidden";
        }

        @Override
        public List<String> getDescription() {
            return Arrays.asList(
                    CC.YELLOW + "If enabled, you will see messages",
                    CC.YELLOW + "sent in the global chat channel."
            );
        }

        @Override
        public MaterialData getMaterial() {
            return new MaterialData(Material.BOOK_AND_QUILL);
        }

        @Override
        public Boolean getDefaultValue() {
            return true;
        }
    };

    public static final PlayerSetting<KillMessage> KILL_MESSAGE
            = new PlayerSetting<KillMessage>("ilib", "kill_message") {
        @Override
        public KillMessage getDefaultValue() {
            return KillMessage.DEFAULT;
        }

        @Override
        public KillMessage parse(String input) {
            try {
                return KillMessage.valueOf(input);
            } catch (IllegalArgumentException e) {
                return KillMessage.DEFAULT;
            }
        }

        @Override
        public ItemStack getIcon(Player player) {
            return new ItemBuilder(Material.NAME_TAG)
                    .setDisplayName(CC.YELLOW + CC.BOLD + "Kill Message")
                    .setLore(
                            " ",
                            CC.YELLOW + "Click to select a kill message.",
                            " ",
                            CC.YELLOW + "Active: " + CC.RED + get(player).getDisplay()
                    ).build();
        }

        @Override
        public void click(Player player, ClickType clickType) {
            new KillMessageMenu().openMenu(player);
        }

        @Override
        public boolean canUpdate(Player player) {
            return AdminBypass.isBypassing(player);
        }

        @Override
        public KillMessage get(Player player) {
            KillMessage killMessage = super.get(player);
            if (!killMessage.canAccess(player)) {
                killMessage = getDefaultValue();
                set(player, killMessage);
            }
            return killMessage;
        }
    };

    @Override
    public List<PlayerSetting> getProvidedSettings() {
        return Arrays.asList(
                GLOBAL_CHAT,
                KILL_MESSAGE
        );
    }

    @Override
    public int getPriority() {
        return 0;
    }
}

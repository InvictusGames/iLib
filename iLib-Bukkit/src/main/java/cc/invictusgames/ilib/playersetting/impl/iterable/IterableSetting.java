package cc.invictusgames.ilib.playersetting.impl.iterable;

import cc.invictusgames.ilib.builder.ItemBuilder;
import cc.invictusgames.ilib.playersetting.PlayerSetting;
import cc.invictusgames.ilib.utils.CC;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.List;

public abstract class IterableSetting<E extends ISettingIterable> extends PlayerSetting<E> {

    private static final String ENABLED_ARROW = CC.YELLOW + CC.BOLD + "  ► ";
    private static final String DISABLED_SPACER = "    ";

    public IterableSetting(String parent, String key) {
        super(parent, key);
    }

    public abstract String getDisplayName();

    public abstract E[] getOptions();

    public abstract List<String> getDescription();

    public abstract MaterialData getMaterial();

    @Override
    public ItemStack getIcon(Player player) {
        List<String> lore = new ArrayList<>();
        lore.add(" ");
        lore.addAll(getDescription());
        lore.add(" ");

        for (E option : getOptions()) {
            if (option == get(player))
                lore.add(ENABLED_ARROW + CC.RED + option.getDisplayName());
            else lore.add(DISABLED_SPACER + CC.RED + option.getDisplayName());
        }

        return new ItemBuilder(getMaterial().getItemType(), getMaterial().getData())
                .setDisplayName(getDisplayName())
                .setLore(lore)
                .build();
    }

    @Override
    public void click(Player player, ClickType clickType) {
        int mod = clickType.isLeftClick() ? 1 : -1;
        int index = get(player).ordinal() + mod;
        E[] options = getOptions();

        if (index >= options.length)
            index = 0;

        if (index < 0)
            index = options.length - 1;

        set(player, options[index]);
    }
}

package cc.invictusgames.ilib.playersetting;

import cc.invictusgames.ilib.ILib;
import cc.invictusgames.ilib.utils.CC;
import com.google.common.base.Splitter;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@RequiredArgsConstructor
public abstract class PlayerSetting<T> {

    private final String parent;
    private final String key;
    private final Map<UUID, T> values = new HashMap<>();

    public abstract T getDefaultValue();

    public abstract T parse(String input);

    public T get(CommandSender sender) {
        if (!(sender instanceof Player))
            return getDefaultValue();

        return get((Player) sender);
    }

    public T get(Player player) {
        return values.getOrDefault(player.getUniqueId(), getDefaultValue());
    }

    public void set(Player player, T value) {
        values.put(player.getUniqueId(), value);
        ILib.getInstance().runTaskAsync(() -> ILib.getInstance().getRedisService().executeBackendCommand(redis ->
                redis.hset("playersettings:" + parent + ":" + key,
                        player.getUniqueId().toString(), toString(value))));
    }

    public void load(UUID uuid) {
        values.put(uuid, ILib.getInstance().getRedisService().executeBackendCommand(redis -> {
            if (!redis.hexists("playersettings:" + parent + ":" + key, uuid.toString()))
                return getDefaultValue();

            return parse(redis.hget("playersettings:" + parent + ":" + key, uuid.toString()));
        }));
    }

    public void remove(UUID uuid) {
        values.remove(uuid);
    }

    public abstract ItemStack getIcon(Player player);

    public abstract void click(Player player, ClickType clickType);

    public boolean canUpdate(Player player) {
        return true;
    }

    public String toString(T value) {
        return value == null ? null : value.toString();
    }

    public static List<String> splitDescription(String description) {
        List<String> list = new ArrayList<>();
        List<String> split = new ArrayList<>(Splitter.fixedLength(25).splitToList(description));

        for (int i = 0; i < split.size(); i++) {
            String s = split.get(i);
            if (i < split.size() - 1 && !s.endsWith(" ") && !split.get(i + 1).startsWith(" ")) {
                s = s + "-";
            }
            list.add(CC.YELLOW + s.trim());
        }
        return list;
    }

}

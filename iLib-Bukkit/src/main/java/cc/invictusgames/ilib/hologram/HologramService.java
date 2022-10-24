package cc.invictusgames.ilib.hologram;

import cc.invictusgames.ilib.ILib;
import cc.invictusgames.ilib.ILibBukkitPlugin;
import cc.invictusgames.ilib.hologram.config.HologramConfig;
import cc.invictusgames.ilib.hologram.config.HologramConfigEntry;
import cc.invictusgames.ilib.hologram.statics.StaticHologram;
import lombok.RequiredArgsConstructor;
import org.bukkit.World;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class HologramService {

    private static final Map<Integer, Hologram> CURRENT_HOLOGRAMS = new ConcurrentHashMap<>();

    private static int hologramId = 1;

    protected static int registerHologram(Hologram hologram) {
        int id = hologramId++;
        CURRENT_HOLOGRAMS.put(id, hologram);
        return id;
    }

    public static void unregisterHologram(int id) {
        CURRENT_HOLOGRAMS.remove(id);
    }

    public static Collection<Hologram> getHolograms() {
        return CURRENT_HOLOGRAMS.values();
    }

    private final ILib instance;

    private HologramConfig config;
    private final Map<Integer, StaticHologram> serializedHolograms = new HashMap<>();
    private final Map<String, Integer> nameMapping = new HashMap<>();

    public void load() {
        config = instance.getConfigurationService().loadConfiguration(HologramConfig.class,
                new File(ILibBukkitPlugin.getInstance().getDataFolder(), "holograms.json"));
    }

    public void loadWorld(World world) {
        for (HologramConfigEntry entry : config.getHolograms()) {
            if (entry.getLocation().getWorld().equals(world.getName())) {
                StaticHologram hologram = new HologramBuilder()
                        .at(entry.getLocation().getLocation())
                        .withSpacing(entry.getSpacing())
                        .staticHologram()
                        .addLines(entry.getLines())
                        .build();
                hologram.setName(entry.getName());
                hologram.spawn();
                serializedHolograms.put(hologram.getId(), hologram);
                nameMapping.put(entry.getName().toLowerCase(), hologram.getId());
            }
        }
    }

    public void save() {
        config.getHolograms().clear();
        for (StaticHologram hologram : serializedHolograms.values())
            config.getHolograms().add(hologram.toConfig());

        try {
            instance.getConfigurationService().saveConfiguration(config,
                    new File(ILibBukkitPlugin.getInstance().getDataFolder(), "holograms.json"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public StaticHologram getHologram(Integer id) {
        return serializedHolograms.get(id);
    }

    public StaticHologram getHologram(String name) {
        if (!nameMapping.containsKey(name.toLowerCase()))
            return null;

        return getHologram(nameMapping.get(name.toLowerCase()));
    }

    public void remove(StaticHologram hologram) {
        hologram.destroy();
        unregisterHologram(hologram.getId());
        serializedHolograms.remove(hologram.getId());
        if (hologram.getName() != null)
            nameMapping.remove(hologram.getName().toLowerCase());
    }

    public void register(StaticHologram hologram) {
        serializedHolograms.put(hologram.getId(), hologram);
        nameMapping.put(hologram.getName().toLowerCase(), hologram.getId());
    }

    public List<StaticHologram> getSerializedHolograms() {
        return Collections.unmodifiableList(new ArrayList<>(serializedHolograms.values()));
    }

}

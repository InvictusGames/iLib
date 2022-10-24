package cc.invictusgames.ilib.npc;

import cc.invictusgames.ilib.ILib;
import cc.invictusgames.ilib.ILibBukkitPlugin;
import cc.invictusgames.ilib.npc.config.NPCConfig;
import cc.invictusgames.ilib.npc.config.NPCConfigEntry;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.File;
import java.io.IOException;
import java.util.*;

@RequiredArgsConstructor
public class NPCService {

    private static final Map<UUID, NPC> CURRENT_NPCS = new HashMap<>();

    private static int npcId = 1;

    protected static int registerNpc(NPC npc) {
        CURRENT_NPCS.put(npc.getUuid(), npc);
        return npcId++;
    }

    public static Collection<NPC> getNpcs() {
        return Collections.unmodifiableCollection(CURRENT_NPCS.values());
    }

    public static NPC getNpc(UUID uuid) {
        return CURRENT_NPCS.get(uuid);
    }

    public static void unregisterNpc(UUID uuid) {
        CURRENT_NPCS.remove(uuid);
    }

    public static NPC getByEntityId(int id) {
        for (NPC npc : CURRENT_NPCS.values()) {
            if (npc.getEntityId() == id)
                return npc;
        }

        return null;
    }

    public static void startRotationTask() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(ILibBukkitPlugin.getInstance(), () -> {
            for (NPC npc : CURRENT_NPCS.values())
                npc.rotate(npc.getLocation().getYaw());
        }, 60L, 60L);
    }

    private final ILib instance;

    private NPCConfig config;
    private final Map<Integer, NPC> serializedNpcs = new HashMap<>();
    private final Map<String, Integer> nameMapping = new HashMap<>();

    public void load() {
        config = instance.getConfigurationService().loadConfiguration(NPCConfig.class,
                new File(ILibBukkitPlugin.getInstance().getDataFolder(), "npcs.json"));
    }

    public void loadWorld(World world) {
        for (NPCConfigEntry entry : config.getNpcs()) {
            if (entry.getLocation().getWorld().equals(world.getName())) {
                NPCBuilder builder = new NPCBuilder()
                        .at(entry.getLocation().getLocation())
                        .displayName(entry.getDisplayName());

                if (entry.getTexture() != null)
                    builder.skinTexture(entry.getTexture(), entry.getSignature());

                if (entry.getCommand() != null) {
                    if (entry.isConsoleCommand())
                        builder.consoleCommand(entry.getCommand());
                    else builder.command(entry.getCommand());
                }

                if (entry.getEquipment() != null)
                    entry.getEquipment().forEach(builder::withEquipment);

                NPC npc = builder.buildAndSpawn();
                npc.setName(entry.getName());
                serializedNpcs.put(npc.getId(), npc);
                nameMapping.put(entry.getName().toLowerCase(), npc.getId());
            }
        }
    }

    public void save() {
        config.getNpcs().clear();
        for (NPC npc : serializedNpcs.values())
            config.getNpcs().add(npc.toConfig());

        try {
            instance.getConfigurationService().saveConfiguration(config,
                    new File(ILibBukkitPlugin.getInstance().getDataFolder(), "npcs.json"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public NPC getNpc(Integer id) {
        return serializedNpcs.get(id);
    }

    public NPC getNpc(String name) {
        if (!nameMapping.containsKey(name.toLowerCase()))
            return null;

        return getNpc(nameMapping.get(name.toLowerCase()));
    }

    public void remove(NPC npc) {
        if (npc.isInitialized())
            npc.despawn();

        serializedNpcs.remove(npc.getId());
        if (npc.getName() != null)
            nameMapping.remove(npc.getName().toLowerCase());
        unregisterNpc(npc.getUuid());
    }

    public void register(NPC npc) {
        serializedNpcs.put(npc.getId(), npc);
        nameMapping.put(npc.getName().toLowerCase(), npc.getId());
    }

    public List<NPC> getSerializedNpcs()  {
        return Collections.unmodifiableList(new ArrayList<>(serializedNpcs.values()));
    }

}

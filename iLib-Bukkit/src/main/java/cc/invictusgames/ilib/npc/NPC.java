package cc.invictusgames.ilib.npc;

import cc.invictusgames.ilib.ILibBukkitPlugin;
import cc.invictusgames.ilib.configuration.defaults.LocationConfig;
import cc.invictusgames.ilib.npc.clickhandler.NPCClickHandler;
import cc.invictusgames.ilib.npc.config.NPCConfigEntry;
import cc.invictusgames.ilib.npc.equipment.EquipmentSlot;
import cc.invictusgames.ilib.npc.network.NPCNetHandler;
import cc.invictusgames.ilib.npc.network.NPCNetworkManager;
import cc.invictusgames.ilib.scoreboard.packet.ScoreboardTeamPacketMod;
import cc.invictusgames.ilib.utils.CC;
import cc.invictusgames.ilib.utils.Statics;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.viaversion.viaversion.api.Via;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
public class NPC {

    public static final String NAMETAG_KEY = "§§NPC_NAMETAGS";

    private final int id;
    private final UUID uuid;
    private final Map<EquipmentSlot, ItemStack> equipment = new HashMap<>();
    private String name;
    private String displayName = CC.RESET;
    private NPCClickHandler clickHandler = NPCClickHandler.COMMAND;
    private Location location;
    private String command = null;
    private boolean consoleCommand;
    private String[] skin;
    private boolean initialized = false;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private EntityPlayer entityPlayer;

    @Setter(AccessLevel.NONE)
    private int entityId;

    protected NPC(NPCBuilder builder) {
        this.uuid = UUID.randomUUID();

        if (builder.getDisplayName() != null)
            this.displayName = builder.getDisplayName();

        if (builder.getClickHandler() != null)
            this.clickHandler = builder.getClickHandler();

        if (builder.getLocation() == null)
            throw new IllegalArgumentException("Please provide a location for the npc using NPCBuilder#at");

        this.location = builder.getLocation();

        if (builder.getCommand() != null)
            this.command = builder.getCommand();

        this.consoleCommand = builder.isConsoleCommand();
        this.id = NPCService.registerNpc(this);

        if (builder.getEquipment() != null)
            equipment.putAll(builder.getEquipment());

        if (builder.getSkinName() != null) {
            Bukkit.getScheduler().runTaskAsynchronously(ILibBukkitPlugin.getInstance(), () -> {
                try {
                    String response = getResponse("https://api.minetools.eu/uuid/" + builder.getSkinName());
                    JsonObject parsed = Statics.JSON_PARSER.parse(response).getAsJsonObject();
                    String uuid = parsed.get("id").getAsString();

                    response = getResponse("https://api.minetools.eu/profile/" + uuid);
                    parsed = Statics.JSON_PARSER.parse(response).getAsJsonObject();
                    JsonObject properties = parsed.get("raw").getAsJsonObject()
                            .get("properties").getAsJsonArray().get(0).getAsJsonObject();

                    skin = new String[]{
                            properties.get("value").getAsString(),
                            properties.get("signature").getAsString()
                    };
                } catch (Exception e) {
                    skin = null;
                    e.printStackTrace();
                }

                Bukkit.getScheduler().runTask(ILibBukkitPlugin.getInstance(), () -> {
                    init();
                    spawn();
                });
            });
            return;
        }

        skin = builder.getSkin();
        init();
        spawn();
    }

    private void init() {
        if (initialized)
            return;

        EntityPlayer entityPlayer = getEntityPlayer();
        entityPlayer.getWorld().addEntity(entityPlayer, CreatureSpawnEvent.SpawnReason.CUSTOM);
        if (!entityPlayer.getBukkitEntity().hasMetadata("iLib-NPC"))
            entityPlayer.getBukkitEntity().setMetadata("iLib-NPC",
                    new FixedMetadataValue(ILibBukkitPlugin.getInstance(), true));
        entityId = entityPlayer.getId();
        Socket socket = new Socket() {
            private final byte[] EMPTY = new byte[50];

            @Override
            public InputStream getInputStream() {
                return new ByteArrayInputStream(EMPTY);
            }

            @Override
            public OutputStream getOutputStream() {
                return new ByteArrayOutputStream(10);
            }
        };

        try {
            NetworkManager networkManager = new NPCNetworkManager(EnumProtocolDirection.CLIENTBOUND);
            networkManager.a(entityPlayer.playerConnection = new NPCNetHandler(MinecraftServer.getServer(),
                    networkManager, entityPlayer));
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Map.Entry<EquipmentSlot, ItemStack> entry : equipment.entrySet()) {
            if (entry.getKey() == EquipmentSlot.HAND) {
                entityPlayer.getBukkitEntity().getInventory().setItemInHand(entry.getValue());
                continue;
            }

            entityPlayer.setEquipment(entry.getKey().ordinal() - 1, CraftItemStack.asNMSCopy(entry.getValue()));
        }

        initialized = true;


        DataWatcher dataWatcher = entityPlayer.getDataWatcher();
        dataWatcher.watch(10, (byte) 0xFF);
    }

    public void spawn() {
        if (!initialized)
            init();

        Bukkit.getOnlinePlayers().forEach(this::spawn);
    }

    public void spawn(Player player) {
        if (!initialized)
            return;

        PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;
        EntityPlayer entityPlayer = getEntityPlayer();

        playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER,
                entityPlayer));
        playerConnection.sendPacket(new PacketPlayOutNamedEntitySpawn(entityPlayer));
        rotate(location.getYaw(), player);

        for (Map.Entry<EquipmentSlot, ItemStack> entry : equipment.entrySet()) {
            playerConnection.sendPacket(new PacketPlayOutEntityEquipment(
                    entityPlayer.getId(),
                    entry.getKey().ordinal(),
                    CraftItemStack.asNMSCopy(entry.getValue())
            ));
        }

        new ScoreboardTeamPacketMod(NAMETAG_KEY, Collections.singletonList(entityPlayer.getName()), 3)
                .sendToPlayer(player);

        // TODO: 24.04.2022 don't remove the player in 1.8
     /*   if (Via.getAPI().getPlayerVersion(player.getUniqueId()) < 20)
            Bukkit.getScheduler().runTaskLaterAsynchronously(ILibBukkitPlugin.getInstance(), () ->
                            playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, entityPlayer)),
                    10);*/
    }

    public void despawn() {
        if (entityPlayer != null)
            entityPlayer.getWorld().removeEntity(entityPlayer);

        entityPlayer = null;
        initialized = false;
    }

    public void rotate(float yaw) {
        Bukkit.getOnlinePlayers().forEach(player -> rotate(yaw, player));
    }

    public void rotate(float yaw, Player player) {
        PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;
        playerConnection.sendPacket(new PacketPlayOutEntityHeadRotation(getEntityPlayer(),
                (byte) (yaw * 256.0F / 360.0F)));
    }

    public void setEquipment(EquipmentSlot slot, ItemStack item) {
        if (item == null)
            equipment.remove(slot);
        else equipment.put(slot, item);

        if (entityPlayer == null)
            return;

        Bukkit.getOnlinePlayers().forEach(player ->
                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityEquipment(
                        entityPlayer.getId(),
                        slot.ordinal(),
                        item == null ? null : CraftItemStack.asNMSCopy(item)
                ))
        );

        if (slot == EquipmentSlot.HAND) {
            entityPlayer.getBukkitEntity().getInventory().setItemInHand(item);
            return;
        }

        entityPlayer.setEquipment(slot.ordinal() - 1, item == null ? null : CraftItemStack.asNMSCopy(item));
    }

    private EntityPlayer getEntityPlayer() {
        if (entityPlayer == null) {
            WorldServer worldServer = ((CraftWorld) location.getWorld()).getHandle();
            entityPlayer = new EntityPlayer(MinecraftServer.getServer(), worldServer,
                    getGameProfile(), new PlayerInteractManager(worldServer));
        }
        entityPlayer.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        return entityPlayer;
    }

    private GameProfile getGameProfile() {
        GameProfile gameProfile = new GameProfile(uuid, displayName);
        if (skin != null) {
            gameProfile.getProperties().removeAll("textures");
            gameProfile.getProperties().put("textures", new Property("textures", skin[0], skin[1]));
        }
        return gameProfile;
    }

    public NPCConfigEntry toConfig() {
        NPCConfigEntry entry = new NPCConfigEntry();
        entry.setLocation(new LocationConfig(location));
        entry.setName(name);
        entry.setDisplayName(displayName);
        if (command != null) {
            entry.setCommand(command);
            entry.setConsoleCommand(consoleCommand);
        }

        if (skin != null) {
            entry.setTexture(skin[0]);
            entry.setSignature(skin[1]);
        }

        entry.setEquipment(equipment);

        return entry;
    }

    private static String getResponse(String urlString) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(urlString).openConnection();
            connection.setReadTimeout(5000);
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                StringBuilder response = new StringBuilder();
                new BufferedReader(new InputStreamReader(connection.getInputStream())).lines().forEach(response::append);
                return response.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}

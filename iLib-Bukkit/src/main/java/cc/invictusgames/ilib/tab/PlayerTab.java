package cc.invictusgames.ilib.tab;

import cc.invictusgames.ilib.ILibBukkitPlugin;
import cc.invictusgames.ilib.npc.NPCService;
import cc.invictusgames.ilib.scoreboard.packet.ScoreboardTeamPacketMod;
import cc.invictusgames.ilib.tab.packet.PlayerInfoPacketMod;
import cc.invictusgames.ilib.utils.CC;
import cc.invictusgames.ilib.utils.ReflectionUtil;
import com.google.common.collect.Table;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.viaversion.viaversion.api.Via;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerTab {

    public static final String BLANK_SKIN_TEXTURE = "eyJ0aW1lc3RhbXAiOjE0MTEyNjg3OTI3NjUsInByb2ZpbGVJZCI6IjNmYmVj" +
            "N2RkMGE1ZjQwYmY5ZDExODg1YTU0NTA3MTEyIiwicHJvZmlsZU5hbWUiOiJsYXN0X3VzZXJuYW1lIiwidGV4dHVyZXMiOnsiU0tJT" +
            "iI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzg0N2I1Mjc5OTg0NjUxNTRhZDZjMjM4YTFlM2" +
            "MyZGQzZTMyOTY1MzUyZTNhNjRmMzZlMTZhOTQwNWFiOCJ9fX0=";

    public static final String BLANK_SKIN_SIGNATURE = "u8sG8tlbmiekrfAdQjy4nXIcCfNdnUZzXSx9BE1X5K27NiUvE1dDNIeBBS" +
            "PdZzQG1kHGijuokuHPdNi/KXHZkQM7OJ4aCu5JiUoOY28uz3wZhW4D+KG3dH4ei5ww2KwvjcqVL7LFKfr/ONU5Hvi7MIIty1eKpoG" +
            "DYpWj3WjnbN4ye5Zo88I2ZEkP1wBw2eDDN4P3YEDYTumQndcbXFPuRRTntoGdZq3N5EBKfDZxlw4L3pgkcSLU5rWkd5UH4ZUOHAP/" +
            "VaJ04mpFLsFXzzdU4xNZ5fthCwxwVBNLtHRWO26k/qcVBzvEXtKGFJmxfLGCzXScET/OjUBak/JEkkRG2m+kpmBMgFRNtjyZgQ1w0" +
            "8U6HHnLTiAiio3JswPlW5v56pGWRHQT5XWSkfnrXDalxtSmPnB5LmacpIImKgL8V9wLnWvBzI7SHjlyQbbgd+kUOkLlu7+717ySDE" +
            "JwsFJekfuR6N/rpcYgNZYrxDwe4w57uDPlwNL6cJPfNUHV7WEbIU1pMgxsxaXe8WSvV87qLsR7H06xocl2C0JFfe2jZR4Zh3k9xzE" +
            "nfCeFKBgGb4lrOWBu1eDWYgtKV67M2Y+B3W5pjuAjwAxn0waODtEn/3jKPbc/sxbPvljUCw65X+ok0UUN1eOwXV5l2EGzn05t3Yhw" +
            "q19/GxARg63ISGE8CKw=";

    public static final TabEntry EMPTY = new TabEntry("");

    private static final Field ACTION_FIELD = ReflectionUtil.getField(PacketPlayOutPlayerInfo.class, "a");
    private static final Field DATA_FIELD = ReflectionUtil.getField(PacketPlayOutPlayerInfo.class, "b");
    private static final Field UUID_FIELD = ReflectionUtil.getField(PacketPlayOutNamedEntitySpawn.class, "b");

    private static final String[] TAB_NAMES = new String[80];

    private final TabService tabService;
    private final Player player;
    private final Map<String, TabEntry> currentEntries = new ConcurrentHashMap<>();
    private final Map<String, GameProfile> profiles = new ConcurrentHashMap<>();
    private String currentHeader = "";
    private String currentFooter = "";

    public PlayerTab(TabService tabService, Player player) {
        this.tabService = tabService;
        this.player = player;
        this.handleNetty();

        for (String tabName : TAB_NAMES) {
            GameProfile gameProfile = new GameProfile(UUID.randomUUID(), tabName);
            gameProfile.getProperties().clear();
            gameProfile.getProperties().put("textures", new Property(
                    "textures",
                    BLANK_SKIN_TEXTURE,
                    BLANK_SKIN_SIGNATURE
            ));

            profiles.put(tabName, gameProfile);
            currentEntries.put(tabName, EMPTY);

            updateTabList(gameProfile, PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, 0);
            createAndAddMember(tabName, tabName);
        }
    }

    private void handleNetty() {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();

        entityPlayer.playerConnection.networkManager.channel.pipeline().addBefore("packet_handler", player.getName(), new ChannelDuplexHandler() {
            @Override
            public void write(ChannelHandlerContext channelHandlerContext, Object o, ChannelPromise channelPromise) throws Exception {
                if (o instanceof PacketPlayOutPlayerInfo) {
                    PacketPlayOutPlayerInfo packet = (PacketPlayOutPlayerInfo) o;
                    PacketPlayOutPlayerInfo.PlayerInfoData data =
                            ((List<PacketPlayOutPlayerInfo.PlayerInfoData>) DATA_FIELD.get(packet)).get(0);

                    Player player = Bukkit.getPlayer(data.getProfile().getId());

                    if (NPCService.getNpc(data.getProfile().getId()) != null) {
                        // player is null because npc isn't a real player
                        super.write(channelHandlerContext, o, channelPromise);
                        return;
                    }

                    // Is real player
                    if (player != null) {
                        EntityPlayer ep = ((CraftPlayer) player).getHandle();
                        super.write(channelHandlerContext, o, channelPromise);
                        Bukkit.getScheduler().runTaskLater(
                                ILibBukkitPlugin.getInstance(), () -> sendPacket(new PacketPlayOutPlayerInfo(
                                        PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, ep)), 10L);
                        return;
                    }

                } else if (o instanceof PacketPlayOutNamedEntitySpawn) {
                    PacketPlayOutNamedEntitySpawn packet = (PacketPlayOutNamedEntitySpawn) o;
                    Player player = Bukkit.getPlayer((UUID) UUID_FIELD.get(packet));

                    if (player != null) {
                        EntityPlayer ep = ((CraftPlayer) player).getHandle();

                        sendPacket(new PacketPlayOutPlayerInfo(
                                PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, ep));

                        super.write(channelHandlerContext, o, channelPromise);
                        return;
                    }
                } else if (o instanceof PacketPlayOutRespawn) {
                    Bukkit.getScheduler().runTaskLater(ILibBukkitPlugin.getInstance(), () -> {
                        sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER,
                                entityPlayer));
                    }, 5);
                    super.write(channelHandlerContext, o, channelPromise);
                    return;
                }

                super.write(channelHandlerContext, o, channelPromise);
            }
        });
    }

    public void hideRealPlayers() {
        for (Player current : Bukkit.getOnlinePlayers()) {
            String playerName = current.getName();
            String name = TabService.getPlayerNameGetter().apply(current);

            updateTabList(((CraftPlayer) current).getProfile(),
                    PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, 0);
            if (!name.equals(playerName))
                updateTabList(((CraftPlayer) current).getProfile(),
                        PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, 0);
        }
    }

    public void update() {
        TabAdapter adapter = tabService.getAdapter();
        if (adapter == null)
            return;

        Table<Integer, Integer, TabEntry> entries = adapter.getEntries(player);
        if (entries == null)
            return;


        for (int row = 0; row < 20; row++) {
            for (int col = 0; col < (is1_8(player) ? 4 : 3); col++) {

                String tabName = TAB_NAMES[row * (is1_8(player) ? 4 : 3) + col];
                TabEntry entry = entries.contains(col, row) ? entries.get(col, row) : EMPTY;

                if (entry == null)
                    entry = EMPTY;

                boolean changeText = true;
                boolean changePing = true;
                boolean changeTexture = true;

                TabEntry currentEntry = currentEntries.get(tabName);
                if (currentEntry != null) {
                    if (entry.getText().equals(currentEntry.getText()))
                        changeText = false;

                    if (entry.getPing() == 0 && currentEntry.getPing() == 0)
                        changePing = false;
                    else if (pingToBars(entry.getPing()) == pingToBars(currentEntry.getPing()))
                        changePing = false;

                    if ((entry.getPing() == 0 && currentEntry.getPing() != 0)
                            || currentEntry.getPing() == 0 && entry.getPing() != 0)
                        changePing = true;

                    if (entry.getTexture().equals(currentEntry.getTexture())
                            && entry.getSignature().equals(currentEntry.getSignature()))
                        changeTexture = false;
                }

                if (changeText) {
                    String[] strings = splitString(CC.translate(entry.getText()));
                    String prefix = strings[0];
                    String suffix = strings[1];
                    if (!suffix.isEmpty()) {
                        if (prefix.charAt(prefix.length() - 1) == ChatColor.COLOR_CHAR) {
                            prefix = prefix.substring(0, prefix.length() - 1);
                            suffix = ChatColor.COLOR_CHAR + suffix;
                        }

                        String suffixPrefix = ChatColor.RESET.toString();
                        if (!ChatColor.getLastColors(prefix).isEmpty())
                            suffixPrefix = ChatColor.getLastColors(prefix);

                        if (suffix.length() <= 14)
                            suffix = suffixPrefix + suffix;
                        else suffix = suffixPrefix + suffix.substring(0, 14);
                    }

                    prefix = prefix.substring(0, Math.min(16, prefix.length()));
                    suffix = suffix.substring(0, Math.min(16, suffix.length()));

                    new ScoreboardTeamPacketMod(tabName, prefix, suffix, null, 2).sendToPlayer(player);
                }

                if (changeTexture && is1_8(player)) {
                    GameProfile gameProfile = profiles.get(tabName);
                    gameProfile.getProperties().clear();
                    gameProfile.getProperties().put(
                            "textures",
                            new Property("textures", entry.getTexture(), entry.getSignature())
                    );

                    updateTabList(gameProfile, PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, 0);
                    updateTabList(gameProfile, PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entry.getPing());
                    profiles.put(tabName, gameProfile);
                }

                if (changePing)
                    updateTabList(profiles.get(tabName),
                            PacketPlayOutPlayerInfo.EnumPlayerInfoAction.UPDATE_LATENCY, entry.getPing());
                else if (currentEntry != null)
                    entry.setPing(currentEntry.getPing());

                currentEntries.put(tabName, entry);
            }
        }

        for (int i = 0; i < (is1_8(player) ? 4 : 3) * 20; i++) {
            int x = i % (is1_8(player) ? 4 : 3);
            int y = i / (is1_8(player) ? 4 : 3);
        }

        String header = adapter.getHeader(player);
        if (header == null)
            header = "";

        header = "{\"text\":\"" + CC.translate(header) + "\"}";

        String footer = adapter.getFooter(player);
        if (footer == null)
            footer = "";

        footer = "{\"text\":\"" + CC.translate(footer) + "\"}";

        if ((!currentHeader.equals(header) || !currentFooter.equals(footer)) && is1_8(player)) {
            // TODO: 23.04.2022 change this to 1.8
            //    sendPacket(new ProtocolInjector.PacketTabHeader(ChatSerializer.a(header), ChatSerializer.a(footer)));
            currentHeader = header;
            currentFooter = footer;
        }

        entries.clear();
    }

    private void updateTabList(GameProfile profile,
                               PacketPlayOutPlayerInfo.EnumPlayerInfoAction action, int ping) {
        new PlayerInfoPacketMod(getEntityPlayer(profile, ping), action).sendToPlayer(player);
    }

    private EntityPlayer getEntityPlayer(GameProfile profile, int ping) {
        final MinecraftServer server = MinecraftServer.getServer();
        final PlayerInteractManager interactManager = new PlayerInteractManager(server.getWorldServer(0));

        EntityPlayer entityPlayer = new EntityPlayer(server, server.getWorldServer(0), profile, interactManager);
        entityPlayer.ping = ping;
        return entityPlayer;
    }

    private void createAndAddMember(String teamName, String member) {
        new ScoreboardTeamPacketMod(teamName, "", "", Collections.singleton(member), 0).sendToPlayer(player);
    }

    private void sendPacket(Packet packet) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    private int pingToBars(int ping) {
        if (ping < 0)
            return 5;

        if (ping < 150)
            return 0;

        if (ping < 300)
            return 1;

        if (ping < 600)
            return 2;

        if (ping < 1000)
            return 3;

        if (ping < 32767)
            return 4;

        return 5;
    }

    private String[] splitString(String line) {
        if (line.length() <= 16)
            return new String[]{line, ""};

        return new String[]{line.substring(0, 16), line.substring(16)};
    }

    public static boolean is1_8(Player player) {
        return Via.getAPI().getPlayerVersion(player.getUniqueId()) > 5;
    }

    static {
        for (int i = 0; i < TAB_NAMES.length; i++) {
            int x = i % 4;
            int y = i / 4;
            String name = (x > 9 ? ("§" + String.valueOf(x).toCharArray()[0] + "§"
                    + String.valueOf(x).toCharArray()[1]) : "§0§" + x)
                    + (y > 9 ? ("§" + String.valueOf(y).toCharArray()[0]
                    + "§" + String.valueOf(y).toCharArray()[1]) : "§0§" + String.valueOf(y).toCharArray()[0]);
            TAB_NAMES[i] = name;
        }
    }

}

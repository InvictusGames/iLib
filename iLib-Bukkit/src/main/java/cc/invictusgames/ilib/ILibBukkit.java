package cc.invictusgames.ilib;

import cc.invictusgames.ilib.chat.ChatListener;
import cc.invictusgames.ilib.chat.ChatService;
import cc.invictusgames.ilib.chat.command.ChatCommand;
import cc.invictusgames.ilib.chatinput.ChatInputListener;
import cc.invictusgames.ilib.command.CommandService;
import cc.invictusgames.ilib.command.defaults.DebugCommands;
import cc.invictusgames.ilib.command.defaults.MongoCommand;
import cc.invictusgames.ilib.command.defaults.RedisCommand;
import cc.invictusgames.ilib.deathmessage.command.KillMessageCommand;
import cc.invictusgames.ilib.hologram.HologramService;
import cc.invictusgames.ilib.hologram.command.HologramCommands;
import cc.invictusgames.ilib.hologram.command.parameter.HologramParameter;
import cc.invictusgames.ilib.hologram.listener.HologramListener;
import cc.invictusgames.ilib.hologram.statics.StaticHologram;
import cc.invictusgames.ilib.menu.listener.MenuListener;
import cc.invictusgames.ilib.npc.NPC;
import cc.invictusgames.ilib.npc.NPCService;
import cc.invictusgames.ilib.npc.command.NPCCommands;
import cc.invictusgames.ilib.npc.command.parameter.EquipmentSlotParameter;
import cc.invictusgames.ilib.npc.command.parameter.NPCParameter;
import cc.invictusgames.ilib.npc.equipment.EquipmentSlot;
import cc.invictusgames.ilib.npc.listener.NPCListener;
import cc.invictusgames.ilib.placeholder.command.PlaceholderCommands;
import cc.invictusgames.ilib.playersetting.PlayerSettingService;
import cc.invictusgames.ilib.playersetting.command.SettingsCommands;
import cc.invictusgames.ilib.playersetting.impl.ILibSettings;
import cc.invictusgames.ilib.playersetting.listener.PlayerSettingListener;
import cc.invictusgames.ilib.protocol.ProtocolService;
import cc.invictusgames.ilib.protocol.TinyProtocolListener;
import cc.invictusgames.ilib.reboot.RebootCommands;
import cc.invictusgames.ilib.utils.ItemUtils;
import cc.invictusgames.ilib.utils.logging.command.LogLevelCommand;
import cc.invictusgames.ilib.utils.logging.listener.LogLevelListener;
import cc.invictusgames.ilib.uuid.UUIDCacheListener;
import cc.invictusgames.ilib.visibility.VisibilityService;
import cc.invictusgames.marvelspigot.MarvelSpigot;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 24.10.2020 / 19:37
 * iLib / cc.invictusgames.ilib
 */

@Getter
public class ILibBukkit extends ILib {

    @Getter
    @Setter
    private static Function<Void, String> serverNameGetter = unused -> Bukkit.getServerName();

    private HologramService hologramService;
    private NPCService npcService;

    public ILibBukkit(ILibBukkitPlugin plugin) {
        super(plugin.getLogger(), plugin.getDataFolder());
    }

    @Override
    public void init() {
        HologramListener hologramListener = new HologramListener(this);
        NPCListener npcListener = new NPCListener(this);

        Arrays.asList(
                new UUIDCacheListener(getUuidCache()),
                new MenuListener(),
                new cc.invictusgames.ilib.chatinputold.listener.ChatInputListener(),
                new LogLevelListener(this),
                new ChatListener(),
                new PlayerSettingListener(),
                hologramListener,
                new TinyProtocolListener(),
                npcListener,
                new ChatInputListener()
        ).forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, ILibBukkitPlugin.getInstance()));

        MarvelSpigot.getInstance().addSimpleMovementHandler(hologramListener);
        ProtocolService.registerTinyProtocol(ILibBukkitPlugin.getInstance(), npcListener);
        ProtocolService.registerTinyProtocol(ILibBukkitPlugin.getInstance(), hologramListener);

        CommandService.registerParameter(StaticHologram.class, new HologramParameter(this));
        CommandService.registerParameter(NPC.class, new NPCParameter(this));
        CommandService.registerParameter(EquipmentSlot.class, new EquipmentSlotParameter());

        CommandService.register(
                ILibBukkitPlugin.getInstance(),
                new DebugCommands(),
                new RedisCommand(this),
                new MongoCommand(),
                new LogLevelCommand(this),
                new ChatCommand(),
                new SettingsCommands(),
                new RebootCommands(),
                new HologramCommands(this),
                new NPCCommands(this),
                new KillMessageCommand(),
                new PlaceholderCommands()
        );

        ItemUtils.loadItems();
        VisibilityService.init();
        NPCService.startRotationTask();
        //  BossBarService.init();

        PlayerSettingService.registerProvider(new ILibSettings());
        ChatService.registerChatChannel(ChatService.getDefaultChannel());

        this.hologramService = new HologramService(this);
        hologramService.load();

        this.npcService = new NPCService(this);
        npcService.load();

    }

    @Override
    public void runTask(Runnable runnable) {
        Bukkit.getScheduler().runTask(ILibBukkitPlugin.getInstance(), runnable);
    }

    @Override
    public void runTaskAsync(Runnable runnable) {
        try {
            TASK_CHAIN.runAsync(() -> runnable);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}

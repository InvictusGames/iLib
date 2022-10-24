package cc.invictusgames.ilib.messages;

import cc.invictusgames.ilib.ILib;
import cc.invictusgames.ilib.utils.CC;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 07.01.2020 / 22:27
 * iLib / cc.invictusgames.ilib.messages
 */

public class YamlMessageService implements MessageService {
    private final Plugin plugin;
    private final String id;
    private File file;
    private YamlConfiguration configuration;
    private YamlConfiguration defaults;

    public YamlMessageService(String id, Plugin plugin) {
        this.id = id;
        this.plugin = plugin;
        loadMessages();
    }

    @Override
    public void loadMessages() {
        this.file = new File(ILib.getInstance().getMainConfig().getMessagesPath(), id + ".yml");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdir();
        }
        this.defaults = this.getDefaults();
        YamlConfiguration configuration;
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            configuration = YamlConfiguration.loadConfiguration(file);
        } catch (IOException e) {
            e.printStackTrace();
            configuration = this.getDefaults();
        }
        this.configuration = configuration;
        this.saveConfiguration();
    }

    @Override
    public String formatMessage(String key, Object... args) {
        return MessageFormat.format(CC.translate(this.configuration.getString(key, key)), args);
    }

    @Override
    public List<String> formatMessages(String key, Object... args) {
        if (!this.configuration.isSet(key)) {
            return Collections.singletonList(key);
        }
        if (this.configuration.isList(key)) {
            List<String> messages = new ArrayList<>();
            for (String message : this.configuration.getStringList(key)) {
                messages.add(CC.translate(MessageFormat.format(message, args)));
            }
            return messages;
        }
        String messageString = this.configuration.getString(key);
        if (messageString.contains("\\n")) {
            List<String> messages = new ArrayList<>();
            for (String message : messageString.split("\\n")) {
                messages.add(MessageFormat.format(CC.translate(message.replace("\\n", "")), args));
            }
            return messages;
        }
        return Collections.singletonList(MessageFormat.format(CC.translate(messageString.replace("\\n", "")), args));
    }

    @Override
    public void sendMessage(CommandSender sender, String key, Object... args) {
        if (!this.configuration.isSet(key)) {
            sender.sendMessage(key);
            return;
        }
        if (this.configuration.isList(key)) {
            for (String message : this.configuration.getStringList(key)) {
                sender.sendMessage(CC.translate(MessageFormat.format(message.replace("\\n", ""), args)));
            }
            return;
        }
        String messageString = this.configuration.getString(key);
        if (messageString.contains("\\n")) {
            for (String message : messageString.split("\\n")) {
                sender.sendMessage(MessageFormat.format(CC.translate(message.replace("\\n", "")), args));
            }
            return;
        }
        sender.sendMessage(MessageFormat.format(CC.translate(messageString.replace("\\n", "")), args));
    }

    private YamlConfiguration getDefaults() {
        return YamlConfiguration.loadConfiguration(plugin.getClass().getClassLoader().getResourceAsStream("messages" +
                ".yml"));
    }

    private void saveConfiguration() {
        this.configuration.options().copyDefaults(true);
        this.configuration.options().copyHeader(true);
        this.configuration.options().header(this.id + " Messages\n" + new Date().toString());
        for (String key : this.defaults.getKeys(true)) {
            this.configuration.addDefault(key, this.defaults.get(key));
        }
        try {
            this.configuration.save(this.file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

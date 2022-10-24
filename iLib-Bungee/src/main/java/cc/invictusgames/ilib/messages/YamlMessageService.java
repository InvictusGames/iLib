package cc.invictusgames.ilib.messages;

import cc.invictusgames.ilib.ILib;
import cc.invictusgames.ilib.utils.CC;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 07.01.2020 / 22:27
 * iLib / cc.invictusgames.ilib.messages
 */

public class YamlMessageService implements MessageService {

    private static final ConfigurationProvider CONFIGURATION_PROVIDER =
            ConfigurationProvider.getProvider(YamlConfiguration.class);

    private final Plugin plugin;
    private final String id;
    private File file;
    private Configuration configuration;
    private Configuration defaults;

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
        Configuration configuration;
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            configuration = CONFIGURATION_PROVIDER.load(file);
        } catch (IOException e) {
            e.printStackTrace();
            configuration = this.getDefaults();
        }
        this.configuration = configuration;
        this.saveConfiguration();
    }

    @Override
    public String formatMessage(String key, Object... args) {
        return CC.translate(MessageFormat.format(this.configuration.getString(key, key), args));
    }

    @Override
    public List<String> formatMessages(String key, Object... args) {
        if (!this.configuration.contains(key)) {
            return Collections.singletonList(key);
        }
        if (this.configuration.get(key) instanceof List) {
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
                messages.add(CC.translate(MessageFormat.format(message.replace("\\n", ""), args)));
            }
            return messages;
        }
        return Collections.singletonList(CC.translate(MessageFormat.format(messageString.replace("\\n", ""), args)));
    }

    @Override
    public void sendMessage(CommandSender sender, String key, Object... args) {
        if (!this.configuration.contains(key)) {
            sender.sendMessage(key);
            return;
        }
        if (this.configuration.get(key) instanceof List) {
            for (String message : this.configuration.getStringList(key)) {
                sender.sendMessage(CC.translate(MessageFormat.format(message.replace("\\n", ""), args)));
            }
            return;
        }
        String messageString = this.configuration.getString(key);
        if (messageString.contains("\\n")) {
            for (String message : messageString.split("\\n")) {
                sender.sendMessage(CC.translate(MessageFormat.format(message.replace("\\n", ""), args)));
            }
            return;
        }
        sender.sendMessage(CC.translate(MessageFormat.format(messageString.replace("\\n", ""), args)));
    }

    private Configuration getDefaults() {
        return CONFIGURATION_PROVIDER.load(plugin.getClass().getClassLoader().getResourceAsStream("messages.yml"));
    }

    private void saveConfiguration() {
        for (String key : this.defaults.getKeys()) {
            if (!configuration.contains(key))
                this.configuration.set(key, this.defaults.get(key));
        }
        try {
            CONFIGURATION_PROVIDER.save(configuration, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

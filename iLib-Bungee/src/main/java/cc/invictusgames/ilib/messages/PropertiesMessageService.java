package cc.invictusgames.ilib.messages;

import cc.invictusgames.ilib.ILib;
import cc.invictusgames.ilib.utils.CC;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.*;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 04.01.2020 / 06:55
 * iLib / cc.invictusgames.ilib.messages
 */

public class PropertiesMessageService implements MessageService {
    private final Plugin plugin;
    private final String id;
    private File file;
    private Properties properties;
    private Properties defaults;

    public PropertiesMessageService(String id, Plugin plugin) {
        this.id = id;
        this.plugin = plugin;
        loadMessages();
    }

    @Override
    public void loadMessages() {
        Properties properties;
        this.file = new File(ILib.getInstance().getMainConfig().getMessagesPath(), id + ".properties");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdir();
        }
        this.defaults = this.getDefaults();
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            properties = new Properties(this.getDefaults());
            properties.load(new FileInputStream(this.file));
        } catch (IOException e) {
            e.printStackTrace();
            properties = new Properties(this.getDefaults());
        }
        this.properties = properties;
        saveProperties();
    }

    public String formatMessage(String key, Object... args) {
        return CC.translate(MessageFormat.format(properties.getProperty(key, key), args));
    }

    public List<String> formatMessages(String key, Object... args) {
        String messageString = properties.getProperty(key, key);

        List<String> messages = new ArrayList<>();
        if (messageString.contains("\\n")) {
            for (String message : messageString.split("\\n")) {
                messages.add(CC.translate(MessageFormat.format(message.replace("\\n", ""), args)));
            }
        } else {
            messages.add(CC.translate(messageString));
        }
        return messages;
    }

    @Override
    public void sendMessage(CommandSender sender, String key, Object... args) {
        String messageString = properties.getProperty(key, key);
        if (messageString.contains("\\n")) {
            for (String message : messageString.split("\\n")) {
                sender.sendMessage(CC.translate(MessageFormat.format(message.replace("\\n", ""), args)));
            }
        } else {
            sender.sendMessage(MessageFormat.format(messageString.replace("\\n", ""), args));
        }
    }

    private void saveProperties() {
        try {
            if (!properties.keySet().equals(this.defaults.keySet())) {
                for (Object key : this.defaults.keySet()) {
                    if (!properties.containsKey(key)) {
                        properties.setProperty((String) key, this.defaults.getProperty((String) key));
                    }
                }
                for (Object key : this.properties.keySet()) {
                    if (!this.defaults.containsKey(key)) {
                        properties.remove(key);
                    }
                }
            }
            properties.store(new FileOutputStream(file), this.id + " Messages");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Properties getDefaults() {
        Properties properties = new Properties();
        try {
            InputStream inputStream = plugin.getClass().getClassLoader().getResourceAsStream("messages.properties");
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }
}

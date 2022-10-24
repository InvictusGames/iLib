package cc.invictusgames.ilib.utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 15.02.2020 / 16:34
 * iLib / cc.invictusgames.ilib.utils
 */

public class ChatMessage {

    private static final Pattern URL_PATTERN = Pattern.compile("^(?:(https?)://)?([-\\w_\\.]{2,}\\.[a-z]{2,4})(/\\S*)" +
            "?$");

    private List<TextComponent> components = new ArrayList<>();


    public ChatMessage(String text) {
        add(text);
    }

    public ChatMessage(String text, String command, String hoverText) {
        add(text);
        if (command != null) {
            this.runCommand(command);
        }
        if (hoverText != null) {
            this.hoverText(hoverText);
        }
    }

    public ChatMessage color(ChatColor chatColor) {
        this.getLastAdded().setColor(chatColor);
        return this;
    }

    public ChatMessage format(ChatColor chatColor) {
        switch (chatColor) {
            case BOLD:
                this.getLastAdded().setBold(true);
                break;
            case ITALIC:
                this.getLastAdded().setItalic(true);
                break;
            case STRIKETHROUGH:
                this.getLastAdded().setStrikethrough(true);
                break;
            case UNDERLINE:
                this.getLastAdded().setUnderlined(true);
                break;
            case MAGIC:
                this.getLastAdded().setObfuscated(true);
                break;
        }
        return this;
    }

    public ChatMessage hoverText(String text) {
        this.getLastAdded().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder(CC.translate(text)).create()));
        return this;
    }

    public ChatMessage runCommand(String command) {
        this.getLastAdded().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        return this;
    }

    public ChatMessage suggestCommand(String command) {
        this.getLastAdded().setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command));
        return this;
    }

    public ChatMessage openUrl(String url) {
        this.getLastAdded().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
        return this;
    }

    public ChatMessage add(String text) {
        TextComponent component = null;
        for (BaseComponent currentComponent : fromLegacyText(text)) {
            if (component == null) {
                component = (TextComponent) currentComponent;
            } else {
                component.addExtra(currentComponent);
            }
        }
        this.components.add(component);
        return this;
    }

    private TextComponent getLastAdded() {
        return this.components.get(this.components.size() - 1);
    }

    public void send(CommandSender sender) {
        TextComponent component = build();
        sender.sendMessage(component);
    }

    public TextComponent build() {
        TextComponent component = null;
        for (TextComponent textComponent : this.components) {
            if (component == null) {
                component = textComponent;
            } else {
                component.addExtra(textComponent);
            }
        }
        return component;
    }

    private BaseComponent[] fromLegacyText(String message) {
        ArrayList<BaseComponent> components = new ArrayList<BaseComponent>();
        StringBuilder builder = new StringBuilder();
        TextComponent component = new TextComponent();
        Matcher matcher = URL_PATTERN.matcher(message);

        for (int i = 0; i < message.length(); i++) {
            char c = message.charAt(i);
            if (c == ChatColor.COLOR_CHAR) {
                if (++i >= message.length()) {
                    break;
                }
                c = message.charAt(i);
                if (c >= 'A' && c <= 'Z') {
                    c += 32;
                }
                ChatColor format = ChatColor.getByChar(c);
                if (format == null) {
                    continue;
                }
                if (builder.length() > 0) {
                    TextComponent old = component;
                    component = new TextComponent(old);
                    old.setText(builder.toString());
                    builder = new StringBuilder();
                    components.add(old);
                }
                if (format == ChatColor.BOLD) {
                    component.setBold(true);
                } else if (format == ChatColor.ITALIC) {
                    component.setItalic(true);
                } else if (format == ChatColor.UNDERLINE) {
                    component.setUnderlined(true);
                } else if (format == ChatColor.STRIKETHROUGH) {
                    component.setStrikethrough(true);
                } else if (format == ChatColor.MAGIC) {
                    component.setObfuscated(true);
                } else if (format == ChatColor.RESET) {
                    format = ChatColor.WHITE;
                    component = new TextComponent();
                    component.setColor(format);
                    component.setBold(false);
                    component.setItalic(false);
                    component.setUnderlined(false);
                    component.setStrikethrough(false);
                    component.setObfuscated(false);
                } else {
                    component = new TextComponent();
                    component.setColor(format);
                    component.setBold(false);
                    component.setItalic(false);
                    component.setUnderlined(false);
                    component.setStrikethrough(false);
                    component.setObfuscated(false);
                }
                continue;
            }
            int pos = message.indexOf(' ', i);
            if (pos == -1) {
                pos = message.length();
            }
            if (matcher.region(i, pos).find()) { //Web link handling

                if (builder.length() > 0) {
                    TextComponent old = component;
                    component = new TextComponent(old);
                    old.setText(builder.toString());
                    builder = new StringBuilder();
                    components.add(old);
                }

                TextComponent old = component;
                component = new TextComponent(old);
                String urlString = message.substring(i, pos);
                component.setText(urlString);
                component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,
                        urlString.startsWith("http") ? urlString : "http://" + urlString));
                components.add(component);
                i += pos - i - 1;
                component = old;
                continue;
            }
            builder.append(c);
        }

        component.setText(builder.toString());
        components.add(component);

        return components.toArray(new BaseComponent[0]);
    }

}

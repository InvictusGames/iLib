package cc.invictusgames.ilib.utils;

import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang3.StringEscapeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author langgezockt (langgezockt@gmail.com)
 * 01.11.2019 / 18:39
 * iLib / cc.invictusgames.ilib.utils
 */

public class CC {
    public static final String BLUE = ChatColor.BLUE.toString();
    public static final String AQUA = ChatColor.AQUA.toString();
    public static final String YELLOW = ChatColor.YELLOW.toString();
    public static final String RED = ChatColor.RED.toString();
    public static final String GRAY = ChatColor.GRAY.toString();
    public static final String GOLD = ChatColor.GOLD.toString();
    public static final String GREEN = ChatColor.GREEN.toString();
    public static final String WHITE = ChatColor.WHITE.toString();
    public static final String BLACK = ChatColor.BLACK.toString();
    public static final String BOLD = ChatColor.BOLD.toString();
    public static final String ITALIC = ChatColor.ITALIC.toString();
    public static final String UNDER_LINE = ChatColor.UNDERLINE.toString();
    public static final String STRIKE_THROUGH = ChatColor.STRIKETHROUGH.toString();
    public static final String RESET = ChatColor.RESET.toString();
    public static final String MAGIC = ChatColor.MAGIC.toString();
    public static final String DBLUE = ChatColor.DARK_BLUE.toString();
    public static final String DAQUA = ChatColor.DARK_AQUA.toString();
    public static final String DGRAY = ChatColor.DARK_GRAY.toString();
    public static final String DGREEN = ChatColor.DARK_GREEN.toString();
    public static final String DPURPLE = ChatColor.DARK_PURPLE.toString();
    public static final String DRED = ChatColor.DARK_RED.toString();
    public static final String PURPLE = ChatColor.DARK_PURPLE.toString();
    public static final String PINK = ChatColor.LIGHT_PURPLE.toString();
    public static final String MENU_BAR = ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() +
            "------------------------";
    public static final String CHAT_BAR = ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() +
            "------------------------------------------------";
    public static final String SMALL_CHAT_BAR = ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() +
            "-----------------";
    public static final String SB_BAR = ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() +
            "----------------------";
    public static final String VERTICAL_BAR = StringEscapeUtils.unescapeJava("\u2503");
    public static final String HEART = ChatColor.DARK_RED.toString() + StringEscapeUtils.unescapeJava("\u2764");
    public static final String LEFT_ARROW = StringEscapeUtils.unescapeJava("\u00ab");
    public static final String RIGHT_ARROW = StringEscapeUtils.unescapeJava("\u00bb");

    public static String translate(String in) {
        return ChatColor.translateAlternateColorCodes('&', in);
    }

    public static List<String> translate(List<String> lines) {
        List<String> toReturn = new ArrayList();
        for (String line : lines) {
            toReturn.add(ChatColor.translateAlternateColorCodes('&', line));
        }
        return toReturn;
    }

    public static List<String> translate(String[] lines) {
        List<String> toReturn = new ArrayList();
        for (String line : lines) {
            if (line != null) {
                toReturn.add(ChatColor.translateAlternateColorCodes('&', line));
            }
        }
        return toReturn;
    }

    public static String strip(String in) {
        return ChatColor.stripColor(in);
    }

    public static List<String> strip(List<String> lines) {
        List<String> toReturn = new ArrayList();
        for (String line : lines) {
            toReturn.add(ChatColor.stripColor(line));
        }
        return toReturn;
    }

    public static List<String> strip(String[] lines) {
        List<String> toReturn = new ArrayList();
        for (String line : lines) {
            if (line != null) {
                toReturn.add(ChatColor.stripColor(line));
            }
        }
        return toReturn;
    }

    public static String format(String in, Object... args) {
        return String.format(translate(in), args);
    }

    public static List<String> format(List<String> lines, Object... args) {
        List<String> toReturn = new ArrayList<>();
        for (String line : lines) {
            toReturn.add(String.format(translate(line), args));
        }
        return toReturn;
    }

    public static String colorBoolean(boolean b) {
        return colorBoolean(b, false);
    }

    public static String colorBoolean(boolean b, boolean capitalize) {
        return colorBoolean(b, (capitalize ? "E" : "e") + "nabled", (capitalize ? "D" : "d") + "isabled");
    }

    public static String colorBoolean(boolean b, String enabled, String disabled) {
        return b ? CC.GREEN + enabled : CC.RED + disabled;
    }
}

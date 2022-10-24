package cc.invictusgames.ilib.menu.menu;

import cc.invictusgames.ilib.ILibBukkitPlugin;
import cc.invictusgames.ilib.builder.ItemBuilder;
import cc.invictusgames.ilib.chatinput.ChatInput;
import cc.invictusgames.ilib.menu.Button;
import cc.invictusgames.ilib.menu.page.PagedMenu;
import cc.invictusgames.ilib.utils.CC;
import cc.invictusgames.ilib.utils.ChatMessage;
import cc.invictusgames.ilib.utils.PasteUtils;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class TextEditMenu extends PagedMenu {

    private final List<String> currentLines;
    private final Consumer<List<String>> consumer;
    private final String title;

    public TextEditMenu(List<String> originalLines, Consumer<List<String>> consumer, String title) {
        this.consumer = consumer;
        this.currentLines = new ArrayList<>(originalLines);
        this.title = title;
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        for (int i = 0; i < currentLines.size(); i++) {
            String text = currentLines.get(i);
            buttons.put(buttons.size(), new LineButton(i, text));
        }

        return buttons;
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(1, new ExportButton());
        buttons.put(2, new ImportButton());
        buttons.put(3, new PreviewButton());
        buttons.put(5, new NewLineButton());
        buttons.put(6, new CancelButton());
        buttons.put(7, new SaveButton());

        return buttons;
    }

    @Override
    public String getRawTitle(Player player) {
        return title;
    }

    @Override
    public boolean isAutoUpdate() {
        return false;
    }

    @Override
    public boolean isClickUpdate() {
        return true;
    }

    @RequiredArgsConstructor
    public class LineButton extends Button {

        private final int index;
        private final String text;

        @Override
        public ItemStack getItem(Player player) {
            return new ItemBuilder(Material.PAPER)
                    .setDisplayName(CC.RESET + (text.isEmpty() ? "{empty}" : text))
                    .addToLore(
                            CC.format("&eLine &c#%d", index + 1),
                            " ",
                            CC.translate("&cLeft-Click &eto change text."),
                            CC.translate("&cRight-Click &eto delete this line."),
                            CC.translate("&cMiddle-Click &eto insert a line here."),
                            CC.translate("&cShift Left/Right-Click &emove left/right.")
                    ).build();
        }

        @Override
        public void click(Player player, int slot, ClickType clickType, int hotbarButton) {
            if (clickType == ClickType.RIGHT) {
                new ConfirmationMenu(
                        "Delete line?",
                        b -> {
                            if (b)
                                currentLines.remove(index);

                            openMenu(player);
                        }
                ).openMenu(player);
                return;
            }

            if (clickType == ClickType.LEFT) {
                player.closeInventory();
                new TextInput(input -> {
                    currentLines.remove(index);
                    currentLines.add(index, input);

                    openMenu(player);
                }, text).send(player);
                return;
            }

            if (clickType == ClickType.MIDDLE) {
                new ConfirmationMenu(
                        "Insert before or after?",
                        "After",
                        "Before",
                        b -> new TextInput(input -> {
                            int mod = b ? 1 : 0;
                            int position = index + mod;
                            if (position < 0)
                                position = 0;

                            if (position > currentLines.size() - 1)
                                currentLines.add(input);
                            else currentLines.add(position, input);

                            openMenu(player);
                        }, null).send(player)
                ).openMenu(player);
                return;
            }

            if (!clickType.isShiftClick())
                return;

            int mod = clickType.isLeftClick() ? -1 : 1;
            int position = index + mod;
            if (position < 0)
                position = 0;

            currentLines.remove(index);
            if (position > currentLines.size() - 1)
                currentLines.add(text);
            else currentLines.add(position, text);
        }
    }

    public class SaveButton extends Button {

        @Override
        public ItemStack getItem(Player player) {
            return new ItemBuilder(Material.EMERALD_BLOCK)
                    .setDisplayName(CC.GREEN + CC.BOLD + "Save Lines")
                    .build();
        }

        @Override
        public void click(Player player, int slot, ClickType clickType, int hotbarButton) {
            player.sendMessage(CC.GREEN + "You have saved the lines.");
            consumer.accept(currentLines);
        }
    }

    public class CancelButton extends Button {

        @Override
        public ItemStack getItem(Player player) {
            return new ItemBuilder(Material.REDSTONE_BLOCK)
                    .setDisplayName(CC.RED + CC.BOLD + "Cancel Editing")
                    .build();
        }

        @Override
        public void click(Player player, int slot, ClickType clickType, int hotbarButton) {
            player.closeInventory();
            player.sendMessage(CC.RED + "You cancelled the text editing.");
        }
    }

    public class PreviewButton extends Button {

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder builder = new ItemBuilder(Material.PAINTING)
                    .setDisplayName(CC.YELLOW + CC.BOLD + "Text Preview");

            int i = 0;
            for (String line : currentLines)
                builder.addToLore(CC.GRAY + (++i) + ". " + CC.RESET + line);

            return builder.build();
        }
    }

    public class NewLineButton extends Button {

        @Override
        public ItemStack getItem(Player player) {
            return new ItemBuilder(Material.SIGN)
                    .setDisplayName(CC.YELLOW + CC.BOLD + "Insert a new line")
                    .build();
        }

        @Override
        public void click(Player player, int slot, ClickType clickType, int hotbarButton) {
            player.closeInventory();

            new TextInput(input -> {
                currentLines.add(input);
                openMenu(player);
            }, null).send(player);
        }
    }

    public class ImportButton extends Button {

        @Override
        public ItemStack getItem(Player player) {
            return new ItemBuilder(Material.BOOK_AND_QUILL)
                    .setDisplayName(CC.YELLOW + CC.BOLD + "Import from link")
                    .build();
        }

        @Override
        public void click(Player player, int slot, ClickType clickType, int hotbarButton) {
            player.closeInventory();
            new ImportInput().send(player);
        }
    }

    public class ExportButton extends Button {

        @Override
        public ItemStack getItem(Player player) {
            return new ItemBuilder(Material.BOOK)
                    .setDisplayName(CC.YELLOW + CC.BOLD + "Export to pastebin")
                    .build();
        }

        @Override
        public void click(Player player, int slot, ClickType clickType, int hotbarButton) {
            Bukkit.getScheduler().runTaskAsynchronously(ILibBukkitPlugin.getInstance(), () -> {
                StringBuilder builder = new StringBuilder();
                currentLines.forEach(s -> builder.append(translateToAlternateChar(s)).append("\n"));

                String paste = PasteUtils.paste(builder.toString(), true);
                if (paste == null) {
                    player.sendMessage(CC.RED + "Failed to export lines.");
                    return;
                }

                player.sendMessage(CC.YELLOW + "Export link: " + CC.RED + paste);
            });
        }
    }

    public class TextInput extends ChatInput<String> {

        private final String current;

        public TextInput(Consumer<String> consumer, String current) {
            super(String.class);

            this.current = current;

            text(CC.translate("&ePlease enter the new line (\"{empty}\" for an empty line), " +
                    "or say &ccancel &eto cancel."));
            escapeMessage(CC.RED + "You cancelled the line insertion.");
            onCancel(TextEditMenu.this::openMenu);

            accept((player, input) -> {
                if (input.equalsIgnoreCase("{empty}"))
                    input = "";
                else input = CC.translate(input);

                consumer.accept(input);
                return true;
            });
        }

        @Override
        public void send(Player player) {
            super.send(player);

            if (current == null)
                return;

            new ChatMessage(CC.format(
                    "&e(Click to get the suggested %s&e)",
                    CC.translate(current)
            )).suggestCommand(translateToAlternateChar(current)).send(player);
        }
    }

    public class ImportInput extends ChatInput<String> {

        public ImportInput() {
            super(String.class);

            escapeMessage(CC.RED + "You cancelled the line importing.");
            text(CC.translate("&ePlease enter the raw url to import the lines from, or say &ccancel &eto cancel."));
            onCancel(TextEditMenu.this::openMenu);

            accept((player, input) -> {
                Bukkit.getScheduler().runTaskAsynchronously(ILibBukkitPlugin.getInstance(), () -> {
                    HttpURLConnection connection = null;
                    try {
                        URL url = new URL(input);
                        connection = (HttpURLConnection) url.openConnection();
                        connection.setConnectTimeout(5000);
                        connection.setReadTimeout(5000);
                        connection.setDoOutput(true);
                        connection.setInstanceFollowRedirects(false);
                        connection.setRequestMethod("GET");
                        connection.setRequestProperty("User-Agent", "ILib Hastebin API");
                        connection.setUseCaches(false);

                        currentLines.clear();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        reader.lines().forEach(s -> currentLines.add(CC.translate(s)));

                    } catch (Exception e) {
                        e.printStackTrace();
                        player.sendMessage(CC.RED + "Something went wrong parsing lines idek");
                    } finally {
                        if (connection != null)
                            connection.disconnect();
                        openMenu(player);
                    }
                });
                return true;
            });
        }
    }

    private static String translateToAlternateChar(String textToTranslate) {
        char[] b = textToTranslate.toCharArray();

        for (int i = 0; i < b.length - 1; ++i) {
            if (b[i] == ChatColor.COLOR_CHAR && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[i + 1]) > -1) {
                b[i] = '&';
                b[i + 1] = Character.toLowerCase(b[i + 1]);
            }
        }

        return new String(b);
    }
}

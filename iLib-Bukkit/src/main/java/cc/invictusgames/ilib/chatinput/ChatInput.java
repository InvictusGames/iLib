package cc.invictusgames.ilib.chatinput;

import cc.invictusgames.ilib.command.CommandService;
import cc.invictusgames.ilib.command.parameter.ParameterType;
import cc.invictusgames.ilib.utils.CC;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Getter(AccessLevel.PROTECTED)
public class ChatInput<T> {

    private static final Map<UUID, ChatInput<?>> INPUT_MAP = new ConcurrentHashMap<>();

    protected static ChatInput<?> getInput(Player player) {
        return INPUT_MAP.get(player.getUniqueId());
    }

    protected static void clear(UUID uuid) {
        INPUT_MAP.remove(uuid);
    }

    private final Class<?> clazz;

    @Setter(AccessLevel.PROTECTED)
    private ChatInputChain parent;

    private String[] text = new String[]{""};

    private boolean exitOnInvalidInput = false;
    private String[] escapeSequences = new String[]{"cancel"};
    private String escapeMessage = CC.RED + "You cancelled the active input.";

    private ChatInputConsumer<T> consumer;
    private Consumer<Player> onCancel;

    public ChatInput(Class<?> clazz) {
        this.clazz = clazz;
    }

    public ChatInput<T> text(String... text) {
        if (text == null)
            text = new String[]{""};

        this.text = text;
        return this;
    }

    public ChatInput<T> exitOnInvalidInput() {
        this.exitOnInvalidInput = true;
        return this;
    }

    public ChatInput<T> escapeSequences(String... escapeSequences) {
        this.escapeSequences = escapeSequences;
        return this;
    }

    public ChatInput<T> escapeMessage(String escapeMessage) {
        this.escapeMessage = escapeMessage;
        return this;
    }

    public ChatInput<T> accept(ChatInputConsumer<T> consumer) {
        this.consumer = consumer;
        return this;
    }

    public ChatInput<T> onCancel(Consumer<Player> consumer) {
        this.onCancel = consumer;
        return this;
    }

    public void send(Player player) {
        if (text.length != 1 || !text[0].isEmpty())
            player.sendMessage(text);
        INPUT_MAP.put(player.getUniqueId(), this);
    }

    protected void handle(Player player, String message) {
        message = message.trim();

        for (String escapeSequence : escapeSequences) {
            if (escapeSequence.equalsIgnoreCase(message)) {
                ChatInput.clear(player.getUniqueId());
                player.sendMessage(escapeMessage);
                if (onCancel != null)
                    onCancel.accept(player);
                return;
            }
        }

        ParameterType<T> parameter = (ParameterType<T>) CommandService.getParameter(clazz);
        if (parameter == null) {
            ChatInput.clear(player.getUniqueId());
            player.sendMessage(CC.RED + "Could not find a ParameterType to parse " + clazz.getName() + "." +
                    " Please contact the server administration if this continues to happen.");
            return;
        }

        T parsed = parameter.parse(player, message);
        if (parsed == null) {
            if (exitOnInvalidInput)
                ChatInput.clear(player.getUniqueId());
            else send(player);
            return;
        }

        if (!consumer.accept(player, parsed)) {
            if (exitOnInvalidInput)
                ChatInput.clear(player.getUniqueId());
            else send(player);
            return;
        }

        ChatInput.clear(player.getUniqueId());
        if (parent != null)
            parent.next(player);
    }
}

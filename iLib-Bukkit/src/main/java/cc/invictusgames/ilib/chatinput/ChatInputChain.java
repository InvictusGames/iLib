package cc.invictusgames.ilib.chatinput;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ChatInputChain {

    private final Map<UUID, Integer> index = new ConcurrentHashMap<>();
    private final List<ChatInput<?>> list = new ArrayList<>();

    private boolean exitOnInvalidInput = false;
    private String[] escapeSequences = null;
    private String escapeMessage = null;

    public ChatInputChain exitOnInvalidInput() {
        this.exitOnInvalidInput = true;
        return this;
    }

    public ChatInputChain escapeSequences(String... escapeSequences) {
        this.escapeSequences = escapeSequences;
        return this;
    }

    public ChatInputChain escapeMessage(String escapeMessage) {
        this.escapeMessage = escapeMessage;
        return this;
    }

    public ChatInputChain next(ChatInput<?> input) {
        list.add(input);

        if (exitOnInvalidInput)
            input.exitOnInvalidInput();

        if (escapeSequences != null)
            input.escapeSequences(escapeSequences);

        if (escapeMessage != null)
            input.escapeMessage(escapeMessage);

        input.setParent(this);

        return this;
    }

    public void start(Player player) {
        if (list.isEmpty())
            return;

        index.put(player.getUniqueId(), 0);
        ChatInput<?> input = list.get(0);
        input.send(player);
    }

    protected void next(Player player) {
        if (list.isEmpty())
            return;

         int index = this.index.getOrDefault(player.getUniqueId(), 0) + 1;
         if (index >= list.size()) {
             this.index.remove(player.getUniqueId());
             return;
         }

         this.index.put(player.getUniqueId(), index);
         ChatInput<?> input = list.get(index);
         input.send(player);
    }

}

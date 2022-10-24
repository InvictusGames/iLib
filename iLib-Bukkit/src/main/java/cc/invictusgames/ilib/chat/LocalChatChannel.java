package cc.invictusgames.ilib.chat;

import java.util.List;

public abstract class LocalChatChannel extends ChatChannel {

    public LocalChatChannel(String name, String displayName, String permission, List<String> aliases,
                            char prefix, int priority) {
        super(name, displayName, permission, aliases, prefix, priority);
    }

}

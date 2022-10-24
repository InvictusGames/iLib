package cc.invictusgames.ilib.messages.page;

import cc.invictusgames.ilib.utils.CC;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 25.02.2020 / 00:25
 * iLib / cc.invictusgames.ilib.messages.page
 */

public abstract class PagedMessage<T> {

    private int maxMessagesPerPage;

    public PagedMessage() {
        this(9);
    }

    public PagedMessage(int maxMessagesPerPage) {
        this.maxMessagesPerPage = maxMessagesPerPage;
    }

    public void display(CommandSender sender, List<T> messages, int page) {
        if (messages.size() == 0) {
            sender.sendMessage(CC.RED + "No entries were found");
            return;
        }

        int maxPages = messages.size() / this.maxMessagesPerPage + 1;
        if ((page <= 0) || (page > maxPages)) {
            sender.sendMessage(CC.RED + "Page " + page + " not found. (1-" + maxPages + ")");
            return;
        }

        int minIndex = (int) ((double) (page - 1) * this.maxMessagesPerPage);
        int maxIndex = (int) ((double) (page) * this.maxMessagesPerPage);

        for (String s : getHeader(page, maxPages))
            sender.sendMessage(s);

        for (int i = minIndex; (i < maxIndex) && (i < messages.size()); i++) {
            this.send(sender, messages.get(i));
        }

        for (String s : this.getFooter(page, maxPages))
            sender.sendMessage(s);
    }

    public abstract List<String> getHeader(int page, int maxPages);

    public abstract List<String> getFooter(int page, int maxPages);

    public abstract void send(CommandSender sender, T t);

}

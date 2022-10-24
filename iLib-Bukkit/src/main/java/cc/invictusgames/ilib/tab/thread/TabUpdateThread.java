package cc.invictusgames.ilib.tab.thread;

import cc.invictusgames.ilib.tab.PlayerTab;
import cc.invictusgames.ilib.tab.TabService;
import lombok.Getter;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class TabUpdateThread extends Thread {

    @Getter
    private static final AtomicBoolean running = new AtomicBoolean(true);

    private final TabService tabService;

    public TabUpdateThread(TabService tabService) {
        super("iLib - Tab Update Thread");
        this.tabService = tabService;
        this.setDaemon(true);
    }

    @Override
    public void run() {
        while (running.get()) {
            for (Map.Entry<UUID, PlayerTab> entry : tabService.getTabs().entrySet()) {
                try {
                    entry.getValue().update();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                Thread.sleep(250L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

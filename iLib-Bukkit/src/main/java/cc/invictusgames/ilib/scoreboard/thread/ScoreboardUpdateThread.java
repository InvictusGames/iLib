package cc.invictusgames.ilib.scoreboard.thread;

import cc.invictusgames.ilib.scoreboard.PlayerScoreboard;
import cc.invictusgames.ilib.scoreboard.ScoreboardService;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.concurrent.atomic.AtomicBoolean;

public class ScoreboardUpdateThread extends Thread {

    private final ScoreboardService scoreboardService;

    @Getter
    private static final AtomicBoolean running = new AtomicBoolean(true);

    public ScoreboardUpdateThread(ScoreboardService scoreboardService) {
        super("iLib - Scoreboard Update Thread");
        this.setDaemon(true);
        this.scoreboardService = scoreboardService;
    }

    @Override
    public void run() {
        while (running.get()) {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                PlayerScoreboard board = ScoreboardService.getBoard(onlinePlayer);
                if (board != null && !board.isRunning())
                    scoreboardService.updateScoreboard(onlinePlayer);
            }
            try {
                Thread.sleep(ScoreboardService.UPDATE_TICKS * 50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

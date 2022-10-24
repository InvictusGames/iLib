package cc.invictusgames.ilib.reboot;

import cc.invictusgames.ilib.ILibBukkitPlugin;
import cc.invictusgames.ilib.utils.CC;
import cc.invictusgames.ilib.utils.TimeUtils;
import org.bukkit.Bukkit;

import java.util.concurrent.TimeUnit;

public class RebootService {

    public static final String CHAT_BAR = CC.RED + "⚠ " + CC.DRED + CC.STRIKE_THROUGH + "------------------------" + CC.RED + " ⚠";

    private static RebootTask rebootTask = null;

    public static void reboot(long millis) {
        if (rebootTask != null)
            return;

        rebootTask = new RebootTask((int) TimeUnit.MILLISECONDS.toSeconds(millis));
        rebootTask.runTaskTimer(ILibBukkitPlugin.getInstance(), 20L, 20L);
    }

    public static void cancel() {
        if (rebootTask == null)
            return;

        rebootTask.cancel();
        rebootTask = null;
        Bukkit.broadcastMessage(RebootService.CHAT_BAR);
        Bukkit.broadcastMessage(CC.RED + "The reboot has been cancelled.");
        Bukkit.broadcastMessage(RebootService.CHAT_BAR);
    }

    public static boolean isRebooting() {
        return rebootTask != null;
    }

    public static String getScoreboardString() {
        if (!isRebooting())
            return null;

        return CC.DRED + CC.BOLD + "Reboot" + CC.GRAY + ": " + CC.DRED + "*"
                + TimeUtils.formatHHMMSS(rebootTask.getSecondsRemaining(), TimeUnit.SECONDS);
    }

}

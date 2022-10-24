package cc.invictusgames.ilib.command.parameter.defaults;


import cc.invictusgames.ilib.command.parameter.ParameterType;
import cc.invictusgames.ilib.utils.CC;
import cc.invictusgames.ilib.utils.TimeUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;


@Data
@AllArgsConstructor
public class Duration {
    private long duration;
    private String source;
    private boolean permanent;

    public static class Type implements ParameterType<Duration> {

        public Duration parse(CommandSender sender, String source) {
            if (source.equalsIgnoreCase("perm") || (source.equalsIgnoreCase("permanent"))) {
                return new Duration(-1, source, true);
            }

            long parsed = TimeUtils.parseTime(source);
            if (parsed == -1) {
                sender.sendMessage(CC.YELLOW + source + CC.RED + " is not a valid duration.");
                return null;
            }

            return new Duration(parsed, source, false);
        }

        @Override
        public List<String> tabComplete(CommandSender sender, List<String> flags) {
            return Collections.emptyList();
        }
    }
}
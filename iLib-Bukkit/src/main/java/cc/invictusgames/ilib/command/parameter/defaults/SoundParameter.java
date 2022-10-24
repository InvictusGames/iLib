package cc.invictusgames.ilib.command.parameter.defaults;

import cc.invictusgames.ilib.command.parameter.ParameterType;
import cc.invictusgames.ilib.utils.CC;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 23.10.2020 / 09:20
 * iLib / cc.invictusgames.ilib.command.parameter.defaults
 */

public class SoundParameter implements ParameterType<Sound> {

    @Override
    public Sound parse(CommandSender sender, String source) {
        Sound parsed = null;
        for (Sound sound : Sound.values()) {
            if (sound.name().equalsIgnoreCase(source)) {
                parsed = sound;
                break;
            }

            if (sound.name().replace("_", "").equalsIgnoreCase(source)) {
                parsed = sound;
                break;
            }
        }

        if (parsed == null)
            sender.sendMessage(CC.RED + "Sound " + CC.YELLOW + source + CC.RED + " not found.");

        return parsed;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, List<String> flags) {
        List<String> completions = new ArrayList<>();
        for (Sound sound : Sound.values()) {
            completions.add(sound.name().toLowerCase());
        }
        return completions;
    }
}

package cc.invictusgames.ilib.command.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 03.06.2020 / 11:23
 * iLib / cc.invictusgames.ilib.commandapi.data
 */

@Getter
@AllArgsConstructor
public class FlagData implements Data {

    private final List<String> names;
    private final boolean defaultValue;
    private final String description;
    private final boolean hidden;
    private final int index;

}

package cc.invictusgames.ilib.utils;

import lombok.Getter;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 17.03.2020 / 18:17
 * iLib / cc.invictusgames.ilib.utils
 */

public class UsageFlag {

    @Getter private String flag;
    @Getter private String description;

    public UsageFlag(String flag, String description) {
        this.flag = flag;
        this.description = description;
    }
}

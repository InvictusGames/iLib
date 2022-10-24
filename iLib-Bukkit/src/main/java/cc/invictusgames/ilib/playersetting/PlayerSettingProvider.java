package cc.invictusgames.ilib.playersetting;

import java.util.List;

public interface PlayerSettingProvider {

    List<PlayerSetting> getProvidedSettings();

    int getPriority();

}

package cc.invictusgames.ilib.playersetting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PlayerSettingService {

    private static final List<PlayerSettingProvider> PROVIDERS = new ArrayList<>();


    public static void registerProvider(PlayerSettingProvider provider) {
        PROVIDERS.add(provider);
        PROVIDERS.sort(Comparator.comparingInt(PlayerSettingProvider::getPriority));
    }

    public static List<PlayerSetting> getAllSettings() {
        List<PlayerSetting> settings = new ArrayList<>();
        for (PlayerSettingProvider provider : PROVIDERS)
            settings.addAll(provider.getProvidedSettings());
        return Collections.unmodifiableList(settings);
    }

}

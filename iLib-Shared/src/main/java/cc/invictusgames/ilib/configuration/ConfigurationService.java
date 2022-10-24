package cc.invictusgames.ilib.configuration;

import java.io.File;
import java.io.IOException;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 12.02.2020 / 21:05
 * iLib / cc.invictusgames.ilib.configuration
 */

public interface ConfigurationService {

    void saveConfiguration(StaticConfiguration configuration, File file) throws IOException;

    <T extends StaticConfiguration> T loadConfiguration(Class<? extends T> clazz, File file);

}

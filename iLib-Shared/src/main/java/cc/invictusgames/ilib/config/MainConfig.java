package cc.invictusgames.ilib.config;

import cc.invictusgames.ilib.configuration.StaticConfiguration;
import cc.invictusgames.ilib.configuration.defaults.RedisConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 13.02.2020 / 21:02
 * iLib / cc.invictusgames.ilib.config
 */

@NoArgsConstructor
@Data
@EqualsAndHashCode
@ToString
public class MainConfig implements StaticConfiguration {

    private String messagesPath = "/home/Messages/";
    private String itemsPath = "/home/items.csv";
    private RedisConfig redisConfig = new RedisConfig();

}

package cc.invictusgames.ilib.configuration.defaults;

import cc.invictusgames.ilib.configuration.StaticConfiguration;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 12.02.2020 / 21:33
 * iLib / cc.invictusgames.ilib.configuration.defaults
 */

@NoArgsConstructor
@Data
@EqualsAndHashCode
@ToString
public class RedisConfig implements StaticConfiguration {

    private String host = "localhost";
    private int port = 6379;
    private boolean authEnabled = false;
    private String authPassword = "password";
    private int dbId = 0;

}

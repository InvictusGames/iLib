package cc.invictusgames.ilib.configuration.defaults;

import cc.invictusgames.ilib.configuration.StaticConfiguration;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 12.02.2020 / 21:29
 * iLib / cc.invictusgames.ilib.configuration.defaults
 */

@NoArgsConstructor
@Data
@EqualsAndHashCode
@ToString
public class MongoConfig implements StaticConfiguration {

    private String host = "localhost";
    private int port = 27017;
    private boolean authEnabled = false;
    private String authUsername = "username";
    private String authPassword = "password";
    private String authDatabase = "admin";

}

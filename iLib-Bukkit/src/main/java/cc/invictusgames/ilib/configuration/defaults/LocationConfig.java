package cc.invictusgames.ilib.configuration.defaults;

import cc.invictusgames.ilib.configuration.StaticConfiguration;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.bukkit.Location;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 15.03.2020 / 13:58
 * iLib / cc.invictusgames.ilib.configuration.defaults
 */
@NoArgsConstructor
@Data
@EqualsAndHashCode
@ToString
public class LocationConfig extends SimpleLocationConfig implements StaticConfiguration {

    public LocationConfig(Location location) {
        this(location, false);
    }

    public LocationConfig(Location location, boolean block) {
        setLocation(location, block);
    }

    private float pitch = 0;
    private float yaw = 0;

    @Override
    public void setLocation(Location location) {
        this.setLocation(location, false);
    }

    @Override
    public void setLocation(Location location, boolean block) {
        super.setLocation(location, block);
        this.pitch = location.getPitch();
        this.yaw = location.getYaw();
    }

    @Override
    public Location getLocation() {
        Location location = super.getLocation();
        location.setPitch(this.pitch);
        location.setYaw(this.yaw);
        return location;
    }
}

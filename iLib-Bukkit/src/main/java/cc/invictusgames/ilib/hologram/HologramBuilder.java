package cc.invictusgames.ilib.hologram;

import cc.invictusgames.ilib.hologram.clickhandler.HologramClickHandler;
import cc.invictusgames.ilib.hologram.statics.StaticHologram;
import cc.invictusgames.ilib.hologram.statics.StaticHologramBuilder;
import cc.invictusgames.ilib.hologram.updating.UpdatingHologramBuilder;
import cc.invictusgames.ilib.utils.Debugger;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Location;

@Getter
@NoArgsConstructor
public class HologramBuilder {

    private Location location;
    private double lineSpacing = 0.23;
    private HologramClickHandler clickHandler;

    public HologramBuilder at(Location location) {
        this.location = location;
        return this;
    }

    public HologramBuilder withSpacing(double lineSpacing) {
        this.lineSpacing = lineSpacing;
        return this;
    }

    public HologramBuilder clickHandler(HologramClickHandler hologramClickHandler) {
        this.clickHandler = hologramClickHandler;
        return this;
    }

    public UpdatingHologramBuilder updating() {
        return new UpdatingHologramBuilder(this);
    }

    public StaticHologramBuilder staticHologram() {
        return new StaticHologramBuilder(this);
    }

    public Hologram build() {
        throw new UnsupportedOperationException("Please specify the hologram type using #updating or #staticHologram");
    }

}

package cc.invictusgames.ilib.hologram.updating;

import cc.invictusgames.ilib.hologram.HologramBuilder;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.concurrent.TimeUnit;

@Getter(AccessLevel.PROTECTED)
@NoArgsConstructor
public class UpdatingHologramBuilder extends HologramBuilder {

    private long interval = -1;
    private HologramProvider provider;

    public UpdatingHologramBuilder(HologramBuilder builder) {
        this.at(builder.getLocation());
        this.withSpacing(builder.getLineSpacing());
        this.clickHandler(builder.getClickHandler());
    }

    public UpdatingHologramBuilder interval(long time, TimeUnit unit) {
        this.interval = unit.toSeconds(time) * 20;
        return this;
    }

    public UpdatingHologramBuilder intervalTicks(long ticks) {
        this.interval = ticks;
        return this;
    }

    public UpdatingHologramBuilder provider(HologramProvider provider) {
        this.provider = provider;
        return this;
    }

    @Override
    public UpdatingHologram build() {
        return new UpdatingHologram(this);
    }
}

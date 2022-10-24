package cc.invictusgames.ilib.hologram.statics;

import cc.invictusgames.ilib.hologram.HologramBuilder;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter(AccessLevel.PROTECTED)
@NoArgsConstructor
public class StaticHologramBuilder extends HologramBuilder {

    private final List<String> lines = new ArrayList<>();

    public StaticHologramBuilder(HologramBuilder builder) {
        this.at(builder.getLocation());
        this.withSpacing(builder.getLineSpacing());
        this.clickHandler(builder.getClickHandler());
    }

    public StaticHologramBuilder addLines(String... lines) {
        this.lines.addAll(Arrays.asList(lines));
        return this;
    }

    public StaticHologramBuilder addLines(Iterable<String> iterable) {
        for (String line : iterable) {
            lines.add(line);
        }
        return this;
    }

    @Override
    public StaticHologram build() {
        return new StaticHologram(this);
    }
}

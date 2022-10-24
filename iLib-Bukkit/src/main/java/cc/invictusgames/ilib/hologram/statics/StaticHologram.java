package cc.invictusgames.ilib.hologram.statics;

import cc.invictusgames.ilib.configuration.defaults.SimpleLocationConfig;
import cc.invictusgames.ilib.hologram.Hologram;
import cc.invictusgames.ilib.hologram.HologramBuilder;
import cc.invictusgames.ilib.hologram.HologramLine;
import cc.invictusgames.ilib.hologram.config.HologramConfigEntry;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class StaticHologram extends Hologram {

    @Getter
    @Setter
    private String name;

    private final List<HologramLine> lines = new ArrayList<>();

    protected StaticHologram(StaticHologramBuilder builder) {
        super(builder);
        for (String line : builder.getLines()) {
            lines.add(new HologramLine(line));
        }
    }

    @Override
    public List<HologramLine> getLines(Player player) {
        return lines;
    }

    public void addLines(String... lines) {
        for (String line : lines) {
            this.lines.add(new HologramLine(line));
        }
        update();
    }

    public void setLine(int position, String text) {
        destroy();
        if (position > lines.size() - 1) {
            lines.add(new HologramLine(text));
        } else if (lines.get(position) != null)
            lines.get(position).setText(text);
        else lines.set(position, new HologramLine(text));
        update();
    }

    public void setLines(Iterable<String> lines) {
        this.lines.clear();
        for (String line : lines) {
            this.lines.add(new HologramLine(line));
        }
        update();
    }

    public List<HologramLine> getCurrentLines() {
        return lines;
    }

    public HologramConfigEntry toConfig() {
        List<String> lines = new ArrayList<>();
        for (HologramLine line : this.lines) {
            lines.add(line.getText());
        }

        return new HologramConfigEntry(
                new SimpleLocationConfig(getLocation()),
                name,
                getLineSpacing(),
                lines
        );
    }

}

package cc.invictusgames.ilib.config.generator;

import cc.invictusgames.ilib.configuration.StaticConfiguration;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 16.07.2020 / 10:58
 * iLib / cc.invictusgames.ilib.config.generator
 */

@NoArgsConstructor
@Data
@EqualsAndHashCode
@ToString
public class OreGeneratorEntry implements StaticConfiguration {

    private boolean enabled = true;
    private boolean mustTouchAir = false;
    private int attempts = 15;
    private int chance = 60;
    private int maxSize = 1;
    private int minY = 5;
    private int maxY = 15;

    public OreGeneratorEntry(int attempts, int chance, int maxSize, int minY, int maxY) {
        this.attempts = attempts;
        this.chance = chance;
        this.maxSize = maxSize;
        this.minY = minY;
        this.maxY = maxY;
    }
}

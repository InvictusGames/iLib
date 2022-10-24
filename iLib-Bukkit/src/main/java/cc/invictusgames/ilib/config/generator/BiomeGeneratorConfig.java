package cc.invictusgames.ilib.config.generator;

import cc.invictusgames.ilib.configuration.StaticConfiguration;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 16.07.2020 / 10:54
 * iLib / cc.invictusgames.ilib.config.generator
 */

@NoArgsConstructor
@Data
@EqualsAndHashCode
@ToString
public class BiomeGeneratorConfig implements StaticConfiguration {

    private boolean oceans = true;
    private boolean plains = true;
    private boolean desert = true;
    private boolean desertHills = true;
    private boolean extremeHills = true;
    private boolean extremeHillsPlus = true;
    private boolean forest = true;
    private boolean forestHills = true;
    private boolean taiga = true;
    private boolean taigaHills = true;
    private boolean swampland = true;
    private boolean icePlains = true;
    private boolean iceMountains = true;
    private boolean mushroomIsland = true;
    private boolean jungle = true;
    private boolean jungleHills = true;
    private boolean birchForest = true;
    private boolean birchForestHills = true;
    private boolean roofedForest = true;
    private boolean coldTaiga = true;
    private boolean coldTaigaHills = true;
    private boolean megaTaiga = true;
    private boolean megaTaigaHills = true;
    private boolean savanna = true;
    private boolean savannaPlateau = true;
    private boolean mesa = true;
    private boolean mesaPlateau = true;
    private boolean mesaPlateauF = true;


}

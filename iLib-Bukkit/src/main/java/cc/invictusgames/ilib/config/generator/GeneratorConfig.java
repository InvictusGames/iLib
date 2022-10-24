package cc.invictusgames.ilib.config.generator;

import cc.invictusgames.ilib.configuration.StaticConfiguration;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import net.minecraft.server.v1_8_R3.BiomeBase;

import java.util.Arrays;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 16.07.2020 / 10:46
 * iLib / cc.invictusgames.ilib.config.generator
 */

@NoArgsConstructor
@Data
@EqualsAndHashCode
@ToString
public class GeneratorConfig implements StaticConfiguration {

    private BiomeGeneratorConfig biomeGeneratorConfig = new BiomeGeneratorConfig();
    private String spawnBiome = "plains";
    private int spawnRadius = 0;

    private float sugarCaneMultiplier = 1.0F;
    private float cavesMultiplier = 0.1F;

    private OreGeneratorEntry coalConfig = new OreGeneratorEntry(15, 60, 16, 5, 128);
    private OreGeneratorEntry ironConfig = new OreGeneratorEntry(15, 60, 8, 5, 64);
    private OreGeneratorEntry goldConfig = new OreGeneratorEntry(15, 60, 8, 5, 29);
    private OreGeneratorEntry diamondConfig = new OreGeneratorEntry(15, 60, 7, 5, 12);
    private OreGeneratorEntry redstoneConfig = new OreGeneratorEntry(15, 60, 7, 5, 12);
    private OreGeneratorEntry lapisConfig = new OreGeneratorEntry(15, 60, 6, 14, 32);

    public BiomeBase getSpawnBiome() {
        return Arrays.stream(BiomeBase.getBiomes())
                .filter(biome -> biome.ah.equalsIgnoreCase(this.spawnBiome))
                .findAny()
                .orElse(null);
    }

}

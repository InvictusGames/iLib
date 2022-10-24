package cc.invictusgames.ilib.npc.config;

import cc.invictusgames.ilib.configuration.StaticConfiguration;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class NPCConfig implements StaticConfiguration {

    private List<NPCConfigEntry> npcs = new ArrayList<>();



}

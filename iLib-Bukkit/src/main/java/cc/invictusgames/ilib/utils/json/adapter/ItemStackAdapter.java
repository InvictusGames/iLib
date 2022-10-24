package cc.invictusgames.ilib.utils.json.adapter;

import cc.invictusgames.ilib.utils.InventoryUtils;
import com.google.gson.*;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Type;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 09.03.2021 / 03:12
 * iLib / cc.invictusgames.ilib.utils.json.adapter
 */

public class ItemStackAdapter implements JsonSerializer<ItemStack>, JsonDeserializer<ItemStack> {

    @Override
    public ItemStack deserialize(JsonElement element, Type type,
                                 JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        if (element == null || element.isJsonNull() || !element.isJsonPrimitive())
            return null;


        return InventoryUtils.itemFromBase64(element.getAsString());
    }

    @Override
    public JsonElement serialize(ItemStack item, Type type, JsonSerializationContext jsonSerializationContext) {
        if (item == null)
            return null;

        return new JsonPrimitive(InventoryUtils.itemToBase64(item));
    }
}

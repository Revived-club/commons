package club.revived.commons.bukkit.adapter;

import com.google.gson.*;

import club.revived.commons.bukkit.serialization.LocationSerializer;

import org.bukkit.Location;

import java.lang.reflect.Type;

public final class LocationTypeAdapter implements JsonSerializer<Location>, JsonDeserializer<Location> {

  @Override
  public Location deserialize(
      final JsonElement json,
      final Type typeOfT,
      final JsonDeserializationContext context) throws JsonParseException {
    return LocationSerializer.deserialize(json.getAsJsonPrimitive().getAsString());
  }

  @Override
  public JsonElement serialize(
      final Location src,
      final Type typeOfSrc,
      final JsonSerializationContext context) {
    return new JsonPrimitive(LocationSerializer.serialize(src));
  }
}

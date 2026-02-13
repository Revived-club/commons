package club.revived.commons.orm.mongodb;

import java.util.UUID;

import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import club.revived.commons.orm.ObjectMapper;
import club.revived.commons.orm.annotations.Entity;
import club.revived.commons.orm.annotations.Identifier;
import club.revived.commons.orm.annotations.Ignore;

public final class MongoObjectMapper implements ObjectMapper<Document> {

  private final Gson gson = new GsonBuilder()
      .serializeNulls()
      .create();

  @NotNull
  @Override
  public Entity read(final @NotNull Document object, final @NotNull Class<Entity> clazz) {
    try {
      final var instance = clazz.getDeclaredConstructor().newInstance();

      for (final var field : clazz.getDeclaredFields()) {
        field.setAccessible(true);

        if (field.isAnnotationPresent(Ignore.class)) {
          continue;
        }

        final var fieldName = field.getName();
        Object value;

        if (field.getType().isAssignableFrom(UUID.class) && fieldName.equals("uuid")) {
          value = object.get("_id");
          if (value != null) {
            if (value instanceof String) {
              value = UUID.fromString((String) value);
            }
          }
        } else {
          value = object.get(fieldName);

          if (value != null && value.equals("null")) {
            value = null;
          }

          if (value != null) {
            value = convertValue(value, field.getType());
          }
        }

        field.set(instance, value);
      }

      return instance;

    } catch (final Exception e) {
      throw new RuntimeException("Failed to deserialize object of type " + clazz.getName(), e);
    }
  }

  @NotNull
  @Override
  public Document write(final Object object) {
    final var jsonObject = new JsonObject();

    for (final var field : object.getClass().getDeclaredFields()) {
      field.setAccessible(true);

      if (field.isAnnotationPresent(Ignore.class)) {
        continue;
      }

      final var name = field.getName();

      try {
        final var value = field.get(object);

        if (value == null) {
          jsonObject.add(name, gson.toJsonTree("null"));
          continue;
        }

        if (field.isAnnotationPresent(Identifier.class)) {
          jsonObject.add("_id", gson.toJsonTree(value));
        } else {
          jsonObject.add(name, gson.toJsonTree(value));
        }
      } catch (final IllegalAccessException e) {
        throw new RuntimeException(e);
      }
    }

    return Document.parse(jsonObject.toString());
  }

  @NotNull
  private Object convertValue(final Object value, final Class<?> targetType) {
    if (targetType.isInstance(value)) {
      return value;
    }

    final var json = gson.toJson(value);
    return gson.fromJson(json, targetType);
  }
}

package club.revived.commons.orm.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import club.revived.commons.Tuple;
import club.revived.commons.orm.ObjectMapper;
import club.revived.commons.data.model.Entity;
import club.revived.commons.orm.annotations.Identifier;
import club.revived.commons.orm.annotations.Ignore;

public final class MongoObjectMapper implements ObjectMapper<Document> {

  private Gson gson;

  public MongoObjectMapper(final List<Tuple<Class<?>, Object>> adapters) {
    final var gsonBuilder = new GsonBuilder()
        .serializeNulls();

    for (final var adapter : adapters) {
      gsonBuilder.registerTypeAdapter(adapter.key(), adapter.val());
    }

    this.gson = gsonBuilder.create();
  }

  public MongoObjectMapper() {
    this(new ArrayList<>());
  }

  @NotNull
  @Override
  public <T extends Entity> T read(@NotNull final Document document, @NotNull final Class<T> clazz) {
    try {
      final T instance = clazz.getDeclaredConstructor().newInstance();

      for (final var field : clazz.getDeclaredFields()) {
        field.setAccessible(true);

        if (field.isAnnotationPresent(Ignore.class))
          continue;

        final String fieldName = field.getName();
        Object value = null;

        if (field.isAnnotationPresent(Identifier.class)) {
          value = document.get("_id");
          if (value != null && field.getType().equals(UUID.class) && value instanceof String) {
            value = UUID.fromString((String) value);
          }
        } else {
          value = document.get(fieldName);

          if ("null".equals(value)) {
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
  public Document write(@NotNull final Object object) {
    final JsonObject jsonObject = new JsonObject();

    for (final var field : object.getClass().getDeclaredFields()) {
      field.setAccessible(true);

      if (field.isAnnotationPresent(Ignore.class))
        continue;

      try {
        final Object value = field.get(object);

        if (field.isAnnotationPresent(Identifier.class)) {
          jsonObject.add("_id", gson.toJsonTree(value != null ? value : null));
        } else {
          jsonObject.add(field.getName(), gson.toJsonTree(value != null ? value : null));
        }
      } catch (final Exception e) {
        e.printStackTrace();
      }
    }

    return Document.parse(jsonObject.toString());
  }

  @NotNull
  private Object convertValue(final Object value, final Class<?> targetType) {
    if (value == null) {
      return null;
    }

    if (targetType.isInstance(value)) {
      return value;
    }

    final String json = gson.toJson(value);
    return gson.fromJson(json, targetType);
  }
}

package club.revived.commons.orm.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import club.revived.commons.Tuple;
import club.revived.commons.orm.ObjectMapper;
import club.revived.commons.orm.annotations.Entity;
import club.revived.commons.orm.annotations.Ignore;

public final class JsonObjectMapper implements ObjectMapper<String> {
  private Gson gson;

  public JsonObjectMapper(final List<Tuple<Class<?>, Object>> adapters) {
    final var gsonBuilder = new GsonBuilder()
        .serializeNulls();

    for (final var adapter : adapters) {
      gsonBuilder.registerTypeAdapter(adapter.key(), adapter.val());
    }
    this.gson = gsonBuilder.create();
  }

  public JsonObjectMapper() {
    this(new ArrayList<>());
  }

  @Override
  public <E extends Entity> @NotNull E read(final @NotNull String json, final @NotNull Class<E> clazz) {
    try {
      final E instance = clazz.getDeclaredConstructor().newInstance();

      @SuppressWarnings("unchecked")
      final Map<String, Object> dataMap = gson.fromJson(json, Map.class);

      for (final var field : clazz.getDeclaredFields()) {
        field.setAccessible(true);
        if (field.isAnnotationPresent(Ignore.class))
          continue;

        final String fieldName = field.getName();
        Object value = dataMap.get(fieldName);

        if ("null".equals(value)) {
          value = null;
        }

        if (value != null) {
          value = convertValue(value, field.getType());
        }

        field.set(instance, value);
      }
      return instance;
    } catch (final Exception e) {
      throw new RuntimeException("Failed to deserialize object of type " + clazz.getName(), e);
    }
  }

  @Override
  public @NotNull String write(final @NotNull Object object) {
    final JsonObject jsonObject = new JsonObject();
    for (final var field : object.getClass().getDeclaredFields()) {
      field.setAccessible(true);
      if (field.isAnnotationPresent(Ignore.class))
        continue;

      try {
        final Object value = field.get(object);
        jsonObject.add(field.getName(), gson.toJsonTree(value != null ? value : null));
      } catch (final Exception e) {
        e.printStackTrace();
      }
    }
    return jsonObject.toString();
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

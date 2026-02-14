package club.revived.commons.orm.codec;

import java.lang.reflect.Constructor;
import java.lang.reflect.RecordComponent;
import java.util.HashMap;
import java.util.Map;

import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;
import org.jetbrains.annotations.NotNull;

import club.revived.commons.orm.annotations.Identifier;
import club.revived.commons.orm.annotations.Ignore;
import club.revived.commons.orm.annotations.Entity;

public final class EntityCodec<T extends Entity> implements Codec<T> {

  private final Class<T> clazz;
  private final CodecRegistry registry;
  private final Constructor<T> constructor;
  private final RecordComponent[] components;

  public EntityCodec(
      @NotNull final Class<T> clazz,
      @NotNull final CodecRegistry registry) {

    if (!clazz.isRecord())
      throw new IllegalArgumentException("EntityCodec supports records only.");

    this.clazz = clazz;
    this.registry = registry;
    this.components = clazz.getRecordComponents();

    try {
      final Class<?>[] parameterTypes = new Class<?>[components.length];

      for (int i = 0; i < components.length; i++) {
        parameterTypes[i] = components[i].getType();
      }

      this.constructor = clazz.getDeclaredConstructor(parameterTypes);
      this.constructor.setAccessible(true);

    } catch (final Exception exception) {
      throw new RuntimeException(
          "Failed to initialize codec for " + clazz.getName(),
          exception);
    }
  }

  @Override
  public void encode(
      @NotNull final BsonWriter writer,
      @NotNull final T value,
      @NotNull final EncoderContext encoderContext) {

    writer.writeStartDocument();

    try {

      for (final RecordComponent component : components) {

        if (component.isAnnotationPresent(Ignore.class))
          continue;

        final String fieldName = component.isAnnotationPresent(Identifier.class)
            ? "_id"
            : component.getName();

        final Object fieldValue = component.getAccessor().invoke(value);

        writer.writeName(fieldName);

        if (fieldValue == null) {
          writer.writeNull();
          continue;
        }

        @SuppressWarnings("unchecked")
        final Codec<Object> codec = (Codec<Object>) registry.get(fieldValue.getClass());

        encoderContext.encodeWithChildContext(codec, writer, fieldValue);
      }

    } catch (final Exception exception) {
      throw new RuntimeException(
          "Failed to encode " + clazz.getName(),
          exception);
    }

    writer.writeEndDocument();
  }

  @Override
  public T decode(
      @NotNull final BsonReader reader,
      @NotNull final DecoderContext decoderContext) {

    final Map<String, Object> values = new HashMap<>();

    reader.readStartDocument();

    while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {

      final String name = reader.readName();

      final RecordComponent component = findComponent(name);

      final Object value;

      if (reader.getCurrentBsonType() == BsonType.NULL) {
        reader.readNull();
        value = null;
      } else {
        @SuppressWarnings("unchecked")
        final Codec<Object> codec = (Codec<Object>) registry.get(component.getType());

        value = decoderContext.decodeWithChildContext(codec, reader);
      }

      values.put(component.getName(), value);
    }

    reader.readEndDocument();

    try {
      final Object[] args = new Object[components.length];

      for (int i = 0; i < components.length; i++) {
        args[i] = values.get(components[i].getName());
      }

      return constructor.newInstance(args);

    } catch (final Exception exception) {
      throw new RuntimeException(
          "Failed to decode " + clazz.getName(),
          exception);
    }
  }

  private RecordComponent findComponent(@NotNull final String bsonName) {

    if ("_id".equals(bsonName)) {
      for (final RecordComponent component : components) {
        if (component.isAnnotationPresent(Identifier.class))
          return component;
      }
    }

    for (final RecordComponent component : components) {
      if (component.getName().equals(bsonName))
        return component;
    }

    throw new IllegalStateException(
        "Unknown field '" + bsonName + "' for " + clazz.getName());
  }

  @Override
  public Class<T> getEncoderClass() {
    return clazz;
  }
}

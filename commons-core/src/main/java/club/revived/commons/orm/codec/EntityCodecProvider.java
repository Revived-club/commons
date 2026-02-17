package club.revived.commons.orm.codec;

import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

import club.revived.commons.data.model.Entity;

public final class EntityCodecProvider implements CodecProvider {

  @Override
  @SuppressWarnings("unchecked")
  public <T> Codec<T> get(
      final Class<T> clazz,
      final CodecRegistry registry) {

    if (!Entity.class.isAssignableFrom(clazz)) {
      return null;
    }

    if (!clazz.isRecord()) {
      return null;
    }

    return (Codec<T>) new EntityCodec<>(
        (Class<? extends Entity>) clazz,
        registry);
  }
}

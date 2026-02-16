package club.revived.commons.data.model;

import java.util.concurrent.CompletableFuture;

import club.revived.commons.orm.ReflectionUtils;
import club.revived.commons.orm.annotations.Identifier;
import club.revived.commons.distribution.Cluster;

public interface Cachable {

  default void cache() {
    CompletableFuture.runAsync(() -> {
      final var clazz = this.getClass();
      final var field = ReflectionUtils.getFirstAnnotatedField(clazz, Identifier.class);

      if (field == null) {
        throw new RuntimeException("No field annotated with @Identifier found in " + clazz.getName());
      }

      field.setAccessible(true);

      try {
        final Object key = field.get(this);

        if (key == null) {
          throw new RuntimeException("Identifier field '" + field.getName() + "' is null");
        }

        final var keyStr = key.toString();

        Cluster.getInstance()
            .getGlobalCache()
            .set(keyStr, this);

      } catch (IllegalAccessException e) {
        throw new RuntimeException("Failed to access identifier field: " + field.getName(), e);
      }
    });
  }
}

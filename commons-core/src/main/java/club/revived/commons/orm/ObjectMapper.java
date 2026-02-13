package club.revived.commons.orm;

import org.jetbrains.annotations.NotNull;

import club.revived.commons.orm.annotations.Entity;

public interface ObjectMapper<T> {

  @NotNull
  <E extends Entity> E read(final @NotNull T object, final @NotNull Class<E> clazz);

  @NotNull
  T write(final @NotNull Object object);
}

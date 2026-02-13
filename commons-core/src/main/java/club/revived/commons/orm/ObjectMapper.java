package club.revived.commons.orm;

import org.jetbrains.annotations.NotNull;

import club.revived.commons.orm.annotations.Entity;

public interface ObjectMapper<T> {

  @NotNull
  Entity read(final @NotNull T object, final @NotNull Class<Entity> clazz);

  @NotNull
  T write(final @NotNull Object object);

}

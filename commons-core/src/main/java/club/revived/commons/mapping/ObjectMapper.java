package club.revived.commons.mapping;

import org.jetbrains.annotations.NotNull;

public interface ObjectMapper<T> {

  @NotNull
  DataObject read(final T object);

  @NotNull
  T write(final DataObject dataObject);

}

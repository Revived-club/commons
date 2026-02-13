package club.revived.commons.mapping.impl;

import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import club.revived.commons.mapping.DataObject;
import club.revived.commons.mapping.ObjectMapper;

public final class MongoObjectMapper implements ObjectMapper<Document> {

  @Override
  public @NotNull DataObject read(final Document object) {
    return null;
  }

  @Override
  public @NotNull Document write(final DataObject dataObject) {
    final var jsonObject = new JsonObject();
    return null;
  }
}

package club.revived.commons.data.model;

import club.revived.commons.data.DataRepository;

public interface Entity {

  default void save() {
    DataRepository.getInstance().save(this);
  }
}

package club.revived.situlae.model;

import club.revived.situlae.DataRepository;

public interface Entity {

  default void save() {
    DataRepository.getInstance().save(this);
  }
}

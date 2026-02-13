package club.revived.commons.orm.annotations;

import club.revived.commons.data.DataRepository;

public interface Entity {

  default void save() {
    DataRepository.getInstance().save(this.getClass(), this);
  }

}

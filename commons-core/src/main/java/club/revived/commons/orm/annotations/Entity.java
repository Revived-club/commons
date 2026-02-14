package club.revived.commons.orm.annotations;

import club.revived.commons.data.DataRepository;

public interface Entity {

  default <T extends Entity> void save() {
    DataRepository.getInstance().save(this);
  }
}

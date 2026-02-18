package club.revived.commons.data.model;

import club.revived.commons.data.DataRepository;

public interface LogMetric {

  default void save() {
    DataRepository.getInstance().writeLog(this);
  }
}

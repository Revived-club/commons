package club.revived.situlae.model.query;

import java.time.Instant;
import java.util.List;

public record QueryFilter<T>(
    Class<T> type,
    List<Condition> conditions,
    TimeRange time,
    Integer limit) {

  public record Condition(
      String field,
      Operator operator,
      Object value) {
  }

  public enum Operator {
    EQ,
    NE,
    GT,
    GTE,
    LT,
    LTE,
    IN
  }

  public record TimeRange(
      Instant from,
      Instant to) {
  }
}

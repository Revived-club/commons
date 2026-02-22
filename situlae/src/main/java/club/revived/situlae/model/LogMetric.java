package club.revived.situlae.model;

import java.time.Instant;

import com.influxdb.annotations.Column;

public abstract class LogMetric {

  @Column(timestamp = true)
  protected final Instant timestamp = Instant.now();

  public LogMetric() {
  }

  public void write() {

  }
}

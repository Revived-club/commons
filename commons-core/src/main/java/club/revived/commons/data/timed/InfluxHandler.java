package club.revived.commons.data.timed;

import club.revived.commons.data.model.DatabaseCredentials;

import com.google.common.base.FinalizablePhantomReference;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.QueryApi;
import com.influxdb.client.WriteApi;
import com.influxdb.client.domain.WritePrecision;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.NotNull;

public final class InfluxHandler {

  private InfluxDBClient client;
  private final DatabaseCredentials credentials;

  public InfluxHandler(DatabaseCredentials credentials) {
    this.credentials = credentials;
  }

  public void connect() {
    final var url = String.format("http://%s:%d", credentials.host(), credentials.port());
    this.client = InfluxDBClientFactory.create(
        url,
        credentials.password().toCharArray(),
        credentials.user(),
        credentials.database());
  }

  public void close() {
    if (client != null) {
      client.close();
    }
  }

  @NotNull
  public <T> CompletableFuture<Void> write(final @NotNull T obj) {
    return CompletableFuture.runAsync(() -> {
      final var writeApi = client.getWriteApiBlocking();
      writeApi.writeMeasurement(WritePrecision.MS, obj);
    });
  }

  @NotNull
  public <T> CompletableFuture<Void> writeBatch(final @NotNull List<T> list) {
    return CompletableFuture.runAsync(() -> {
      if (list == null || list.isEmpty()) {
        return;
      }

      final var writeApi = client.getWriteApiBlocking();
      writeApi.writeMeasurements(WritePrecision.MS, list);
    });
  }

  @NotNull
  public <T> CompletableFuture<List<T>> getAll(final @NotNull String measurement, final @NotNull Class<T> type) {
    return this.query(String.format("""
        from(bucket: "%s")
          |> range(start: 0)
          |> filter(fn: (r) => r._measurement == "%s")
        """, credentials.database(), measurement), type);
  }

  @NotNull
  public <T> CompletableFuture<List<T>> getAll(
      final @NotNull String measurement,
      final @NotNull String tagKey,
      final @NotNull String tagValue,
      final @NotNull Class<T> type) {
    return this.query(String.format("""
        from(bucket: "%s")
          |> range(start: 0)
          |> filter(fn: (r) => r._measurement == "%s" and r["%s"] == "%s")
        """, credentials.database(), measurement, tagKey, tagValue), type);
  }

  @NotNull
  public <T> CompletableFuture<List<T>> get(
      final String measurement,
      final int hours,
      final Class<T> type) {
    return this.query(String.format("""
        from(bucket: "%s")
          |> range(start: -%dh)
          |> filter(fn: (r) => r._measurement == "%s")
        """, credentials.database(), hours, measurement), type);
  }

  @NotNull
  public <T> CompletableFuture<List<T>> get(
      final @NotNull String measurement,
      final @NotNull String tagKey,
      final @NotNull String tagValue,
      final @NotNull int hours,
      final @NotNull Class<T> type) {
    return query(String.format("""
        from(bucket: "%s")
          |> range(start: -%dh)
          |> filter(fn: (r) => r._measurement == "%s" and r["%s"] == "%s")
        """, credentials.database(), hours, measurement, tagKey, tagValue), type);
  }

  @NotNull
  private <T> CompletableFuture<List<T>> query(final String flux, final Class<T> type) {
    return CompletableFuture.supplyAsync(() -> {
      final QueryApi queryApi = client.getQueryApi();

      final List<T> results = new ArrayList<>();
      queryApi.query(flux, type).forEach(results::add);

      return results;
    });
  }
}

package club.revived.commons.data.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.NotNull;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.QueryApi;
import com.influxdb.client.domain.WritePrecision;

import club.revived.commons.data.LogDatabaseProvider;
import club.revived.commons.data.model.DatabaseCredentials;

public final class InfluxHandler implements LogDatabaseProvider {

  private InfluxDBClient client;
  private final DatabaseCredentials credentials;

  public InfluxHandler(final DatabaseCredentials credentials) {
    this.credentials = credentials;
  }

  @Override
  public void connect() {
    final var url = String.format("http://%s:%d", credentials.host(), credentials.port());

    this.client = InfluxDBClientFactory.create(
        url,
        credentials.password().toCharArray(),
        credentials.user(),
        credentials.database());

    if (this.client.getBucketsApi().findBucketByName("logs") == null) {
      throw new IllegalStateException("the logs bucket does not exist in influxdb!");
    }
  }

  @Override
  public void close() {
    if (client != null) {
      client.close();
    }
  }

  @Override
  public <T> @NotNull CompletableFuture<Void> write(final @NotNull T obj) {
    return CompletableFuture.runAsync(() -> {
      final var writeApi = client.getWriteApiBlocking();
      writeApi.writeMeasurement(WritePrecision.NS, obj);
    });
  }

  @Override
  public <T> @NotNull CompletableFuture<Void> writeBatch(final @NotNull List<T> list) {
    return CompletableFuture.runAsync(() -> {
      if (list.isEmpty()) {
        return;
      }

      final var writeApi = client.getWriteApiBlocking();
      writeApi.writeMeasurements(WritePrecision.NS, list);
    });
  }

  @Override
  public <T> @NotNull CompletableFuture<List<T>> getAll(
      final @NotNull String measurement,
      final @NotNull Class<T> type) {

    return this.query(String.format("""
        from(bucket: "%s")
          |> range(start: -30d)
          |> filter(fn: (r) => r._measurement == "%s")
          |> pivot(rowKey:["_time"], columnKey: ["_field"], valueColumn: "_value")
        """, credentials.database(), measurement), type);
  }

  @Override
  public <T> @NotNull CompletableFuture<List<T>> getAll(
      final @NotNull String measurement,
      final @NotNull String tagKey,
      final @NotNull String tagValue,
      final @NotNull Class<T> type) {

    final String flux = String.format("""
        from(bucket: "%s")
          |> range(start: 0)  // from the beginning of time
          |> filter(fn: (r) => r._measurement == "%s" and r["%s"] == "%s")
          |> pivot(rowKey:["_time"], columnKey: ["_field"], valueColumn: "_value")
        """, credentials.database(), measurement, tagKey, tagValue);

    return this.query(flux, type);
  }

  @Override
  public <T> @NotNull CompletableFuture<List<T>> get(
      final @NotNull String measurement,
      final int hours,
      final @NotNull Class<T> type) {

    return this.query(String.format("""
        from(bucket: "%s")
          |> range(start: -%dh)
          |> filter(fn: (r) => r._measurement == "%s")
          |> pivot(rowKey:["_time"], columnKey: ["_field"], valueColumn: "_value")
        """, credentials.database(), hours, measurement), type);
  }

  @Override
  public <T> @NotNull CompletableFuture<List<T>> get(
      final @NotNull String measurement,
      final @NotNull String tagKey,
      final @NotNull String tagValue,
      final int hours,
      final @NotNull Class<T> type) {

    return this.query(String.format("""
        from(bucket: "%s")
          |> range(start: -%dh)
          |> filter(fn: (r) => r._measurement == "%s" and r["%s"] == "%s")
          |> pivot(rowKey:["_time"], columnKey: ["_field"], valueColumn: "_value")
        """, credentials.database(), hours, measurement, tagKey, tagValue), type);
  }

  @Override
  public <T> @NotNull CompletableFuture<List<T>> getAll(
      final @NotNull String measurement,
      final @NotNull String tagKey,
      final @NotNull List<String> tagValues,
      final @NotNull Class<T> type) {

    if (tagValues.isEmpty()) {
      return CompletableFuture.completedFuture(List.of());
    }

    final StringBuilder filter = new StringBuilder();
    for (int i = 0; i < tagValues.size(); i++) {
      if (i > 0) {
        filter.append(" or ");
      }

      filter.append(String.format("r[\"%s\"] == \"%s\"", tagKey, tagValues.get(i)));
    }

    final String flux = String.format("""
        from(bucket: "%s")
          |> range(start: 0)  // from the beginning of time
          |> filter(fn: (r) => r._measurement == "%s" and (%s))
          |> pivot(rowKey:["_time"], columnKey: ["_field"], valueColumn: "_value")
        """, credentials.database(), measurement, filter);

    return this.query(flux, type);
  }

  @Override
  public <T> @NotNull CompletableFuture<List<T>> get(
      @NotNull final String measurement,
      @NotNull final String tagKey,
      @NotNull final List<String> tagValues,
      final int hours,
      @NotNull final Class<T> type) {

    if (tagValues.isEmpty()) {
      return CompletableFuture.completedFuture(List.of());
    }

    final StringBuilder filter = new StringBuilder();
    for (int i = 0; i < tagValues.size(); i++) {
      if (i > 0) {
        filter.append(" or ");
      }

      filter.append(String.format("r[\"%s\"] == \"%s\"", tagKey, tagValues.get(i)));
    }

    final String flux = String.format("""
        from(bucket: "%s")
          |> range(start: -%dh)
          |> filter(fn: (r) => r._measurement == "%s" and (%s))
          |> pivot(rowKey:["_time"], columnKey: ["_field"], valueColumn: "_value")
        """, credentials.database(), hours, measurement, filter);

    return this.query(flux, type);
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

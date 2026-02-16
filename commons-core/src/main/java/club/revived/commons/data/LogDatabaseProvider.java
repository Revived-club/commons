package club.revived.commons.data;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface LogDatabaseProvider {

  void connect();

  void close();

  @NotNull
  <T> CompletableFuture<Void> write(final @NotNull T obj);

  @NotNull
  <T> CompletableFuture<Void> writeBatch(final @NotNull List<T> list);

  @NotNull
  <T> CompletableFuture<List<T>> getAll(
      final @NotNull String measurement,
      final @NotNull Class<T> type);

  @NotNull
  <T> CompletableFuture<List<T>> getAll(
      final @NotNull String measurement,
      final @NotNull String tagKey,
      final @NotNull String tagValue,
      final @NotNull Class<T> type);

  @NotNull
  <T> CompletableFuture<List<T>> get(
      final @NotNull String measurement,
      final int hours,
      final @NotNull Class<T> type);

  @NotNull
  <T> CompletableFuture<List<T>> get(
      final @NotNull String measurement,
      final @NotNull String tagKey,
      final @NotNull String tagValue,
      final int hours,
      final @NotNull Class<T> type);

  @NotNull
  <T> CompletableFuture<List<T>> getAll(
      final @NotNull String measurement,
      final @NotNull String tagKey,
      final @NotNull List<String> tagValues,
      final @NotNull Class<T> type);

  @NotNull
  <T> CompletableFuture<List<T>> get(
      final @NotNull String measurement,
      final @NotNull String tagKey,
      final @NotNull List<String> tagValues,
      final int hours,
      final @NotNull Class<T> type);
}

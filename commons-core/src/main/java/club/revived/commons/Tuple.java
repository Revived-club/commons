package club.revived.commons;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Tuple<K, V> {
  private K key;
  private V val;

  public Tuple(@Nullable K key, @Nullable V val) {
    this.key = key;
    this.val = val;
  }

  public static @NotNull <K, V> Tuple<K, V> of(@Nullable K key, @Nullable V val) {
    return new Tuple<>(key, val);
  }

  @Override
  public @NotNull String toString() {
    return "[<key> : <val>]"
        .replaceAll("<key>", this.key.toString())
        .replaceAll("<val>", this.val.toString());
  }

  public @NotNull Tuple<K, V> key(@Nullable K key) {
    this.key = key;
    return this;
  }

  public @NotNull Tuple<K, V> val(@Nullable V val) {
    this.val = val;
    return this;
  }

  public @NotNull K key() {
    return key;
  }

  public @Nullable V val() {
    return val;
  }
}

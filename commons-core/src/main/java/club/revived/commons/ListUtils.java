package club.revived.commons;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public final class ListUtils {

  @SafeVarargs
  public static <T> void addNullSafe(final List<T> list, final T... elements) {
    for (final T element : elements) {
      addNullSafe(list, element);
    }
  }

  public static <T> boolean addNullSafe(final List<T> list, final T element) {
    if (list == null || element == null) {
      return false;
    }

    return list.add(element);
  }

  @NotNull
  public static <T> List<List<T>> splitInHalf(final T[] array) {
    final int mid = array.length / 2;

    final List<T> list = Arrays.asList(array);

    final List<T> first = new ArrayList<>(list.subList(0, mid));
    final List<T> second = new ArrayList<>(list.subList(mid, list.size()));

    return List.of(first, second);
  }

  @NotNull
  public static <T> List<List<T>> splitInHalf(final Collection<? extends T> list) {
    final int mid = list.size() / 2;

    final List<T> newList = new ArrayList<>(list);

    final List<T> first = new ArrayList<>(newList.subList(0, mid));
    final List<T> second = new ArrayList<>(newList.subList(mid, newList.size()));

    return List.of(first, second);
  }
}

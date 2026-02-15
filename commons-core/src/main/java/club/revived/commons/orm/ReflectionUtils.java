package club.revived.commons.orm;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ReflectionUtils {

  @NotNull
  public static List<Field> getAnnotatedFields(final Class<?> clazz, final Class<? extends Annotation> annotation) {
    final var fields = new ArrayList<Field>();
    for (final Field field : clazz.getDeclaredFields()) {
      if (!field.isAnnotationPresent(annotation)) {
        continue;
      }

      fields.add(field);
    }

    return fields;
  }

  @Nullable
  public static Field getFirstAnnotatedField(final Class<?> clazz, final Class<? extends Annotation> annotation) {
    for (final Field field : clazz.getDeclaredFields()) {
      if (field.isAnnotationPresent(annotation)) {
        return field;
      }
    }
    return null;
  }
}

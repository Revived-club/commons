package club.revived.commons.serialization;

import java.io.*;

public final class FileSerializer {

  public static void serialize(final Serializable object, final String filePath) throws IOException {
    if (object == null) {
      throw new IllegalArgumentException("Object to serialize must not be null");
    }

    try (final ObjectOutputStream oos = new ObjectOutputStream(
        new BufferedOutputStream(new FileOutputStream(filePath)))) {
      oos.writeObject(object);
      oos.flush();
    }
  }

  @SuppressWarnings("unchecked")
  public static <T> T deserialize(final String filePath)
      throws IOException, ClassNotFoundException {

    try (final ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(filePath)))) {
      return (T) ois.readObject();
    }
  }
}

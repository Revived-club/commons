package club.revived.commons;

public final class NumberUtils {

  public static boolean isInteger(final String a) {
    try {
      Integer.parseInt(a);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public static boolean isDouble(final String a) {
    try {
      Double.parseDouble(a);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public static boolean isFloat(final String a) {
    try {
      Float.parseFloat(a);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public static boolean isLong(final String a) {
    try {
      Long.parseLong(a);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public static boolean isShort(final String a) {
    try {
      Short.parseShort(a);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public static int countFits(int number, int divisor) {
    if (divisor == 0) {
      return 0;
    }
    return number / divisor;
  }
}

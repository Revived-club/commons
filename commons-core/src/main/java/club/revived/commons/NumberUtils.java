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

  public static int levenshteinDistance(String s1, String s2) {
    if (s1 == null || s2 == null) {
      return Integer.MAX_VALUE;
    }

    if (s1.equals(s2)) {
      return 0;
    }

    int len1 = s1.length();
    int len2 = s2.length();

    if (len1 == 0)
      return len2;
    if (len2 == 0)
      return len1;

    int[][] dp = new int[len1 + 1][len2 + 1];

    for (int i = 0; i <= len1; i++) {
      dp[i][0] = i;
    }

    for (int j = 0; j <= len2; j++) {
      dp[0][j] = j;
    }

    for (int i = 1; i <= len1; i++) {
      for (int j = 1; j <= len2; j++) {
        int cost = s1.charAt(i - 1) == s2.charAt(j - 1) ? 0 : 1;

        dp[i][j] = Math.min(
            Math.min(
                dp[i - 1][j] + 1,
                dp[i][j - 1] + 1),
            dp[i - 1][j - 1] + cost);
      }
    }

    return dp[len1][len2];
  }

  public static double normalizedLevenshtein(String s1, String s2) {
    int distance = levenshteinDistance(s1, s2);
    int maxLen = Math.max(s1.length(), s2.length());

    if (maxLen == 0) {
      return 1.0;
    }

    return 1.0 - ((double) distance / maxLen);
  }

  public static double jaroWinklerSimilarity(String s1, String s2) {
    double jaro = jaroSimilarity(s1, s2);

    if (jaro < 0.7) {
      return jaro;
    }

    int prefixLength = 0;
    int maxPrefix = Math.min(4, Math.min(s1.length(), s2.length()));

    for (int i = 0; i < maxPrefix; i++) {
      if (s1.charAt(i) == s2.charAt(i)) {
        prefixLength++;
      } else {
        break;
      }
    }

    return jaro + (prefixLength * 0.1 * (1.0 - jaro));
  }

  private static double jaroSimilarity(String s1, String s2) {
    if (s1.equals(s2)) {
      return 1.0;
    }

    int len1 = s1.length();
    int len2 = s2.length();

    if (len1 == 0 || len2 == 0) {
      return 0.0;
    }

    int matchDistance = Math.max(len1, len2) / 2 - 1;
    if (matchDistance < 0) {
      matchDistance = 0;
    }

    boolean[] s1Matches = new boolean[len1];
    boolean[] s2Matches = new boolean[len2];

    int matches = 0;
    int transpositions = 0;

    for (int i = 0; i < len1; i++) {
      int start = Math.max(0, i - matchDistance);
      int end = Math.min(i + matchDistance + 1, len2);

      for (int j = start; j < end; j++) {
        if (s2Matches[j] || s1.charAt(i) != s2.charAt(j)) {
          continue;
        }
        s1Matches[i] = true;
        s2Matches[j] = true;
        matches++;
        break;
      }
    }

    if (matches == 0) {
      return 0.0;
    }

    int k = 0;
    for (int i = 0; i < len1; i++) {
      if (!s1Matches[i]) {
        continue;
      }
      while (!s2Matches[k]) {
        k++;
      }
      if (s1.charAt(i) != s2.charAt(k)) {
        transpositions++;
      }
      k++;
    }

    return (((double) matches / len1) +
        ((double) matches / len2) +
        ((double) (matches - transpositions / 2.0) / matches)) / 3.0;
  }
}

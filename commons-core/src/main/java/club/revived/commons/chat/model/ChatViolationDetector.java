package club.revived.commons.chat.model;

import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ChatViolationDetector {

  @Nullable
  public CompletableFuture<Violation> detect(final @NotNull String message);
}

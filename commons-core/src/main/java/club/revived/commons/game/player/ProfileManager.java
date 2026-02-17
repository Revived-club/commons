package club.revived.commons.game.player;

import club.revived.commons.data.DataRepository;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class ProfileManager {

    private  static  ProfileManager instance;

    @NotNull
    public CompletableFuture<Void> saveProfile(final PlayerProfile profile) {
        return DataRepository.getInstance().save(PlayerProfile.class, profile);
    }

    @NotNull
    public CompletableFuture<Optional<PlayerProfile>> getProfile(final UUID uuid) {
        return DataRepository.getInstance().get(PlayerProfile.class, uuid);
    }

    @NotNull
    public CompletableFuture<Optional<PlayerProfile>> getProfile(final String name) {
        return DataRepository.getInstance().getByField(PlayerProfile.class, "name", name)
                .thenApply(list -> list.isEmpty() ? Optional.empty() : Optional.of(list.getFirst()));
    }

    public  static  ProfileManager getInstance() {
        if (instance == null) {
            instance = new ProfileManager();
            return  instance;
        }

        return  instance;
    }
}

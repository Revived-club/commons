package club.revived.concordia;

import club.revived.concordia.messaging.MessageManager;
import club.revived.concordia.provider.PubSubProvider;
import club.revived.concordia.provider.StorageProvider;
import club.revived.concordia.storage.CacheManager;
import club.revived.concordia.storage.Watcher;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public final class Concordia {

    private static Concordia instance;

    @NotNull
    private final PubSubProvider pubSubProvider;

    @NotNull
    private final StorageProvider storageProvider;

    @NotNull
    private final String nodeId;

    @NotNull
    private final ScheduledExecutorService scheduler;

    @NotNull
    private final MessageManager messageManager;

    @NotNull
    private final CacheManager cacheManager;


    @NotNull
    private final Watcher watcher;

    @NotNull
    private final Gson gson;

    private Concordia(
            final @NotNull PubSubProvider pubSubProvider,
            final @NotNull StorageProvider storageProvider,
            final @NotNull String nodeId
    ) {
        this.pubSubProvider = pubSubProvider;
        this.storageProvider = storageProvider;
        this.nodeId = nodeId;
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.messageManager = new MessageManager();
        this.cacheManager = new CacheManager();
        this.watcher = new Watcher();
        this.gson = new GsonBuilder()
                .setLongSerializationPolicy(LongSerializationPolicy.STRING)
                .create();
    }

    public static void init(
            final @NotNull PubSubProvider pubSubProvider,
            final @NotNull StorageProvider storageProvider,
            final @NotNull String nodeId
    ) {
        if (instance != null) {
            throw new IllegalStateException("Concordia is already initialized");
        }
        instance = new Concordia(pubSubProvider, storageProvider, nodeId);
    }

    @NotNull
    public static Concordia instance() {
        if (instance == null) {
            throw new IllegalStateException("Concordia is not initialized");
        }
        return instance;
    }

    @NotNull
    public PubSubProvider pubSubProvider() {
        return this.pubSubProvider;
    }

    @NotNull
    public StorageProvider storageProvider() {
        return this.storageProvider;
    }

    @NotNull
    public String nodeId() {
        return this.nodeId;
    }

    @NotNull
    public ScheduledExecutorService scheduler() {
        return this.scheduler;
    }

    @NotNull
    public MessageManager messageManager() {
        return this.messageManager;
    }

    @NotNull
    public CacheManager cacheManager() {
        return this.cacheManager;
    }

    @NotNull
    public Watcher watcher() {
        return this.watcher;
    }

    @NotNull
    public Gson gson() {
        return this.gson;
    }

    public void subscribe(final @NotNull String channel) {
        this.pubSubProvider.subscribe(channel, data -> this.messageManager.handleIncoming(channel, data));
    }
}

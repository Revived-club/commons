package club.revived.concordia.storage;

import club.revived.concordia.Concordia;
import club.revived.concordia.provider.InMemoryPubSubProvider;
import club.revived.concordia.provider.InMemoryStorageProvider;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class WatcherTest {

    private InMemoryPubSubProvider pubSubProvider;
    private Watcher watcher;

    @Before
    public void setUp() {
        pubSubProvider = new InMemoryPubSubProvider();
        InMemoryStorageProvider storageProvider = new InMemoryStorageProvider();

        try {
            java.lang.reflect.Field instanceField = Concordia.class.getDeclaredField("instance");
            instanceField.setAccessible(true);
            instanceField.set(null, null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Concordia.init(pubSubProvider, storageProvider, "test-node");
        watcher = Concordia.instance().watcher();
    }

    @Test
    public void testWatchJson() {
        String key = "test-json-key";
        TestData expectedData = new TestData("watcher-test");
        AtomicReference<TestData> receivedData = new AtomicReference<>();

        watcher.watch(key, TestData.class, receivedData::set);

        String json = Concordia.instance().gson().toJson(expectedData);
        pubSubProvider.publish(key + ":updates", json.getBytes()).join();

        assertNotNull(receivedData.get());
        assertEquals(expectedData.name(), receivedData.get().name());
    }

    @Test
    public void testWatchProtobuf() {
        String key = "test-proto-key";
        club.revived.commons.proto.Cachable expectedMessage = club.revived.commons.proto.Cachable.newBuilder()
                .setKey("proto-key")
                .setValue("proto-value")
                .build();
        AtomicReference<club.revived.commons.proto.Cachable> receivedMessage = new AtomicReference<>();

        watcher.watch(key, club.revived.commons.proto.Cachable.parser(), receivedMessage::set);

        pubSubProvider.publish(key + ":updates", expectedMessage.toByteArray()).join();

        assertNotNull(receivedMessage.get());
        assertEquals(expectedMessage.getKey(), receivedMessage.get().getKey());
        assertEquals(expectedMessage.getValue(), receivedMessage.get().getValue());
    }

    public record TestData(String name) {}
}

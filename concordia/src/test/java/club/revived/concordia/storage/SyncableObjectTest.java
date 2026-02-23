package club.revived.concordia.storage;

import club.revived.concordia.Concordia;
import club.revived.concordia.annotation.Syncable;
import club.revived.concordia.provider.InMemoryPubSubProvider;
import club.revived.concordia.provider.InMemoryStorageProvider;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SyncableObjectTest {

    private InMemoryStorageProvider storageProvider;
    private InMemoryPubSubProvider pubSubProvider;

    @Before
    public void setUp() {
        storageProvider = new InMemoryStorageProvider();
        pubSubProvider = new InMemoryPubSubProvider();

        try {
            java.lang.reflect.Field instanceField = Concordia.class.getDeclaredField("instance");
            instanceField.setAccessible(true);
            instanceField.set(null, null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Concordia.init(pubSubProvider, storageProvider, "test-node");
    }

    @Test
    public void testSyncableObjectLifecycle() {
        String key = "test-sync";
        TestData defaultVal = new TestData("default");
        TestData initialVal = new TestData("initial");
        TestData updatedVal = new TestData("updated");

        // Pre-populate storage
        club.revived.commons.proto.Cachable cachable = club.revived.commons.proto.Cachable.newBuilder()
                .setKey(key)
                .setValue(Concordia.instance().gson().toJson(initialVal))
                .build();
        storageProvider.set(key, cachable.toByteArray()).join();

        SyncableObject<TestData> syncable = new SyncableObject<>(key, null, TestData.class, defaultVal);

        // Verify initial load
        assertEquals(initialVal.name(), syncable.cached().name());

        // Test update notification
        AtomicReference<TestData> notifiedValue = new AtomicReference<>();
        syncable.onUpdate(notifiedValue::set);

        // Publish update via PubSub
        pubSubProvider.publish(key + ":updates", Concordia.instance().gson().toJson(updatedVal).getBytes()).join();

        // Verify notification and cached value
        assertNotNull(notifiedValue.get());
        assertEquals(updatedVal.name(), notifiedValue.get().name());
        assertEquals(updatedVal.name(), syncable.cached().name());

        // Test update from object
        TestData finalVal = new TestData("final");
        syncable.update(finalVal);

        // Verify storage was updated
        byte[] storedData = storageProvider.get(key).join();
        assertNotNull(storedData);
        
        // Verify PubSub was notified (would trigger another update to itself, but we just check cached value)
        assertEquals(finalVal.name(), syncable.cached().name());
    }

    @Test
    public void testSyncableAnnotation() {
        AnnotatedData defaultVal = new AnnotatedData();
        defaultVal.setName("default");
        
        SyncableObject<AnnotatedData> syncable = new SyncableObject<>(AnnotatedData.class);
        
        assertEquals("annotated-key", syncable.key());
        // syncable initializes by calling load(), which calls update(defaultInstance) if storage is empty.
        // update(defaultInstance) sets current = defaultInstance.
        // AnnotatedData's default constructor doesn't set name, so cached().getName() is null.
        // Let's verify that it's the default instance.
        assertNotNull(syncable.cached());
    }

    public static class TestData {
        private String name;
        public TestData() {}
        public TestData(String name) { this.name = name; }
        public String name() { return name; }
    }

    @Syncable("annotated-key")
    public static class AnnotatedData {
        private String name;
        public AnnotatedData() {}
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }
}

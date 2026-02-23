package club.revived.concordia.storage;

import club.revived.concordia.Concordia;
import club.revived.concordia.provider.InMemoryPubSubProvider;
import club.revived.concordia.provider.InMemoryStorageProvider;
import com.google.protobuf.Message;
import com.google.protobuf.Parser;
import org.junit.Before;
import org.junit.Test;

import java.util.Base64;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;

public class CacheManagerTest {

    private InMemoryStorageProvider storageProvider;
    private InMemoryPubSubProvider pubSubProvider;
    private CacheManager cacheManager;

    @Before
    public void setUp() {
        storageProvider = new InMemoryStorageProvider();
        pubSubProvider = new InMemoryPubSubProvider();

        // Reset Concordia instance using reflection
        try {
            java.lang.reflect.Field instanceField = Concordia.class.getDeclaredField("instance");
            instanceField.setAccessible(true);
            instanceField.set(null, null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Concordia.init(pubSubProvider, storageProvider, "test-node");
        cacheManager = Concordia.instance().cacheManager();
    }

    @Test
    public void testWriteJson() {
        String key = "test-key";
        TestData value = new TestData("test-value");
        
        AtomicBoolean pubSubCalled = new AtomicBoolean(false);
        pubSubProvider.subscribe(key + ":updates", data -> {
            pubSubCalled.set(true);
            String json = new String(data);
            assertTrue(json.contains("test-value"));
        });

        cacheManager.write(key, value).join();

        // Verify storage
        byte[] storedData = storageProvider.get(key).join();
        assertNotNull(storedData);
        
        // Verify PubSub
        assertTrue("PubSub should have been called", pubSubCalled.get());
    }

    @Test
    public void testReadJson() {
        String key = "test-key";
        TestData expectedValue = new TestData("test-value");
        String json = Concordia.instance().gson().toJson(expectedValue);
        
        club.revived.commons.proto.Cachable cachable = club.revived.commons.proto.Cachable.newBuilder()
                .setKey(key)
                .setValue(json)
                .build();
        
        storageProvider.set(key, cachable.toByteArray()).join();

        TestData actualValue = cacheManager.read(key, TestData.class, null).join();

        assertNotNull(actualValue);
        assertEquals(expectedValue.name(), actualValue.name());
    }

    @Test
    public void testWriteProtobuf() {
        String key = "proto-key";
        club.revived.commons.proto.Cachable message = club.revived.commons.proto.Cachable.newBuilder()
                .setKey("inner-key")
                .setValue("inner-value")
                .build();

        AtomicBoolean pubSubCalled = new AtomicBoolean(false);
        pubSubProvider.subscribe(key + ":updates", data -> {
            pubSubCalled.set(true);
            assertArrayEquals(message.toByteArray(), data);
        });

        cacheManager.write(key, message).join();

        // Verify storage
        byte[] storedData = storageProvider.get(key).join();
        assertNotNull(storedData);
        
        // Verify PubSub
        assertTrue(pubSubCalled.get());
    }

    @Test
    public void testReadProtobuf() throws Exception {
        String key = "proto-key";
        club.revived.commons.proto.Cachable innerMessage = club.revived.commons.proto.Cachable.newBuilder()
                .setKey("inner-key")
                .setValue("inner-value")
                .build();
        
        String encodedValue = Base64.getEncoder().encodeToString(innerMessage.toByteArray());
        club.revived.commons.proto.Cachable cachable = club.revived.commons.proto.Cachable.newBuilder()
                .setKey(key)
                .setValue(encodedValue)
                .build();

        storageProvider.set(key, cachable.toByteArray()).join();

        club.revived.commons.proto.Cachable actualValue = cacheManager.read(
                key, 
                club.revived.commons.proto.Cachable.class, 
                club.revived.commons.proto.Cachable.parser()
        ).join();

        assertNotNull(actualValue);
        assertEquals(innerMessage.getKey(), actualValue.getKey());
        assertEquals(innerMessage.getValue(), actualValue.getValue());
    }

    @Test
    public void testBatchWrite() {
        String key1 = "key1";
        TestData val1 = new TestData("val1");
        String key2 = "key2";
        TestData val2 = new TestData("val2");

        cacheManager.batchWrite(List.of(
                new CacheManager.Entry<>(key1, val1),
                new CacheManager.Entry<>(key2, val2)
        )).join();

        assertNotNull(storageProvider.get(key1).join());
        assertNotNull(storageProvider.get(key2).join());
    }

    public record TestData(String name) {}
}

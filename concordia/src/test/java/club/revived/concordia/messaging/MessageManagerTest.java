package club.revived.concordia.messaging;

import club.revived.commons.proto.Envelope;
import club.revived.commons.proto.UUID;
import club.revived.concordia.Concordia;
import club.revived.concordia.provider.InMemoryPubSubProvider;
import club.revived.concordia.provider.InMemoryStorageProvider;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

public class MessageManagerTest {

    private InMemoryPubSubProvider pubSubProvider;
    private InMemoryStorageProvider storageProvider;
    private MessageManager messageManager;

    @Before
    public void setUp() {
        pubSubProvider = new InMemoryPubSubProvider();
        storageProvider = new InMemoryStorageProvider();

        try {
            java.lang.reflect.Field instanceField = Concordia.class.getDeclaredField("instance");
            instanceField.setAccessible(true);
            instanceField.set(null, null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Concordia.init(pubSubProvider, storageProvider, "test-node");
        messageManager = Concordia.instance().messageManager();
    }

    @Test
    public void testRegisterAndHandleHandler() {
        AtomicReference<club.revived.commons.proto.Cachable> receivedMessage = new AtomicReference<>();
        
        messageManager.registerHandler(
                club.revived.commons.proto.Cachable.class,
                club.revived.commons.proto.Cachable.parser(),
                receivedMessage::set
        );

        club.revived.commons.proto.Cachable message = club.revived.commons.proto.Cachable.newBuilder()
                .setKey("test-key")
                .setValue("test-value")
                .build();

        Envelope envelope = Envelope.newBuilder()
                .setTarget("*")
                .setSender("other-node")
                .setPayload(message.toByteString())
                .build();

        messageManager.handleIncoming("test-channel", envelope.toByteArray());

        assertNotNull(receivedMessage.get());
        assertEquals("test-key", receivedMessage.get().getKey());
    }

    @Test
    public void testTargetFiltering() {
        AtomicReference<club.revived.commons.proto.Cachable> receivedMessage = new AtomicReference<>();

        messageManager.registerHandler(
                club.revived.commons.proto.Cachable.class,
                club.revived.commons.proto.Cachable.parser(),
                receivedMessage::set
        );

        club.revived.commons.proto.Cachable message = club.revived.commons.proto.Cachable.newBuilder()
                .setKey("test-key")
                .build();

        // Envelope targeted to another node
        Envelope envelope = Envelope.newBuilder()
                .setTarget("other-node")
                .setSender("sender-node")
                .setPayload(message.toByteString())
                .build();

        messageManager.handleIncoming("test-channel", envelope.toByteArray());

        assertNull("Should not have received message targeted to another node", receivedMessage.get());

        // Envelope targeted to this node
        envelope = Envelope.newBuilder()
                .setTarget("test-node")
                .setSender("sender-node")
                .setPayload(message.toByteString())
                .build();

        messageManager.handleIncoming("test-channel", envelope.toByteArray());

        assertNotNull("Should have received message targeted to this node", receivedMessage.get());
    }

    @Test
    public void testAckHandling() {
        String correlationId = "corr-123";
        AtomicReference<Envelope> receivedAck = new AtomicReference<>();

        messageManager.registerAck(
                correlationId,
                java.time.Duration.ofSeconds(1),
                () -> fail("Should not timeout"),
                receivedAck::set
        );

        Envelope ackEnvelope = Envelope.newBuilder()
                .setCorrelationId(UUID.newBuilder().setValue(correlationId).build())
                .setIsAck(true)
                .setTarget("test-node")
                .build();

        messageManager.handleIncoming("test-channel", ackEnvelope.toByteArray());

        assertNotNull(receivedAck.get());
        assertEquals(correlationId, receivedAck.get().getCorrelationId().getValue());
    }

    @Test
    public void testConcordiaSubscription() {
        AtomicReference<club.revived.commons.proto.Cachable> receivedMessage = new AtomicReference<>();
        String channel = "test-channel";

        messageManager.registerHandler(
                club.revived.commons.proto.Cachable.class,
                club.revived.commons.proto.Cachable.parser(),
                receivedMessage::set
        );

        // This is what Concordia.subscribe(channel) does
        Concordia.instance().subscribe(channel);

        club.revived.commons.proto.Cachable message = club.revived.commons.proto.Cachable.newBuilder()
                .setKey("test-key")
                .build();

        Envelope envelope = Envelope.newBuilder()
                .setTarget("*")
                .setSender("other-node")
                .setPayload(message.toByteString())
                .build();

        // Publish to the provider, which should trigger messageManager.handleIncoming
        pubSubProvider.publish(channel, envelope.toByteArray()).join();

        assertNotNull("Message should have been received via PubSubProvider -> MessageManager", receivedMessage.get());
        assertEquals("test-key", receivedMessage.get().getKey());
    }
}

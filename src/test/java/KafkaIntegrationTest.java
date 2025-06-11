import gac.andrzej.sklep.KafkaMessageConsumer;
import gac.andrzej.sklep.KafkaMessageProducer;


import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.KafkaContainer; // Import dla KafkaContainer
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
class KafkaIntegrationTest {

    private KafkaMessageProducer producer;
    private KafkaMessageConsumer consumer;
    private ExecutorService consumerExecutor;
    private static final String TEST_TOPIC = "test_topic";
    private static final String CONSUMER_GROUP_ID = "test_group";

    // Definiujemy kontener Kafka. Testcontainers automatycznie uruchomi też Zookeepera.
    @Container
    private static final KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.6.0")); // Użyj stabilnej wersji Kafka

    @BeforeEach
    void setUp() throws InterruptedException {
        // Kontener Kafka uruchamia się automatycznie. Pobieramy adres brokerów.
        String bootstrapServers = kafkaContainer.getBootstrapServers();

        producer = new KafkaMessageProducer(bootstrapServers, TEST_TOPIC);
        consumer = new KafkaMessageConsumer(bootstrapServers, CONSUMER_GROUP_ID, TEST_TOPIC);

        // Uruchomienie konsumenta w osobnym wątku
        consumerExecutor = Executors.newSingleThreadExecutor();
        consumerExecutor.submit(consumer);

        // Czekamy chwilę, aby konsument miał czas na subskrypcję topicu i uruchomienie się
        Thread.sleep(1000); // Daj konsumentowi czas na inicjalizację
    }

    @AfterEach
    void tearDown() {
        if (producer != null) {
            producer.close();
        }
        if (consumer != null) {
            consumer.stop();
        }
        if (consumerExecutor != null) {
            consumerExecutor.shutdownNow(); // Próbuje zatrzymać wszystkie aktywne wątki
            try {
                if (!consumerExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                    System.err.println("Consumer executor did not terminate in time.");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Test
    void shouldProduceAndConsumeMessage() throws InterruptedException {
        String key = "key1";
        String value = "Hello Kafka from Testcontainers!";

        producer.sendMessage(key, value);

        // Dajemy konsumentowi czas na odebranie wiadomości
        Thread.sleep(2000);

        List<String> receivedMessages = consumer.getReceivedMessages();
        assertFalse(receivedMessages.isEmpty(), "Messages list should not be empty");
        assertEquals(1, receivedMessages.size(), "Should have received exactly one message");
        assertEquals(value, receivedMessages.get(0), "Received message content should match sent content");
    }

    @Test
    void shouldHandleMultipleMessages() throws InterruptedException {
        producer.sendMessage("keyA", "Message A");
        producer.sendMessage("keyB", "Message B");

        Thread.sleep(2000);

        List<String> receivedMessages = consumer.getReceivedMessages();
        assertEquals(2, receivedMessages.size(), "Should have received two messages");
        assertTrue(receivedMessages.contains("Message A"), "Should contain Message A");
        assertTrue(receivedMessages.contains("Message B"), "Should contain Message B");
    }
}

package gac.andrzej.sklep;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;

public class KafkaMessageConsumer implements Runnable {

    private final KafkaConsumer<String, String> consumer;
    private final String topic;
    private final List<String> receivedMessages = new CopyOnWriteArrayList<>();
    private volatile boolean running = true;

    public KafkaMessageConsumer(String bootstrapServers, String groupId, String topic) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); // Zaczynaj od początku topicu
        this.consumer = new KafkaConsumer<>(props);
        this.topic = topic;
    }

    public List<String> getReceivedMessages() {
        return receivedMessages;
    }

    @Override
    public void run() {
        consumer.subscribe(Collections.singletonList(topic));
        while (running) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
            for (ConsumerRecord<String, String> record : records) {
                receivedMessages.add(record.value());
                // System.out.println("Consumed message: " + record.value()); // Debug log - można odkomentować w razie potrzeby
            }
        }
        consumer.close();
    }

    /**
     * Oczekuje na określoną liczbę wiadomości lub przekroczenie limitu czasu.
     * @param expectedMessageCount Oczekiwana liczba wiadomości.
     * @param timeout Czas oczekiwania w milisekundach.
     * @return true, jeśli otrzymano oczekiwaną liczbę wiadomości w limicie czasu, false w przeciwnym razie.
     */
    public boolean waitUntilMessagesReceived(int expectedMessageCount, long timeout) {
        long startTime = System.currentTimeMillis();
        while (receivedMessages.size() < expectedMessageCount && (System.currentTimeMillis() - startTime < timeout)) {
            try {
                Thread.sleep(50); // Krótka pauza przed kolejnym sprawdzeniem
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return receivedMessages.size() >= expectedMessageCount;
    }

    public void stop() {
        running = false;
    }
}

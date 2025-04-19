package com.cjremmett.application.kafka;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Configuration
@EnableKafkaStreams // Crucial annotation to enable Kafka Streams processing in Spring
public class KafkaTimestampStreamProcessor {

    @Value("${app.kafka.input-topic}")
    private String inputTopic;

    @Value("${app.kafka.output-topic}")
    private String outputTopic;

    // Define the processing topology as a Spring Bean
    // Spring Kafka automatically detects Beans of type KStream, KTable, or GlobalKTable
    // and adds them to the StreamsBuilder managed by the StreamsBuilderFactoryBean.
    @Bean
    public KStream<String, String> processTimestampAppending(StreamsBuilder streamsBuilder) {

        System.out.println("Initializing Kafka Streams topology: Read from " + inputTopic + "', append timestamp, write to " + outputTopic);

        // 1. Read from the input topic
        // We specify the SerDes explicitly for clarity, though defaults could be used
        // if configured globally in application.yml
        KStream<String, String> sourceStream = streamsBuilder.stream(
                inputTopic,
                Consumed.with(Serdes.String(), Serdes.String()) // Key Serde, Value Serde
        );

        // 2. Transform the message: Append the timestamp
        KStream<String, String> transformedStream = sourceStream.mapValues(value -> {
            try {
                // Get current timestamp (using Instant for precision and UTC)
                Instant now = Instant.now();
                // Format the timestamp (ISO 8601 format in UTC is generally preferred)
                String timestamp = DateTimeFormatter.ISO_INSTANT.format(now);
                // String timestamp = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS z")
                //                                  .withZone(ZoneOffset.UTC) // Or system default: ZoneId.systemDefault()
                //                                  .format(now);

                String newValue = value + " | ProcessedTimestamp: " + timestamp;
                System.out.println("Transforming message. Original: " + value + ", New: " + newValue);
                return newValue;
            } catch (Exception e) {
                System.out.println("Error processing message value: " + value + ". Skipping message.");
                // Returning null typically filters out the message in Kafka Streams
                // Consider sending to a Dead Letter Queue (DLQ) for more robust error handling
                return null;
            }
        });

        // Optional: Filter out messages where transformation failed (returned null)
        KStream<String, String> filteredStream = transformedStream.filter((key, value) -> value != null);


        // 3. Write the transformed message to the output topic
        // Again, specifying SerDes explicitly.
        filteredStream.to(
                outputTopic,
                Produced.with(Serdes.String(), Serdes.String()) // Key Serde, Value Serde
        );

        System.out.println("Kafka Streams topology defined successfully.");

        // Returning the KStream isn't strictly necessary for .to() to work,
        // but it follows the pattern expected by Spring Kafka for bean-defined topologies.
        return sourceStream; // Or return filteredStream, it doesn't significantly matter here
    }

    // Note: Spring Boot's auto-configuration for Kafka Streams typically handles
    // the creation of the StreamsBuilderFactoryBean and manages the StreamsBuilder
    // lifecycle when @EnableKafkaStreams is present and properties are set in
    // application.yml. You usually don't need to define these beans manually unless
    // you need very specific customization.
}
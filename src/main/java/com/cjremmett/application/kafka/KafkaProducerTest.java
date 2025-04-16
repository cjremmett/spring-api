package com.cjremmett.application.kafka;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.Future;

@RestController
public class KafkaProducerTest {

	@JsonAutoDetect
	public static class KafkaTestEvent
	{
		public String message;

		public KafkaTestEvent(
				String _message
		){
			this.message = _message;
		}

		public KafkaTestEvent(){}

		public String getMessage()
		{
			return message;
		}

		public void setMessage(String message)
		{
			this.message = message;
		}
	}

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping("/produce-message")
	@JsonIgnoreProperties(ignoreUnknown = true)
	public void produceMessage(@RequestBody KafkaTestEvent kafkaTestEvent) {
		String bootstrapServers = "localhost:9092";
		String topicName = "user-event";

		Properties properties = new Properties();
		properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

		// --- Create Kafka Producer ---
		// Using try-with-resources ensures the producer is closed automatically
		try (KafkaProducer<String, String> producer = new KafkaProducer<>(properties)) {

			// --- Create Producer Record ---
			String messageKey = "key-" + System.currentTimeMillis(); // Optional key
			String messageValue = "Hello Kafka! This is message " + System.currentTimeMillis();
			ProducerRecord<String, String> record =
					new ProducerRecord<>(topicName, messageKey, messageValue);

			System.out.printf("Producing record: Key=%s, Value=%s%n", record.key(), record.value());

			// --- Send Message ---
			// send() is asynchronous by default. It returns a Future.
			// To make it synchronous (wait for confirmation), call .get() on the Future.
			try {
				Future<RecordMetadata> future = producer.send(record);
				// Wait for the send operation to complete and get metadata
				RecordMetadata metadata = future.get();

				System.out.printf("Message sent successfully! Topic=%s, Partition=%d, Offset=%d, Timestamp=%d%n",
						metadata.topic(),
						metadata.partition(),
						metadata.offset(),
						metadata.timestamp());

			} catch (Exception e) {
				// Handle exceptions during sending (e.g., network issues, serialization errors)
				System.err.println("Error sending message: " + e.getMessage());
				e.printStackTrace(); // Log the full stack trace for debugging
			}

			// Optional: Flush any buffered records before exiting (good practice, though close() also flushes)
			// producer.flush();

		} catch (Exception e) {
			// Handle exceptions during producer creation or closing
			System.err.println("Error creating or closing Kafka Producer: " + e.getMessage());
			e.printStackTrace();
		}

		System.out.println("Producer finished.");
	}
}
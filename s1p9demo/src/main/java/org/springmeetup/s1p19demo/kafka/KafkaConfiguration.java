package org.springmeetup.s1p19demo.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.internals.ConsumerFactory;
import reactor.kafka.receiver.internals.DefaultKafkaReceiver;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;
import reactor.kafka.sender.internals.DefaultKafkaSender;
import reactor.kafka.sender.internals.ProducerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfiguration {

	@Value("${kafka.bootstrap.servers}")
	String bootstrapServers;

	@Value("${kafka.livescore.topic}")
	String topicName;

	@Bean
	KafkaReceiver kafkaReceiver() {

		Map<String, Object> configProps = new HashMap<>();
		configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		configProps.put(ConsumerConfig.CLIENT_ID_CONFIG, "live-score-client");
		configProps.put(ConsumerConfig.GROUP_ID_CONFIG, "live-score-group-id");
		configProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);

		return new DefaultKafkaReceiver(ConsumerFactory.INSTANCE,
				ReceiverOptions.create(configProps).subscription(Arrays.asList(topicName))
		);
	}

	@Bean
	KafkaSender<String, String> kafkaSender() {
		Map<String, Object> configProps = new HashMap<>();
		configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		configProps.put(ProducerConfig.RETRIES_CONFIG, 10);
		configProps.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, "5000");
		configProps.put(ProducerConfig.BATCH_SIZE_CONFIG, "163850"); // 163KByte
		configProps.put(ProducerConfig.LINGER_MS_CONFIG, "100");
		configProps.put(ProducerConfig.ACKS_CONFIG, "1");
		configProps.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "1");

		return new DefaultKafkaSender<>(ProducerFactory.INSTANCE,
				SenderOptions.create(configProps));
	}

}
package org.springmeetup.s1p19demo.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.ConnectableFlux;
import reactor.kafka.receiver.KafkaReceiver;

import javax.annotation.PostConstruct;

@Service
@RequiredArgsConstructor
public class KafkaService {

	private final KafkaReceiver<String,String> kafkaReceiver;

	private ConnectableFlux<ServerSentEvent<String>> eventPublisher;

	@PostConstruct
	public void init() {
		eventPublisher = kafkaReceiver.receive()
				.map(consumerRecord -> ServerSentEvent.builder(consumerRecord.value()).build())
				.publish();

		// subscribes to the KafkaReceiver -> starts consumption (without observers attached)
		eventPublisher.connect();
	}

	public ConnectableFlux<ServerSentEvent<String>> getEventPublisher() {
		return eventPublisher;
	}

}

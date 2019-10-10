package org.springmeetup.s1p19demo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import org.springmeetup.s1p19demo.model.Match;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

@Service
@RequiredArgsConstructor
public class ApiRestService {

	private final ReactiveRedisTemplate<String, Match> matchReactiveRedisTemplate;
	private final KafkaSender<String, String> kafkaSender;

	private final ObjectMapper objectMapper;

	@Value("${kafka.livescore.topic}")
	String topicName;

	private ReactiveHashOperations<String, String, Match> reactiveMatchHashOperations() {
		return matchReactiveRedisTemplate.<String, Match>opsForHash();
	}


	public Mono<Match> findMatchById(Long id) {
		return reactiveMatchHashOperations()
				.get("matches", id.toString())
				.switchIfEmpty(Mono.error(new IllegalArgumentException("unable to find a match with id : " + id)));
	}

	public Mono<String> saveMatchDetails(Match match) {

		final SenderRecord<String, String, Long> senderRecord = matchToSenderRecord(match);

		return reactiveMatchHashOperations()
				.put("matches", match.getMatchId().toString(), match)
				.then(
						kafkaSender.send(Mono.just(senderRecord))
							.next()
							.log()
							.map(longSenderResult -> longSenderResult.exception() == null)
				)
				.map(aBoolean -> aBoolean ? "OK": "NOK");
	}

	private SenderRecord<String, String, Long> matchToSenderRecord(Match match) {
		final String matchJsonStr;
		try {
			matchJsonStr = objectMapper.writeValueAsString(match);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}

		return SenderRecord.create(new ProducerRecord<String, String>(topicName, matchJsonStr), match.getMatchId());
	}
}

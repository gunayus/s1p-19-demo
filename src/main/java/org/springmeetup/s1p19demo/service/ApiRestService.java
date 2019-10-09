package org.springmeetup.s1p19demo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
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

	private ReactiveHashOperations<String, String, Match> matchReactiveHashOperations() {
		return matchReactiveRedisTemplate.<String, Match>opsForHash();
	}

	public Mono<Match> findMatchById(Long id) {
		return matchReactiveHashOperations().get("matches", id.toString());
	}

	public Mono<String> saveMatchDetails(Match match) {
		final String matchStr;
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.setPropertyNamingStrategy(new PropertyNamingStrategy.KebabCaseStrategy());

			matchStr = objectMapper.writeValueAsString(match);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}

		final SenderRecord<String, String, Long> senderRecord =
				SenderRecord.create(new ProducerRecord<String, String>("live-score-topic", matchStr), match.getMatchId());

		return matchReactiveHashOperations().put("matches", match.getMatchId().toString(), match)
				.then(
						kafkaSender.send(Mono.just(senderRecord))
								.next()
								.doOnNext(longSenderResult -> System.out.println(longSenderResult.recordMetadata()))
								.map(longSenderResult -> true)
				)
				.map(hashOperationResult -> hashOperationResult ? "OK" : "NOK")
				.onErrorResume(throwable -> Mono.just("EXCEPTION : " + throwable.getMessage()))
				;
	}

}

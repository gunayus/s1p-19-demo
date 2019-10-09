package org.springmeetup.s1p19demo.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import org.springmeetup.s1p19demo.kafka.KafkaService;
import org.springmeetup.s1p19demo.model.Match;
import org.springmeetup.s1p19demo.service.ApiRestService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class ApiRestController {

	private final ApiRestService apiRestService;
	private final KafkaService kafkaService;

	@GetMapping("/match/{id}")
	public Mono<Match> getMatchById(@PathVariable("id") Long id) {
		return apiRestService.findMatchById(id);
	}

	@PostMapping("/match")
	public Mono<String> saveMatchDetails(@RequestBody Match match) {
		return apiRestService.saveMatchDetails(match);
	}

	@GetMapping(value = "/match/{id}/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<ServerSentEvent<Match>> streamMatchEvents(@PathVariable("id") Long id) {
		return kafkaService.getEventPublisher()
				.log()
				.map(stringServerSentEvent -> {

					ObjectMapper objectMapper = new ObjectMapper();
					objectMapper.setPropertyNamingStrategy(new PropertyNamingStrategy.KebabCaseStrategy());

					Match match = null;
					try {
						match = objectMapper.readValue(stringServerSentEvent.data(), Match.class);
					} catch (Exception ex) {
						return null;
					}

					return ServerSentEvent.<Match>builder()
							.data(match)
							.build();
				})
				.log()
				.filter(matchServerSentEvent -> matchServerSentEvent.data().getMatchId().equals(id));
	}

}

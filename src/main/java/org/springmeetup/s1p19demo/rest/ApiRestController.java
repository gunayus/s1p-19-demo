package org.springmeetup.s1p19demo.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import org.springmeetup.s1p19demo.kafka.KafkaService;
import org.springmeetup.s1p19demo.model.Match;
import org.springmeetup.s1p19demo.service.ApiRestService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ApiRestController {

	private final ApiRestService apiRestService;
	private final KafkaService kafkaService;

	private final ObjectMapper objectMapper;

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
		return Flux.empty();
	}


	private ServerSentEvent<Match> matchToServerSentEvent(Match match) {
		return ServerSentEvent.<Match>builder()
				.data(match)
				.build();
	}

	private ServerSentEvent<Match> toHeartBeatServerSentEvent(Long tick) {
		return matchToServerSentEvent(Match.builder()
				.matchId(0l)
				.name("Heart-Beat-Match-"+ tick).build());
	}

	private Match jsonStrToMatch(String jsonStr) {
		Match match = null;
		try {
			match = objectMapper.readValue(jsonStr, Match.class);
		} catch (Exception ex) {
			log.error("parsing exception", ex);
			return null;
		}

		return match;
	}


}

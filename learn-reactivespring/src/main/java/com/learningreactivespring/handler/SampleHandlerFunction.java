package com.learningreactivespring.handler;

import java.time.Duration;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class SampleHandlerFunction {

	public Mono<ServerResponse> flux(ServerRequest request) {
		return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
				.body(Flux.just(1, 2, 3, 4).delayElements(Duration.ofSeconds(1)).log(), Integer.class);
	}
	
	public Mono<ServerResponse> mono(ServerRequest request) {
		return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
				.body(Mono.just(1).delayElement(Duration.ofSeconds(1)).log(), Integer.class);
	}
}
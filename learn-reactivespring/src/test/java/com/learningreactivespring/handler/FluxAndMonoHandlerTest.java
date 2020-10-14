package com.learningreactivespring.handler;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@SpringBootTest
@AutoConfigureWebTestClient
@DirtiesContext()
class FluxAndMonoHandlerTest {

	@Autowired
	WebTestClient webTestClient;

	@Test
	public void flux_approch1() {
		Flux<Integer> response = webTestClient.get().uri("/functional/flux").accept(MediaType.APPLICATION_JSON)
				.exchange().expectStatus().isOk().returnResult(Integer.class).getResponseBody();

		StepVerifier.create(response).expectSubscription().expectNext(1, 2, 3, 4).verifyComplete();
	}

	@Test
	public void flux_approch4() {
		List<Integer> expected = Arrays.asList(1, 2, 3, 4);

		webTestClient.get().uri("/functional/flux").accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isOk()
				.expectBodyList(Integer.class).consumeWith(result -> {
					assertEquals(expected, result.getResponseBody());
				});
	}

	@Test
	public void mono_approch2() {
		Integer expected = Integer.valueOf(1);

		webTestClient.get().uri("/functional/mono").accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isOk()
				.expectBody(Integer.class).consumeWith(result -> {
					assertEquals(expected, result.getResponseBody());
				});
	}
}

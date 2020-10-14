package com.learningreactivespring.controller;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@WebFluxTest(FluxAndMonoController.class)
class FluxAndMonoControllerTest {

	@Autowired
	private WebTestClient webTestClient;

	@Test
	public void flux_approach1() {
		Flux<Integer> response = webTestClient.get().uri("/flux").accept(MediaType.APPLICATION_JSON).exchange()
				.expectStatus().isOk().returnResult(Integer.class).getResponseBody();

		StepVerifier.create(response).expectSubscription().expectNext(1, 2, 3, 4).verifyComplete();
	}

	@Test
	public void flux_approach2() {
		webTestClient.get().uri("/flux").accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_JSON).expectBodyList(Integer.class).hasSize(4);
	}

	@Test
	public void flux_approach3() {
		List<Integer> expected = Arrays.asList(1, 2, 3, 4);

		EntityExchangeResult<List<Integer>> returnResult = webTestClient.get().uri("/flux")
				.accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isOk().expectBodyList(Integer.class)
				.returnResult();

		assertEquals(expected, returnResult.getResponseBody());
	}

	@Test
	public void flux_approach4() {
		List<Integer> expected = Arrays.asList(1, 2, 3, 4);

		webTestClient.get().uri("/flux").accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isOk()
				.expectBodyList(Integer.class).consumeWith(response -> {
					assertEquals(expected, response.getResponseBody());
				});
	}

	@Test
	public void fluxStream() {
		Flux<Long> response = webTestClient.get().uri("/flux-stream").accept(MediaType.APPLICATION_STREAM_JSON)
				.exchange().expectStatus().isOk().returnResult(Long.class).getResponseBody();

		StepVerifier.create(response).expectSubscription().expectNext(0L, 1L, 2L, 3L).thenCancel().verify();
	}

	@Test
	public void mono_approach1() {
		webTestClient.get().uri("/mono").accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isOk()
				.expectBody(Integer.class).consumeWith(result -> {
					assertEquals(1, result.getResponseBody());
				});
	}
	
	@Test
	public void mono_approach2() {
		EntityExchangeResult<Integer> result = webTestClient.get().uri("/mono").accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isOk()
				.expectBody(Integer.class).returnResult();
		
		assertEquals(1, result.getResponseBody());
	}
}

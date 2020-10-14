package com.learningreactivespring.fuxandmonobackgroud;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.util.retry.Retry;

class FluxAndMonoErrorTest {

	@Test
	public void fluxErrorHandling() {
		Flux<String> stringFLux = Flux.just("A", "B", "C")
				.concatWith(Flux.error(new RuntimeException("Exception occured"))).concatWith(Flux.just("D"))
				.onErrorResume(e -> {
					System.out.println("Error: " + e);
					return Flux.just("default", "default1");
				}).log();

		StepVerifier.create(stringFLux).expectSubscription().expectNext("A", "B", "C")
				// .expectError(RuntimeException.class).verify();
				.expectNext("default", "default1").verifyComplete();
	}

	@Test
	public void fluxErrorHandling_onErrorReturn() {
		Flux<String> stringFLux = Flux.just("A", "B", "C")
				.concatWith(Flux.error(new RuntimeException("Exception occured"))).concatWith(Flux.just("D"))
				.onErrorReturn("default").log();

		StepVerifier.create(stringFLux).expectSubscription().expectNext("A", "B", "C")
				// .expectError(RuntimeException.class).verify();
				.expectNext("default").verifyComplete();
	}

	@Test
	public void fluxErrorHandling_onErrorMap() {
		Flux<String> stringFLux = Flux.just("A", "B", "C")
				.concatWith(Flux.error(new RuntimeException("Exception occured"))).concatWith(Flux.just("D"))
				.onErrorMap(e -> new CustomExeption(e)).log();

		StepVerifier.create(stringFLux).expectSubscription().expectNext("A", "B", "C").expectError(CustomExeption.class)
				.verify();
	}

	@Test
	public void fluxErrorHandling_onErrorMap_withRetry() {
		Flux<String> stringFLux = Flux.just("A", "B", "C")
				.concatWith(Flux.error(new RuntimeException("Exception occured"))).concatWith(Flux.just("D"))
				.onErrorMap(e -> new CustomExeption(e)).log().retry(2);

		StepVerifier.create(stringFLux).expectSubscription().expectNext("A", "B", "C").expectNext("A", "B", "C")
				.expectNext("A", "B", "C").expectError(CustomExeption.class).verify();
	}
	
	@Test
	public void fluxErrorHandling_onErrorMap_withRetryBackof() {
		Flux<String> stringFLux = Flux.just("A", "B", "C")
				.concatWith(Flux.error(new RuntimeException("Exception occured"))).concatWith(Flux.just("D"))
				.onErrorMap(e -> new CustomExeption(e)).log().retryWhen(Retry.backoff(2, Duration.ofSeconds(2)));

		StepVerifier.create(stringFLux).expectSubscription().expectNext("A", "B", "C").expectNext("A", "B", "C")
				.expectNext("A", "B", "C").expectError(IllegalStateException.class).verify();
	}

}

package com.learningreactivespring.fuxandmonobackgroud;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class FuxAndMonoTest {

	@Test
	public void fuxTest() {
		Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
				.concatWith(Flux.error(new RuntimeException("Exception occured"))).log();
		stringFlux.subscribe(System.out::println, e -> System.err.println("Exception: " + e.getMessage()));
	}

	@Test
	public void fuxTestElements_withoutError() {
		Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring").log();

		StepVerifier.create(stringFlux).expectNext("Spring").expectNext("Spring Boot").expectNext("Reactive Spring")
				.verifyComplete();
	}

	@Test
	public void fuxTestElements_withError() {
		Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
				.concatWith(Flux.error(new RuntimeException("Exception occured"))).log();

		StepVerifier.create(stringFlux).expectNext("Spring").expectNext("Spring Boot").expectNext("Reactive Spring")
				// .expectError(RuntimeException.class)
				.expectErrorMessage("Exception occured").verify();
	}

	@Test
	public void fuxTestElements_withError1() {
		Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
				.concatWith(Flux.error(new RuntimeException("Exception occured"))).log();

		StepVerifier.create(stringFlux).expectNext("Spring", "Spring Boot", "Reactive Spring")
				// .expectError(RuntimeException.class)
				.expectErrorMessage("Exception occured").verify();
	}

	@Test
	public void fuxTestElementsCount_withError() {
		Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
				.concatWith(Flux.error(new RuntimeException("Exception occured"))).log();

		StepVerifier.create(stringFlux).expectNextCount(3)
				// .expectError(RuntimeException.class)
				.expectErrorMessage("Exception occured").verify();
	}
	
	@Test
	public void monoTest() {
		Mono<String> stringMono = Mono.just("Spring").log();
		
		StepVerifier.create(stringMono).expectNext("Spring").verifyComplete();
	}
	
	@Test
	public void monoTest_error() {
		Mono<Object> monoError = Mono.error(new RuntimeException("Exception occured")).log();
		
		StepVerifier.create(monoError).expectErrorMessage("Exception occured").verify();
	}

}

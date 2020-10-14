package com.learningreactivespring.fuxandmonobackgroud;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

class VirtualTimeTest {

	@Test
	public void testingWithoutVirtualTime() {
		Flux<Long> interval = Flux.interval(Duration.ofSeconds(1)).take(3).log();
		
		StepVerifier.create(interval).expectSubscription().expectNext(0L, 1L, 2L).verifyComplete();
	}

	@Test
	public void testingWithVirtualTime() {
		VirtualTimeScheduler.getOrSet();
		
		Flux<Long> interval = Flux.interval(Duration.ofSeconds(1)).take(3).log();
		
		StepVerifier.withVirtualTime(() -> interval.log())
		.expectSubscription().thenAwait(Duration.ofSeconds(3)).expectNext(0L, 1L, 2L).verifyComplete();
	}
	
	@Test
	public void combainUsingMerge_withDelay() {
		VirtualTimeScheduler.getOrSet();
		
		Flux<String> flux1 = Flux.just("A", "B", "C").delayElements(Duration.ofSeconds(1));
		Flux<String> flux2 = Flux.just("D", "E", "F").delayElements(Duration.ofSeconds(1));
		
		Flux<String> mergeFlux = Flux.merge(flux1, flux2);
		// The order is not granted...
		//StepVerifier.create(mergeFlux).expectSubscription().expectNext("A", "B", "C", "D", "E", "F").verifyComplete();
		StepVerifier.withVirtualTime(() -> mergeFlux.log()).expectSubscription().thenAwait(Duration.ofSeconds(6)).expectNext("A", "B", "C", "D", "E", "F").verifyComplete();
	}
}

package com.learningreactivespring.fuxandmonobackgroud;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

class FluxAndMonoCombineTest {

	@Test
	public void combineUsingMerge() {
		Flux<String> flux1 = Flux.just("A", "B", "C");
		Flux<String> flux2 = Flux.just("D", "E", "F");
		
		Flux<String> mergeFlux = Flux.merge(flux1, flux2).log();
		StepVerifier.create(mergeFlux).expectSubscription().expectNext("A", "B", "C", "D", "E", "F").verifyComplete();
	}
	
	@Test
	public void combainUsingMerge_withDelay() {
		Flux<String> flux1 = Flux.just("A", "B", "C").delayElements(Duration.ofSeconds(1));
		Flux<String> flux2 = Flux.just("D", "E", "F").delayElements(Duration.ofSeconds(1));
		
		Flux<String> mergeFlux = Flux.merge(flux1, flux2).log();
		// The order is not granted...
		StepVerifier.create(mergeFlux).expectSubscription().expectNext("A", "B", "C", "D", "E", "F").verifyComplete();
	}

	@Test
	public void combineUsingConcat() {
		Flux<String> flux1 = Flux.just("A", "B", "C");
		Flux<String> flux2 = Flux.just("D", "E", "F");
		
		Flux<String> concatFlux = Flux.concat(flux1, flux2).log();
		StepVerifier.create(concatFlux).expectSubscription().expectNext("A", "B", "C", "D", "E", "F").verifyComplete();
	}
	
	@Test
	public void combainUsingConcat_withDelay() {
		Flux<String> flux1 = Flux.just("A", "B", "C").delayElements(Duration.ofSeconds(1));
		Flux<String> flux2 = Flux.just("D", "E", "F").delayElements(Duration.ofSeconds(1));
		
		Flux<String> concatFlux = Flux.concat(flux1, flux2).log();
		StepVerifier.create(concatFlux).expectSubscription().expectNext("A", "B", "C", "D", "E", "F").verifyComplete();
	}
	
	@Test
	public void combineUsingZip() {
		Flux<String> flux1 = Flux.just("A", "B", "C").log();
		Flux<String> flux2 = Flux.just("D", "E", "F").log();
		
		Flux<String> concatFlux = Flux.zip(flux1, flux2, (s1, s2) -> s1.concat(s2)).log();
		StepVerifier.create(concatFlux).expectSubscription().expectNext("AD", "BE", "CF").verifyComplete();
	}
}

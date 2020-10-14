package com.learningreactivespring.fuxandmonobackgroud;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

class FluxAndMonoFilter {

	@Test
	public void filterTest() {
		List<String> names = Arrays.asList("Adam", "Anna", "Jack", "Janny");
		Flux<String> namesFlux = Flux.fromIterable(names).filter(s -> s.startsWith("A")).log();
		
		StepVerifier.create(namesFlux).expectNext("Adam", "Anna").verifyComplete();
	}

	@Test
	public void filterLengthTest() {
		List<String> names = Arrays.asList("Adam", "Anna", "Jack", "Janny");
		Flux<String> namesFlux = Flux.fromIterable(names).log().filter(s -> s.length() <= 4).log();
		
		StepVerifier.create(namesFlux).expectNext("Adam", "Anna", "Jack").verifyComplete();
	}
}

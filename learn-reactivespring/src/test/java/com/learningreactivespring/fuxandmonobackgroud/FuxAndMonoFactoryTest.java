package com.learningreactivespring.fuxandmonobackgroud;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class FuxAndMonoFactoryTest {
	
	@Test
	public void fluxUsingIterator() {
		List<String> names = Arrays.asList("Adam", "Anna", "Jack", "Janny");
		Flux<String> namesFlux = Flux.fromIterable(names).log();
		
		StepVerifier.create(namesFlux).expectNext("Adam", "Anna", "Jack", "Janny").verifyComplete();
	}

	@Test
	public void fluxUsingArray() {
		String[] arrayString = new String[] {"Adam", "Anna", "Jack", "Janny"};
		Flux<String> namesFlux = Flux.fromArray(arrayString).log();
		
		StepVerifier.create(namesFlux).expectNext("Adam", "Anna", "Jack", "Janny").verifyComplete();
	}
	
	@Test
	public void fluxUsingStream() {
		Stream<String> stream = Stream.of("Adam", "Anna", "Jack", "Janny");
		Flux<String> namesFlux = Flux.fromStream(stream).log();
		
		StepVerifier.create(namesFlux).expectNext("Adam", "Anna", "Jack", "Janny").verifyComplete();
	}
	
	@Test
	public void monoUsingJustOrEmpty() {
		Mono<Object> emptyMono = Mono.justOrEmpty(null).log(); //Mono.Empty();
		
		StepVerifier.create(emptyMono).verifyComplete();
	}
	
	@Test
	public void monoUsingSupplier() {
		Supplier<String> supplier = () -> "Adam";
		
		System.out.println("Value: " + supplier.get());
		
		Mono<String> monoString = Mono.fromSupplier(supplier).log();
		StepVerifier.create(monoString).expectNext("Adam").verifyComplete();
	}
	
	@Test
	public void fluxUsingRange() {
		Flux<Integer> integerFlux = Flux.range(1, 5).log();
		
		StepVerifier.create(integerFlux).expectNext(1, 2, 3, 4, 5).verifyComplete();
	}
}

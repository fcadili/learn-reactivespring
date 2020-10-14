package com.learningreactivespring.fuxandmonobackgroud;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

class FluxAndMonoTransformTest {

	@Test
	public void transformUsingMap() {
		List<String> names = Arrays.asList("Adam", "Anna", "Jack", "Janny");
		Flux<String> namesFlux = Flux.fromIterable(names).log();

		StepVerifier.create(namesFlux.map(name -> name.toUpperCase()).log()).expectNext("ADAM", "ANNA", "JACK", "JANNY")
				.verifyComplete();
	}

	@Test
	public void transformUsingMap_length() {
		List<String> names = Arrays.asList("Adam", "Anna", "Jack", "Janny");
		Flux<String> namesFlux = Flux.fromIterable(names).log();

		StepVerifier.create(namesFlux.map(name -> name.length()).log()).expectNext(4, 4, 4, 5).verifyComplete();
	}

	@Test
	public void transformUsingMap_lengthAndRepeat() {
		List<String> names = Arrays.asList("Adam", "Anna", "Jack", "Janny");
		Flux<String> namesFlux = Flux.fromIterable(names).repeat(1).log();

		StepVerifier.create(namesFlux.map(name -> name.length()).log()).expectNext(4, 4, 4, 5, 4, 4, 4, 5)
				.verifyComplete();
	}

	@Test
	public void transformUsingMap_filter() {
		List<String> names = Arrays.asList("Adam", "Anna", "Jack", "Janny");
		Flux<String> namesFlux = Flux.fromIterable(names).filter(s -> s.length() > 4).log();

		StepVerifier.create(namesFlux.map(name -> name.length()).log()).expectNext(5).verifyComplete();
	}

	@Test
	public void transformUsingFlatMap() {
		List<String> names = Arrays.asList("A", "B", "C", "D", "E", "F");
		Flux<String> stringFlux = Flux.fromIterable(names).flatMap(s -> {
			return Flux.fromIterable(convertToList(s)); // db or external service call that returns a Flux<String>
		}).log();

//		StepVerifier.create(stringFlux).expectNextSequence(Arrays.asList("A", "newValue"))
//				.expectNextSequence(Arrays.asList("B", "newValue")).expectNextSequence(Arrays.asList("C", "newValue"))
//				.expectNextSequence(Arrays.asList("D", "newValue")).expectNextSequence(Arrays.asList("E", "newValue"))
//				.expectNextSequence(Arrays.asList("F", "newValue")).verifyComplete();

		StepVerifier.create(stringFlux).expectNext("A", "newValue", "B", "newValue", "C", "newValue", "D", "newValue",
				"E", "newValue", "F", "newValue").verifyComplete();
	}

	private List<String> convertToList(String s) {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Arrays.asList(s, "newValue");
	}

	@Test
	public void transformUsingFlatMap_usingparallel() {
		List<String> names = Arrays.asList("A", "B", "C", "D", "E", "F");
		Flux<Flux<String>> stringFluxFlux = Flux.fromIterable(names).window(2); // Flux<Flux<String>
		Flux<List<String>> stringFluxFluxStep2 = stringFluxFlux
				.flatMap(flux -> flux.map(this::convertToList).subscribeOn(Schedulers.parallel())).log();
		Flux<String> stringFlux = stringFluxFluxStep2.flatMap(l -> Flux.fromIterable(l)).log();
//				
//				flatMap(s -> {
//			return Flux.fromIterable(convertToList(s)); // db or external service call that returns a Flux<String>
//		}).log();

		StepVerifier.create(stringFlux).expectNextCount(12).verifyComplete();
	}
	
	@Test
	public void transformUsingFlatMap_parallel() {
		List<String> names = Arrays.asList("A", "B", "C", "D", "E", "F");
		Flux<Flux<String>> stringFluxFlux = Flux.fromIterable(names).window(2); // Flux<Flux<String>
//		Flux<List<String>> stringFluxFluxStep2 = stringFluxFlux
//				.concatMap(flux -> flux.map(this::convertToList).subscribeOn(Schedulers.parallel())).log();
		Flux<List<String>> stringFluxFluxStep2 = stringFluxFlux
				.flatMapSequential(flux -> flux.map(this::convertToList).subscribeOn(Schedulers.parallel())).log();
		Flux<String> stringFlux = stringFluxFluxStep2.flatMap(l -> Flux.fromIterable(l)).log();
//				
//				flatMap(s -> {
//			return Flux.fromIterable(convertToList(s)); // db or external service call that returns a Flux<String>
//		}).log();

		StepVerifier.create(stringFlux).expectNextCount(12).verifyComplete();
	}
}

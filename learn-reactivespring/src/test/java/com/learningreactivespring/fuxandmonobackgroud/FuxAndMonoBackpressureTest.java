package com.learningreactivespring.fuxandmonobackgroud;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

class FuxAndMonoBackpressureTest {

	@Test
	public void backpressureTest() {
		Flux<Integer> integerFlux = Flux.range(1, 10).log();

		StepVerifier.create(integerFlux).expectSubscription().thenRequest(1).expectNext(1).thenRequest(1).expectNext(2)
				.thenCancel().verify();
	}

	@Test
	public void backpressure() {
		Flux<Integer> integerFlux = Flux.range(1, 10).log();

		integerFlux.subscribe(element -> System.out.println("Element is: " + element),
				(e) -> System.err.println("===> " + e),
				() -> System.out.println("END"),
				(subscription) -> {
					subscription.request(2);
					//subscription.request(1);
					//subscription.cancel();
				});
	}
	
	@Test
	public void backpressure_cancel() {
		Flux<Integer> integerFlux = Flux.range(1, 10).log();

		integerFlux.subscribe(element -> System.out.println("Element is: " + element),
				(e) -> System.err.println("===> " + e),
				() -> System.out.println("END"),
				(subscription) -> {
					subscription.cancel();
				});
	}
	
	@Test
	public void backpressure_customizebackpressure() {
		Flux<Integer> integerFlux = Flux.range(1, 10).log();
		
		integerFlux.subscribe(new BaseSubscriber<Integer>() {
			@Override
			protected void hookOnNext(Integer value) {
				//request(1);
				System.out.println("Value recieved is: " + value);
				if (value == 4) {
					cancel();
				}
			}
		});
	}
}

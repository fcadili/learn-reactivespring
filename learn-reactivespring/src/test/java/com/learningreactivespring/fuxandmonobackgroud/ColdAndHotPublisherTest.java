package com.learningreactivespring.fuxandmonobackgroud;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;

import org.junit.jupiter.api.Test;

import reactor.core.Disposable;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

class ColdAndHotPublisherTest {

	@Test
	public void coldPublisherTest() throws InterruptedException {
		Flux<String> stringFlux = Flux.just("A","B","C","D", "E").delayElements(Duration.ofSeconds(1));
		
		stringFlux.subscribe(s -> System.out.println("Subscriber 1: " + s));
		
		Thread.sleep(2000);
		
		stringFlux.subscribe(s -> System.out.println("Subscriber 2: " + s)); // emit values from the beginning
		
		Thread.sleep(4000);
	}
	
	@Test
	public void hotPublisherTest() throws InterruptedException {
		Flux<String> stringFlux = Flux.just("A","B","C","D", "E").delayElements(Duration.ofSeconds(1));
		
		ConnectableFlux<String> connectableFlux = stringFlux.publish();
		Disposable disposable = connectableFlux.connect();
		
		connectableFlux.subscribe(s -> System.out.println("Subscriber 1: " + s));
		
		Thread.sleep(3000);
		
		connectableFlux.subscribe(s -> System.out.println("Subscriber 2: " + s)); // does not emit values from the beginning
		
		Thread.sleep(4000);
	}

}

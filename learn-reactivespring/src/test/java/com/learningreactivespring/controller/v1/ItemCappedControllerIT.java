package com.learningreactivespring.controller.v1;

import static com.learningreactivespring.constants.ItemConstants.ITEMS_END_POINT;
import static com.learningreactivespring.constants.ItemConstants.V1_CAPPED;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.learningreactivespring.document.ItemCapped;
import com.learningreactivespring.repository.ItemReactiveCappedRepository;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@SpringBootTest
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@Slf4j
@DirtiesContext
class ItemCappedControllerIT {

	@Autowired
	WebTestClient webClient;

	@Autowired
	private ReactiveMongoOperations operation;

	@Autowired
	ItemReactiveCappedRepository cappedRepository;

	@BeforeEach
	private void setup() {
		operation.dropCollection(ItemCapped.class).then(operation.createCollection(ItemCapped.class,
				CollectionOptions.empty().maxDocuments(20L).size(50000L).capped())).block();

		// non sequential insert
		Flux.interval(Duration.ofMillis(1)).map(i -> new ItemCapped(null, "Random item " + i, 100.0 + i))
				.flatMap(item -> cappedRepository.save(item)).take(5).doOnNext(item -> {
					log.info("Inserted item is: {}", item);
				}).blockLast();
	}

	@Test
	public void test() {
		Flux<ItemCapped> result = webClient.get().uri(V1_CAPPED + ITEMS_END_POINT).exchange().expectStatus().isOk()
				.returnResult(ItemCapped.class).getResponseBody();

		// generate java.lang.IllegalStateException: state should be: open, why?
		StepVerifier.create(result).expectSubscription()
				.expectNextCount(5).thenCancel().verify();
	}
}

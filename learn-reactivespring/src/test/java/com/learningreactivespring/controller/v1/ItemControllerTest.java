package com.learningreactivespring.controller.v1;

import static com.learningreactivespring.constants.ItemConstants.EXCEPTION_END_POINT;
import static com.learningreactivespring.constants.ItemConstants.ITEMS_END_POINT;
import static com.learningreactivespring.constants.ItemConstants.V1;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ListBodySpec;

import com.learningreactivespring.document.Item;
import com.learningreactivespring.repository.ItemReactiveRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@WebFluxTest(ItemController.class)
class ItemControllerTest {

	@Autowired
	private WebTestClient webTestClient;

	@MockBean
	private ItemReactiveRepository repository;

	@Test
	public void getAllItemsTest() {
		Flux<Item> expected = Flux.just(new Item("001", "Apple Watch", 210.49)).log();
		when(repository.findAll()).thenReturn(expected);

		Flux<Item> result = webTestClient.get().uri(V1 + ITEMS_END_POINT).exchange().returnResult(Item.class)
				.getResponseBody();

		StepVerifier.create(result).expectSubscription().expectNextCount(1L).verifyComplete();
	}

	@Test
	public void getAllItemsTest_approch2() {
		Flux<Item> expected = Flux.just(new Item("001", "Apple Watch", 210.49)).log();
		when(repository.findAll()).thenReturn(expected);

		ListBodySpec<Item> bodyList = webTestClient.get().uri(V1 + ITEMS_END_POINT).exchange().expectStatus().isOk()
				.expectBodyList(Item.class).hasSize(1);
	}

	@Test
	public void getAllItemsTest_approch3() {
		Flux<Item> expected = Flux.just(new Item("001", "Apple Watch", 210.49)).log();
		when(repository.findAll()).thenReturn(expected);

		ListBodySpec<Item> bodyList = webTestClient.get().uri(V1 + ITEMS_END_POINT).exchange().expectStatus().isOk()
				.expectBodyList(Item.class).hasSize(1).consumeWith(response -> {
					List<Item> items = response.getResponseBody();
					items.forEach(item -> {
						assertTrue(item.getId() != null);
					});
				});
	}

	@Test
	public void getItemsWithDescriptionTest() {
		Flux<Item> expected = Flux.just(new Item("001", "Apple Watch", 210.49)).log();
		when(repository.findByDescriptionLike("Apple")).thenReturn(expected);

		Flux<Item> result = webTestClient.get().uri(V1 + ITEMS_END_POINT + "?desc=Apple").exchange().expectStatus()
				.isOk().returnResult(Item.class).getResponseBody();

		StepVerifier.create(result).expectSubscription()
				.expectNextMatches(item -> "Apple Watch".equals(item.getDescription())).verifyComplete();
	}

	@Test
	public void getItemTest() {
		Mono<Item> expected = Mono.just(new Item("001", "Apple Watch", 210.49)).log();
		when(repository.findById("001")).thenReturn(expected);

		Flux<Item> result = webTestClient.get().uri(V1 + ITEMS_END_POINT + "/001").exchange().expectStatus().isOk()
				.returnResult(Item.class).getResponseBody();

		StepVerifier.create(result).expectSubscription().expectNextMatches(item -> "001".equals(item.getId()))
				.verifyComplete();
	}

	@Test
	public void getNoItemTest() {
		Mono<Item> expected = Mono.empty();
		when(repository.findById("002")).thenReturn(expected);

		Flux<Item> result = webTestClient.get().uri(V1 + ITEMS_END_POINT + "/002").exchange().expectStatus()
				.isNotFound().returnResult(Item.class).getResponseBody();

		StepVerifier.create(result).expectSubscription().verifyComplete();
	}

	@Test
	public void postItem() {
		Item sendItem = new Item(null, "Apple Watch", 210.49);
		Item exectedItem = new Item("001", "Apple Watch", 210.49);
		when(repository.save(sendItem)).thenReturn(Mono.just(exectedItem).log());

		Flux<Item> result = webTestClient.post().uri(V1 + ITEMS_END_POINT).contentType(MediaType.APPLICATION_JSON)
				.body(Mono.just(sendItem), Item.class).exchange().expectStatus().isCreated().returnResult(Item.class)
				.getResponseBody();

		StepVerifier.create(result).expectNextMatches(item -> "001".equals(item.getId())).verifyComplete();
	}

	@Test
	public void postItem2() {
		Item sendItem = new Item(null, "Apple Watch", 210.49);
		Item exectedItem = new Item("001", "Apple Watch", 210.49);
		when(repository.save(sendItem)).thenReturn(Mono.just(exectedItem).log());

		webTestClient.post().uri(V1 + ITEMS_END_POINT).body(Mono.just(sendItem), Item.class).exchange().expectStatus()
				.isCreated().expectBody().jsonPath("$.id").isEqualTo(exectedItem.getId()).jsonPath("$.description")
				.isEqualTo(exectedItem.getDescription()).jsonPath("$.price").isEqualTo(exectedItem.getPrice());
	}

	@Test
	public void updateItem() {
		Item itemStored = new Item("001", "Apple Watch", 210.49);
		Item itemUpdated = new Item("001", "Apple Watch", 250.10);

		when(repository.findById("001")).thenReturn(Mono.just(itemStored).log());
		when(repository.save(itemUpdated)).thenReturn(Mono.just(itemUpdated).log());

		Flux<Item> result = webTestClient.put().uri(V1 + ITEMS_END_POINT).contentType(MediaType.APPLICATION_JSON)
				.body(Mono.just(itemUpdated), Item.class).exchange().expectStatus().isOk().returnResult(Item.class)
				.getResponseBody();

		StepVerifier.create(result)
				.expectNextMatches(item -> Math.abs(itemUpdated.getPrice() - item.getPrice()) < 0.001).verifyComplete();
	}

	@Test
	public void updateItemNotFound() {
		Item itemUpdated = new Item("001", "Apple Watch", 250.10);

		when(repository.findById("001")).thenReturn(Mono.empty());
		// when(repository.save(itemUpdated)).thenReturn(Mono.just(itemUpdated).log());

		Flux<Item> result = webTestClient.put().uri(V1 + ITEMS_END_POINT).contentType(MediaType.APPLICATION_JSON)
				.body(Mono.just(itemUpdated), Item.class).exchange().expectStatus().isNotFound()
				.returnResult(Item.class).getResponseBody();

		StepVerifier.create(result).verifyComplete();
	}

	@Test
	public void deleteItem() {
		Item itemStored = new Item("001", "Apple Watch", 210.49);

		when(repository.findById("001")).thenReturn(Mono.just(itemStored).log());
		when(repository.delete(itemStored)).thenReturn(Mono.empty());

		webTestClient.delete().uri(V1 + ITEMS_END_POINT + "/001").exchange().expectStatus().isOk().expectBody()
				.isEmpty();
	}

	@Test
	public void deleteItemNotFound() {
		Item itemStored = new Item("002", "Apple Watch", 210.49);

		when(repository.findById("001")).thenReturn(Mono.empty());

		webTestClient.delete().uri(V1 + ITEMS_END_POINT + "/001").exchange().expectStatus().isNotFound().expectBody()
				.isEmpty();
	}
	
	@Test
	public void runtimeException() {
		webTestClient.get().uri(V1 + EXCEPTION_END_POINT + "2").exchange().expectStatus().is5xxServerError()
			.expectBody(String.class).isEqualTo("Runtime exception occurred");
	}
}

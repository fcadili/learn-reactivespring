package com.learningreactivespring.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.annotation.DirtiesContext;

import com.learningreactivespring.document.Item;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@DataMongoTest
@DirtiesContext()
class ItemRectiveRepositoryTest {

	@Autowired
	private ItemReactiveRepository itemReactiveRepository;

	private List<Item> listItems = Arrays.asList(new Item(null, "Samsung TV", 400.0), new Item(null, "LG TV", 420.0),
			new Item(null, "Apple watch", 299.99), new Item(null, "Beats Headphones", 149.99));

	@BeforeEach
	public void setup() {
		itemReactiveRepository.deleteAll().thenMany(itemReactiveRepository.saveAll(listItems).log()).blockLast();
	}

	@Test
	public void getAllItems() {
		Flux<Item> items = itemReactiveRepository.findAll();

		StepVerifier.create(items).expectSubscription().expectNextCount(4L).verifyComplete();
	}

	@Test
	public void getItemById() {
		Flux<Item> items = Flux.fromIterable(listItems)
				.filter(item -> "Apple watch".equals(item.getDescription()) && item.getId() != null)
				.flatMap(item -> itemReactiveRepository.findById(item.getId()));

		StepVerifier.create(items).expectSubscription()
				.expectNextMatches(item -> "Apple watch".equals(item.getDescription())).verifyComplete();
	}

	@Test
	public void findByDescriptionTest() {
		Flux<Item> items = itemReactiveRepository.findByDescription("Apple watch");

		StepVerifier.create(items).expectSubscription()
				.expectNextMatches(item -> "Apple watch".equals(item.getDescription())).verifyComplete();
	}

	@Test
	public void findByDescriptionLikeTest() {
		Flux<Item> items = itemReactiveRepository.findByDescriptionLike("Apple");

		StepVerifier.create(items).expectSubscription()
				.expectNextMatches(item -> "Apple watch".equals(item.getDescription())).verifyComplete();
	}

	@Test
	public void saveItemTest() {
		Flux<Item> item = itemReactiveRepository.save(new Item(null, "Apple AirPod Headphone", 198.99))
				.thenMany(itemReactiveRepository.findByDescriptionLike("AirPod"));

		StepVerifier.create(item).expectSubscription()
				.expectNextMatches(i -> "Apple AirPod Headphone".equals(i.getDescription())).verifyComplete();
	}

	@Test
	public void updateItemTest() {
		Flux<Item> updatedItems = itemReactiveRepository.findByDescriptionLike("Apple").map(item -> {
			item.setDescription(item.getDescription().toUpperCase());
			return item;
		}).flatMap(item -> {
			return itemReactiveRepository.save(item);
		}).thenMany(itemReactiveRepository.findByDescriptionLike("APPLE"));

		StepVerifier.create(updatedItems).expectSubscription()
				.expectNextMatches(item -> "APPLE WATCH".equals(item.getDescription())).verifyComplete();
	}

	@Test
	public void deleteItemTest() {
		Mono<Void> deleteItem = itemReactiveRepository.deleteByDescription("Apple watch");

		StepVerifier.create(deleteItem.then(itemReactiveRepository.count())).expectSubscription().expectNext(3L)
				.verifyComplete();
	}
	
	@Test
	public void deleteItemTest2() {
		Flux<Void> deleteItem = itemReactiveRepository.findByDescription("Apple watch")
				.flatMap(item -> itemReactiveRepository.delete(item));

		StepVerifier.create(deleteItem.then(itemReactiveRepository.count())).expectSubscription().expectNext(3L)
				.verifyComplete();
	}
}

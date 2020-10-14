package com.learningreactivespring.initialize;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.stereotype.Component;

import com.learningreactivespring.document.Item;
import com.learningreactivespring.document.ItemCapped;
import com.learningreactivespring.repository.ItemReactiveCappedRepository;
import com.learningreactivespring.repository.ItemReactiveRepository;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Component
@Slf4j
@Profile("!test")
public class ItemDataInitializer implements CommandLineRunner {

	@Autowired
	private ItemReactiveRepository repository;
	
	@Autowired
	private ItemReactiveCappedRepository cappedRepository;

	@Autowired
	private ReactiveMongoOperations operation;

	private List<Item> data() {
		return Arrays.asList(new Item(null, "Samsung TV", 400.0), new Item(null, "LG TV", 420.0),
				new Item(null, "Apple watch", 299.99), new Item(null, "Beats Headphones", 149.99));
	}
	
	private void insertDataCapped() {
		Flux.interval(Duration.ofSeconds(1)).map( i -> new ItemCapped(null, "Random item " + i, 100.0 + i))
			.flatMap(item -> cappedRepository.save(item)).subscribe(item -> {
				log.info("Inserted item is: {}", item);
			});
		
//		Flux<ItemCapped> items = Flux.interval(Duration.ofSeconds(1)).map( i -> new ItemCapped(null, "Random item " + i, 100.0 + i));
//		cappedRepository.insert(items).subscribe(item -> {
//			log.debug("Insert: {}", item);
//		});
	}

	private void createCaptedCollection() {
		operation.dropCollection(ItemCapped.class).then(operation.createCollection(ItemCapped.class,
				CollectionOptions.empty().maxDocuments(20L).size(50000L).capped())).block();
		
		insertDataCapped();
	}

	@Override
	public void run(String... args) throws Exception {

		repository.saveAll(Flux.fromIterable(data())).subscribe();
		createCaptedCollection();
	}

}

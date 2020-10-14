package com.learningreactivespring.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.learningreactivespring.document.Item;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ItemReactiveRepository extends ReactiveMongoRepository<Item, String> {

	Flux<Item> findByDescription(String description);
	
	Flux<Item> findByDescriptionLike(String description);
	
	Mono<Void> deleteByDescription(String description);
}
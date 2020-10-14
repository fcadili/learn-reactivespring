package com.reactivestream.service;

import com.reactivestream.domain.Item;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ItemService {

	public Flux<Item> getItemsUsingRetrieve();

	public Flux<Item> getItemsUsingExchange();

	Mono<Item> getOneItemUsingRetrieve(String id);

	Mono<Item> getOneItemUsingExchange(String id);

	Mono<Item> editItemUsingRetrieve(Item item);

	Mono<Item> editItemUsingExchange(Item item);

	Mono<Item> deleteItemUsingRetrieve(String id);

	Mono<Item> deleteItemUsingExchange(String id);

}
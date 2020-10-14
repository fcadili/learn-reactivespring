package com.reactivestream.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.reactivestream.domain.Item;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ItemServiceImp implements ItemService {

	private WebClient webClient;

	public ItemServiceImp(WebClient.Builder webClientBuilder) {
		this.webClient = webClientBuilder.baseUrl("http://localhost:8080").build();
	}

	@Override
	public Flux<Item> getItemsUsingExchange() {
		return webClient.get().uri("/v1/items").accept(MediaType.APPLICATION_STREAM_JSON).exchange()
				.flatMapMany(response -> response.bodyToFlux(Item.class)).log("client service [EXCHANGE]: ");
	}

	@Override
	public Flux<Item> getItemsUsingRetrieve() {
		return webClient.get().uri("/v1/items").accept(MediaType.APPLICATION_STREAM_JSON).retrieve()
				.bodyToFlux(Item.class).log("client service: ").log("client service [RETRIVE]: ");
	}

	@Override
	public Mono<Item> getOneItemUsingExchange(String id) {
		return webClient.get().uri("/v1/items/{id}", id).accept(MediaType.APPLICATION_STREAM_JSON).exchange()
				.flatMap(response -> response.bodyToMono(Item.class)).log("client service [EXCHANGE]: ");
	}

	@Override
	public Mono<Item> getOneItemUsingRetrieve(String id) {
		return webClient.get().uri("/v1/items/{id}", id).accept(MediaType.APPLICATION_STREAM_JSON).retrieve()
				.bodyToMono(Item.class).log("client service [RETRIVE]: ");
	}

	@Override
	public Mono<Item> editItemUsingExchange(Item item) {
		return webClient.post().uri("/v1/items").bodyValue(item)
				.header("Content-Type", MediaType.APPLICATION_JSON_VALUE).accept(MediaType.APPLICATION_STREAM_JSON)
				.exchange().flatMap(response -> {
					if (response.statusCode() == HttpStatus.OK)
						return response.bodyToMono(Item.class).log("client service [EXCHANGE]: ");
					else
						return Mono.error(new RuntimeException("No data found: " + response.statusCode()));
				});
	}

	@Override
	public Mono<Item> editItemUsingRetrieve(Item item) {
		return webClient.post().uri("/v1/items").bodyValue(item)
				.header("Content-Type", MediaType.APPLICATION_JSON_VALUE).accept(MediaType.APPLICATION_STREAM_JSON)
				.retrieve()
				.onStatus(HttpStatus::is4xxClientError, response -> Mono.error(new RuntimeException("No data found")))
				.bodyToMono(Item.class).log("client service [RETRIVE]: ");
	}

	@Override
	public Mono<Item> deleteItemUsingExchange(String id) {
		return webClient.delete().uri("/v1/items/{id}", id).accept(MediaType.APPLICATION_STREAM_JSON).exchange()
				.flatMap(response -> {
					if (response.statusCode() == HttpStatus.OK)
						return response.bodyToMono(Item.class).log("client service [EXCHANGE]: ");
					else
						return Mono.error(new RuntimeException("No data found: " + response.statusCode()));
				});
	}

	@Override
	public Mono<Item> deleteItemUsingRetrieve(String id) {
		return webClient.delete().uri("/v1/items/{id}", id).accept(MediaType.APPLICATION_STREAM_JSON).retrieve()
				.onStatus(HttpStatus::is4xxClientError, response -> Mono.error(new RuntimeException("No data found")))
				.bodyToMono(Item.class).log("client service [RETRIVE]: ");
	}
}

package com.reactivestream.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.ArgumentMatchers.anyString;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonAnyFormatVisitor;
import com.reactivestream.domain.Item;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

	private enum using {
		EXCHANGE, RETRIVE
	};

	@Mock
	private WebClient.Builder webClientBuilder;
	@Mock
	private WebClient webClient;
	@Mock
	private WebClient.RequestHeadersUriSpec requestHeaderUriSpec;
	@Mock
	private WebClient.ResponseSpec responseSpec;

	@Mock
	private WebClient.RequestBodyUriSpec requestBodyUriSpec;

	@Mock
	private ClientResponse response;

	// @InjectMocks
	private ItemService service;

	@BeforeEach
	public void setUpWebClient() {

		when(webClientBuilder.baseUrl(anyString())).thenReturn(webClientBuilder);
		when(webClientBuilder.build()).thenReturn(webClient);

		service = new ItemServiceImp(webClientBuilder);
	}

	@Test
	public void getItemsUsingExchange() {

		Flux<Item> expected = Flux.just(new Item("001", "Samsung TV", 400.0), new Item("002", "LG TV", 420.0));

		getItemsConfigure();
		when(requestHeaderUriSpec.exchange()).thenReturn(Mono.just(response));
		when(response.bodyToFlux(Item.class)).thenReturn(expected);

		StepVerifier.create(service.getItemsUsingExchange()).expectSubscription()
				.consumeNextWith(item -> "001".equals(item.getId())).consumeNextWith(item -> "002".equals(item.getId()))
				.verifyComplete();
	}

	@Test
	public void getItemsUsingRetrive() {

		Flux<Item> expected = Flux.just(new Item("001", "Samsung TV", 400.0), new Item("002", "LG TV", 420.0));

		getItemsConfigure();
		when(requestHeaderUriSpec.retrieve()).thenReturn(responseSpec);
		when(responseSpec.bodyToFlux(Item.class)).thenReturn(expected);

		StepVerifier.create(service.getItemsUsingRetrieve()).expectSubscription()
				.consumeNextWith(item -> "001".equals(item.getId())).consumeNextWith(item -> "002".equals(item.getId()))
				.verifyComplete();
	}

	private void getItemsConfigure() {
		when(webClient.get()).thenReturn(requestHeaderUriSpec);
		when(requestHeaderUriSpec.uri("/v1/items")).thenReturn(requestHeaderUriSpec);
		when(requestHeaderUriSpec.accept(MediaType.APPLICATION_STREAM_JSON)).thenReturn(requestHeaderUriSpec);
	}

	@Test
	public void getOneItemUsingExchange() {

		Mono<Item> expected = Mono.just(new Item("001", "Samsung TV", 400.0));

		getOneItemConfigure("001");
		when(requestHeaderUriSpec.exchange()).thenReturn(Mono.just(response));
		when(response.bodyToMono(Item.class)).thenReturn(expected);

		StepVerifier.create(service.getOneItemUsingExchange("001")).expectSubscription()
				.consumeNextWith(item -> "001".equals(item.getId())).verifyComplete();
	}

	@Test
	public void getOneItemUsingRetrive() {

		Mono<Item> expected = Mono.just(new Item("001", "Samsung TV", 400.0));

		getOneItemConfigure("001");
		when(requestHeaderUriSpec.retrieve()).thenReturn(responseSpec);
		when(responseSpec.bodyToMono(Item.class)).thenReturn(expected);

		StepVerifier.create(service.getOneItemUsingRetrieve("001")).expectSubscription()
				.consumeNextWith(item -> "001".equals(item.getId())).verifyComplete();
	}

	@Test
	public void getNoItemUsingExchange() {

		Mono<Item> expected = Mono.empty();

		getOneItemConfigure("001");
		when(requestHeaderUriSpec.exchange()).thenReturn(Mono.just(response));
		when(response.bodyToMono(Item.class)).thenReturn(expected);

		StepVerifier.create(service.getOneItemUsingExchange("001")).expectSubscription().verifyComplete();
	}

	@Test
	public void getNoItemUsingRetrive() {

		Mono<Item> expected = Mono.empty();

		getOneItemConfigure("001");
		when(requestHeaderUriSpec.retrieve()).thenReturn(responseSpec);
		when(responseSpec.bodyToMono(Item.class)).thenReturn(expected);

		StepVerifier.create(service.getOneItemUsingRetrieve("001")).expectSubscription().verifyComplete();
	}

	private void getOneItemConfigure(String id) {
		when(webClient.get()).thenReturn(requestHeaderUriSpec);
		when(requestHeaderUriSpec.uri("/v1/items/{id}", id)).thenReturn(requestHeaderUriSpec);
		when(requestHeaderUriSpec.accept(MediaType.APPLICATION_STREAM_JSON)).thenReturn(requestHeaderUriSpec);
	}

	@Test
	public void editItemUsingExchange() {
		Item sendItem = new Item("001", "Samsung TV", 400.0);
		Mono<Item> expected = Mono.just(sendItem);

		postOneItemConfigure(sendItem);
		when(requestHeaderUriSpec.exchange()).thenReturn(Mono.just(response));
		when(response.statusCode()).thenReturn(HttpStatus.OK);
		when(response.bodyToMono(Item.class)).thenReturn(expected);

		StepVerifier.create(service.editItemUsingExchange(sendItem)).expectSubscription()
				.consumeNextWith(item -> "001".equals(item.getId())).verifyComplete();
	}

	@Test
	public void editItemUsingRetrive() {
		Item sendItem = new Item("001", "Samsung TV", 400.0);
		Mono<Item> expected = Mono.just(sendItem);

		postOneItemConfigure(sendItem);
		when(requestHeaderUriSpec.retrieve()).thenReturn(responseSpec);
		when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
		when(responseSpec.bodyToMono(Item.class)).thenReturn(expected);

		StepVerifier.create(service.editItemUsingRetrieve(sendItem)).expectSubscription()
				.consumeNextWith(item -> "001".equals(item.getId())).verifyComplete();
	}

	@Test
	public void editNoItemUsingExchange() {
		Item sendItem = new Item("001", "Samsung TV", 400.0);
		String expected = "No data found: " + HttpStatus.NOT_FOUND;

		postOneItemConfigure(sendItem);
		when(requestHeaderUriSpec.exchange()).thenReturn(Mono.just(response));
		when(response.statusCode()).thenReturn(HttpStatus.NOT_FOUND);

		StepVerifier.create(service.editItemUsingExchange(sendItem)).expectSubscription().expectErrorMessage(expected)
				.verify();
	}

	@Test
	public void editNoItemUsingRetrive() {
		fail("Cannot mock responseSpec.onStatus when HttpStatus::is4xxClientError");

		Item sendItem = new Item("001", "Samsung TV", 400.0);
		Throwable expected = new RuntimeException("No data found");

		postOneItemConfigure(sendItem);
		when(requestHeaderUriSpec.retrieve()).thenReturn(responseSpec);
		when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec); // <- how mock ?
		when(responseSpec.bodyToMono(Item.class)).thenReturn(Mono.empty());

		StepVerifier.create(service.editItemUsingRetrieve(sendItem)).expectSubscription()
				.expectErrorMessage(expected.getMessage()).verify();
	}

	private void postOneItemConfigure(Item item) {
		when(webClient.post()).thenReturn(requestBodyUriSpec);
		when(requestBodyUriSpec.uri("/v1/items")).thenReturn(requestBodyUriSpec);
		when(requestBodyUriSpec.bodyValue(item)).thenReturn(requestHeaderUriSpec);
		when(requestHeaderUriSpec.header("Content-Type", MediaType.APPLICATION_JSON_VALUE))
				.thenReturn(requestHeaderUriSpec);
		when(requestHeaderUriSpec.accept(MediaType.APPLICATION_STREAM_JSON)).thenReturn(requestHeaderUriSpec);
	}

	@Test
	public void deleteItemUsingExchange() {
		String sendId = "001";
		Mono<Item> expected = Mono.empty();

		deleteOneItemConfigure(sendId);
		when(requestHeaderUriSpec.exchange()).thenReturn(Mono.just(response));
		when(response.statusCode()).thenReturn(HttpStatus.OK);
		when(response.bodyToMono(Item.class)).thenReturn(expected);

		StepVerifier.create(service.deleteItemUsingExchange(sendId)).expectSubscription().verifyComplete();
	}

	@Test
	public void deleteItemUsingRetrive() {
		String sendId = "001";
		Mono<Item> expected = Mono.empty();

		deleteOneItemConfigure(sendId);
		when(requestHeaderUriSpec.retrieve()).thenReturn(responseSpec);
		when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
		when(responseSpec.bodyToMono(Item.class)).thenReturn(expected);

		StepVerifier.create(service.deleteItemUsingRetrieve(sendId)).expectSubscription().verifyComplete();
	}

	@Test
	public void deleteNoItemUsingExchange() {
		String sendId = "001";
		String expected = "No data found: " + HttpStatus.NOT_FOUND;

		deleteOneItemConfigure(sendId);
		when(requestHeaderUriSpec.exchange()).thenReturn(Mono.just(response));
		when(response.statusCode()).thenReturn(HttpStatus.NOT_FOUND);

		StepVerifier.create(service.deleteItemUsingExchange(sendId)).expectSubscription()
				.expectErrorMessage(expected).verify();
	}

	@Test
	public void deleteNoItemUsingRetrive() {
		fail("Cannot mock responseSpec.onStatus when HttpStatus::is4xxClientError");
		String sendId = "001";
		Throwable expected = new RuntimeException("No data found");

		deleteOneItemConfigure(sendId);
		when(requestHeaderUriSpec.retrieve()).thenReturn(responseSpec);
		when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec); // <- how to mock?
		when(responseSpec.bodyToMono(Item.class)).thenReturn(Mono.empty());

		StepVerifier.create(service.deleteItemUsingRetrieve(sendId)).expectSubscription()
				.expectErrorMessage(expected.getMessage()).verify();
	}

	private void deleteOneItemConfigure(String id) {
		when(webClient.delete()).thenReturn(requestHeaderUriSpec);
		when(requestHeaderUriSpec.uri("/v1/items/{id}", id)).thenReturn(requestHeaderUriSpec);
		when(requestHeaderUriSpec.accept(MediaType.APPLICATION_STREAM_JSON)).thenReturn(requestHeaderUriSpec);
	}
}

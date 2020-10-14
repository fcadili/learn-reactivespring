package com.reactivestream.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.reactivestream.domain.Item;
import com.reactivestream.service.ItemService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/client", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
public class ItemClientController {

	@Autowired
	private ItemService service;

	@GetMapping("/retrive")
	Flux<Item> getItemsUsingExchange() {
		return service.getItemsUsingExchange();
	}

	@GetMapping("/retrive2")
	Flux<Item> getItemsUsingRetrive() {
		return service.getItemsUsingRetrieve();
	}

	@GetMapping("/retriveOne/{id}")
	Mono<ResponseEntity<Item>> getOneItemUsingExchange(@PathVariable String id) {
		return service.getOneItemUsingExchange(id).map(item -> new ResponseEntity<Item>(item, HttpStatus.OK))
				.defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@GetMapping("/retriveOne2/{id}")
	Mono<ResponseEntity<Item>> getOneItemUsingRetrive(@PathVariable String id) {
		return service.getOneItemUsingRetrieve(id).map(item -> new ResponseEntity<Item>(item, HttpStatus.OK))
				.defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}
	
	@PostMapping("/edit")
	Mono<ResponseEntity<Item>> editItemUsingExchange(@RequestBody Item ItemPosted) {
		return service.editItemUsingExchange(ItemPosted).map(item -> new ResponseEntity<Item>(item, HttpStatus.OK))
				.defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}
	
	@PostMapping("/edit2")
	Mono<ResponseEntity<Item>> editItemUsingRetrive(@RequestBody Item ItemPosted) {
		return service.editItemUsingRetrieve(ItemPosted).map(item -> new ResponseEntity<Item>(item, HttpStatus.OK))
				.defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}
	
	@PostMapping("/delete/{id}")
	Mono<ResponseEntity<Item>> deleteItemUsingExchange(@PathVariable String id) {
		return service.deleteItemUsingExchange(id).map(item -> new ResponseEntity<Item>(item, HttpStatus.OK))
				.defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}
	
	@PostMapping("/delete2/{id}")
	Mono<ResponseEntity<Item>> deleteItemUsingRetrieve(@PathVariable String id) {
		return service.deleteItemUsingRetrieve(id).map(item -> new ResponseEntity<Item>(item, HttpStatus.OK))
				.defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}
}

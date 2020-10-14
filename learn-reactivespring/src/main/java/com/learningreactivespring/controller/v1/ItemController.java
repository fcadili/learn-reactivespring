package com.learningreactivespring.controller.v1;

import static com.learningreactivespring.constants.ItemConstants.EXCEPTION_END_POINT;
import static com.learningreactivespring.constants.ItemConstants.ITEMS_END_POINT;
import static com.learningreactivespring.constants.ItemConstants.V1;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.learningreactivespring.document.Item;
import com.learningreactivespring.repository.ItemReactiveRepository;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
@RequestMapping(value = V1, produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
public class ItemController {

	@Autowired
	private ItemReactiveRepository repository;
	
//	Use global: ControllerExceptionHandler
//	/**
//	 * Override the default exception returned by EXCEPTION_END_POINT + "2"
//	 * 
//	 * @param ex
//	 * @return
//	 */
//	@ExceptionHandler
//	public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
//		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
//	}

	@GetMapping(ITEMS_END_POINT)
	public Flux<Item> getAllItems(@RequestParam(value = "desc", required = false) Optional<String> description) {
		Flux<Item> items;

		if (description.isEmpty()) {
			items = repository.findAll();
		} else {
			items = repository.findByDescriptionLike(description.get());
		}
		return items;
	}

	@GetMapping(ITEMS_END_POINT + "/{id}")
	public Mono<ResponseEntity<Item>> getItem(@PathVariable String id) {
		return repository.findById(id).map(item -> new ResponseEntity<Item>(item, HttpStatus.OK))
				.defaultIfEmpty(new ResponseEntity<Item>(HttpStatus.NOT_FOUND));
	}

	@PostMapping(ITEMS_END_POINT)
	@ResponseStatus(HttpStatus.CREATED)
	public Mono<Item> postItem(@RequestBody Item item) {
		return repository.save(item);
	}

//	@PutMapping(ITEMS_END_POINT)
//	public Mono<ResponseEntity<Item>> updateItem(@RequestBody Item item) {
//		return repository.findById(item.getId())
//				.flatMap(i0 -> repository.save(item).map(i1 -> new ResponseEntity<Item>(i1, HttpStatus.OK)))
//				.defaultIfEmpty(new ResponseEntity<Item>(HttpStatus.NOT_FOUND));
//	}

	@PutMapping(ITEMS_END_POINT)
	public Mono<ResponseEntity<Item>> updateItem(@RequestBody Item item) {
		return repository.findById(item.getId()).flatMap(oldItem -> repository.save(item))
				.map(i -> new ResponseEntity<Item>(i, HttpStatus.OK))
				.defaultIfEmpty(new ResponseEntity<Item>(HttpStatus.NOT_FOUND));
	}

	@DeleteMapping(ITEMS_END_POINT + "/{id}")
	public Mono<ResponseEntity<Void>> deleteItem(@PathVariable String id) {
		return repository.findById(id).flatMap(item -> {
			repository.delete(item);
			return Mono.just(new ResponseEntity<Void>(HttpStatus.OK));
		}).defaultIfEmpty(new ResponseEntity<Void>(HttpStatus.NOT_FOUND));
	}
	
	@GetMapping(EXCEPTION_END_POINT)
	public Flux<Item> runtimeException() {
		return repository.findAll().concatWith(Mono.error(new RuntimeException("Runtime exception occurred")));
	}
	
	@GetMapping(EXCEPTION_END_POINT + "2")
	public Mono<Item> runtimeException2() {
		return Mono.error(new RuntimeException("Runtime exception occurred"));
	}
}

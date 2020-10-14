package com.learningreactivespring.controller.v1;

import static com.learningreactivespring.constants.ItemConstants.V1_CAPPED;
import static com.learningreactivespring.constants.ItemConstants.ITEMS_END_POINT;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.learningreactivespring.document.ItemCapped;
import com.learningreactivespring.repository.ItemReactiveCappedRepository;

import reactor.core.publisher.Flux;

@RestController
@RequestMapping(value = V1_CAPPED, produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
public class ItemCappedController {

	@Autowired
	private ItemReactiveCappedRepository repository;

	@GetMapping(ITEMS_END_POINT)
	public Flux<ItemCapped> getAll() {
		return repository.findItemsBy();
	}
}

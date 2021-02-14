package com.store.demo.controller;

import com.store.demo.dto.ResourceIdDto;
import com.store.demo.dto.SliceCollection;
import com.store.demo.exception.ResourceNotFoundException;
import com.store.demo.model.Order;
import com.store.demo.service.OrderService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Validated
@RestController
@RequestMapping("/api/orders")
public class OrderController extends AbstractController
{
	private final OrderService orderService;

	public OrderController(final OrderService orderService)
	{
		this.orderService = orderService;
	}

	@GetMapping
	public HttpEntity<SliceCollection<Order>> getOrders(@PageableDefault(size = 20) Pageable pageable)
	{
		final Slice<Order> orders = orderService.getAll(pageable);
		return ResponseEntity.ok(SliceCollection.of(orders));
	}

	@GetMapping(value = "/{orderId}/products/{productId}")
	public ResponseEntity<Order> getByOrderIdAndProductId(@PathVariable final String orderId, @PathVariable final String productId)
	{
		final Optional<Order> dto = orderService.getByOrderIdAndProductId(orderId, productId);
		return dto.map(body -> ResponseEntity.ok().body(body)).orElseThrow(() -> new ResourceNotFoundException(orderId));
	}

	@GetMapping(value = "/{orderId}")
	public ResponseEntity<List<Order>> getByOrderId(@PathVariable final String orderId)
	{
		final List<Order> orders = orderService.getByOrderId(orderId);
		return ResponseEntity.ok().body(orders);
	}

	@GetMapping(value = "/streaming")
	public ResponseEntity<Void> initializeStructuredStreaming()
	{
		orderService.initializeStructuredStreaming();
		return ResponseEntity.ok().build();
	}

	@DeleteMapping(value = "/{orderId}")
	public ResponseEntity<Void> deleteByOrderId(@PathVariable final String orderId)
	{
		orderService.deleteByOrderId(orderId);
		return ResponseEntity.noContent().build();
	}

	@PostMapping
	public ResponseEntity<ResourceIdDto> create(@RequestBody @Valid final Order dto)
	{
		final String id = orderService.create(dto);
		return ResponseEntity.ok(ResourceIdDto.of(id));
	}
}
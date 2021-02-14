package com.store.demo.service;

import com.store.demo.model.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Optional;

public interface OrderService
{
	/**
	 * Returns the requested page of orders
	 *
	 * @param pageable the page request criteria.
	 * @return the requested orders page
	 */
	Slice<Order> getAll(Pageable pageable);

	/**
	 * Retrieves an order with provided order id and product id.
	 *
	 * @param orderId   the order identifier.
	 * @param productId the product identifier.
	 * @return the requested order, or {@link Optional#empty()} if the resource is not found.
	 */
	Optional<Order> getByOrderIdAndProductId(String orderId, String productId);

	/**
	 * Retrieves orders with provided order id.
	 *
	 * @param orderId the order identifier.
	 * @return the requested order list.
	 */
	List<Order> getByOrderId(String orderId);

	/**
	 * Deletes orders with provided order id.
	 *
	 * @param orderId the order identifier.
	 * @return the requested order list.
	 */
	void deleteByOrderId(String orderId);

	/**
	 * Creates a new order.
	 *
	 * @param order
	 * @return the id of the order created.
	 */
	String create(Order order);

	void initializeStructuredStreaming();
}

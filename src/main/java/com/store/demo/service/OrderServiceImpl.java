package com.store.demo.service;

import com.store.demo.model.Order;
import com.store.demo.model.OrderPrimaryKey;
import com.store.demo.repository.OrderRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService
{

	private OrderRepository orderRepository;

	public OrderServiceImpl(final OrderRepository orderRepository)
	{
		this.orderRepository = orderRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public Slice<Order> getAll(final Pageable pageable)
	{
		return orderRepository.findAll(pageable);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Order> getByOrderIdAndProductId(final String orderId, final String productId)
	{
		return orderRepository.findById(
				OrderPrimaryKey.builder().orderId(UUID.fromString(orderId)).productId(UUID.fromString(productId)).build());
	}

	@Override
	public List<Order> getByOrderId(final String orderId)
	{
		return orderRepository.findProductNamesAndPricesFromOrder(UUID.fromString(orderId));
	}

	@Override
	public void deleteByOrderId(final String orderId)
	{
		orderRepository.deleteByKeyOrderId(UUID.fromString(orderId));
	}

	@Override
	@Transactional
	public String create(final Order order)
	{
		return orderRepository.save(order).getKey().getOrderId().toString();
	}
}
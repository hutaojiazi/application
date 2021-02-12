package com.store.demo.repository;

import com.store.demo.model.Order;
import com.store.demo.model.OrderPrimaryKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends CassandraRepository<Order, OrderPrimaryKey>
{
	void deleteByKeyOrderId(final UUID orderId);

	@Query("SELECT product_name, product_price FROM orders WHERE order_id = :orderId")
	List<Order> findProductNamesAndPricesFromOrder(@Param("orderId") final UUID orderId);

	List<Order> findByKeyOrderId(final UUID orderId);

	@Query(allowFiltering = true)
	List<Order> findByProductName(final String productName);

}
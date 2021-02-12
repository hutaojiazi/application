package com.store.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.io.Serializable;
import java.time.Instant;

@Table(value = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order implements Serializable
{

	@PrimaryKey
	private OrderPrimaryKey key;

	@Column("product_quantity")
	@CassandraType(type = CassandraType.Name.INT)
	private Integer productQuantity;

	@Column("product_name")
	@CassandraType(type = CassandraType.Name.TEXT)
	private String productName;

	@CassandraType(type = CassandraType.Name.DECIMAL)
	@Column("product_price")
	private Float productPrice;

	@CassandraType(type = CassandraType.Name.TIMESTAMP)
	@Column("added_to_order_at")
	private Instant addedToOrderTimestamp;
}

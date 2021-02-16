package com.store.demo.service;

import com.store.demo.messaging.dto.DatasetKafkaMessageDto;
import com.store.demo.messaging.dto.OrderCreatedMessageDto;
import com.store.demo.model.Order;
import com.store.demo.model.OrderPrimaryKey;
import com.store.demo.repository.OrderRepository;
import com.store.demo.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.catalyst.encoders.ExpressionEncoder;
import org.apache.spark.sql.streaming.OutputMode;
import org.apache.spark.sql.streaming.Trigger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.store.demo.util.Constants.KAFKA_TOPIC;
import static com.store.demo.util.Constants.MESSAGE_ID;
import static com.store.demo.util.Constants.MESSAGE_KEY;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService
{
	@Value("${spring.kafka.bootstrap-servers}")
	private String bootstrapServers;

	private final OrderRepository orderRepository;
	private final KafkaTemplate<String, String> kafkaTemplate;
	private final SparkSession sparkSession;

	public OrderServiceImpl(final OrderRepository orderRepository, final KafkaTemplate<String, String> kafkaTemplate,
			final SparkSession sparkSession)
	{
		this.orderRepository = orderRepository;
		this.kafkaTemplate = kafkaTemplate;
		this.sparkSession = sparkSession;
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
		final String orderId = orderRepository.save(order).getKey().getOrderId().toString();
		final OrderCreatedMessageDto payload = OrderCreatedMessageDto.builder()
				.orderId(orderId)
				.message("A new order has been placed")
				.build();

		final MessageBuilder<OrderCreatedMessageDto> messageBuilder = MessageBuilder.withPayload(payload);

		messageBuilder.setHeader(MESSAGE_ID, UUID.randomUUID().toString())
				.setHeader(KafkaHeaders.TOPIC, KAFKA_TOPIC)
				.setHeader(KafkaHeaders.MESSAGE_KEY, MESSAGE_KEY)
				.setHeader(KafkaHeaders.TIMESTAMP, OffsetDateTime.now().toInstant().toEpochMilli());

		log.info("Sending kafka message with payload: " + payload);

		kafkaTemplate.send(messageBuilder.build());

		return orderId;
	}

	@Override
	public void initializeStructuredStreaming()
	{
		final Dataset<DatasetKafkaMessageDto> orders = sparkSession.readStream()
				.format("kafka")
				.option("kafka.bootstrap.servers", bootstrapServers)
				.option("subscribe", Constants.KAFKA_TOPIC)
				.option("includeHeaders", "true")
				.load()
				.selectExpr("CAST(topic AS STRING)", "CAST(key AS STRING)", "CAST(value AS STRING)", "CAST(timestamp AS STRING)")
				.as(ExpressionEncoder.javaBean(DatasetKafkaMessageDto.class));

		try
		{
			orders.writeStream()
					.outputMode(OutputMode.Append())
					.format("console")
					.trigger(Trigger.ProcessingTime(2, TimeUnit.SECONDS))
					.option("truncate", "false")
					.start()
					.awaitTermination();

			orders.writeStream()
					.format("kafka")
					.option("kafka.bootstrap.servers", bootstrapServers)
					.option("topic", Constants.KAFKA_TOPIC_PROCESSED)
					.option("checkpointLocation", "/path/checkpointLocation")
					.start()
					.awaitTermination();
		}
		catch (final Exception e)
		{
			log.warn("Error occurred when initialize structured streaming.", e);
		}
	}
}
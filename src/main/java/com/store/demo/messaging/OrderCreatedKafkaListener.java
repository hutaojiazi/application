package com.store.demo.messaging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.store.demo.util.Constants.KAFKA_TOPIC;
import static com.store.demo.util.Constants.MESSAGE_ID;

@Slf4j
@Component
public class OrderCreatedKafkaListener
{
	@Transactional
	@KafkaListener(topics = KAFKA_TOPIC)
	public void process(@Payload final Message<String> message, @Header(MESSAGE_ID) final String messageId)
	{
		log.info("Received kafka message: {}", message);
	}
}

package com.store.demo.messaging.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DatasetKafkaMessageDto
{
	private String topic;
	private String key;
	private String value;
	private String timestamp;
}

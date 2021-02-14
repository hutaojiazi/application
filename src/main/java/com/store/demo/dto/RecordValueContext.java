package com.store.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecordValueContext implements Serializable
{
	private String symbol;
	private String companyName;
	private Double value;
	private Integer shareVolume;
}
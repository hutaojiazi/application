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
public class StockOverview implements Serializable
{
	private String symbol;
	private String companyName;
	private Double thirtyDayLow;
	private Double thirtyDayHigh;
	private Integer thirtyDayAverageVolume;
}

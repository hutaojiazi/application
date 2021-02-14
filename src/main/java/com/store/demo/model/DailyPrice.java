package com.store.demo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyPrice implements Serializable
{
	private String symbol;
	private int year;
	private int month;
	private int day;
	private double value;
	private double previousValue;
	private double valueChange;
	private double percentChange;
	private int shareVolume;

	public LocalDate getDate()
	{
		return LocalDate.of(this.year, this.month + 1, this.day);
	}
}

package com.store.demo.service;

import com.store.demo.dto.DailyPriceRecord;
import com.store.demo.repository.StockRecordRepository;
import org.apache.spark.api.java.JavaSparkContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockRecordService
{
	private JavaSparkContext javaSparkContext;
	private StockRecordRepository recordRepository;

	@Autowired
	public StockRecordService(final JavaSparkContext javaSparkContext, final StockRecordRepository recordRepository)
	{
		this.javaSparkContext = javaSparkContext;
		this.recordRepository = recordRepository;
	}

	public void save(List<DailyPriceRecord> records)
	{
		recordRepository.save(javaSparkContext, records);
	}
}

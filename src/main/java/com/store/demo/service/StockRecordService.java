package com.store.demo.service;

import com.store.demo.dto.Company;
import com.store.demo.dto.DailyPriceRecord;
import com.store.demo.repository.StockRecordRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockRecordService
{
	private StockRecordRepository recordRepository;

	public StockRecordService(final StockRecordRepository recordRepository)
	{
		this.recordRepository = recordRepository;
	}

	public List<Company> getCompanies()
	{
		return recordRepository.getCompanies();
	}

	public void save(List<DailyPriceRecord> records)
	{
		recordRepository.save(records);
	}
}

package com.store.demo.service;

import com.store.demo.model.Company;
import com.store.demo.model.DailyPrice;
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

	public void save(List<DailyPrice> records)
	{
		recordRepository.save(records);
	}
}

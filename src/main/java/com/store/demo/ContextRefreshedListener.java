package com.store.demo;

import com.store.demo.service.ImportStockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ContextRefreshedListener implements ApplicationListener<ContextRefreshedEvent>
{
	private ImportStockService importStockService;

	public ContextRefreshedListener(final ImportStockService importStockService)
	{
		this.importStockService = importStockService;
	}

	@Override
	public void onApplicationEvent(final ContextRefreshedEvent event)
	{
		log.info("Time to ingest");
		importStockService.importFromCsv("ticker.csv");
	}
}

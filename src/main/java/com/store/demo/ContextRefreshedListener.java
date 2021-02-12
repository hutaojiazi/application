package com.store.demo;

import com.store.demo.service.RecordIngestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ContextRefreshedListener implements ApplicationListener<ContextRefreshedEvent>
{
	private RecordIngestionService recordIngestionService;

	public ContextRefreshedListener(final RecordIngestionService recordIngestionService)
	{
		this.recordIngestionService = recordIngestionService;
	}

	@Override
	public void onApplicationEvent(final ContextRefreshedEvent event)
	{
		log.info("Time to ingest");
		recordIngestionService.ingestFromCsv("ticker.csv");
	}
}

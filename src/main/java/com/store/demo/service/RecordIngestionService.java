package com.store.demo.service;

import com.store.demo.dto.DailyPriceRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RecordIngestionService
{
	private StockRecordService stockRecordService;

	public RecordIngestionService(final StockRecordService recordService)
	{
		this.stockRecordService = recordService;
	}

	public void ingestFromCsv(String filename)
	{
		try
		{
			log.info("URI = {}", ClassLoader.getSystemResource(filename).toURI());

			final List<DailyPriceRecord> lines = Files.lines(Paths.get(ClassLoader.getSystemResource(filename).toURI())).map(line -> {
				try
				{
					return DailyPriceRecord.buildFromFileRow(line);
				}
				catch (ParseException exception)
				{
					log.warn("Error occurred while parsing the record.", exception);
					return null;
				}
			}).collect(Collectors.toList());

			log.info("Received {} lines", lines.size());
			stockRecordService.save(lines);

		}
		catch (IOException e)
		{
			log.error("Could not find file {}", filename, e);
		}
		catch (URISyntaxException e)
		{
			e.printStackTrace();
		}
	}
}

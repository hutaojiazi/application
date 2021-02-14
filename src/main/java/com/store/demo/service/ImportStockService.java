package com.store.demo.service;

import com.store.demo.model.DailyPrice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ImportStockService
{
	private static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd";

	private StockRecordService stockRecordService;

	public ImportStockService(final StockRecordService recordService)
	{
		this.stockRecordService = recordService;
	}

	public void importFromCsv(String filename)
	{
		try
		{
			final List<DailyPrice> lines = Files.lines(Paths.get(ClassLoader.getSystemResource(filename).toURI()))
					.map(line -> buildDailyPrice(line))
					.collect(Collectors.toList());

			stockRecordService.save(lines);
		}
		catch (IOException e)
		{
			log.error("Could not find file {}", filename, e);
		}
		catch (URISyntaxException e)
		{
			log.error("Invalid file location {}", filename, e);
		}
	}

	private DailyPrice buildDailyPrice(final String fileRow)
	{
		final String[] tokens = fileRow.split("\\|");
		assert tokens.length == 7;

		final LocalDate date = LocalDate.from(DateTimeFormatter.ofPattern(DATE_FORMAT_PATTERN).parse(tokens[1]));

		return DailyPrice.builder()
				.symbol(tokens[0])
				.year(date.getYear())
				.month(date.getMonthValue())
				.day(date.getDayOfMonth())
				.value(Double.parseDouble(tokens[2]))
				.previousValue(Double.parseDouble(tokens[3]))
				.valueChange(Double.parseDouble(tokens[4]))
				.percentChange(Double.parseDouble(tokens[5]))
				.shareVolume(Integer.parseInt(tokens[6]))
				.build();
	}
}

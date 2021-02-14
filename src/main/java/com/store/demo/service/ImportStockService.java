package com.store.demo.service;

import com.store.demo.model.DailyPrice;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ImportStockService
{
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
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
					.map(line -> buildFromFileRow(line))
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

	private DailyPrice buildFromFileRow(final String fileRow)
	{
		final String[] tokens = fileRow.split("\\|");
		assert tokens.length == 7;

		try
		{
			final Date recordedDate = dateFormat.parse(tokens[1]);
			final Calendar cal = DateUtils.toCalendar(recordedDate);

			return DailyPrice.builder()
					.symbol(tokens[0])
					.year(cal.get(Calendar.YEAR))
					.month(cal.get(Calendar.MONTH))
					.day(cal.get(Calendar.DAY_OF_MONTH))
					.value(Double.parseDouble(tokens[2]))
					.previousValue(Double.parseDouble(tokens[3]))
					.valueChange(Double.parseDouble(tokens[4]))
					.percentChange(Double.parseDouble(tokens[5]))
					.shareVolume(Integer.parseInt(tokens[6]))
					.build();
		}
		catch (ParseException exception)
		{
			log.warn("Error occurred while parsing the record.", exception);

		}
		return null;
	}
}

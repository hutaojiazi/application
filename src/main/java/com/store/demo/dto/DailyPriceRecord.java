package com.store.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.time.DateUtils;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyPriceRecord implements Serializable
{
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	private String symbol;
	//private Date recordedDate;
	private double value;
	private double previousValue;
	private double valueChange;
	private double percentChange;
	private int shareVolume;

	private int year;
	private int month;
	private int day;

	public static DailyPriceRecord buildFromFileRow(final String fileRow) throws ParseException
	{
		final String[] tokens = fileRow.split("\\|");
		assert tokens.length == 7;

		final Date recordedDate = dateFormat.parse(tokens[1]);
		final Calendar cal = DateUtils.toCalendar(recordedDate);

		return DailyPriceRecord.builder()
				.symbol(tokens[0])
				//.recordedDate(recordedDate)
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

	public LocalDate getDate()
	{
		return LocalDate.of(this.year, this.month + 1, this.day);
	}
}

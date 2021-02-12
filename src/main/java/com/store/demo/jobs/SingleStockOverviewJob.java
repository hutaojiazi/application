package com.store.demo.jobs;

import com.store.demo.dto.Company;
import com.store.demo.dto.DailyPriceRecord;
import com.store.demo.dto.StockOverview;
import com.store.demo.dto.StockQuery;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import static com.datastax.spark.connector.japi.CassandraJavaUtil.javaFunctions;
import static com.datastax.spark.connector.japi.CassandraJavaUtil.mapRowTo;

public class SingleStockOverviewJob
{
	private static final String ORDER_DEMO = "order_demo";

	/**
	 * Calculates an Overview of a given stock symbol, for specified period.
	 *
	 * @param context
	 * @param stockQuery
	 * @return
	 */
	public StockOverview execute(final JavaSparkContext context, final StockQuery stockQuery)
	{
		final StockOverview overview = new StockOverview();
		overview.setSymbol(stockQuery.getSymbol());

		final JavaRDD<Company> companies = javaFunctions(context).cassandraTable(ORDER_DEMO, "company", mapRowTo(Company.class));

		final JavaRDD<DailyPriceRecord> symbolResults = javaFunctions(context).cassandraTable(ORDER_DEMO, "daily_price_record",
				mapRowTo(DailyPriceRecord.class))
				.where("symbol = ? and year = ?", stockQuery.getSymbol(), stockQuery.getStart().getYear())
				.filter(record -> (stockQuery.getStart().compareTo(record.getDate()) > 0))
				.cache();

		final JavaRDD<Double> rawValues = symbolResults.map(DailyPriceRecord::getValue).cache();
		overview.setThirtyDayHigh(rawValues.reduce((c1, c2) -> c1 > c2 ? c1 : c2));
		overview.setThirtyDayLow(rawValues.reduce((c1, c2) -> c1 < c2 ? c1 : c2));

		final int[] result = symbolResults.aggregate(new int[2], (arr, record) -> {
			arr[0] += record.getShareVolume();
			arr[1] += 1;
			return arr;
		}, (arr1, arr2) -> {
			final int[] combine = new int[2];
			combine[0] = arr1[0] + arr2[0];
			combine[1] = arr1[1] + arr2[1];
			return combine;
		});

		overview.setThirtyDayAverageVolume(result[0] / result[1]);

		return overview;
	}
}

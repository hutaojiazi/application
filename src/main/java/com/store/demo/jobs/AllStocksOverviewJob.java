package com.store.demo.jobs;

import com.store.demo.dto.RecordValueContext;
import com.store.demo.dto.StockOverview;
import com.store.demo.dto.StockQuery;
import com.store.demo.model.Company;
import com.store.demo.model.DailyPrice;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

import java.util.List;

import static com.datastax.spark.connector.japi.CassandraJavaUtil.javaFunctions;
import static com.datastax.spark.connector.japi.CassandraJavaUtil.mapRowTo;

public class AllStocksOverviewJob
{
	private static final String ORDER_DEMO = "order_demo";

	public List<StockOverview> execute(final JavaSparkContext context, final StockQuery stockQuery)
	{
		final JavaPairRDD<String, Company> companies = javaFunctions(context).cassandraTable(ORDER_DEMO, "company",
				mapRowTo(Company.class)).mapToPair(company -> new Tuple2<>(company.getSymbol(), company));

		final JavaRDD<DailyPrice> symbolResults = javaFunctions(context).cassandraTable(ORDER_DEMO, "daily_price_record",
				mapRowTo(DailyPrice.class)).filter(record -> (stockQuery.getStart().compareTo(record.getDate()) > 0)).cache();

		final JavaPairRDD<String, DailyPrice> keyedRecords = symbolResults.mapToPair(
				record -> new Tuple2<>(record.getSymbol(), record));

		final JavaPairRDD<String, RecordValueContext> values = keyedRecords.join(companies).mapValues(joinedTuple -> {
			final DailyPrice current = joinedTuple._1();
			final Company company = joinedTuple._2();
			return new RecordValueContext(current.getSymbol(), company.getName(), current.getValue(), current.getShareVolume());
		}).cache();

		final JavaPairRDD<String, RecordValueContext> thirtyDayHighs = values.reduceByKey(
				(context1, context2) -> context1.getValue() > context2.getValue() ? context1 : context2);

		final JavaPairRDD<String, RecordValueContext> thirtyDayLows = values.reduceByKey(
				(context1, context2) -> context1.getValue() < context2.getValue() ? context1 : context2);

		final JavaPairRDD<String, int[]> countAndTotalValue = values.aggregateByKey(new int[2], (arr, record) -> {
			arr[0] += record.getShareVolume();
			arr[1] += 1;
			return arr;
		}, (arr1, arr2) -> {
			final int[] combined = new int[2];
			combined[0] = arr1[0] + arr2[0];
			combined[1] = arr1[1] + arr2[1];
			return combined;
		});

		final JavaRDD<StockOverview> finalResults = thirtyDayHighs.join(thirtyDayLows)
				.mapValues(highLowTuple -> StockOverview.builder()
						.companyName(highLowTuple._1.getCompanyName())
						.symbol(highLowTuple._1.getSymbol())
						.thirtyDayHigh(highLowTuple._1.getValue())
						.thirtyDayLow(highLowTuple._2().getValue())
						.build())
				.join(countAndTotalValue)
				.mapValues(joinedTuple -> {
					final StockOverview overview = joinedTuple._1;
					final int[] arr = joinedTuple._2;
					overview.setThirtyDayAverageVolume(arr[0] / arr[1]);
					return overview;
				})
				.map(tuple -> tuple._2);

		return finalResults.collect();
	}
}

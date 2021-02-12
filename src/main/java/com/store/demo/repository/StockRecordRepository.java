package com.store.demo.repository;

import com.store.demo.dto.DailyPriceRecord;
import org.apache.spark.api.java.JavaSparkContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.datastax.spark.connector.japi.CassandraJavaUtil.javaFunctions;
import static com.datastax.spark.connector.japi.CassandraJavaUtil.mapToRow;

@Repository
public class StockRecordRepository
{
	@Value("${spring.data.cassandra.keyspace:order_demo}")
	private String keySpace;

	public StockRecordRepository()
	{
		// empty constructor
	}

	public void save(JavaSparkContext context, List<DailyPriceRecord> records)
	{
		javaFunctions(context.parallelize(records)).writerBuilder(keySpace, "daily_price_record", mapToRow(DailyPriceRecord.class))
				.saveToCassandra();
	}
}

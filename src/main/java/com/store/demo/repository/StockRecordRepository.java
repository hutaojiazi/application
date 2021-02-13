package com.store.demo.repository;

import com.datastax.spark.connector.CassandraSparkExtensions;
import com.store.demo.dto.Company;
import com.store.demo.dto.DailyPriceRecord;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
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

	private final JavaSparkContext sparkContext;
	private final SparkSession sparkSession;

	public StockRecordRepository(final JavaSparkContext sparkContext)
	{
		this.sparkContext = sparkContext;
		this.sparkSession = SparkSession.builder()
				.config(sparkContext.getConf())
				.withExtensions(new CassandraSparkExtensions())
				.getOrCreate();
	}

	public List<Company> getCompanies()
	{
		final Dataset<Row> dataset = sparkSession.sql("SELECT symbol, name, address FROM order_demo.daily_price_record");
		final List<Company> list = dataset.as(Encoders.bean(Company.class)).collectAsList();
		return list;
	}

	public void save(final List<DailyPriceRecord> records)
	{
		javaFunctions(sparkContext.parallelize(records)).writerBuilder(keySpace, "daily_price_record",
				mapToRow(DailyPriceRecord.class)).saveToCassandra();
	}
}

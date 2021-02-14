package com.store.demo.repository;

import com.store.demo.model.Company;
import com.store.demo.model.DailyPrice;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static com.datastax.spark.connector.japi.CassandraJavaUtil.javaFunctions;
import static com.datastax.spark.connector.japi.CassandraJavaUtil.mapRowTo;
import static com.datastax.spark.connector.japi.CassandraJavaUtil.mapToRow;
import static com.store.demo.model.definition.DatabaseDefinition.FORMAT;
import static com.store.demo.model.definition.DatabaseDefinition.KEY_SPACE;
import static com.store.demo.model.definition.DatabaseDefinition.KeySpace.ORDER_DEMO;
import static com.store.demo.model.definition.DatabaseDefinition.TABLE;
import static com.store.demo.model.definition.DatabaseDefinition.Table.COMPANY;
import static com.store.demo.model.definition.DatabaseDefinition.Table.DAILY_PRICE_RECORD;

@Repository
public class StockRecordRepository
{
	private final JavaSparkContext sparkContext;
	private final SparkSession sparkSession;

	public StockRecordRepository(final JavaSparkContext sparkContext, final SparkSession sparkSession)
	{
		this.sparkContext = sparkContext;
		this.sparkSession = sparkSession;
	}

	public List<Company> getCompanies()
	{
//		final JavaRDD<Company> list = javaFunctions(sparkContext).cassandraTable(ORDER_DEMO, COMPANY, mapRowTo(Company.class));
//		return list.collect();
		final Dataset<Row> dataset = sparkSession.read().format(FORMAT).options(Map.of(KEY_SPACE, ORDER_DEMO, TABLE, COMPANY)).load();
		return dataset.as(Encoders.bean(Company.class)).collectAsList();
	}

	public void save(final List<DailyPrice> records)
	{
		javaFunctions(sparkContext.parallelize(records)).writerBuilder(ORDER_DEMO, DAILY_PRICE_RECORD, mapToRow(DailyPrice.class))
				.saveToCassandra();
//		final Dataset<DailyPrice> dataset = sparkSession.createDataset(records, Encoders.bean(DailyPrice.class));
//		dataset.write()
//				.mode(SaveMode.Overwrite)
//				.format(FORMAT)
//				.options(Map.of(KEY_SPACE, ORDER_DEMO, TABLE, DAILY_PRICE_RECORD))
//				.save();
	}
}

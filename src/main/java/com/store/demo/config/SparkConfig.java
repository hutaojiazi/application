package com.store.demo.config;

import com.datastax.spark.connector.CassandraSparkExtensions;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SparkConfig
{
	@Value("${spark.master}")
	private String sparkMaster;

	@Value("${spark.appName}")
	private String sparkAppName;

	@Value("${spring.data.cassandra.contact-points:localhost}")
	private String contactPoints;

	@Value("${spring.data.cassandra.port:0000}")
	private int port;

	@Bean
	public JavaSparkContext sparkContext()
	{
		final SparkConf conf = new SparkConf().set("spark.cassandra.connection.host", contactPoints)
				.set("spark.cassandra.connection.port", String.valueOf(port))
				.set("confirm.truncate", "true")
				.set("spark.submit.deployMode", "client");

		final JavaSparkContext context = new JavaSparkContext(sparkMaster, sparkAppName, conf);
		//context.addJar("/jobs/spark-shared.jar");

		return context;
	}

	@Bean
	public SparkSession sparkSession()
	{
		final SparkConf conf = new SparkConf().set("spark.cassandra.connection.host", contactPoints)
				.set("spark.cassandra.connection.port", String.valueOf(port))
				.set("confirm.truncate", "true")
				.set("spark.submit.deployMode", "client");
		return SparkSession.builder()
				.config(conf)
				.appName(sparkAppName)
				.master(sparkMaster)
				.withExtensions(new CassandraSparkExtensions())
				.getOrCreate();
	}
}

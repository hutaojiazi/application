package com.store.demo.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class SparkContextConfig
{
	@Value("${spark.master}")
	String sparkMasterUrl;

	@Value("${spring.data.cassandra.contact-points:localhost}")
	private String contactPoints;

	@Bean
	public JavaSparkContext javaSparkContext()
	{
		log.info("Connecting to spark with master Url: {}, and cassandra host: {}", sparkMasterUrl, contactPoints);

		SparkConf conf = new SparkConf(true).set("spark.cassandra.connection.host", contactPoints)
				.set("spark.submit.deployMode", "client");

		JavaSparkContext context = new JavaSparkContext(sparkMasterUrl, "SparkDemo", conf);
		//context.addJar("/jobs/spark-shared.jar");

		log.debug("SparkContext created");
		return context;
	}
}

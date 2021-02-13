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
	private String sparkMasterUrl;

	@Value("${spark.appName}")
	private String sparkAppName;

	@Value("${spring.data.cassandra.contact-points:localhost}")
	private String contactPoints;

	@Value("${spring.data.cassandra.port:0000}")
	private int port;

	@Bean
	public JavaSparkContext sparkContext()
	{
		log.info("Connecting to spark with master Url: {}, and cassandra host: {}", sparkMasterUrl, contactPoints);

		final SparkConf conf = new SparkConf().set("spark.cassandra.connection.host", contactPoints)
				.set("spark.cassandra.connection.port", String.valueOf(port))
				.set("spark.submit.deployMode", "client");

		final JavaSparkContext context = new JavaSparkContext(sparkMasterUrl, sparkAppName, conf);
		//context.addJar("/jobs/spark-shared.jar");

		log.debug("SparkContext created");
		return context;
	}
}

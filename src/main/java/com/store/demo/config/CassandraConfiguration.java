package com.store.demo.config;

import com.store.demo.DemoApplication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cassandra.CassandraProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.config.SchemaAction;
import org.springframework.data.cassandra.core.cql.keyspace.CreateKeyspaceSpecification;
import org.springframework.data.cassandra.core.cql.keyspace.DataCenterReplication;
import org.springframework.data.cassandra.core.cql.keyspace.KeyspaceOption;
import org.springframework.data.cassandra.core.cql.session.init.KeyspacePopulator;
import org.springframework.data.cassandra.core.cql.session.init.ResourceKeyspacePopulator;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Configuration
@EnableConfigurationProperties(CassandraProperties.class)
@Slf4j
public class CassandraConfiguration extends AbstractCassandraConfiguration
{
	@Value("${spring.data.cassandra.contact-points:localhost}")
	private String contactPoints;

	@Value("${spring.data.cassandra.port:0000}")
	private int port;

	@Value("${spring.data.cassandra.keyspace:order_demo}")
	private String keySpace;

	@Value("${spring.data.cassandra.schema-action}")
	private String schemaAction;

	@Override
	protected String getKeyspaceName()
	{
		return keySpace;
	}

	@Override
	protected String getContactPoints()
	{
		return contactPoints;
	}

	@Override
	protected int getPort()
	{
		return port;
	}

	@Override
	public SchemaAction getSchemaAction()
	{
		return SchemaAction.valueOf(schemaAction);
	}

	@Override
	protected List<CreateKeyspaceSpecification> getKeyspaceCreations()
	{
		return Arrays.asList(CreateKeyspaceSpecification.createKeyspace(getKeyspaceName())
				.ifNotExists(true)
				.withNetworkReplication(DataCenterReplication.of(getLocalDataCenter(), 1))
				.with(KeyspaceOption.DURABLE_WRITES));
	}

	@Override
	protected KeyspacePopulator keyspacePopulator()
	{
		return Optional.ofNullable(getResources()).map(ResourceKeyspacePopulator::new).orElse(null);
	}

	private Resource[] getResources()
	{
		Resource[] resources = null;
		try
		{
			PathMatchingResourcePatternResolver loader = new PathMatchingResourcePatternResolver();
			resources = loader.getResources("classpath:cassandra/scripts/*.cql");
		}
		catch (final IOException e)
		{
			log.error("Error reading in the cql files from the classpath", e);
		}
		return resources;
	}

	@Override
	public String[] getEntityBasePackages()
	{
		return new String[] { DemoApplication.class.getPackageName() + ".model" };
	}
}

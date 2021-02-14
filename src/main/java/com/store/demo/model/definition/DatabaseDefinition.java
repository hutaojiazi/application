package com.store.demo.model.definition;

public final class DatabaseDefinition
{
	public static final String KEY_SPACE = "keyspace";
	public static final String TABLE = "table";
	public static final String FORMAT = "org.apache.spark.sql.cassandra";

	private DatabaseDefinition()
	{
		// empty
	}

	public static final class KeySpace
	{
		public static final String ORDER_DEMO = "order_demo";

		private KeySpace()
		{
			// empty
		}
	}

	public static final class Table
	{
		public static final String COMPANY = "company";
		public static final String DAILY_PRICE_RECORD = "daily_price_record";
		public static final String ORDERS = "orders";

		private Table()
		{
			// empty
		}
	}
}

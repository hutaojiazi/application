package com.store.demo.controller;

import com.store.demo.dto.StockOverview;
import com.store.demo.dto.StockQuery;
import com.store.demo.jobs.AllStocksOverviewJob;
import com.store.demo.jobs.SingleStockOverviewJob;
import org.apache.spark.api.java.JavaSparkContext;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDate;
import java.util.List;

@Controller
public class StockController
{
	private final JavaSparkContext javaSparkContext;

	public StockController(final JavaSparkContext javaSparkContext)
	{
		this.javaSparkContext = javaSparkContext;
	}

	@GetMapping(value = "/api/stocks")
	public ResponseEntity<List<StockOverview>> getAll()
	{
		final List<StockOverview> overviews = (new AllStocksOverviewJob()).execute(javaSparkContext,
				StockQuery.builder().start(LocalDate.now().minusDays(3)).build());
		return ResponseEntity.ok().body(overviews);
	}

	@GetMapping(value = "/api/stocks/{symbol}")
	public ResponseEntity<StockOverview> getById(@PathVariable final String symbol)
	{
		final StockQuery query = StockQuery.builder().symbol(symbol).start(LocalDate.now().minusDays(3)).build();
		final StockOverview overview = (new SingleStockOverviewJob()).execute(javaSparkContext, query);
		return ResponseEntity.ok().body(overview);
	}

}

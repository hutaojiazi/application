package com.store.demo.controller;

import com.store.demo.dto.StockOverview;
import com.store.demo.dto.StockQuery;
import com.store.demo.jobs.AllStocksOverviewJob;
import com.store.demo.jobs.SingleStockOverviewJob;
import com.store.demo.model.Company;
import com.store.demo.service.StockRecordService;
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
	private final StockRecordService stockRecordService;
	private final JavaSparkContext sparkContext;

	public StockController(final StockRecordService stockRecordService, final JavaSparkContext sparkContext)
	{
		this.stockRecordService = stockRecordService;
		this.sparkContext = sparkContext;
	}

	@GetMapping(value = "/api/companies")
	public ResponseEntity<List<Company>> getCompanies()
	{
		final List<Company> companies = stockRecordService.getCompanies();
		return ResponseEntity.ok().body(companies);
	}

	@GetMapping(value = "/api/stocks")
	public ResponseEntity<List<StockOverview>> getAll()
	{
		final List<StockOverview> overviews = (new AllStocksOverviewJob()).execute(sparkContext,
				StockQuery.builder().start(LocalDate.now().minusDays(3)).build());
		return ResponseEntity.ok().body(overviews);
	}

	@GetMapping(value = "/api/stocks/{symbol}")
	public ResponseEntity<StockOverview> getById(@PathVariable final String symbol)
	{
		final StockQuery query = StockQuery.builder().symbol(symbol).start(LocalDate.now().minusDays(3)).build();
		final StockOverview overview = (new SingleStockOverviewJob()).execute(sparkContext, query);
		return ResponseEntity.ok().body(overview);
	}

}

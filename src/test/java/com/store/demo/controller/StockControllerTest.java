package com.store.demo.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.store.demo.DemoApplication;
import com.store.demo.dto.StockOverview;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = { DemoApplication.class })
@AutoConfigureMockMvc
public class StockControllerTest
{
	@Autowired
	private MockMvc mockMvc;

//	@Test
//	public void shouldReturnDefaultMessage() throws Exception
//	{
//		final MvcResult result = this.mockMvc.perform(get("/api/companies")).andDo(print()).andExpect(status().isOk()).andReturn();
//		final List<StockOverview> actual = new ObjectMapper().readValue(result.getResponse().getContentAsString(),
//				new TypeReference<List<StockOverview>>()
//				{
//				});
//
//		assertThat(actual).isEmpty();
//	}
}

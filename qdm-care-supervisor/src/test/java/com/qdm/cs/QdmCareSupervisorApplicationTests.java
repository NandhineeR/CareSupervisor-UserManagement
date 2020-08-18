package com.qdm.cs;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qdm.cs.usermanagement.response.ResponseInfo;

@RunWith(SpringRunner.class)
@SpringBootTest
public class QdmCareSupervisorApplicationTests {

	private MockMvc mockMvc;
	
	@Autowired
	private WebApplicationContext context;

	ObjectMapper om = new ObjectMapper();

	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
	}

	@Test
	public void testGetCareGiver() throws Exception {
		MvcResult result = mockMvc.perform(get("/careGiver/getCareGiver").content(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk()).andReturn();
		String resultContent = result.getResponse().getContentAsString();
		ResponseInfo response = om.readValue(resultContent, ResponseInfo.class);
		Assert.assertTrue(response.getStatus_code() == 200);
	}

}

package com.nest.core;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.http.MediaType;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import com.nest.core.member_management_service.dto.JoinMemberRequest;
import com.nest.core.member_management_service.dto.LoginMemberRequest;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
class CoreApplicationTests {

	private static String toJson(Object object) throws Exception {
		return new ObjectMapper().writeValueAsString(object);
	}

	@Autowired
	private WebApplicationContext context;
	private MockMvc mockMvc;

	@BeforeAll
	public void setup() {
		this.mockMvc = MockMvcBuilders
						.webAppContextSetup(context)
						.apply(springSecurity())					
						.build();
	}

	@Test
	void contextLoads() {
	}

	// Login Tests
	@Test
	void correctLoginReturnsAccessAndRefreshTokens() throws Exception {
		LoginMemberRequest testLogin = new LoginMemberRequest();
		testLogin.setEmail("testingboy@test.com");
		testLogin.setPassword("123");

		MvcResult result = mockMvc.perform(
				post("/api/v1/member/login")
					.contentType(MediaType.APPLICATION_JSON)
					.content(toJson(testLogin))
			).andExpect(content().contentType(MediaType.APPLICATION_JSON))			
			.andReturn();
		String jsonResponse = result.getResponse().getContentAsString();
		if (!jsonResponse.contains("access_token") || !jsonResponse.contains("refresh_token")) {
			throw new Exception("Login response does not contain access and refresh tokens");
		}
	}

	@Test
	void incorrectLoginReturnsUnauthorized() throws Exception {
		LoginMemberRequest testLogin = new LoginMemberRequest();
		testLogin.setEmail("testingboy@test.com");
		testLogin.setPassword("wrongpassword");

		mockMvc.perform(
			post("/api/v1/member/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(testLogin))
			).andExpect(status().isUnauthorized())
			.andExpect(content().string("Invalid password."));
	}

	// Join Tests
	// No idea how to test this without creating a new user every time
	// @Test	
	// void correctJoinReturnsSignupSuccess() throws Exception {
	// 	JoinMemberRequest testJoin = new JoinMemberRequest();

	// }

	@Test
	void duplicateEmailFailsToJoin() throws Exception {
		JoinMemberRequest testJoin = new JoinMemberRequest();
		testJoin.setEmail("testingboy@test.com");
		testJoin.setPassword("123");
		testJoin.setUsername("testingboy");
		
		mockMvc.perform((post("/api/v1/member/join"))
						.contentType(MediaType.APPLICATION_JSON)
						.content(toJson(testJoin)))
						.andExpect(status().isConflict())
						.andExpect(content().string("Email is already registered."));
	}
}

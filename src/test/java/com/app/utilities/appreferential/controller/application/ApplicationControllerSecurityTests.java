package com.app.utilities.appreferential.controller.application;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ApplicationControllerSecurityTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	@WithMockUser(roles = "APP_OWNER")
	void given_application_owner_then_applicationUpload_should_throw_403() throws Exception {
		MockMultipartFile file = new MockMultipartFile("file", "some content".getBytes());
		mockMvc.perform(
				multipart("/applications/actions/upload").file(file).header("Content-Type", "multipart/form-data"))
				.andExpect(status().isForbidden());
	}

}

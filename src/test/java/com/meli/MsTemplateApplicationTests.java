package com.meli;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest(classes = MsTemplateApplication.class)
class MsTemplateApplicationTests {

	private ApplicationContext applicationContext;

	public MsTemplateApplicationTests(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	@Test
	void contextLoads() {
		assertThat(applicationContext).isNotNull();
	}
}

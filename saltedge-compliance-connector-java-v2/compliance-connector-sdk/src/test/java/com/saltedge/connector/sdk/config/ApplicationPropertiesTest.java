package com.saltedge.connector.sdk.config;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationPropertiesTest {
	@Autowired
	ApplicationProperties applicationProperties;

	@Test
	public void valuesTest() {
		assertThat(applicationProperties.getPrivateKeyName()).isNotEmpty();
		assertThat(applicationProperties.getPrivateKey()).isNotNull();

		PrioraProperties prioraProperties = applicationProperties.getPriora();
		assertThat(prioraProperties.getAppCode()).isEqualTo("spring_connector_example_md");
		assertThat(prioraProperties.getAppId()).isEqualTo("QWERTY");
		assertThat(prioraProperties.getAppSecret()).isEqualTo("ASDFG");
		assertThat(prioraProperties.getPrioraBaseUrl().toString()).isEqualTo("http://localhost");
		assertThat(prioraProperties.getPrioraPublicKey()).isNotNull();
	}
}

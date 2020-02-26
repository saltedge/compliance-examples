package com.saltedge.connector.sdk.config;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ConstantsTest {
	@Test
	public void valuesTest() {
		assertThat(Constants.API_BASE_PATH).isEqualTo("/api/priora/v2");
		assertThat(Constants.CALLBACK_BASE_PATH).isEqualTo("/api/connectors/v2");
		assertThat(Constants.HEADER_AUTHORIZATION).isEqualTo("authorization");
		assertThat(Constants.HEADER_CLIENT_ID).isEqualTo("client-id");
		assertThat(Constants.HEADER_ACCESS_TOKEN).isEqualTo("access-token");
		assertThat(Constants.STATUS_WAITING_CONFIRMATION_CODE).isEqualTo("waiting_confirmation_code");
		assertThat(Constants.STATUS_REDIRECT).isEqualTo("redirect");
	}
}

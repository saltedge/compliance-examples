/*
 * @author Constantin Chelban (constantink@saltedge.com)
 * Copyright (c) 2020 Salt Edge.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.saltedge.connector.sdk.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ApplicationPropertiesTest {
	@Autowired
	ApplicationProperties applicationProperties;

	@Test
	public void valuesTest() {
//		assertThat(applicationProperties.getPrivateKeyName()).isNotEmpty();
		assertThat(applicationProperties.getPrivateKey()).isNotNull();

		PrioraProperties prioraProperties = applicationProperties.getPriora();
		assertThat(prioraProperties.getAppCode()).isEqualTo("spring_connector_example_md");
		assertThat(prioraProperties.getAppId()).isEqualTo("QWERTY");
		assertThat(prioraProperties.getAppSecret()).isEqualTo("ASDFG");
		assertThat(prioraProperties.getPrioraBaseUrl().toString()).isEqualTo("http://localhost");
		assertThat(prioraProperties.getPrioraPublicKey()).isNotNull();
//		s = "-----BEGIN PRIVATE KEY-----\nMIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDOzsDAbk6SOiQM\neVLteTiaNANlvrTpYyj6X+oGPNtNMyqUDqGjq0xMJ8681ugitAQrT+3v3AwXCNgF\n4czACLNo2Mu7G1MgFG16xL0KHRbCJdYbUhTiblR8QeCIdWiKa27qcnG1K/VjgRDD\n07J2y4yLX4djPgGIUqbfAYkbXe06JBZmaFCOQOuYNVpdgmDx4dUii8UC08WjRcIN\nVQR3iVpS8/kdhduXdvHHte79UIep0ZZ6WGH50QJ2ywHFw8gaNR4F96rJKZ4fiv5j\ncuMzoMROCO4ljQsF/lpG9pGztMYYI2sEjkY/txAitugCcyCfRIUpKqw8PHx9uynl\nLUUhHg9FAgMBAAECggEAFYo/VUun718S5iG023smxBjqyawlV7G2UO2wiFnKVa4g\nny8u8sciuUPkKpMPtp2dWY2HsNXwhG2tYl90XgQJ+7+o6KId9FdsV78qGWWSO0KJ\nnGreNf9b2V1bL5ta+h/ae5zT7xTXCDgnMN5Z1jm5N8MJbPLGYhaJvuyABVOxI77y\nb5AhepFpD22bcxgOO1GhU4DTwceLdaJnqS34FCcYoUkl/W/rRwvgS8tkpHAbR8ki\n9vpOz1DqO+YGSf0tlFqa5YGnmeFGaEJenge5PNfQy1W7RUJadG3GD9DKU3qxt1xq\ny+pbx6uH0SxseW+q96qyWfROgdCywRTElRyLfdbYuQKBgQD3apDf/lxIV63EV++1\n8GCSzyjxBBPN086t57WM1EOw5znT483toelrN/tX1DEPHkteaskGzG2gzYtEy7mD\n85coEtj4O+xQKfG0ugLzm1Kjj4JkCTGfqPW8g6k2qQHsXVxow6nfaMtZ9RIuOZEe\naWspbj64Eavu89uW2PB/ZCPZkwKBgQDV+4UxTgDaud3PwEji1bSwnnZuyZiK7jkm\nKg/uXqveD8bGGB5c29toa7ysnns3JIYKc7jX2oPzmEmh0tTNW1cbX8jsLL9IDR83\ny42EKcenQ6TR2woyeoDCdPZqtU1SHUHKNeXWmXDLOpxWjiWTOXZF4Oz8kAEeFgFD\n62q6sp0axwKBgQDMorJy4ZH4L7TEaasVQ1zM8FS/i4zJt78BLV2GvmXCYmz86TUA\nr+M80A+t5A4zn+3ciNv+KIUf9AGVhB7LmWTTO0uDfSSGZTrKLPOWA0jFiUPic4w8\nruWY5xjE4eJ6Wfm9mN/erXFL1RwVaX2ytqRwnwN3AP7A46cizAmWg//2ywKBgQCD\n1+GNK36jRfpBUzyVZw9Z84kNYfE3SOi1dx0xxesne5kB3UJ3/I8Hm7o4sb1BkKN2\nNS8i8GR05tQXfQvZK/pTx4TI4BlLj6o4epbLY+K8UJVxKAD0cT58kgxyWxs1CQnM\nScE/pHM3BPnm6taEC9ev3pKmWyQVIYHOE3NDbWIXaQKBgEX877wDFW7CgWwPmja1\n/sErBKGao4kBHrXRvV03hqbp1fexNgRZ4VXESyJXOgzZrSOjY2Qb4dnlYEzvXtx+\nULLhQoTbof3uTVezh6CCoS1+AXe7nvYgNcmVyRce+PGA4kfa/NsPKET7NbpGrVb4\nNgCAIoz5xb6kl3FkxFiBYdd8\n-----END PRIVATE KEY-----"
	}
}

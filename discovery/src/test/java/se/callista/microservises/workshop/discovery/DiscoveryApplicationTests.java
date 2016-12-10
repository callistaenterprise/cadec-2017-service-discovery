package se.callista.microservises.workshop.discovery;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DiscoveryApplication.class)
@WebAppConfiguration
@IntegrationTest("server.port=0")
//@Ignore
public class DiscoveryApplicationTests {
	
	@Value("${local.server.port}")
	private int port = 0;

	@Test
	public void catalogLoads() {
		@SuppressWarnings("rawtypes")
		ResponseEntity<Map> entity = new TestRestTemplate("user", "password").getForEntity("http://localhost:" + port + "/eureka/apps", Map.class);
		assertEquals(HttpStatus.OK, entity.getStatusCode());
		entity.getBody().forEach((k, v) -> System.err.println("app: " + k + " = " + v));
	}

	@Test
	public void adminLoads() {
		@SuppressWarnings("rawtypes")
		ResponseEntity<Map> entity = new TestRestTemplate("user", "password").getForEntity("http://localhost:" + port + "/env", Map.class);
		assertEquals(HttpStatus.OK, entity.getStatusCode());
		entity.getBody().forEach((k, v) -> System.err.println("env: " + k + " = " + v));
	}

}

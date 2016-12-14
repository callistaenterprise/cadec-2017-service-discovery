package se.callista.quotes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class QuotesApplication {

	private static final Logger LOG = LoggerFactory.getLogger(QuotesApplication.class);
	public static void main(String[] args) {
		int verNo = 11;
		SpringApplication.run(QuotesApplication.class, args);
		LOG.info("QuotesApplication v{} started", verNo);
	}
}

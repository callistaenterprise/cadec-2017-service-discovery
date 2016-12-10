package se.callista.quotes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class QuotesApplication {

	public static void main(String[] args) {
		SpringApplication.run(QuotesApplication.class, args);
	}
}

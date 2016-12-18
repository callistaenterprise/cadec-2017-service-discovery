package se.callista.portal;

import java.security.Security;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import javax.annotation.PostConstruct;
import javax.net.ssl.HttpsURLConnection;

@EnableDiscoveryClient
@SpringBootApplication
public class PortalApplication extends WebMvcConfigurerAdapter {

	private static final Logger LOG = LoggerFactory.getLogger(PortalApplication.class);

	@Value("${server.port}")
	private String port;


	// TODO: Make the LoadBalanced bean annotation conditional, based on if spring.cloud.discovery.enabled is true or false...
	@Bean
	@LoadBalanced
	public RestTemplate restTemplate() {
		HttpComponentsClientHttpRequestFactory factory =
			new HttpComponentsClientHttpRequestFactory(HttpClientBuilder.create()
				.setSSLHostnameVerifier(HttpsURLConnection.getDefaultHostnameVerifier())
        .setConnectionManager(new PoolingHttpClientConnectionManager(1, TimeUnit.SECONDS))
				.build());

		factory.setConnectTimeout(500);
		factory.setReadTimeout(5000);

		return new RestTemplate(factory);
	}

	@PostConstruct
	public void postConstruct() {
		LOG.info("PortalApplication use port {}", port);
	}

	public static void main(String[] args) {
		int verNo = 11;
		SpringApplication.run(PortalApplication.class, args);

		LOG.info("JVM DNS Cache TTL: {}", Security.getProperty("networkaddress.cache.ttl"));
		java.security.Security.setProperty("networkaddress.cache.ttl" , "3");
		LOG.info("JVM DNS Cache TTL: {}", Security.getProperty("networkaddress.cache.ttl"));
		LOG.info("JVM DNS Cache Negative TTL: {}", Security.getProperty("networkaddress.cache.negative.ttl"));
		LOG.info("PortalApplication v{} started", verNo);
		LOG.info("PortalApplication dummy log-message...");
	}

	@Bean
	public ResourceBundleMessageSource messageSource() {
		ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
		messageSource.setBasename("portal");
		return messageSource;
	}

	@Bean
	public LocaleResolver localeResolver() {
	    SessionLocaleResolver slr = new SessionLocaleResolver();
	    slr.setDefaultLocale(new Locale("sv"));
	    return slr;
	}

	@Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
        lci.setParamName("language");
        return lci;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }
}

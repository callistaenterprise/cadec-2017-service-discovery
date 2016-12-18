package se.callista.portal;

import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HttpsURLConnection;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

/**
 * Created by magnus on 2016-12-18.
 */
public class DnsTest {

  private static final Logger LOG = LoggerFactory.getLogger(DnsTest.class);

  @Ignore
  @Test
  public void httpCientDnsTest() {
    HttpComponentsClientHttpRequestFactory factory =
      new HttpComponentsClientHttpRequestFactory(HttpClientBuilder.create()
        .setSSLHostnameVerifier(HttpsURLConnection.getDefaultHostnameVerifier())
        .setConnectionManager(new PoolingHttpClientConnectionManager(1, TimeUnit.SECONDS))
        .build());

    factory.setConnectTimeout(500);
    factory.setReadTimeout(5000);

    RestTemplate t = new RestTemplate(factory);

    String url = "http://swarm-worker-1:8080/api/quote";

    for (int i = 0; i < 10; i++) {
      long ts = System.currentTimeMillis();
      LOG.debug("\n\n   ### 1. Trying to get a quote from: {}\n", url);
      Quote quote = t.getForObject(url, Quote.class);
      assertEquals("en", quote.getLanguage());
      ts = System.currentTimeMillis() - ts;
      LOG.debug("\n\n   ### 2. Ok response from: {}, took {}ms\n", quote.getIpAddress(), ts);
      sleep(6);
    }

  }

  private void sleep(int seconds) {
    try {
      Thread.sleep(seconds*1000);
    } catch (InterruptedException e) {}
  }
}

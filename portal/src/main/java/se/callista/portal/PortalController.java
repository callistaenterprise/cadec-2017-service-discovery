package se.callista.portal;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.*;

import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HttpsURLConnection;

//@Controller
@RestController
public class PortalController {

	private static final Logger LOG = LoggerFactory.getLogger(PortalController.class);

	@Value("${quote.server}")
	private String quoteServer;

	@Value("${quote.port}")
	private String quotePort;

    RestTemplate restTemplate = createNewRestTemplate();

	@RequestMapping("/quote")
	public Quote quote() {
		Quote quote;
		try {
			LOG.debug("1. Log DNS entry before call");
			printDNSCachedInfo(quoteServer);

			String url = "http://" + quoteServer + ":" + quotePort + "/api/quote";
			LOG.debug("2. Trying to get a quote from: {}", url);

			quote = restTemplate.getForObject(url, Quote.class);

			LOG.debug("3. Log DNS entry after call");
			printDNSCachedInfo(quoteServer);

			LOG.info("PortalController dummy log-message...");

		} catch (Exception e) {
			LOG.warn("Failed to get quote: " + e.getMessage());
			throw new RuntimeException(e);
		}
		return quote;
	}

	@RequestMapping("/quoteWithoutRetries")
	public String quoteNoRetries() {
		String quote;
		try {
			LOG.debug("1. Log DNS entry before call");
			printDNSCachedInfo(quoteServer);

			String url = "http://" + quoteServer + ":" + quotePort + "/api/quote";
			LOG.debug("2. Trying to get a quote using plain HttpURLConnection from: {}", url);

			quote = sendGet(url);

			LOG.debug("3. Log DNS entry after call");
			printDNSCachedInfo(quoteServer);

			LOG.info("PortalController dummy log-message...");

		} catch (Exception e) {
			LOG.warn("Failed to get quote: " + e.getMessage());
			throw new RuntimeException(e);
		}
		return quote;
	}

	@RequestMapping("/home")
    public String home(Model model, Locale locale) {
    	String language = locale.getLanguage();
    	Quote quote = null;
    	int retries = 0;
    	while (quote == null && retries < 5) {
	        try {
				quote = restTemplate.getForObject("http://"+quoteServer+":"+quotePort+"/quote?language="+language, Quote.class);
			} catch (RestClientException e) {
				LOG.warn("Failed to get quote: " + e.getMessage());
				retries++;
			}
		}
    	if (quote == null) {
    		throw new RuntimeException("Cannot get quote");
    	}
    	model.addAttribute("quote", quote.getQuote());
        return "home";
    }

	public RestTemplate createNewRestTemplate() {
		HttpComponentsClientHttpRequestFactory factory =
				new HttpComponentsClientHttpRequestFactory(HttpClientBuilder.create()
						.setSSLHostnameVerifier(HttpsURLConnection.getDefaultHostnameVerifier())
						.build());

		factory.setConnectTimeout(500);
		factory.setReadTimeout(5000);

		return new RestTemplate(factory);
	}

	private void printDNSCachedInfo(String dnsName) throws Exception {

//		System.out.println("### printDNSCaches " + new Date() +" ###");

//		// put some values in the internal DNS cache
//
//		// good DNS name
//		InetAddress.getByName("stackoverflow.com");
//		InetAddress.getByName("www.google.com");
//		InetAddress.getByName("www.rgagnon.com");
//		try {
//			// bad DNS name
//			InetAddress.getByName("bad.rgagnon.com");
//		}
//		catch (UnknownHostException e) {
//			// do nothing
//		}

		// dump the good DNS entries
		String addressCache = "addressCache";
//		System.out.println("---------" + addressCache + "---------");
		String dnsInfo = printDNSCache(addressCache, dnsName);
		if (dnsInfo != null) {
			LOG.debug("DNS cache for {}: {}", dnsName, dnsInfo);
			return;
		}

		// dump the bad DNS entries
		String negativeCache = "negativeCache";
//		System.out.println("---------" + negativeCache + "---------");
		dnsInfo = printDNSCache(negativeCache, dnsName);
		if (dnsInfo != null) {
			LOG.debug("DNS negativeCache for {}: {}", dnsName, dnsInfo);
			return;
		}

		LOG.debug("No DNS cache info found for {}", dnsName);

	}

	/**
	 * By introspection, dump the InetAddress internal DNS cache
	 *
	 * @param cacheName  can be addressCache or negativeCache
	 * @throws Exception
	 */

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private String printDNSCache(String cacheName, String dnsName) throws Exception {
		Class<InetAddress> iaclass = InetAddress.class;
		Field acf = iaclass.getDeclaredField(cacheName);
		acf.setAccessible(true);
		Object addressCache = acf.get(null);
		Class cacheClass = addressCache.getClass();
		Field cf = cacheClass.getDeclaredField("cache");
		cf.setAccessible(true);
		Map<String, Object> cache = (Map<String, Object>) cf.get(addressCache);

		for (Map.Entry<String, Object> hi : cache.entrySet()) {
			Object cacheEntry = hi.getValue();
			Class cacheEntryClass = cacheEntry.getClass();
			Field expf = cacheEntryClass.getDeclaredField("expiration");
			expf.setAccessible(true);
			long expires = (Long) expf.get(cacheEntry);

			Field af = cacheEntryClass.getDeclaredField("addresses"); // JDK 1.7, older version maybe "address"
			af.setAccessible(true);
			InetAddress[] addresses = (InetAddress[]) af.get(cacheEntry);
			List<String> ads = new ArrayList<String>(addresses.length);

			for (InetAddress address : addresses) {
				ads.add(address.getHostAddress());
			}

			if (dnsName.equalsIgnoreCase(hi.getKey())) {
				return ads + " " + new Date(expires);
			}
		}

		return null;
	}

	private String sendGet(String url) throws Exception {

		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(
				new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		//print result
		String x = response.toString();
		System.out.println("###: RESPONSE: " + x);
		return x;

	}
}

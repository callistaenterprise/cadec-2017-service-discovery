package se.callista.quotes;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Quote {

	private String ipAddress;

	private String quote;
	private String language;

	public Quote() {}

	public Quote(String quote, String language) {
		this.quote = quote;
		this.language = language;
		ipAddress = findMyIpAddress();
	}
	
	public String getQuote() {
		return quote;
	}
	public String getLanguage() {
		return language;
	}
	public String getIpAddress() {
		return ipAddress;
	}

	private String findMyIpAddress() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
	}
}

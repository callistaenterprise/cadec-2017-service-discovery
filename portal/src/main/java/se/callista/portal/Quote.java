package se.callista.portal;

public class Quote {

	private String ipAddress;

	private String quote;
	private String language;

	public Quote() {}

	public Quote(String quote, String language, String ipAddress) {
		this.quote = quote;
		this.language = language;
		this.ipAddress = ipAddress;
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
}

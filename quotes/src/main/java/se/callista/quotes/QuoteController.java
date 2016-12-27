package se.callista.quotes;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class QuoteController {

    private static final Logger LOG = LoggerFactory.getLogger(QuoteController.class);

    @Value("${server.port}")
    private String port;

    Map<String, List<String>> quotes;
	Random random = new Random();
	String ipAddress = findMyIpAddress();
	
	public QuoteController() {
        quotes = new HashMap<>();
        quotes.put("en", Arrays.<String>asList("To be or not to be", "You, too, Brutus?", "Champagne should be cold, dry and free"));
        quotes.put("sv", Arrays.<String>asList("Att vara eller inte vara", "Även du, min Brutus?", "Champagne skall vara kall, torr och gratis"));
	}
    
    @RequestMapping("/api/quote")
    public Quote quote(
        @RequestParam(required=false, defaultValue="en") String language,
        @RequestParam(required=false, defaultValue="12") int strength) {

        if (QuotesHealthIndicator.isAlive) {

            // FIX ME! Bug in version 3, corrected in version 4
            // language = "en";

            List<String> list = quotes.get(language);
            String quoteText = list.get(random.nextInt(list.size()));
            Quote quote = new Quote(quoteText, language, ipAddress + ":" + port);

            LOG.debug("Will encrypt quote using BCrypt with strength = {} log rounds", strength);
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(strength);

            String encryptedQuote = encoder.encode(quoteText);
            LOG.info("Encrypted quote: '" + encryptedQuote + "'");
            LOG.info("Delivered quote: '" + quoteText + "'");
            return quote;
        } else {
            return null;
        }
    }

    @RequestMapping("/poison")
    public String poison() {
        QuotesHealthIndicator.isAlive = false;
        LOG.info("Took posion");
        return "Ouch!";
    }

    private String findMyIpAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

}
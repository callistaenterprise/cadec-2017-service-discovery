package se.callista.quotes;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class QuoteController {

    private static final Log LOG = LogFactory.getLog(QuoteController.class);
    
    Map<String, List<String>> quotes;
	Random random = new Random();
	
	public QuoteController() {
        quotes = new HashMap<>();
        quotes.put("en", Arrays.<String>asList("To be or not to be", "You, too, Brutus?", "Champagne should be cold, dry and free"));
        quotes.put("sv", Arrays.<String>asList("Att vara eller inte vara", "Även du, min Brutus?", "Champagne skall vara kall, torr och gratis"));
	}
    
    @RequestMapping("/api/quote")
    public Quote quote(@RequestParam(value="language", defaultValue="en") String language) {
        if (QuotesHealthIndicator.isAlive) {

            // FIX ME!
            language = "en";

            List<String> list = quotes.get(language);
            String quoteText = list.get(random.nextInt(list.size()));
            Quote quote = new Quote(quoteText, language);
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
}
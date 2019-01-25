import webv.WebVerif;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {


    private static int nbThreads = 5;
    private static String[] hostList = { "http://crunchify.com", "http://yahoo.com",
            "http://www.ebay.com", "http://google.com",
            "http://www.example.co", "https://paypal.com",
            "http://bing.com/", "http://techcrunch.com/",
            "http://mashable.com/", "http://thenextweb.com/",
            "http://wordpress.com/", "http://wordpress.org/",
            "http://example.com/", "http://sjsu.edu/",
            "http://ebay.co.uk/", "http://google.co.uk/",
            "http://www.wikipedia.org/",
            "http://en.wikipedia.org/wiki/Main_Page" };


    public static void main(String[] args) {

        ExecutorService es = Executors.newFixedThreadPool(nbThreads);


        for (int i = 0; i < nbThreads; i++) {
            es.execute(new Thread(new WebVerif(hostList[i], )));
        }



    }
}

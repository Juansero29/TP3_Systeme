Juan RODRIGUEZ - B2-PM 

## TP3

### 1. ExecutorService et Runnable

### Question A.


```
public class WebVerif implements Runnable{


    private String website;

    public WebVerif(String website) {

        this.website = website;
    }

    @Override
    public void run() {

        System.setProperty("http.proxyHost","IP");
        System.setProperty("http.proxyPort", "8080");

        try {
            var url = new URL(website);

            var connection = (HttpURLConnection)url.openConnection();

            var status = connection.getResponseCode();


        } catch (Exception e) {

        }
    }
}

```


### Question B


Y-a t-il des données partagées ? Lesquelles ? 
Il n'y en a pas. Chaque site a une instance de URL et ouvre une connexion indépendament. 


Soit nbthreads le nb de threads du Pool. A combien fixez vous nbthreads et pourquoi ?
Je fixe le nb de threads au nombre de sites à tester. Cela permettra que chaque thread se chargera d'un site à la fois et chacun ouvrira une connexion.



### Question C


## Version 1

GetStatus.java :

```

import webv.WebVerif;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GetStatus {


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

    private static int nbThreads = hostList.length;

    private static ExecutorService executor = Executors.newFixedThreadPool(nbThreads);

    public static void main(String[] args) {

        CountDownLatch latch = new CountDownLatch(nbThreads);

        for (int i = 0; i < nbThreads; i++) {
            executor.execute(new Thread(new WebVerif(hostList[i], latch)));
        }
        
        try{
            latch.await();
        } catch (InterruptedException ex)
        {
            System.out.println(ex);
        }


        executor.shutdown();


    }
}


```


WebVerif.java :

```
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CountDownLatch;

/**
 * Defines a WebVerif
 *
 * @author Juan
 * Created on: 2019 - January - 25 at 09:14
 * Part of TP3's {@link webv} package
 */
public class WebVerif implements Runnable{


    private String website;
    private CountDownLatch latch;


    public WebVerif(String website, CountDownLatch latch) {

        this.website = website;
        this.latch = latch;
    }

    @Override
    public void run() {

        System.setProperty("http.proxyHost","IP");
        System.setProperty("http.proxyPort", "8080");

        try {
            var url = new URL(website);

            var connection = (HttpURLConnection)url.openConnection();

            var status = connection.getResponseCode();


            latch.countDown();


        } catch (Exception e) {


        }
    }
}
```


## Version 2 (callable)


GetStatus.java :

```

import webv.WebVerif;

import java.util.concurrent.*;

public class GetStatus {


    private static String[] hostList = {"http://crunchify.com", "http://yahoo.com",
            "http://www.ebay.com", "http://google.com",
            "http://www.example.co", "https://paypal.com",
            "http://bing.com/", "http://techcrunch.com/",
            "http://mashable.com/", "http://thenextweb.com/",
            "http://wordpress.com/", "http://wordpress.org/",
            "http://example.com/", "http://sjsu.edu/",
            "http://ebay.co.uk/", "http://google.co.uk/",
            "http://www.wikipedia.org/",
            "http://en.wikipedia.org/wiki/Main_Page"};

    private static int nbThreads = hostList.length;

    private static ExecutorService executor = Executors.newFixedThreadPool(nbThreads);

    public static void main(String[] args) {
        CountDownLatch latch = new CountDownLatch(nbThreads);


        try {

            for (int i = 0; i < nbThreads; i++) {

                FutureTask<Integer> futureTask = new FutureTask<>(new WebVerif(hostList[i], latch));

                executor.execute(futureTask);


                while(!futureTask.isDone())
                {
                    var codeStatut = futureTask.get(10, TimeUnit.MILLISECONDS);

                    System.out.println("Code de statut pur site " + hostList[i] + "est " + codeStatut);
                }
            }

            latch.await();

        } catch (InterruptedException ex) {
            System.out.println(ex);
        } catch (ExecutionException ex){
            System.out.println(ex);
        } catch (TimeoutException ex){
            System.out.println(ex);
        }


        executor.shutdown();

    }
}

```


WebVerif.java:

```

/**
 * Defines a WebVerif
 *
 * @author Juan
 * Created on: 2019 - January - 25 at 09:14
 * Part of TP3's {@link webv} package
 */
public class WebVerif implements Callable<Integer> {


    private String website;
    private CountDownLatch latch;


    public WebVerif(String website, CountDownLatch latch) {

        this.website = website;
        this.latch = latch;
    }


    @Override
    public Integer call() throws Exception {

        System.setProperty("http.proxyHost","IP");
        System.setProperty("http.proxyPort", "8080");

        try {
            var url = new URL(website);

            var connection = (HttpURLConnection)url.openConnection();

            var status = connection.getResponseCode();


            latch.countDown();

            return status;


        } catch (Exception e) {
            System.out.println(e);
        }

        return -1;
    }
}
```


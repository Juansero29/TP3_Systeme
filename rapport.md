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


### 2. ExecutorService, Callable et FutureTask

## Question A


Pour (2), on va stocker dans une collection à l'index i, le nombre de bananes collectées (modélisées ici par un chiffre) par le singe i.  Quelle classe allez vous utiliser et pourquoi?


J'utilise la classe Vector<Integer> puisque cette liste sera une ressource partagée pour les singes, on mettra dedans le nombre de bananes collectées par un singe à chaque fois qu'il a fini de collecter, mais si jamais deux singes finissent au même temps, on veut pas que la liste soit accessible en écriture pour les deux ou + singes. Le vector assure une exclusion mutuelle


## Question B

Quelle est la solution que vous avez retenue pour effectuer la tache "addition de bananes"?



J'utilise une barrière "CountDownLatch"  pour que chaque singe décrémente le compteur de un. Une fois le compteur est à zéro, on sait que tous les singes on fini et on pourra compter le nombre total de bananes.

Singe.java:
```

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

/**
 * Defines a Singe
 *
 * @author Juan
 * Created on: 2019 - January - 25 at 10:10
 * Part of TP3_Systeme's {@link singes} package
 */
public class Singe implements Callable<Integer> {

    private CountDownLatch cdl;

    public Singe(CountDownLatch barriere) {
        cdl = barriere;
    }

    @Override
    public Integer call() throws Exception {

         var r = new Random();
         var bananes = r.nextInt(10);

         cdl.countDown();

         return bananes;

    }


}


```

Ramasseur.java:

```


import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.*;

/**
 * Defines a Ramasseur
 *
 * @author Juan
 * Created on: 2019 - January - 25 at 10:18
 * Part of TP3_Systeme's {@link singes} package
 */
public class Ramasseur {


    private static Integer numberOfBananasCollected;
    private static ExecutorService executorService = Executors.newFixedThreadPool(10);


    public static void main(String[] args) {

        CountDownLatch barriere = new CountDownLatch(10);


        var bananes = new Vector<Integer>();
        
        var singes = new ArrayList<Singe>();


        var tasks = new ArrayList<FutureTask<Integer>>();

        for (int i = 0; i < 10; i++) {
            singes.add(new Singe(barriere));
        }


        singes.forEach(s -> {

            var singeTask = new FutureTask<>(s);
            tasks.add(singeTask);
            executorService.execute(singeTask);

        });


        while(true)
        {


            for (int i = 0; i < 10 ; i++) {

                try {
                    if (tasks.get(i).isDone()) {

                        var b = tasks.get(i).get();

                        bananes.add(i, b);

                        System.out.println("Le singe " + i + " a collecte " + b + " bananes");

                    }
                } catch(InterruptedException | ExecutionException e)
                {
                    e.printStackTrace();
                }

            }


            try {
                barriere.await();

                

                for (var b: bananes) {
                    numberOfBananasCollected += b;
                }


                System.out.println("Le nombre total de bananes est " + numberOfBananasCollected);

            } catch(InterruptedException  e)
            {
                e.printStackTrace();
            }

        }




    }
}

```



## Question C


On devrait faire un block synchronized à chaque affection dans la collection de bananes pour éviter que deux singes affectent les bananes collectées au même temps. 



## Question D

Il faudrait maintenant utiliser une Hashtable, qui mémorisera le numéro du singe en tant que clé et la liste de bananes collectées en valeurs pour chaque clé. Si on utilise une Hashtable, elle sera ThreadSafe. 

En ce qui concerne le singe, il peut utiliser une ArrayList pour les bananes puisque chaque thread aura sa propre liste et ne sera donc pas une ressource partagée.


Singe.java :

```
public class Singe implements Callable<List<Banane>> {

    private CountDownLatch cdl;

    public Singe(CountDownLatch barriere) {
        cdl = barriere;
    }

    @Override
    public List<Banane> call() throws Exception {

        var r = new Random();
        var nbBananes = r.nextInt(10);
        var bananes = new ArrayList<Banane>();

        for (int i = 0; i < nbBananes; i++) {
            bananes.add(new Banane());
        }
        cdl.countDown();

        return bananes;

    }


}

``





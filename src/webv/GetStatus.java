package webv;



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

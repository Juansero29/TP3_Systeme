package singes;/*
 * LICENSE:
 *This is free and unencumbered software released into the public domain.
 *
 *Anyone is free to copy, modify, publish, use, compile, sell, or
 *distribute this software, either in source code form or as a compiled
 *binary, for any purpose, commercial or non-commercial, and by any
 *means.
 *
 *In jurisdictions that recognize copyright laws, the author or authors
 *of this software dedicate any and all copyright interest in the
 *software to the public domain. We make this dedication for the benefit
 *of the public at large and to the detriment of our heirs and
 *successors. We intend this dedication to be an overt act of
 *relinquishment in perpetuity of all present and future rights to this
 *software under copyright law.
 *
 *THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 *OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 *ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 *OTHER DEALINGS IN THE SOFTWARE.
 *
 *For more information, please refer to <http://unlicense.org/>
 *
 */


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

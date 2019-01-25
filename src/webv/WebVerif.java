package webv;

/*
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


import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

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

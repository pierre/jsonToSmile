package com.ning.metrics.jsonToSmile;

import com.ning.http.client.SimpleAsyncHttpClient;
import com.ning.http.client.consumers.OutputStreamBodyConsumer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static com.ning.metrics.jsonToSmile.JsonUtils.extractDataFromActionCore;

public class JsonInputStreamFactory
{
    public static InputStream getJson(String file)
    {
        if (file.startsWith("http://")) {
            return getJsonFromActionCore(file);
        }
        else {
            return getJsonFromFile(file);
        }
    }

    private static InputStream getJsonFromActionCore(String url)
    {
        InputStream in = null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        SimpleAsyncHttpClient client = new SimpleAsyncHttpClient.Builder()
            .setUrl(url)
            .build();

        Future future;
        try {
            long then = System.currentTimeMillis();
            future = client.get(new OutputStreamBodyConsumer(out));
            future.get();
            System.err.println(String.format("Got data from action-core in %d secs", (System.currentTimeMillis() - then) / 1000));
            out.close();

            in = extractDataFromActionCore(new ByteArrayInputStream(out.toByteArray()));
        }
        catch (IOException e) {
            System.err.println("Exception when contacting the action-core: " + e.getLocalizedMessage());
            return null;
        }
        catch (InterruptedException e) {
            System.err.println("Exception when contacting the action-core: " + e.getLocalizedMessage());
            return null;
        }
        catch (ExecutionException e) {
            System.err.println("Exception when contacting the action-core: " + e.getLocalizedMessage());
            return null;
        }

        return in;
    }


    private static InputStream getJsonFromFile(String file)
    {
        try {
            return new FileInputStream(file);
        }
        catch (FileNotFoundException e) {
            return null;
        }
    }
}

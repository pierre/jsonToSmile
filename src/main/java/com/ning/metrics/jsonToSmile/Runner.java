package com.ning.metrics.jsonToSmile;

import java.io.IOException;
import java.io.InputStream;

import static com.ning.metrics.jsonToSmile.JsonUtils.prettyPrintJson;

public class Runner
{
    public static void main(String[] args) throws IOException
    {
        String file = args[0];
        InputStream in = JsonInputStreamFactory.getJson(file);
        prettyPrintJson(in);

        System.exit(0);
    }
}

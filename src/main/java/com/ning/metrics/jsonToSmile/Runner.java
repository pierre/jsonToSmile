package com.ning.metrics.jsonToSmile;

import java.io.IOException;
import java.io.InputStream;

public class Runner
{
    public static void main(String[] args) throws IOException
    {
        String file = args[0];
        InputStream in = JsonInputStreamFactory.getJson(file);
        //prettyPrintJson(in);

        final HadoopWriter writer = new HadoopWriter("my-smile-file");
        writer.open();
        writer.write(in);
        writer.close();

        System.exit(0);
    }
}

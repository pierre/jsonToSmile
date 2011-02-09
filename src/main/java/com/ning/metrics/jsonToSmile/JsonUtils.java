package com.ning.metrics.jsonToSmile;

import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class JsonUtils
{
    private static final ObjectMapper objectMapper = new ObjectMapper(new JsonFactory());


    public static void prettyPrintJson(InputStream in) throws IOException
    {
        // TODO doesn't work?
        objectMapper.configure(SerializationConfig.Feature.INDENT_OUTPUT, true);

        JsonParser jp;
        JsonGenerator gen = objectMapper.getJsonFactory().createJsonGenerator(System.out, JsonEncoding.UTF8);
        try {
            jp = objectMapper.getJsonFactory().createJsonParser(in);
        }
        catch (IOException e) {
            System.err.println("Exception while printing input stream: " + e.getLocalizedMessage());
            return;
        }

        while (jp.nextToken() != null) {
            HashMap value = objectMapper.readValue(jp, HashMap.class);
            objectMapper.writeValue(gen, value);
        }
        jp.close();
        gen.close();

        System.out.flush();
    }
}

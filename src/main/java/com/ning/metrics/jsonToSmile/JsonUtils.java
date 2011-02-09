package com.ning.metrics.jsonToSmile;

import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;

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

    /**
     * The API output looks like:
     * <p/>
     * {
     * "path" : "/path/to/Event/2010/07/09",
     * "parentPath" : "/path/to/Event/2010/07",
     * "entries" : [
     * "content" : {
     * "entries" : []
     * }
     * ]
     * }
     *
     * @param in InputStream from the Action core JSON API
     * @return inputstream of events only
     * @throws IOException Generic parsing exception
     */
    public static InputStream extractDataFromActionCore(InputStream in) throws IOException
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        JsonParser jp;
        JsonGenerator gen = objectMapper.getJsonFactory().createJsonGenerator(out, JsonEncoding.UTF8);
        try {
            jp = objectMapper.getJsonFactory().createJsonParser(in);
        }
        catch (IOException e) {
            System.err.println("Exception while printing input stream: " + e.getLocalizedMessage());
            return null;
        }

        while (jp.nextToken() != null) {
            JsonNode value = objectMapper.readValue(jp, JsonNode.class);
            JsonNode entries = value.path("entries");
            for (int i = 0; i < entries.size(); i++) {
                JsonNode content = entries.path(i).path("content");
                Iterator<JsonNode> it = content.path("entries").getElements();
                while (it.hasNext()) {
                    JsonNode node = it.next();
                    objectMapper.writeValue(gen, node);
                }
            }
        }
        jp.close();
        gen.close();

        return new ByteArrayInputStream(out.toByteArray());
    }

}

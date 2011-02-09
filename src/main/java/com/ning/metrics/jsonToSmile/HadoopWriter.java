package com.ning.metrics.jsonToSmile;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Writable;

import java.io.IOException;
import java.io.InputStream;

public class HadoopWriter
{
    private final Writable BOOL_WRITABLE;

    private final String targetFilename;
    private final Configuration conf;
    private SequenceFile.Writer writer;

    public HadoopWriter(String targetFilename)
    {
        this.targetFilename = targetFilename;

        conf = new Configuration();
        conf.set("fs.default.name", System.getProperty("hadoop.uri", "file:///var/tmp"));
        conf.set("hadoop.job.ugi", "pierre,pierre");
        conf.setStrings("io.serializations", "org.apache.hadoop.io.serializer.WritableSerialization");

        byte[] truth = {(byte) '1'};
        BOOL_WRITABLE = new BytesWritable(truth);
    }

    public void write(InputStream in) throws java.io.IOException
    {
        int len = in.available();
        while (len > 0) {
            byte[] bytes = new byte[len];
            int breakable = in.read(bytes);
            writer.append(BOOL_WRITABLE, new BytesWritable(bytes));

            if (breakable == -1) {
                break;
            }
            len = in.available();
        }
    }

    public void open() throws IOException
    {
        FileSystem fs = FileSystem.get(conf);
        writer = SequenceFile.createWriter(fs, fs.getConf(),
            new Path("/tmp", targetFilename), BytesWritable.class, BytesWritable.class,
            SequenceFile.CompressionType.BLOCK);
    }

    public void close() throws IOException
    {
        writer.sync();
        writer.close();
    }
}

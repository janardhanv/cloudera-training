// (c) Copyright 2009 Cloudera, Inc.

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.lib.IdentityReducer;
import org.apache.hadoop.mapred.lib.db.DBConfiguration;
import org.apache.hadoop.mapred.lib.db.DBInputFormat;
import org.apache.hadoop.mapred.lib.db.DBWritable;
import org.apache.hadoop.util.ToolRunner;
import org.apache.hadoop.util.Tool;

/**
 * AccessLogFilter
 *
 * Reads the access_log entries, parses them, determines if they match a URL (or set
 * of URLs) we are interested in, and emits the ip addresses that accessed that site.
 * Uniquifies URLs in the output.
 *
 * @author(aaron)
 */
public class AccessLogFilter extends Configured implements Tool {

  // where to put the data in hdfs when we're done
  private static final String OUTPUT_PATH = "filtered_ip_addrs";

  // where to read the data from.
  private static final String INPUT_PATH = "/shared/access_logs";

  /**
   * TODO: Reads in the access log line which has the form
   * ip-addr - - [timestamp] "Request str" return_code return_size "referrer url" "user agent"
   * Determines if the request string is for the file we care about, and if so, emits the
   * ip address.
   */
  static class LogFilterMapper extends MapReduceBase
      implements Mapper<LongWritable, Text, Text, Text> {

    public LogFilterMapper() { }

    public void map(LongWritable key, Text value, OutputCollector<Text, Text> output,
        Reporter reporter) throws IOException {

      // TODO: Write the map method!
    }
  }

  /**
   * TODO: Emit the "A.B.C." portion of unique IP addresses we see.
   */
  static class LogFilterReducer extends MapReduceBase
      implements Reducer<Text, Text, Text, NullWritable> {

    public void reduce(Text key, Iterator<Text> values,
        OutputCollector<Text, NullWritable> output, Reporter reporter) throws IOException {
      // TODO: Write the uniquifying reducer here.
      // use NullWritable.get() for the value, since you only emit a key.
    }
  }

  /** Driver for the actual MapReduce process */
  public int run(String [] args) throws IOException {
    JobConf conf = new JobConf(getConf(), AccessLogFilter.class);

    FileInputFormat.addInputPath(conf, new Path(INPUT_PATH));
    FileOutputFormat.setOutputPath(conf, new Path(OUTPUT_PATH));

    conf.setMapperClass(LogFilterMapper.class);
    conf.setReducerClass(LogFilterReducer.class);

    conf.setOutputKeyClass(Text.class);
    conf.setOutputValueClass(NullWritable.class);

    conf.setMapOutputValueClass(Text.class);

    JobClient.runJob(conf);
    return 0;
  }

  public static void main(String [] args) throws Exception {
    int ret = ToolRunner.run(new AccessLogFilter(), args);
    System.exit(ret);
  }
}


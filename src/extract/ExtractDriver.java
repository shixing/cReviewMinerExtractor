package extract;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import common.util.Config;
import common.util.XmlInputFormat;

public class ExtractDriver {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			runJob(args[0], args[1]);

		} catch (IOException ex) {
			Logger.getLogger(ExtractDriver.class.getName()).log(Level.SEVERE,
					null, ex);
		}

	}

	public static void runJob(String input, String output) throws IOException {

		Configuration conf = new Configuration();
		conf.set("xmlinput.start", Config.RawPageStartKey);
		conf.set("xmlinput.end", Config.RawPageEndKey);
		conf.set("mapred.job.tracker", Config.JobTracker);
		conf
				.set(
						"io.serializations",
						"org.apache.hadoop.io.serializer.JavaSerialization,org.apache.hadoop.io.serializer.WritableSerialization");

		Job job = new Job(conf, "Extraction for " + input);

		FileInputFormat.setInputPaths(job, input);
		job.setJarByClass(ExtractDriver.class);
		job.setMapperClass(ExtractMap.class);
		// job.setReducerClass(KeyValueSortReducer.class);
		job.setInputFormatClass(XmlInputFormat.class);
		job.setOutputKeyClass(NullWritable.class);
		job.setOutputValueClass(Text.class);
		MultipleOutputs.addNamedOutput(job, "sinadata",
				TextOutputFormat.class, NullWritable.class, Text.class);
		MultipleOutputs.addNamedOutput(job, "nosinadata",
				TextOutputFormat.class, NullWritable.class, Text.class);

		Path outPath = new Path(output);
		FileOutputFormat.setOutputPath(job, outPath);
		FileSystem dfs = FileSystem.get(outPath.toUri(), conf);
		if (dfs.exists(outPath)) {
			dfs.delete(outPath, true);
		}

		try {
			job.waitForCompletion(true);
		} catch (InterruptedException ex) {
			Logger.getLogger(ExtractDriver.class.getName()).log(Level.SEVERE,
					null, ex);
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(ExtractDriver.class.getName()).log(Level.SEVERE,
					null, ex);
		}

	}

}

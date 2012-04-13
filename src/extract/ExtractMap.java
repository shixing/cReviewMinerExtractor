package extract;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;

import extract.elements.Page;
import extract.mainProcess.MainProcess;
import extract.ontology.Attribute;
import extract.ontology.DataRecord;
import extract.ontology.Field;
import extract.ontology.Ontology;
import extract.ontology.PageOutput;
import extract.webPageFinder.PageFactory;

/**
 * 
 * @author root
 */
public class ExtractMap extends Mapper<LongWritable, Text, NullWritable, Text> {

	private Text outValue = new Text("");
	private MultipleOutputs<NullWritable, Text> mos;

	@Override
	public void map(LongWritable key, Text value1, Context context)
			throws IOException, InterruptedException {

//		String xmlString = value1.toString().replace("\n", "###$$###");
		String xmlString = value1.toString();
		
		PageFactory pf=new PageFactory();
		Page page=pf.getPagebyXML(xmlString);
		
		//better a member of the class, for init() is time-consuming, better do it once.
		MainProcess mp=new MainProcess();
		mp.init();
		mp.processOnePage(page);
		String resultString = page.getPo().toXML();
		
		//get ontology
		PageOutput po=page.getPo();
		//get PageURL; may judge the website
		String pageURL=po.getPageURL();
		for (DataRecord dr:po.getDataRecords()) //dataRecord is derived from certain Ontology
		{
			String ontologyFileName=dr.getOntologyFile();
			Ontology ontology=mp.getOntology(ontologyFileName);
			//get HBaseTableName
			ontology.getHBTableName();

			//get Attribute(Column of the table) and Content
			for (Field f:dr.getFields())
			{
				//typename;
				f.getTypeName();
				//get content;
				f.getContent();
				
				//get the relevant HBase information;
				Attribute attr=f.getAttr();
				//typename; same with f.getTypeName();
				attr.getTypeName();
				//column name in the table
				attr.getHBColumnName();
				//dataType in the HBase
				attr.getHBDataType();
			}			
		}
		
		
		
		
		
		
		outValue.set(resultString);

		if (resultString.contains("sina.com.cn")) {
			mos.write(NullWritable.get(), outValue, "sinadata");
		} else {
			mos.write(NullWritable.get(), outValue, "nosinadata");
		}
		// context.write(NullWritable.get(), new Text(resultString));
	}

	@Override
	protected void setup(Context context) throws IOException,
			InterruptedException {
		mos = new MultipleOutputs<NullWritable, Text>(context);
		super.setup(context);
	}

	@Override
	protected void cleanup(Context context) throws IOException,
			InterruptedException {
		mos.close();
		super.cleanup(context);
	}

}

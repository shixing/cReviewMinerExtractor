/**
 * 
 */
package extract.filter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import common.util.SplitUtil;

import extract.config.Config;
import extract.elements.Page;
import extract.ontology.DataRecord;
import extract.ontology.Field;
import extract.ontology.PageOutput;
import extract.util.StringReplacer;

/**
 * @author shixing
 *
 */
public class WordFilter {
	ArrayList<String> source=new ArrayList<String>();
	ArrayList<String> dest=new ArrayList<String>();
	ArrayList<Integer> times=new ArrayList<Integer>();
	
	
	public WordFilter(String filename)
	{
		readConfigFile(filename);
	}
	private void readConfigFile(String filePath)
	{
		try
		{
			BufferedReader fin=new BufferedReader(new InputStreamReader(new FileInputStream(filePath),"utf8"));
			String line=null;
			while ((line=fin.readLine())!=null)
			{
				if (line.startsWith("//"))
						continue;
				String ll[]=line.split("=");
				if (ll.length==3)
				{
					this.source.add(ll[0]);
					this.dest.add(ll[1]);
					this.times.add(Integer.parseInt(ll[2]));
				}
			}
		}catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public StringReplacer getStringReplacer()
	{
		int times[]=new int[this.times.size()];
		String s[]=new String[this.times.size()];
		String d[]=new String[this.times.size()];
		for (int i=0;i<this.times.size();i++)
		{
			times[i]=this.times.get(i);
			s[i]=this.source.get(i);
			d[i]=this.dest.get(i);
		}
		return new StringReplacer(s,d,times);
	}
	
	//���˺ͷִ�
	public void filterAndSpilt(Page page)
	{
		StringReplacer sr=this.getStringReplacer();
		PageOutput po=page.getPo();
		for (DataRecord dr:po.getDataRecords())
		{
			for (Field field:dr.getFields())
			{
				if (field.getContent()==null || field.getContent().equals(""))
					continue;
				String c=sr.replace(field.getContent());
				c=SplitUtil.splitString(c);
				field.setContent(c);
			}
		}
	}
	
	public String toString()
	{
		String output="";
		System.out.println(this.source.size());
		for (int i=0;i<this.source.size();i++)
		{
			System.out.println(this.source.get(i)+" "+this.dest.get(i)+" "+this.times.get(i));
		}
		return output;
	}
	
	//for test
	public static void main(String argv[])
	{
		WordFilter wf=new WordFilter(Config.getNewInstance().getValue("wordFilterFile"));
		System.out.println(wf.toString());
		StringReplacer sr=wf.getStringReplacer();
		System.out.println(sr.toTreeString());
		System.out.println(sr.replace("<!doctype html>"));
	}
}

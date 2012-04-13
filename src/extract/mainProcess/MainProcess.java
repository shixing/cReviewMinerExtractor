/**
 * 
 */
package extract.mainProcess;

import java.io.*;
import java.util.HashMap;

import extract.blogNews.BlogNewsExtractor;
import extract.config.Config;
import extract.elements.Page;
import extract.filter.PageType;
import extract.filter.URLClassifier;
import extract.filter.WordFilter;
import extract.generater.Generator;
import extract.ontology.Attribute;
import extract.ontology.AttributeFactory;
import extract.ontology.Ontology;
import extract.ontology.PageOutput;
import extract.ontology.Wrapper;
import extract.storer.BufferedStorer;
import extract.util.StringReplacer;
import extract.webPageFinder.PageIterator;

/**
 * @author shixing
 * Ŀǰ��Ϊ���ԣ�ʵ�ʵ�д��Ӧ��֧��ontology��Storer�Ķ�Ӧ
 */
public class MainProcess {
	private Ontology blogOntology=null;
	private Ontology bloggerOntology=null;
	private Ontology movieOntology=null;
	private Ontology musicOntology=null;
	private Ontology newsOntology=null;
	private Ontology postOntology=null;
	private Ontology reviewOntology=null;
	private Ontology twitterOntology=null;
	private Ontology userOntology=null;
	private HashMap<String,Ontology> ontologyMap=null;
	
	private BufferedStorer blogStorer=null;
	private BufferedStorer newsStorer=null;
	private BufferedStorer postStorer=null;
	private Config config=null;
	private WordFilter wf=null;
	private URLClassifier ucla=null;
	
	
	public static void main(String argc[])
	{
		long start=System.currentTimeMillis();
		MainProcess mp=new MainProcess();
		mp.init();
		mp.testDR();
//		mp.testDR();
		long end=System.currentTimeMillis();
		long ss=(end-start)/1000;
		System.out.println(ss/60+"min"+ss%60+"s");
	}
	
	public void init()
	{
		config=Config.getNewInstance();
		wf=new WordFilter(config.getValue("wordFilterFile"));
		ucla=new URLClassifier(config.getValue("filterFile"));
		this.initOntology(config);
	}
	
	public void processOnePage(Page page)
	{
		//if(!page.getUrl().equals("http://blog.sina.com.cn/s/blog_44491d9d0102e29n.html?tj=1"))
		//	continue;
		//judge PageType based on url
		String url=page.getUrl();
		PageType pageType=ucla.judgePageType(url);
		page.setPageType(pageType);
		if (pageType==PageType.BLOG || pageType==PageType.NEWS || pageType==PageType.POST)
			System.out.println(pageType.toString()+" "+page.getUrl());
		
		switch(pageType)
		{
		case BLOG:
			BlogNewsExtractor bnExtractor=new BlogNewsExtractor();
			bnExtractor.extract(page, this.blogOntology);
			if (page.getPo().getDataRecords().size()>0)
			{
				wf.filterAndSpilt(page);
			}
			break;
		case NEWS:
			bnExtractor=new BlogNewsExtractor();
			bnExtractor.extract(page, this.newsOntology);
			if (page.getPo().getDataRecords().size()>0)
			{
				wf.filterAndSpilt(page);
			}
			break;
		case POST:
			bnExtractor=new BlogNewsExtractor();
			bnExtractor.extract(page, this.postOntology);
			if (page.getPo().getDataRecords().size()>0)
			{
				wf.filterAndSpilt(page);
			}
			break;
		default:
			break;
		}
		//add into the bufferedStorer
	}

	private void go()
	{
		//call different extractor based on different PageType
		PageIterator pi=new PageIterator(config.getValue("rawDataFile"));
		int count=0;
		int breaknum=0;
		while (pi.hasNext())
		{

			count++;
			if (count%20==0)
			{
				System.out.println(count+" "+breaknum);
			}
			Page page=pi.next();
			//if(!page.getUrl().equals("http://blog.sina.com.cn/s/blog_44491d9d0102e29n.html?tj=1"))
			//	continue;
			//judge PageType based on url
			String url=page.getUrl();
			PageType pageType=ucla.judgePageType(url);
			page.setPageType(pageType);
			if (pageType==PageType.BLOG || pageType==PageType.NEWS || pageType==PageType.POST)
				System.out.println(pageType.toString()+" "+page.getUrl());
			
			switch(pageType)
			{
			case BLOG:
				BlogNewsExtractor bnExtractor=new BlogNewsExtractor();
				bnExtractor.extract(page, this.blogOntology);
				if (page.getPo().getDataRecords().size()>0)
				{
					wf.filterAndSpilt(page);
					this.blogStorer.addPageOutput(page.getPo());
				}
				break;
			case NEWS:
				bnExtractor=new BlogNewsExtractor();
				bnExtractor.extract(page, this.newsOntology);
				if (page.getPo().getDataRecords().size()>0)
				{
					wf.filterAndSpilt(page);
					this.newsStorer.addPageOutput(page.getPo());
				}
				break;
			case POST:
				bnExtractor=new BlogNewsExtractor();
				bnExtractor.extract(page, this.postOntology);
				if (page.getPo().getDataRecords().size()>0)
				{
					wf.filterAndSpilt(page);
					this.postStorer.addPageOutput(page.getPo());
				}
				break;
			default:
				breaknum++;
				break;
			}
			//add into the bufferedStorer
		}
		//close the BufferedStorer;
		this.newsStorer.close();
		this.blogStorer.close();
		this.postStorer.close();
	}
	
	private void testDR()
	{
		//init
		Generator g=new Generator();
		int maxDepth=Integer.parseInt(config.getValue("matchStringDepth"));
		BufferedStorer drStorer=new BufferedStorer(20, "resource/extract/dataRecord", "dr", "utf8");
		StringReplacer sr=new WordFilter(config.getValue("wordFilterFile")).getStringReplacer();
		//call different extractor based on different PageType
		PageIterator pi=new PageIterator(config.getValue("rawDataFile"));
		int count=0;
		int breaknum=0;
		while (pi.hasNext())
		{
			count++;
			if (count%80==0)
			{
				System.out.println(count+" "+breaknum);
			}
			Page page=pi.next();
			//replace <!doctype html> --> <html> && others
			if (page.getContent().equals(""))
				continue;
			page.setContent(sr.replace(page.getContent()));
			
			//if(!page.getUrl().equals("http://blog.sina.com.cn/s/blog_44491d9d0102e29n.html?tj=1"))
			//	continue;
			//judge PageType based on url
			String url=page.getUrl();
			PageType pageType=ucla.judgePageType(url);
			page.setPageType(pageType);
			if (pageType==PageType.BLOG || pageType==PageType.NEWS || pageType==PageType.POST)
				System.out.println(pageType.toString()+" "+page.getUrl());
			try{
			g.generate(page, maxDepth);
			}catch (Exception e)
			{
				e.printStackTrace();
				System.out.println(page.getUrl());
				try {
					BufferedWriter fout=new BufferedWriter(new OutputStreamWriter(new FileOutputStream("resource/extract/temp/errorPage.txt")));
					fout.write(page.getUrl());
					fout.write(page.getContent());
					fout.flush();
					fout.close();
					return;
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
			switch(pageType)
			{
			case BLOG:
				BlogNewsExtractor bnExtractor=new BlogNewsExtractor();
				bnExtractor.extract(page, this.blogOntology);
				if (page.getPo().getDataRecords().size()>0)
					this.blogStorer.addPageOutput(page.getPo());
				drStorer.addString(page.getUrl()+"\n");
				drStorer.addString(page.getTree().toRecordString());
				drStorer.addString("\n");
				break;
			case NEWS:
				bnExtractor=new BlogNewsExtractor();
				bnExtractor.extract(page, this.newsOntology);
				if (page.getPo().getDataRecords().size()>0)
					this.newsStorer.addPageOutput(page.getPo());
				break;
			case POST:
				bnExtractor=new BlogNewsExtractor();
				bnExtractor.extract(page, this.postOntology);
				if (page.getPo().getDataRecords().size()>0)
					this.postStorer.addPageOutput(page.getPo());
				break;
			default:
				breaknum++;
				break;
			}
			//add into the bufferedStorer
		}
		//close the BufferedStorer;
		this.newsStorer.close();
		this.blogStorer.close();
		this.postStorer.close();
	    drStorer.close();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public Ontology getOntology(String ontologyFileName)
	{
		return this.ontologyMap.get(ontologyFileName);
	}
	
	private void initOntology(Config config)
	{
		this.ontologyMap=new HashMap<String,Ontology>();
		AttributeFactory af=new AttributeFactory();
		this.blogOntology=new Ontology(config.getValue("blogOntologyFile"),af);
		this.ontologyMap.put(config.getValue("blogOntologyFile"), this.blogOntology);
		this.newsOntology=new Ontology(config.getValue("newsOntologyFile"),af);
		this.ontologyMap.put(config.getValue("newsOntologyFile"), this.newsOntology);
		this.postOntology=new Ontology(config.getValue("postOntologyFile"),af);
		this.ontologyMap.put(config.getValue("postOntologyFile"), this.postOntology);
		//BufferedStorer init
		int fileLimit=Integer.parseInt(config.getValue("outputFileSize"));
		String encoding=config.getValue("fileEncoding");
		this.blogStorer=new BufferedStorer(fileLimit, config.getValue("blogDir"), config.getValue("blogPrefix"), encoding);
		this.postStorer=new BufferedStorer(fileLimit, config.getValue("postDir"), config.getValue("postPrefix"), encoding);
		this.newsStorer=new BufferedStorer(fileLimit, config.getValue("newsDir"), config.getValue("newsPrefix"), encoding);
	}
	
	private void testOntology()
	{
		this.initOntology(Config.getNewInstance());
		for (Attribute a:this.blogOntology.getAttributes())
		{
			System.out.println(a.getTypeName());
			for (Wrapper w:a.getWrappers())
			{
				System.out.println(w.getUrlPattern());
				System.out.println(w.getContentPattern());
			}
		}
	}
}

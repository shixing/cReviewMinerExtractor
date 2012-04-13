/**
 * 
 */
package extract.webPageFinder;

import tsinghua.crawlers.messages.Storage.PageResult;

import com.google.protobuf.TextFormat;
import com.google.protobuf.TextFormat.ParseException;

import extract.elements.LocalPage;
import extract.elements.Page;
import extract.elements.SimplePage;

/**
 * @author shixing
 * ��ĵõ�
 */
public class PageFactory {
	public Page getPage(String url)
	{
		Page p=new LocalPage();
		p.setUrl(url);
		p.initAll();
		return p;
	}
	
	public Page getPage(String url,String content)
	{
		Page p=new SimplePage();
		p.setUrl(url);
		p.setContent(content);
		return p;
	}
	public Page getPagebyXML(String xmlString)
	{
		PageResult.Builder pgResultBuilder = PageResult.newBuilder();
		try {
			TextFormat.merge(xmlString, pgResultBuilder);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PageResult pgResult = pgResultBuilder.build();
		PageFactory pf=new PageFactory();
		Page curPage=null;
		curPage=pf.getPage(pgResult.getUrl(), pgResult.getHtml());
		curPage.setCrawler(pgResult.getCrawlerName());
		curPage.getPo().setPageURL(curPage.getUrl());
		return curPage;
	}
}

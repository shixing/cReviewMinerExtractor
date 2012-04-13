/**
 * 
 */
package extract.generater;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import org.htmlparser.Parser;

import extract.elements.HTree;
import extract.elements.Page;

/**
 * @author shixing
 *
 */
public class Generator {
	/**
	 * 
	 * ���ܣ� �������page�У��õ������õ�dataRecord����
	 * ����ֵ˵���� ��
	 * @param page �����page
	 */
	public void generate(Page page,int matchStringDepth)
	{
		HTree tree=new HTree(matchStringDepth);
		try {
			Parser parser=new Parser();
			parser.setInputHTML(page.getContent());
			tree.buildTree(parser);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.println("generalize");
		tree.generalizeTree();
		//System.out.println("outputGeneralizeString");
		page.setTree(tree);
	}
}

/**
 * 
 */
package extract.extractor;

import extract.elements.Page;
import extract.ontology.Ontology;
import extract.ontology.PageOutput;

/**
 * @author shixing
 * ������һ����ȡ������ʵ�ֵĽӿ�
 */
public interface Extractor {
	/**
	 * 
	 * ���ܣ���һ��ҳ�棨page���У����ontologyȥ��ȡ����,��������page�����pageoutput��
	 * ����ֵ˵����
	 * @param page 
	 * @return 
	 */
	public void extract(Page page,Ontology ontology);
}

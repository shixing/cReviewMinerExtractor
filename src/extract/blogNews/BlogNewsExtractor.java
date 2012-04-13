/**
 * 
 */
package extract.blogNews;

import java.util.ArrayList;

import extract.filter.*;
import extract.elements.Page;
import extract.extractor.Extractor;
import extract.filter.PageType;
import extract.ontology.Ontology;
import extract.ontology.PageOutput;

/**
 * @author shixing
 *
 */
public class BlogNewsExtractor implements Extractor {

	/* (non-Javadoc)
	 * @see extract.extractor.Extractor#extract(extract.elements.Page, extract.ontology.Ontology)
	 */
	@Override
	public void extract(Page page, Ontology ontology) {
		// TODO Auto-generated method stub
		WrapperProcessor processor = new WrapperProcessor(page.getPageType(),
				ontology);
		processor.processOnePage(page);
	}
}

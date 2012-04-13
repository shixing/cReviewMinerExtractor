/**
 * 
 */
package extract.ontology;

/**
 * @author shixing
 *
 */
public class SynonymAttributeFactory extends AttributeFactory {
	public Attribute getNewInstance()
	{
		return new SynonymAttribute();
	}
	
}

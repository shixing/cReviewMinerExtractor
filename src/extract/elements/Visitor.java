/**
 * 
 */
package extract.elements;

/**
 * @author shixing
 *
 */
public interface Visitor {
	public abstract void visitor(AbstractHNode ahnode);
	public abstract String stringVisitor(AbstractHNode abnode,int level);
}


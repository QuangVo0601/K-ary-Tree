import java.util.Iterator;

/**
 * Define an interface of related Iterator methods that need to be implemented.
 * @author CS310 professors.
 * @param <T> the type of the value in iterators.
 */
interface TreeIterable<T> {
	public Iterator<T> getLevelOrderIterator();
	public Iterator<T> getPreOrderIterator();
	public Iterator<T> getPostOrderIterator();
}
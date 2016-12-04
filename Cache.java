/**	
 * @param <T> 
 */
public class Cache<T> {
	private final int MAX_SIZE;

// thought of using dll instead of creating two list like in lab1( still not sure how we are supposed to do it)
// still not sure what all the files we need but if needed i guess we could use concept of dll from 221 class.


	private DoubleLinkedList<T> list = null;
	
	public Cache(int maxSize){
		MAX_SIZE = maxSize;
		list = new DoubleLinkedList<T>();
	}

	/**
	 * it Searches cache for the given element
	 * moves the element to the front of the cache 
	 * \creates a new element at the front
	 * @param element
	 * @return boolean 
	 */
	public T getObject(T element){

	  removeObject(element);
		return addObject(element);
	}
	
	/**
	 * adding an element to the front of the cache and then trimming the
	 * cache to its MAX_SIZE if needed.
	 * @param element
	 */
	public T addObject(T element){
		list.addToFront(element);
		while (list.size() > MAX_SIZE){
		return list.removeLast();
		}
		return null;
	}
	
	public boolean isEmpty(){
		return list.isEmpty();
	}
	/**
	 * removes an element from the cache
	 * @param element
	 */
	public boolean removeObject(T element){
		try{
			list.remove(element);
			return true;
		}catch (ElementNotFoundException e){
			return false;
		}
	}
	
	public T removeFront(){
		return list.removeFirst();
	}

	public void clearCache(){
		list.clear();
	}
}

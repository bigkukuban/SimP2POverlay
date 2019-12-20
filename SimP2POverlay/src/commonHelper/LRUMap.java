package commonHelper;

public class LRUMap<K,V> extends org.apache.commons.collections4.map.LRUMap<K,V> implements ForwardPointerMap<K,V> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4223731322242221523L;

	public  LRUMap(int size)  
	{
		super(size);
	}
}

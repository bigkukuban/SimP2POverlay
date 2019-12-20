package commonHelper;

public interface ForwardPointerMap<K,V> extends java.util.Map<K,V>{
	
	public V get(Object key, boolean updateCache);

}

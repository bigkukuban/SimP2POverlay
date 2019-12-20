package commonHelper;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class LRDMap<K,V> implements ForwardPointerMap<K,V>
{

	public class ItemDensity
	{
		
		public ItemDensity(K key, double density)
		{
			ItemKey = key;
			DensityValue = density;	
		}
	
		public K ItemKey;
		public double DensityValue;		
	}
	
	
	//wie build always 5 intervals 

	public LRDMap()
	{	
		_referenceHistoryLength =  _maxSize *10;
		_intervalLength = _referenceHistoryLength / 5;
	}
	

	public LRDMap(int maxSize)
	{		
		_maxSize = maxSize; 
		_referenceHistoryLength =  _maxSize *10;
		_intervalLength = _referenceHistoryLength / 5;
	}
	
	
	//maximal size of the given cache , maximal contained number of elements
	int _maxSize = 100;
	
	// size for intervals 
	int _intervalLength = 100;
	double _constatValue_C3 = 2;
	
	// length of the reference history 
	int _referenceHistoryLength;		
	LinkedList<ItemDensity> _cachedItemsDensities = new LinkedList<ItemDensity>();
	
	TreeMap<K,V> _cachedItems = new TreeMap<K,V>();
	LinkedList<K> _referencesHistory = new LinkedList<K>();
	
	@SuppressWarnings("unchecked")
	@Override
	public Set entrySet() {
		return _cachedItems.entrySet();
	}

	@Override
	public void clear() {
		_cachedItems.clear();		
		_referencesHistory.clear();
	}

	@Override
	public boolean containsKey(Object key) 
	{
		return _cachedItems.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value)
	{						
		return _cachedItems.containsValue(value);
	}

	@SuppressWarnings("unchecked")
	@Override
	public V get(Object key, boolean updateCache) 
	{
		K typedKey = (K)key;
		
		// to enter value into referencesHistory
		if(updateCache)
		{
			HandleAccessToKey(typedKey);
		}
				
		return _cachedItems.get(typedKey);		
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public V get(Object key) 
	{								
		K typedKey = (K)key;
		
		// to enter value into referencesHistory
		HandleAccessToKey(typedKey);		
		return _cachedItems.get(typedKey);
	}

	@Override
	public boolean isEmpty() {
		return _cachedItems.isEmpty();
	}

	@Override
	public Set<K> keySet() {
		return _cachedItems.keySet();
	}

	@Override
	public int size() {
		return _cachedItems.size();
	}

	@Override
	public Collection<V> values() 
	{
		return _cachedItems.values();
	}	

	
	@SuppressWarnings("unchecked")
	@Override
	public Object put(Object key, Object value) {
		
		// if max size not reached, add item and recalculate the RD-Values
		//if the key is already contained: only replace the object an return
		if(_cachedItems.containsKey((K)key))
		{
			HandleAccessToKey((K)key);
			RecalculateRDValues();
			return _cachedItems.put((K)key, (V)value);
		}
		
		
		if(_cachedItems.size() <_maxSize)
		{
			_cachedItemsDensities.add(new ItemDensity((K)key,0));
			_cachedItems.put((K)key, (V)value);			
			
		} else 
		{
			RecalculateRDValues();
			//remove the item with lowest density
			ItemDensity oldItem = _cachedItemsDensities.getLast();
			_cachedItemsDensities.remove(oldItem);
			_cachedItems.remove(oldItem.ItemKey);			
			
			_cachedItemsDensities.add(new ItemDensity((K)key,0));
			_cachedItems.put((K)key, (V)value);						
		}
					
		HandleAccessToKey((K)key);
		RecalculateRDValues();
		return null;
	}

	@Override
	public void putAll(Map m)
	{
		Iterator it = m.entrySet().iterator();
		
	    while (it.hasNext()) 
	    {
	    	 Map.Entry pair = (Map.Entry)it.next();
	    	 put(pair.getKey(), pair.getValue());
	    }
	}

	@Override
	public V remove(Object key) 
	{
		V obj = _cachedItems.remove(key);
		
		ItemDensity oldEntry = null;
		
		for(ItemDensity itm : _cachedItemsDensities)
		{
			if(itm.ItemKey == key)
			{
				oldEntry = itm;
			}
		}
		_cachedItemsDensities.remove(oldEntry);
		
		RecalculateRDValues();
		
		return obj;		
	}


	private void HandleAccessToKey(K key)
	{
		_referencesHistory.addFirst(key);
		
		if(_referencesHistory.size() > this._referenceHistoryLength)
		{
			_referencesHistory.removeLast();
		}
	}
	
	private void RecalculateRDValues()
	{
		for(ItemDensity itm : _cachedItemsDensities)
		{		
			// calculate densityy for given 
			itm.DensityValue = CalculateDensityForGivenKey(itm.ItemKey);					
		}
		
		// sort _cachedItemsDensities by density		
		Collections.sort(_cachedItemsDensities, new Comparator<ItemDensity>(){

			@Override
			public int compare(LRDMap<K, V>.ItemDensity arg0, LRDMap<K, V>.ItemDensity arg1) 
			{
				if(arg0.DensityValue > arg1.DensityValue)
				{
					return -1;
				}				
				if(arg0.DensityValue < arg1.DensityValue)
				{
					return 1;
				}				
				return 0;
			}			
		});
		
	}
	
	
	private double CalculateDensityForGivenKey(K key)
	{
		//determine the oldest reference and Sum of all References (consider the age)
		//and calculate the density
		
		if(_referencesHistory.size() == 0) return 0.0;
		
		int iCurrentItem =0;
		int iPosOldestItem = 0;		
		double referenceCounter = 0.0;
		
		//TODO: check the order of items, we should start from the youngest item
		for(K keyIn : _referencesHistory)
		{
			iCurrentItem++;		
			
			if(keyIn == key)
			{
				iPosOldestItem = iCurrentItem;
				
				int currentInterval = iCurrentItem / _intervalLength + 1;
				
				if(currentInterval == 1)
				{
					// in the youngest interval, the references have always the value 1
					referenceCounter = referenceCounter +  1;
					
				} else 
				{
					
					// the older intervals have less influence on the reference density
					referenceCounter = referenceCounter +  1/(_constatValue_C3 * currentInterval);	
				}												
			}
		}
		
		double divider = (_referencesHistory.size() - iPosOldestItem);
		
		if(divider <= 0) divider = 1; 
		
		return referenceCounter/divider;
	}


	
}

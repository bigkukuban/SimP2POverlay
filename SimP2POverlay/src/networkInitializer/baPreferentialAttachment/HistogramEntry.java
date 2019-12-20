package networkInitializer.baPreferentialAttachment;

import java.util.ArrayList;

import peersModel.interfaces.IPeer;

public class HistogramEntry {
	public double Probability;
	public ArrayList<IPeer> IssuedItems;

	public HistogramEntry()
	{
		Probability= 0;
		IssuedItems = new ArrayList<IPeer>();	
	}
	
	public HistogramEntry(ArrayList<IPeer> issuedItems, double probability) 
	{
		IssuedItems = issuedItems;
		Probability = probability;
	}
}
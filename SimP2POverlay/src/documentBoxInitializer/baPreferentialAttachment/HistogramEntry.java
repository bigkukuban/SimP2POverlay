package documentBoxInitializer.baPreferentialAttachment;

import java.util.ArrayList;

import documentTreeModel.interfaces.IDocumentBox;

public class HistogramEntry {
	public double Probability;
	public ArrayList<IDocumentBox> IssuedItems;

	public HistogramEntry()
	{
		Probability= 0;
		IssuedItems = new ArrayList<IDocumentBox>();	
	}
	
	public HistogramEntry(ArrayList<IDocumentBox> issuedItems, double probability) 
	{
		IssuedItems = issuedItems;
		Probability = probability;
	}
}
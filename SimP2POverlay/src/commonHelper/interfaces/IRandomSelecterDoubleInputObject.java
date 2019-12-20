package commonHelper.interfaces;

public interface IRandomSelecterDoubleInputObject
{
	public Object GetReferencedObject();
		
	//issued value of 0 will lead to probability of 0, thus this item will never be selected.
	public double GetIssuedValue();
		
}

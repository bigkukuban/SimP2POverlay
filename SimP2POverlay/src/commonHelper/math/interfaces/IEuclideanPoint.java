package commonHelper.math.interfaces;

public interface IEuclideanPoint 
{
		double [] GetComponents();
		long GetDimensions();
		IEuclideanPoint CloneMe();
		double GetDistanceToOther(IEuclideanPoint point);
}

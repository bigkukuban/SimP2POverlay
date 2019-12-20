package commonHelper.math.interfaces;

public interface IVector extends IEuclideanPoint{

	 double GetLength();
	 double [] GetUnit();
	 IVector GetUnitVector();	 
	 IVector AddVector(IVector other);
	 IVector SubstractVector(IVector other);	 
	 IVector MultiplicateWithScalar(double value);
	 double ScalaProduct(IVector other);
	
	
}

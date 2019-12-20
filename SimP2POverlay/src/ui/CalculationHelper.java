package ui;

 public  class CalculationHelper {

	 private static float calc_factor = 0.03f;
	 private static float calc_factor_heigh = 0.03f;
	 
	public static int RecalcPositionInUserValue(float position)
	{
		return (int) ((position + 1.0f) / calc_factor);
	}
	
	
	public static float RecalcPositionInModelValue(int iUserValue)
	{
		return (float)iUserValue*calc_factor - 1.0f;
	}
	
	
	public static int RecalcHeighInUserValue(float position)
	{
		return (int) ((position) / calc_factor_heigh);
	}
	
	
	public static float RecalcHeighInModelValue(int iUserValue)
	{
		return (float)iUserValue*calc_factor_heigh;
	}
}

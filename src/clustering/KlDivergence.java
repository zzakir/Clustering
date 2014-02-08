package clustering;

public class KlDivergence 
{
	public static double calculateContinuous(Double[] p1, Double [] p2)
	{
		double kldiv = 0;
		for(int i=0;i<p1.length;i++)
		{			
			if(p1[i] == 0){continue;}			
			if(p2[i] == 0){continue;}			
			kldiv += Math.log(p1[i]/p2[i])/Math.log(2);			
		}		
		return Math.abs(kldiv)/p1.length;
	}
	
	public static double calculateDiscrete(Double[] p1, Double [] p2)
	{
		double kldiv = 0;
		for(int i=0;i<p1.length;i++)
		{			
			if(p1[i] == 0){continue;}			
			if(p2[i] == 0){continue;}			
			kldiv += p1[i] * (Math.log(p1[i]/p2[i])/Math.log(2));			
		}		
		return Math.abs(kldiv);
	}
}

package clustering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import dSet.DataSet;

public class KMedoid 
{	
	private int clusters;	
	private Random rd;		
	private double clusteringCost;	
	private int iterations =0;
	private ArrayList<ArrayList<String>> current_output;
	private ArrayList<ArrayList<String>> prev_output;
	HashMap<String,Double[]> pdfList;
	private ArrayList<String> data;
	private String[] medoids;	
	
	public KMedoid(int clusters,ArrayList<String> data)
	{
		this.clusters = clusters;					
		current_output = new ArrayList<ArrayList<String>>(clusters);
		pdfList = new HashMap<String,Double[]>();
		prev_output = new ArrayList<ArrayList<String>>(clusters);
		medoids = new String[clusters];
		this.data=data;
		rd = new Random(System.currentTimeMillis());			
	}
	public ArrayList<ArrayList<String>> cluster()
	{		
		if(clusters>data.size())
		{
			System.out.println("Clusters must be less than the number of data elements");			
		}
		else if(clusters==0)
		{
			System.out.println("Clusters must be greater than zero");
		}
		else if (clusters==data.size())
		{
			for(int i=0;i<clusters;i++)
			{
				current_output.add(new ArrayList<String>());
				current_output.get(i).add(data.get(i));
			}
		}
		else
		{
			for(int i=0;i<clusters;i++)
			{
				current_output.add(new ArrayList<String>());
				prev_output.add(new ArrayList<String>());
				medoids[i] = "";
			}
						
			for(int i=0;i<data.size();i++)
			{				
				Double[] pd = DataSet.continuousPobabilityDistribution(data.get(i));				
				pdfList.put(data.get(i),pd);
			}
			
			for(int i=0;i<clusters;i++)
			{
				medoids[i] = chooseRandomMedoids();									
			}				
							
			assign();													
			
			System.out.println("Iterations : "+iterations);
			System.out.println("Final Medoids are : ");
			for(int i=0;i<medoids.length;i++)
			{
				System.out.println(medoids[i]);
			}
		}		
		return current_output;
	}
	
	public void assign()
	{
		
		int[] output = new int[data.size()];	
		double[] cost = new double[data.size()];		
		
		for(int i=0;i<data.size();i++)
		{			
			int index =0;
			if(data.get(i).equals(medoids[0]))
			{
				output[i] = 0;
				cost[i] = 0;
			}
			else
			{
				double distance = KlDivergence.calculateContinuous(pdfList.get(data.get(i)),pdfList.get(medoids[0]));
				for(int j=1;j<medoids.length;j++)
				{						
					if(data.get(i).equals(medoids[j]))
					{
						index = j;
						distance = 0;
						break;
					}
					else
					{
						double tempDistance = KlDivergence.calculateContinuous(pdfList.get(data.get(i)),pdfList.get(medoids[j]));
						if(tempDistance<distance)
						{
							distance = tempDistance;							
							index = j;														
						}						
					}				
				}
				output[i] = index;
				cost[i] = distance;
			}			
		}		
						
		for(int i=0;i<cost.length;i++)
		{
			clusteringCost = clusteringCost+cost[i];				
		}			
					
				
		assignValuesToMedoids(output);
		swapMedoids();
	}	
	
	public void assignValuesToMedoids(int assignment[])
	{
		prev_output.clear();		
		prev_output.addAll(current_output);
		for(int i=0;i<clusters;i++)
		{	
			if(current_output.get(i).size()>0)
			{				
				current_output.remove(i);							
				current_output.add(i, new ArrayList<String>());
			}			
			for(int j=0;j<assignment.length;j++)
			{				
				if(assignment[j] == i)
				{					
					ArrayList<String> str = current_output.get(i);						
					str.add(data.get(j));									
					current_output.remove(i);
					current_output.add(i,str);									
				}
			}								
		}
	}
	
	public void swapMedoids()
	{											
		boolean swapOK = false;	
		boolean stop = false;			
		String temp_medoids[] = medoids.clone();		
			
		String temp_medoid = chooseRandomMedoids();
		while(!stop)
		{						
			for(int j=0;j<clusters;j++)
			{		
				//System.out.println(iterations);
				iterations++;
				temp_medoids[j] = temp_medoid;						
				swapOK = reCalculateCost(temp_medoids);
				
				if(swapOK)
				{							
					medoids[j] = temp_medoid;
					temp_medoids = medoids.clone();					
				}
				else
				{
					if(stopSwap())
					{
						stop =true;
						break;
					}					
				}		
				temp_medoid = chooseRandomMedoids();
			}								
			stop = stopSwap();				
		}														
	}
	
	public String chooseRandomMedoids()
	{
		String medoid =data.get(rd.nextInt(data.size()));			
		boolean flag = isMedoidAlreadySelected(medoid);			
		while(flag)
		{					
			medoid = data.get(rd.nextInt(data.size()));
			flag = isMedoidAlreadySelected(medoid);										
		}			
		return medoid;
	}
	
	public boolean stopSwap()
	{						
		if(prev_output.equals(current_output))
		{
			return true;
		}
		else
		{
			return false;
		}		
	}
	
	public boolean isMedoidAlreadySelected(String medoid)	
	{						
		for(int i=0;i<medoids.length;i++)
		{			
			if(medoids[i].equals(medoid))
			{				
				return true;
			}
		}		
		return false;
	}
	
	public boolean reCalculateCost(String[] medoids)
	{		
		int[] output = new int[data.size()];
		boolean swapOK = false;
		double[] cost = new double[data.size()];		
		double newClusteringCost = 0;		
				
		for(int i=0;i<data.size();i++)
		{									
			int ind = 0;
			if(data.get(i).equals(medoids[0]))
			{
				output[i] = 0;
				cost[i] = 0;
			}
			else
			{
				double distance = KlDivergence.calculateContinuous(pdfList.get(data.get(i)),pdfList.get(medoids[0]));
				for(int j=1;j<medoids.length;j++)
				{	
					if(data.get(i).equals(medoids[j]))
					{
						ind = j;
						distance = 0;
						break;
					}
					else
					{
						double tempDistance = KlDivergence.calculateContinuous(pdfList.get(data.get(i)),pdfList.get(medoids[j]));
						if(tempDistance<distance)
						{
							distance = tempDistance;
							ind = j;														
						}						
					}				
				}	
				output[i] = ind;
				cost[i] = distance;
			}			
		}		
				
		for(int i=0;i<cost.length;i++)
		{
			newClusteringCost = newClusteringCost+cost[i];
		}						
		//System.out.println(clusteringCost+"->"+newClusteringCost);	
		double DEC = clusteringCost-newClusteringCost; 		
		if(DEC>0)
		{
			clusteringCost = newClusteringCost;
			assignValuesToMedoids(output);			
			swapOK = true;			
		}	
		else
		{
			double new_dec = Math.exp(DEC*Math.log(iterations));			
			if(new_dec>Math.random())
			{
				swapOK =true;
				assignValuesToMedoids(output);				
			}
		}				
		
		return swapOK;
	}
}

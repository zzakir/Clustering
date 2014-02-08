package dSet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

public class DataSet extends ArrayList<String>
{					
	private static final long serialVersionUID = 1L;
	private File dataFile;
	private String seperator;
	public static int totalValues;	
	
	public DataSet(File dataFile,String separator,int totalValues)
	{
		this.dataFile = dataFile;
		this.seperator = separator;
		DataSet.totalValues = totalValues;
	}

	public ArrayList<String> loadData()
	{
		ArrayList<String> data = new ArrayList<String>();
		try
		{			
			FileInputStream fi = new FileInputStream(dataFile);
			BufferedReader b = new BufferedReader(new InputStreamReader(fi));		
			String line = b.readLine();
			while(line!=null)
			{
				StringTokenizer st = new StringTokenizer(line,seperator);
				String formattedData="";
				String temp="";
				int count=0;
				while(st.hasMoreElements())
				{					
					if(count==0)
					{
						temp=st.nextToken();
					}
					if(count<=totalValues)
					{
						if(count==0)
						{
							formattedData="[";
						}
						else if(count==totalValues)
						{
							formattedData= formattedData+st.nextToken()+"];"+temp;
						}
						else
						{
							formattedData= formattedData+st.nextToken()+",";
						}						
					}										
					count++;
				}		
				if(!"".equalsIgnoreCase(formattedData))
				{					
					data.add(formattedData);
				}				
				line= b.readLine();
			}
			fi.close();
			b.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}		
		return data;
	}
	
	public static Double[] discreteProbabilityDistribution(String data)
	{	
		int max_rating = 5;
		Double[] pmf = new Double[max_rating];
		Double[] smoothed_pmf = new Double[max_rating];
		String str[] = data.split(";");		
		String valStr = str[0].substring(1, str[0].length()-1);
		String[] valArr = valStr.split(",");
		HashMap<Integer, Integer> valMap = new HashMap<Integer,Integer>();
		for(int i=0;i<valArr.length;i++)
		{
			int val = Integer.parseInt(valArr[i]);			
			if(i==0)
			{
				valMap.put(val,1);
			}
			else
			{
				if (valMap.containsKey(val))
				{
					int count = valMap.get(val);
					count++;
					valMap.put(val, count);
				}
				else
				{
					valMap.put(val,1);
				}				
			}
		}
		
		double mean = calculateMean(valArr);
		double h = calculateVariance(valArr,mean);
		
		for(int i=0;i<max_rating;i++)
		{
			if(valMap.get(i+1) != null)
			{
				int count = valMap.get(i+1); 
				pmf[i] = (double)count/totalValues;
			}			
			else
			{
				pmf[i] = 0.0;
			}
			smoothed_pmf[i] = (pmf[i]+h)/(1+(h*max_rating));
		}
		return smoothed_pmf;
	}
	
	public static Double[] continuousPobabilityDistribution(String data)
	{		
		Double[] pdf = new Double[totalValues];
		
		String str[] = data.split(";");		
		String valStr = str[0].substring(1, str[0].length()-1);
		String[] valArr = valStr.split(",");		

		double mean = calculateMean(valArr);
		double h = calculateVariance(valArr,mean);
		Double[] smoothed_pdf = new Double[totalValues];		
		
		double val1 = 1/(totalValues*h*Math.sqrt(2*Math.PI));						
		
		for(int i=0;i<totalValues;i++)
		{
			double sum =0;
			for(int j=0;j<totalValues;j++)
			{					
				double val2 = Math.pow((Double.valueOf(valArr[i])-Double.valueOf(valArr[j])),2);
				double val3 = 2 * Math.pow(h, 2);
				double val4 = Math.exp(-val2/val3);
				sum = sum+ val4;																
			}			
			pdf[i] =val1*sum;
			smoothed_pdf[i] = (pdf[i]+h)/(1+(h*totalValues));			
		}				
		return smoothed_pdf;
	}
	
	public static double calculateMean(String[] data)
	{		
		double mean=0;		
		for(int i=0;i<totalValues;i++)
		{			
			mean = mean+Double.valueOf(data[i]);
		}
		mean = mean/totalValues;
		return mean;
	}
	
	public static double calculateVariance(String[] data, double mean)
	{
		double var=0;			
		double sd=0;
		double h=0;
		
		for(int i=0;i<totalValues;i++)
		{
			var = var + Math.pow((Double.valueOf(data[i])-mean),2);
		}		
		var = var/totalValues;
		sd = Math.sqrt(var);
		h = 1.06*sd*Math.pow(totalValues, -0.2);
		return h;
	}	
}

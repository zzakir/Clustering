package classes;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

import clustering.KMedoid;

import dSet.DataSet;

public class Test 
{
	public static void main(String[] args)
	{
		System.out.println("Enter path of your data file : ");
		Scanner sc = new Scanner(System.in);
		String path = sc.next(); 
		System.out.println("Enter number of clusters : ");
		int no_of_clusters = sc.nextInt();		
		DataSet ds = new DataSet(new File(path),"  ",12);
		sc.close();
		ArrayList<String> data = ds.loadData();		
		
		KMedoid km = new KMedoid(no_of_clusters,data);
		ArrayList<ArrayList<String>> output = km.cluster();
		
		File dir = new File("E:/Clusters/");

		for(File file: dir.listFiles()) file.delete(); 
		
		for(int i=0;i<output.size();i++)
		{
			ArrayList<String> cluster =  output.get(i);
			try
			{				
				File f = new File("E:/Clusters/Cluster"+(i+1)+".data");
				f.createNewFile();
				FileOutputStream is = new FileOutputStream(f);
		        OutputStreamWriter osw = new OutputStreamWriter(is);    
		        Writer w = new BufferedWriter(osw);		        
				Iterator<String> itr = cluster.iterator();
				while(itr.hasNext())
				{
					String str = itr.next();
					String data_val[] = str.split(";");
					String date = data_val[1];
					w.write(date+"  ");
					String val = data_val[0];
					String valStr =val.substring(1, val.length()-1);
					String valArr[] = valStr.split(",");
					for(int t=0;t<valArr.length;t++)
					{
						w.write(valArr[t]+"  "); 
					}					
					w.write("\r\n");
				}
				w.close();				
				osw.close();
				is.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}			
			System.out.println("Size of Cluster "+(i+1)+" : "+cluster.size());
 		}		
	}
}

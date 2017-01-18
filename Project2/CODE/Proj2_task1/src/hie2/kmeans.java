package hie2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

public class kmeans {
	static DecimalFormat decim = new DecimalFormat("#.##");
	static LinkedHashMap<Double,ArrayList<Double>> hm= new LinkedHashMap<Double,ArrayList<Double>>();

	static TreeMap<Double,Double> dtPt_cl_2=new TreeMap<Double,Double>();
	static ArrayList<Double> alForPickRandom=new ArrayList<Double>();
	static int num_clusters;
	static int c;
	static int flag=0;
	//-----------------------------------------------------------------------------------------------------------Main		

	public static void main(String[] args) throws NumberFormatException, IOException {
		// TODO Auto-generated method stub
		System.out.println("Enter number of clusters:");
		@SuppressWarnings("resource")
		Scanner sc= new Scanner(System.in);
		num_clusters= sc.nextInt();
		@SuppressWarnings("resource")
		BufferedReader br = new BufferedReader(new FileReader("/Users/sat/Desktop/Project2_Task1/new_data_Set_2.csv"));
		String line = null;

		while ((line = br.readLine()) != null) {
			ArrayList<Double> al=new ArrayList<Double>();		      
			String[] values = line.split(",");
			c=values.length-2;
			for(int i=2;i<values.length;i++)
			{
				al.add(Double.parseDouble((values[i]))); 	
			}		
			alForPickRandom.add(Double.parseDouble(values[0]));
			hm.put(Double.parseDouble(values[0]), al);			       
		}
		double[] initCentroids=pickRandomStartClusters(num_clusters);

		double[] k= recursive(initCentroids);	
		TreeSet<Double> set1 = new TreeSet<Double>(dtPt_cl_2.values());
		LinkedHashMap<Double, Double> hm_mapping=new LinkedHashMap<Double,Double>();
		double i=1;
		for (Double s : set1) {
			hm_mapping.put(i, s);
			i++;
		}

		TreeMap<Double,Double> tm_replace=new TreeMap<Double,Double>();
		tm_replace.putAll(dtPt_cl_2);

		for(Map.Entry<Double, Double>e:tm_replace.entrySet())
		{
			double d=e.getValue();

			for(Entry<Double, Double> e2:hm_mapping.entrySet())
			{
				if(d==e2.getValue())
				{
					double a2=e.getKey();
					double b2=e2.getKey();
					tm_replace.put(e.getKey(), e2.getKey());				
				}
			}
		}
		printtoFile(tm_replace);
	}

	//printToFILE		

	private static void printtoFile(TreeMap<Double, Double> tm_replace) throws IOException {
		// TODO Auto-generated method stub

		int recordsToPrint = tm_replace.size();

		FileWriter fstream;
		BufferedWriter out;
		fstream = new FileWriter("/Users/sat/Desktop/Project2_Task1/res_d2_kmeans.txt");
		out = new BufferedWriter(fstream);
		int count = 0;
		Iterator<Entry<Double, Double>> it = tm_replace.entrySet().iterator();
		while (it.hasNext() && count < recordsToPrint) {
			Entry<Double, Double> pairs = it.next();
			Double a1 = pairs.getKey();
			String s1=String.valueOf(a1);
			String[] s2=s1.split("\\.");
			if(s2[1].equals(String.valueOf(0)))
			{	       
				Integer i1 = a1.intValue();	        
				Double a2 = pairs.getValue();
				Integer i2 = a2.intValue();	 
				out.write(i1+"\t"+i2 + "\n");	    	       
			}
			else       	
			{

			}
			count++;
		}	   
		out.close();			
	}


	//---------------------------------------------------------------------------------------------recursive recursive

	private static double[] recursive(double[] initCentroids) {
		TreeMap<Double,Double> tm1= assignClusters(initCentroids);
		if(flag!=0 && tm1.equals(dtPt_cl_2))
		{
			double[] d={0};
			return d;
		}
		else 
		{
			double[] d=recompute(tm1);
			flag=1;
			dtPt_cl_2.clear();
			dtPt_cl_2.putAll(tm1);
			return recursive(d);
		}
	}

	//recompute
	private static double[] recompute(TreeMap<Double, Double> tm1) {
		// TODO Auto-generated method stub
		TreeSet<Double> set1 = new TreeSet<Double>(tm1.values());
		ArrayList<Double> al_set=new ArrayList<Double>();
		al_set.addAll(set1);
		TreeMap<Double,ArrayList<Double>> tm_loc=new TreeMap<Double,ArrayList<Double>>();		
		for(int i=0;i<al_set.size();i++)
		{
			double d1=al_set.get(i);
			ArrayList<Double> al_dtPt_cl=new ArrayList<Double>();
			for(Map.Entry<Double, Double>e:tm1.entrySet())
			{
				double d2=e.getValue();
				if(d1==d2)
					al_dtPt_cl.add(e.getKey());
			}
			tm_loc.put(d1, al_dtPt_cl);						 
		}

		ArrayList<Double> List_newCentroid=new ArrayList<Double>();
		for(Map.Entry<Double,ArrayList<Double>>e:tm_loc.entrySet())
		{
			ArrayList<Double> al_dtPnts=new ArrayList<Double>(e.getValue());
			List_newCentroid.add(computeAverageOfAl(al_dtPnts));
		}	
		double[] arr_newCentroid= new double[List_newCentroid.size()];
		for(int i=0;i<List_newCentroid.size();i++)
			arr_newCentroid[i]=List_newCentroid.get(i);
		return arr_newCentroid;
	}

	//----------------------------------------------------------------------------------------computeAverageOfAl
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static Double computeAverageOfAl(ArrayList<Double> al_dtPnts) {

		ArrayList<List> d=new ArrayList<List>(hm.values());
		ArrayList<Double> l1=new ArrayList<Double>();
		l1=(ArrayList<Double>) d.get(1);
		int cnt=l1.size();
		int totalPnts=al_dtPnts.size();
		double[][] mat= new double[al_dtPnts.size()][cnt];
		double sum1=0;
		for(int i=0;i<al_dtPnts.size();i++)
			sum1=sum1+al_dtPnts.get(i);    	   
		double cent=Double.parseDouble(decim.format(sum1/al_dtPnts.size()));

		for(int i=0;i<al_dtPnts.size();i++)
		{
			Double m=al_dtPnts.get(i);
			ArrayList<Double> al=new ArrayList<Double>(hm.get(m));
			for(int j=0;j<al.size();j++)
			{
				double k=al.get(j);		  
				mat[i][j]=k;
			}		
		}
		//do columnwise sum
		ArrayList<Double> new_centroid=new ArrayList<Double>();
		for(int j=0;j<mat[0].length;j++)
		{
			double sum=0;
			for(int i=0;i<mat.length;i++)
				sum=sum+mat[i][j];			
			double res=Double.parseDouble(decim.format(sum/totalPnts));
			new_centroid.add(res);
		}
		
		hm.put(cent, new_centroid);
		return cent;		
	}

	//assignClusters

	private static TreeMap<Double,Double> assignClusters(double[] initCentroids) {
		// TODO Auto-generated method stub
		TreeMap<Double,Double> dtPt_cl_1=new TreeMap<Double,Double>();
		ArrayList<Double> centList=new ArrayList<Double>();
		for(int i=0;i<initCentroids.length;i++)
			centList.add(initCentroids[i]);
		int hm_flag=0;
		for(Map.Entry<Double, ArrayList<Double>>e:hm.entrySet())		 
		{
			double j=e.getKey();
			TreeMap<Double,Double> tm=new TreeMap<Double,Double>();
			for(int i=0;i<initCentroids.length;i++)//all centroids
			{	 
				if(centList.contains(j))
				{
					dtPt_cl_1.put(j, j);
					break;
				}
				else
				{	 
					hm_flag=1;
					double dist_cal=dist(initCentroids[i],j);
					tm.put(dist_cal,initCentroids[i]);					
				}				 
			}	 
			if(hm_flag==1)
			{
				Entry<Double,Double> ent = tm.firstEntry();
				dtPt_cl_1.put(j, ent.getValue());
				hm_flag=0;
			}
		}

		return dtPt_cl_1;

	}
	//dist		

	public static double dist(double i,double j)
	{
		double sum=0;
		ArrayList<Double> l1=new ArrayList<Double>();
		l1=(ArrayList<Double>) hm.get(i);

		ArrayList<Double> l2=new ArrayList<Double>() ;
		l2=(ArrayList<Double>) hm.get(j);
		for(int i1=0;i1<l1.size();i1++)
		{
			double temp= l1.get(i1)-l2.get(i1);
			double temp2=temp*temp;
			sum=sum+temp2;	
		}
		double dist=Double.parseDouble(decim.format(Math.sqrt(sum)));
		return dist;		
	}



	private static double[] pickRandomStartClusters(int num_clusters) {

		// TODO Auto-generated method stub
		double[] start_with= new double[num_clusters];

		Collections.shuffle(alForPickRandom);
		for (int i=0; i< num_clusters; i++) 
			start_with[i]=alForPickRandom.get(i);
		return start_with;
	}

	//printArray		

	public static void printArray(double matrix[][]) 
	{

		for (double[] row : matrix)
			System.out.println(Arrays.toString(row));   	  
	}

}

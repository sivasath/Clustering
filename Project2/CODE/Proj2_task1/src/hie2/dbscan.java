package latest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.script.ScriptException;

public class dbscan {
	static int min_Pts=4;
	static Double e_dist=(double) 0;
	static LinkedHashMap<String,ArrayList<Double>> hm= new LinkedHashMap<String,ArrayList<Double>>();
	static LinkedHashMap<String,ArrayList<String>> clusters= new LinkedHashMap<String,ArrayList<String>>();
	static HashMap<Integer,Integer> node_then_clusters= new HashMap<Integer,Integer>();
	static LinkedHashMap<String,ArrayList<String>> clusters_backup= new LinkedHashMap<String,ArrayList<String>>();
	static double[][] data_mat=null;
	static DecimalFormat decim = new DecimalFormat("#.##");
	static double inf = Double.POSITIVE_INFINITY;


	//to find e-dist
	public static void form_clusters(){
		ArrayList<Double> min=new ArrayList<Double>();
		
		for(int i=0;i<hm.size();i++){
			ArrayList<Double> toFindMin=new ArrayList<Double>();
			for(int j=0;j<hm.size();j++){
				if(i!=j)
					toFindMin.add(data_mat[i][j]);
			}
			min.add(Collections.min(toFindMin));
		}

		double sum = 0;
		for (int i = 0; i < min.size(); i++) 
			sum += min.get(i);
		e_dist=sum / min.size();
		System.out.println("e_dist"+ e_dist);

		//create a common arraylist put all the elemnets that has been allocated to a cluster.
		ArrayList<Integer> beenAssigned=new ArrayList<Integer>();
		Integer cluster_id=0;
		for(int i=0;i<(data_mat.length-1);i++){
			ArrayList<String> clusterArray=new ArrayList<String>();
			for(Integer j=0;j<(data_mat[0].length-1);j++){
				if(i!=j){						
					if(data_mat[i][j]<e_dist && data_mat[i][j] != inf){
						setting_to_zero(j);
						clusterArray.add(Integer.toString(j));
						beenAssigned.add((j));
						node_then_clusters.put(j, cluster_id);
					}
				}
			}

			if(clusterArray.size() > min_Pts){
				clusters.put(cluster_id.toString(),clusterArray );
				cluster_id++;
			}
		}
		ArrayList<String> outliers=new ArrayList<String>();
		for(Integer i=0;i<hm.size();i++){
			if(!beenAssigned.contains(i)){
				outliers.add(i.toString());
				node_then_clusters.put(i, -1);
			}

		}
		 
		clusters.put("-1", outliers);
	}
	public static void printMap() {
		ArrayList<String> toPrint=new ArrayList<String>();


		ArrayList<String> Print=new ArrayList<String>();
		for (Map.Entry entry : clusters.entrySet()) {
			Print=(ArrayList<String>) entry.getValue();
			for(int l=0;l<Print.size();l++){
				System.out.println(entry.getKey() + ", " + Print.get(l));
			}
		}
	}
	public static void setting_to_zero(int k){
		for(int i=0;i<(data_mat.length-1);i++)
			data_mat[i][k]=inf;
	}

	public static void printArray(double matrix[][]) 
	{
		for (double[] row : matrix) 
			System.out.println(Arrays.toString(row));       
	}
	public static void printArrayList(ArrayList<Double> np) 
	{
		for (Double row : np) 
			System.out.println(" min values:  "+row);       
	}



	public static double dist(int i,int j)
	{
		double sum=0;
		ArrayList<ArrayList<Double>> d=new ArrayList<ArrayList<Double>>(hm.values());

		ArrayList<Double> l1=new ArrayList<Double>();
		l1=(ArrayList<Double>) d.get(i);

		ArrayList<Double> l2=new ArrayList<Double>() ;
		l2=(ArrayList<Double>) d.get(j);

		// dist calc

		for(int i1=0;i1<l1.size();i1++)
		{
			double temp= l1.get(i1)-l2.get(i1);
			double temp2=temp*temp;
			sum=sum+temp2;	
		}

		double dist=Double.parseDouble(decim.format(Math.sqrt(sum)));
		return dist;	

	}



	public static void main(String[] args) throws NumberFormatException, IOException, ScriptException {
		// TODO Auto-generated method stub
		BufferedReader br = new BufferedReader(new FileReader("/Users/sat/Desktop/Project2_Task1/cho/cho.csv"));
		String line = null;
		int io=0;
		while ((line = br.readLine()) != null) {
			ArrayList<Double> al=new ArrayList<Double>();
			io++;
			String[] values = line.split(",");
			for(int i=2;i<values.length;i++)
				al.add(Double.parseDouble((values[i])));	 
			hm.put(values[0], al);	    
		}
		data_mat=new double[hm.size()][hm.size()];
		for(int i=0;i<hm.size();i++)
		{		    	
			for(int j=0;j<hm.size();j++)
			{
				if(i==j)
					data_mat[i][j]=0;
				else
					data_mat[i][j]=dist(i,j);		    		  
			}		    	  
		}	
		// cluster formation
		form_clusters();
		print_to_file();

	}
	
	public static void print_to_file() throws IOException,ScriptException{

		FileWriter file1 = new FileWriter("/Users/dny/Desktop/Project2_Task1/cho/result_cho_DBScan.txt");
		BufferedWriter out_File1 = new BufferedWriter(file1);		
		Iterator<Entry<Integer, Integer>> it1 = node_then_clusters.entrySet().iterator();
		while (it1.hasNext() ) {
			Entry<Integer, Integer> pairs = it1.next();
			String toWrite=(pairs.getKey()+"\t" + pairs.getValue());
			System.out.print(toWrite);
			out_File1.write(toWrite);
			out_File1.newLine();
		}
		out_File1.close();
}

}

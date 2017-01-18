package latest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.Map.Entry;

public class hierarchial {
	static LinkedHashMap<String,ArrayList<Double>> hm= new LinkedHashMap<String,ArrayList<Double>>();
	static List<String> temp=new ArrayList<String>();
	static List<String> cluster_list=new ArrayList<String>();
	static double[][] data_mat=null;
	static DecimalFormat decim = new DecimalFormat("#.##");
	static double inf = Double.POSITIVE_INFINITY;
	static ArrayList<String> al_interm= new ArrayList<String>();
	static HashMap<Integer,Integer> hm_end=new HashMap<Integer,Integer>();
	public static void printArray(double matrix[][]) 
	{
		for (double[] row : matrix) 
			System.out.println(Arrays.toString(row));       
	}

	@SuppressWarnings("unchecked")
	public static double dist(int i,int j)
	{
		double sum=0;
		@SuppressWarnings("rawtypes")
		ArrayList<List> d=new ArrayList<List>(hm.values());
		ArrayList<Double> l1=new ArrayList<Double>();
		l1=(ArrayList<Double>) d.get(i);
		ArrayList<Double> l2=new ArrayList<Double>() ;
		l2=(ArrayList<Double>) d.get(j);

		for(int i1=0;i1<l1.size();i1++)
		{
			double temp= l1.get(i1)-l2.get(i1);
			double temp2=temp*temp;
			sum=sum+temp2;	
		}

		double dist=Double.parseDouble(decim.format(Math.sqrt(sum)));
		return dist;	

	}


	//-----------------------------------------------------------------------------------------------------------ComputeMinima		

	public static int[] computeMinima(double matrix[][]) {
		int [] a=new int[2];
		double min=matrix[0][1];
		int min_row=0;
		int min_col=1;
		for(int i=0;i<matrix.length;i++)
		{
			for(int j=0;j<matrix.length;j++)
			{
				boolean flag=false;
				if(i==j)
					flag=true;

				if(flag==false && i>=j)
				{
					if(matrix[i][j] < min)
					{           
						min = matrix[i][j]; 
						min_row=i;
						min_col=j;
					}
				}	    		  
			}	    	  
		}
		a[0]=min_row;
		a[1]=min_col;
		return a;		
	}


	//-----------------------------------------------------------------------------------------------------------refinedMatrix		
	private static double[][] refinedMatrix(double[][] data_mat, int[] res) {

		int row=res[0];
		int col=res[1];
		@SuppressWarnings("unused")
		int s=data_mat[0].length;
		for(int i=0;i<data_mat[0].length;i++)
		{   							
			if(row!=i||col!=i){
				data_mat[row][i]=Math.min(data_mat[row][i], data_mat[col][i]);
				data_mat[i][row]=data_mat[row][i];				
			}			
		}
		for(int i=0;i<data_mat[0].length;i++)
		{
			data_mat[col][i]=inf;
			data_mat[i][col]=inf;
		}



		StringBuilder sb= new StringBuilder();
		String s_p1=temp.get(row);
		s_p1=s_p1.replaceAll("[()]", "");
		String p1[]=s_p1.split(",");
		int k1=Integer.parseInt(p1[0]);


		String s_p2=temp.get(col);
		s_p2=s_p2.replaceAll("[()]", "");
		String p2[]=s_p2.split(",");
		int k2=Integer.parseInt(p2[0]);
		String s1=temp.get(k1);
		String s2=temp.get(k2);
		String cluster=	sb.append("(").append(s1).append(",").append(s2).append(")").toString();
		temp.set(k2,"null1");
		temp.set(k1, cluster);
		cluster_list.add(cluster);
		return data_mat;

	}

	//-----------------------------------------------------------------------------------------------------------rec_fun		

	private static double[][] rec_fn(double[][] data_mat2) {
		// TODO Auto-generated method stub
		//you have dist matrix here;
		int[] res=computeMinima(data_mat);
		data_mat=refinedMatrix(data_mat,res);

		int inf_count=0;
		int null1=0;
		for(int i=0;i<data_mat.length;i++)
		{
			if(data_mat[i][0]==inf)
				inf_count++;

		}
		for(int i=0;i<temp.size();i++)
		{
			if(temp.get(i).equals("null1"))
				null1++;
		}
		if(null1>=(data_mat.length-1))
			return data_mat; 
		else
			return rec_fn(data_mat);

	}	

	public static void main(String[] args) throws NumberFormatException, IOException {
		// TODO Auto-generated method stub
		@SuppressWarnings("resource")
		//ip1
		BufferedReader br = new BufferedReader(new FileReader("/Users/dny/Desktop/Project2_Task1/iyer/iyer.csv"));
		String line = null;
		@SuppressWarnings("unused")
		int io=0;
		while ((line = br.readLine()) != null) {
			ArrayList<Double> al=new ArrayList<Double>();
			io++;

			String[] values = line.split(",");
			@SuppressWarnings("unused")
			int h=values.length;
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

		for(int i=0;i<data_mat.length;i++)
		{
			temp.add(String.valueOf(i));
		}
		data_mat= rec_fn(data_mat);
		@SuppressWarnings("unused")
		String s=cluster_list.get(cluster_list.size()-1);
		beforeend();
		TreeMap<Integer, Integer> treeMap = new TreeMap<Integer, Integer>();
		treeMap.putAll(hm_end);
		printtoFile(treeMap);

	}

	private static void printtoFile(TreeMap<Integer, Integer> treeMap) throws IOException {
		// TODO Auto-generated method stub

		int recordsToPrint = treeMap.size();
		FileWriter fstream;
		BufferedWriter out;
		fstream = new FileWriter("/Users/dny/Desktop/Project2_Task1/iyer/iyer_dnydddbj.txt");
		out = new BufferedWriter(fstream);
		int count = 0;
		Iterator<Entry<Integer, Integer>> it = treeMap.entrySet().iterator();

		while (it.hasNext() && count < recordsToPrint) {
			Entry<Integer, Integer> pairs = it.next();
			Integer a1 = pairs.getKey();
			Integer a2=pairs.getValue();
			out.write(a1+"\t"+a2 + "\n");	    	       
			count++;
		}	   
		out.close();			
	}


	private static void beforeend() {
		// TODO Auto-generated method stub
		String s=cluster_list.get(cluster_list.size()-1);
		@SuppressWarnings("resource")
		Scanner sc = new Scanner(System.in);
		int jump= sc.nextInt();
		int k= rec(s,jump);

		for(int i=0;i<al_interm.size();i++)
		{

			String s_digi=al_interm.get(i);
			@SuppressWarnings("unused")
			char[] ch=new char[s_digi.length()];
			ArrayList<Integer> al_digits=new ArrayList<Integer>();

			String s1=al_interm.get(i);
			String s2=s1.replace("(", "");
			String s3=s2.replace(")", "");

			if(s3.charAt(0)==',')
				s3=	s3.substring(1);
			String[] sArr=s3.split(",");
			int fnu=sArr.length;

			for(int j=0;j<sArr.length;j++)
			{
				int h=Integer.parseInt(sArr[j]);
				al_digits.add(h);
			}

			for(int k1=0;k1<al_digits.size();k1++)
				hm_end.put(al_digits.get(k1), i+1);
		}

	}

	private static int rec(String s, int jump) {
		// TODO Auto-generated method stub
		int jump_jr=0;
		int ind=0;
		for(int i=0;i<s.length();i++)
		{
			if(s.charAt(i)=='(')
				jump_jr++;
			if(jump_jr==jump)
			{
				ind=i;
				System.out.println("i:"+i);
				break;
			}
		}
		int cl=0;
		int ans=0;
		for(int i=ind;i<s.length();i++)
		{
			if(s.charAt(i)=='(')
				cl++;
			if(s.charAt(i)==')')
				cl--;
			if(cl==0)
			{
				ans=i;
				break;
			}
		}
		int res=ans+1;
		al_interm.add(s.substring(ind,res));

		String last=s.substring(0,ind)+s.substring(res,s.length());
		int num=jump-1;
		if((num<2))
		{
			al_interm.add(last);
			return 0;
		}
		else
		{
			return rec(last,num);
		}

	}

}

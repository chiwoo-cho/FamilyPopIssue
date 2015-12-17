package com.nclab.sociophone.processors;



import java.util.Iterator;
import java.util.Vector;
import java.util.ArrayList;
import java.util.HashMap;


public class KmeansCluster {
	ArrayList<ArrayList<double[]>> data;
	double[][] centroid;
	int cluster_size = 0;
	int cluster_dimension = 0;
	
	public KmeansCluster(int size, int dimension){
		cluster_size = size;
		cluster_dimension = dimension;
		data = new ArrayList<ArrayList<double[]>>();
		centroid = new double[size][dimension]; 
	}
	
	public void ChangeClusterSize(int size){
		cluster_size = size;
		//re-clustering
		
	}
	
	public ArrayList<ArrayList<double[]>> GetData(){
		return data;
	}
	
	public void Clustering(ArrayList<double[]> in_data){
		if(in_data.size() < cluster_size)
			return;
		
		InitClustering(in_data);
		CalculateCentroid();
		while(LoopClustering()){
			CalculateCentroid();
		}
		
		// DEBUG
		//Log.i("MYTAG","---- Check centroids ----");
		ArrayList<ArrayList<double[]>> res = this.GetData();
		System.out.println("KMeans : ");
		for(ArrayList<double[]> a : res) {
			for(double[] b : a) {
			//	Log.i("MYTAG",b[0] +", " + b[1] + ", " + b[2]);
				System.out.println(b[0] +", " + b[1]);
			}
			System.out.println("");
			//Log.i("MYTAG"," ");
		}
	}
	
	private void InitClustering(ArrayList<double[]> in){		
		data.clear();
		centroid = new double[cluster_size][cluster_dimension];
		
		Iterator iter = in.iterator();
		int count = 0;
		while(iter.hasNext()){
			double[] v = (double[]) iter.next();
			
			if(count < cluster_size){
				centroid[count] = v;
				ArrayList<double[]> list = new ArrayList<double[]>();
				list.add(v);
				data.add(list);
			}
			else{
				//find nearest centroid
				int min_index = selectCentroid(v);
				data.get(min_index).add(v);
			}
			++count;
		}
	}
	
	private boolean LoopClustering(){
		ArrayList<ArrayList<double[]>> new_data = new ArrayList<ArrayList<double[]>>();
		for(int i =0; i<cluster_size; ++i){
			ArrayList<double[]> list = new ArrayList<double[]>();
			new_data.add(list);
		}
		
		Iterator<ArrayList<double[]>> iter = data.iterator();
		int clusterIdx = 0;
		boolean res = false;
		while(iter.hasNext()){
			ArrayList<double[]> cluster_list = (ArrayList<double[]>)iter.next();
			Iterator<double[]> cluster_iter = cluster_list.iterator();
			while(cluster_iter.hasNext()){
				double[] v = (double[]) cluster_iter.next();
				
				//find nearest centroid
				int min_index = selectCentroid(v);
				
				if(min_index != clusterIdx)
					res = true;
				new_data.get(min_index).add(v);
			}
			clusterIdx ++ ;
		}
		
		
		data = new_data;
		return res;
	}
	private int selectCentroid(double[] v) {
		int min_index = 0;
		double min_distance = Double.MAX_VALUE;
		for(int i=0; i<cluster_size; ++i){
			double[] c = centroid[i];
			double distance = getDistance(v, c);
			if(distance < min_distance){
				min_distance = distance;
				min_index=i;
			}
		}
		return min_index;
	}
	private void CalculateCentroid(){
		Iterator iter = data.iterator();
		int clusterIdx = 0;

		while(iter.hasNext()){
			ArrayList<double[]> cluster_list = (ArrayList<double[]>)iter.next();
			
			double[] mean = new double[cluster_dimension];
			for(int i=0; i<cluster_dimension; ++i){
				mean[i] = 0;
			}
			
			Iterator cluster_iter = cluster_list.iterator();
			while(cluster_iter.hasNext()){
				double[] v = (double[]) cluster_iter.next();
				
				for(int i=0; i<cluster_dimension; ++i){
					mean[i] += v[i];
				}
			}
			
			for(int i=0; i<cluster_dimension; ++i){
				mean[i] /= cluster_list.size();
			}
			
			centroid[clusterIdx] = mean;
			if(cluster_list.size() == 0){
				double[] zeroVector = new double[cluster_dimension];
				for(int i=0;i<cluster_dimension;i++)
					zeroVector[i] = 0;
				centroid[clusterIdx] = zeroVector;
			}
			++clusterIdx;
		}
	}
	
	public void AddOneVectorToLearning(double[] vec){
		//find nearest
		double min_distance = Double.MAX_VALUE;
		int min_index = 0;
		
		for(int i=0; i<cluster_size; ++i){
			double[] c = centroid[i];
			double distance = 0;
			distance = getDistance(vec, c);
			if(distance < min_distance){
				min_distance = distance;
				min_index=i;
			}
		}
		data.get(min_index).add(vec);
		
		double[] mean = new double[cluster_dimension];
		for(int i=0; i<cluster_dimension; ++i){
			mean[i] = 0;
		}
		
		Iterator cluster_iter = data.get(min_index).iterator();
		while(cluster_iter.hasNext()){
			double[] v = (double[]) cluster_iter.next();
			
			for(int i=0; i<cluster_dimension; ++i){
				mean[i] += v[i];
			}
		}
		
		for(int i=0; i<cluster_dimension; ++i){
			mean[i] /= data.get(min_index).size();
		}
		
		centroid[min_index] = mean;

		while(LoopClustering()){
			CalculateCentroid();
		}
	}
	
	public double getDistance(double[] a, double[] b) {
		double distance = 0;
		for(int j=0; j<cluster_dimension; ++j){
			distance += (a[j] - b[j])*(a[j] - b[j]);
		
			
		}
		return distance;
		/*double As=0, Bs=0, AB=0;
		for(int j=0;j<cluster_dimension;j++) {
			As += a[j]*a[j];
			Bs += b[j]*b[j];
			AB += a[j]*b[j];
		}
		As = Math.sqrt(As);
		Bs = Math.sqrt(Bs);
		return (AB / (As*Bs));*/
	}
	public double[] GetCentroid(double[] vec){
		//find nearest
		double min_distance = Double.MAX_VALUE;
		int min_index = 0;
		
		for(int i=0; i<cluster_size; ++i){
			double[] c = centroid[i];
			double distance = 0;
			distance = getDistance(c, vec);
			if(distance < min_distance){
				min_distance = distance;
				min_index=0;
			}
		}
		return centroid[min_index];
	}
	
	
	public static void main(String[] args) {
		KmeansCluster clusterer = new KmeansCluster(3,2);
		double[] data0;
		ArrayList<double[]> datas = new ArrayList<double[]>();
		for(int i=0;i<10;i++) {
			data0 = new double[2];
			data0[0] = 1+(double)i*0.01d;
			data0[1] = 1+(double)((i+1)%2)*0.01d;
			datas.add(data0);
		}
		for(int i=0;i<10;i++) {
			data0 = new double[2];
			data0[0] = 4+(double)i*0.01d;
			data0[1] = 4+(double)((i+1)%2)*0.01d;
			datas.add(data0);
		}
		for(int i=0;i<10;i++) {
			data0 = new double[2];
			data0[0] = -50+(double)i*0.01d;
			data0[1] = -50+(double)((i+1)%2)*0.01d;
			datas.add(data0);
		}
		clusterer.Clustering(datas);
		ArrayList<ArrayList<double[]>> res = clusterer.GetData();
		for(ArrayList<double[]> a : res) {
			for(double[] b : a) {
				System.out.println(b[0] +", " + b[1]);
			}
			System.out.println();
		}
		
	}
}

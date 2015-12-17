package com.nclab.sociophone.processors;

import android.os.Environment;
import android.os.Handler;
import android.util.Log;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import com.nclab.sociophone.SocioPhoneConstants;

import libsvm.*;
/**
 * Module for processing sound data (Volume vectors)
 * 
 * @author Chanyou
 *
 */
public class SoundProcessManager{
	
	private static final boolean usingJUnit = false;

	private int num_member = 0;
	private KmeansCluster clusterManager;
	//private EMClusterer emClusterer;
	boolean trained = false, isOrderBase = false, loaded = false, allowOverlap;
	private Handler mHandler;
	public int train_size = 150;	//80 sec / 0.3
	private VolumeVectorBuffer buffer;
	private svm_model model;
	private ArrayList<double[]> trainingBuffer;
	public double[] thresholds;
	public double dThresholdRatio = 0.15d;
	
	private boolean debug = SocioPhoneConstants.debug;
	public SoundProcessManager(Handler handler, int size, boolean orderbase, boolean allowOverlap) {
		mHandler = handler;
		num_member = SocioPhoneConstants.numberOfPhones = size;
		this.allowOverlap = allowOverlap;
		isOrderBase = orderbase;
		buffer = new VolumeVectorBuffer(400, size);
		trainingBuffer = new ArrayList<double[]>();
		thresholds = new double[size];
		for(int i=0; i<size; ++i){
			thresholds[i] =30000;
		}
		if(allowOverlap)
			buffer.doNormalize = false;
		
		clusterManager = new KmeansCluster(num_member+1, num_member);
		//emClusterer = new EMClusterer(num_member+1, num_member);
	}
	/**
	 * 
	 * 
	 * @param index The index of the device
	 * @param data Volume value
	 * @param timestamp The timestamp of the data 
	 * @return Don't care. it's just for debugging.
	 */
	public int addData(int index, double data, long timestamp){
		
		//volume_data.get(index).add(data);
		
		if(buffer.put(index, data, timestamp)) {
			if(isOrderBase){
				if(allowOverlap){
					
					orderBasedProcessAllowOverlap();
				}
				else{
					OrderBaseGetData();
				}
				return -1;
			}
			else{
				// When you need to save the model, activate below.
				
				/*if(MainActivity.loadFlag) {	
					if(loaded) {
						PatternBaseGetData();
					} else {
						
						try {
							File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + SocioPhoneConstants.MODEL_FILE_NAME);
							FileInputStream fin;
							fin = new FileInputStream(file);
							ObjectInputStream ois = new ObjectInputStream(fin);
							model = (svm_model) ois.readObject();
							ois.close();
							
							Log.i("MYTAG", "Trainined");
							trained = true;
							loaded = true;
							mHandler.obtainMessage(SocioPhoneConstants.PROCESS_TRAINING_FINISHED).sendToTarget();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				} else {*/
					if(trained){
						
						return patternBaseMatch();
					}
					else{
						if(Training(train_size)){
							if(debug)
								Log.i("MYTAG", "Trainined");
							trained = true;
							if(!usingJUnit)
								mHandler.obtainMessage(SocioPhoneConstants.PROCESS_TRAINING_FINISHED).sendToTarget();
						}
					}
				//}
			}
		}
		return -2;
	}

	
	private boolean Training(int size){
		// Use buffer
		if(buffer.ready < size) {
			return false;
		}
		ArrayList<double[]> vec_data = new ArrayList<double[]>();
		double[] d;
		while((d = buffer.popFromBottom())!=null) {
			vec_data.add(d);
		}
		
	//	emClusterer.doCluster(vec_data);
		
		
		clusterManager.Clustering(vec_data);
		
		//mHandler.obtainMessage(SocioPhoneConstants.DISPLAY_LOG, "clustering end : " + (kmeans_end - kmeans_start)).sendToTarget();
		// TODO : Check the clustered data
		
		BuildSVM(clusterManager.GetData());
		//BuildSVM(emClusterer.getResult());
		
		
		
		// Saving model
		/*
		if (model != null) {
			try {
				File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + SocioPhoneConstants.MODEL_FILE_NAME);
				FileOutputStream fout = new FileOutputStream(file, false);
				ObjectOutputStream oos = new ObjectOutputStream(fout);   
				oos.writeObject(model);
				oos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		*/
		
		return true;	
	}
	public double varianceThreshold = 1000000000;
	public double volThreshold = 100000;
	private int OrderBaseGetData(){
		// Use buffer
		int idx = 0;
		double variance = 0;
		double mean = 0;
		double[] volumeVector = buffer.popFilled();
		double max = Double.MIN_VALUE;
		
		for(int i=0;i<volumeVector.length;i++)
			mean += volumeVector[i];
		mean /= volumeVector.length;
		for(int i=0;i<volumeVector.length;i++) {
			variance += (volumeVector[i] - mean)*(volumeVector[i] - mean);
			if(max < volumeVector[i]) {
				max = volumeVector[i];
				idx = i + 1;
			}
		}
		variance /= volumeVector.length;
		if(variance < varianceThreshold) {
			idx = 0;
		}
		if(max < volThreshold) {
			idx = 0;
		}
		mHandler.obtainMessage(SocioPhoneConstants.PROCESS_FINISHED, String.valueOf(idx)).sendToTarget();
	//	mHandler.obtainMessage(SocioPhoneConstants.PARAMETER_LOG, String.format("%.2f,%.2f", max, variance)).sendToTarget();
		return idx;
	}
	
	/**
	 * order based speaker guessing when app allows overlap 
	 */
	private void orderBasedProcessAllowOverlap() {
		String result = "", log = "";
		double[] volumeVector = buffer.popFilled();
		double dynamicThreshold = 0d;
		double max = 0;
		int maxIdx = 0;
		// Filter out...
		for(int i=0 ; i<volumeVector.length; i++) {
			if(volumeVector[i] - thresholds[i] > max) {
				max = volumeVector[i] - thresholds[i];
				maxIdx = i;
			}
		}
		
		dynamicThreshold = (max) * dThresholdRatio;
		
		for(int i=0;i<volumeVector.length;i++) {
			//Log.i("MYTAG", "i : " + i +", val : " + volumeVector[i]);
			if(volumeVector[i] > thresholds[i] + dynamicThreshold)
				result += (i+1) + ",";
			
			log += volumeVector[i] +", ";
			
		}
			
		if(result.equals("")) 
			result = "0";
		mHandler.obtainMessage(SocioPhoneConstants.PROCESS_FINISHED, result).sendToTarget();
		mHandler.obtainMessage(SocioPhoneConstants.PARAMETER_LOG_MULTI, log).sendToTarget();
		
	}
	private void BuildSVM(ArrayList<ArrayList<double[]>> in){
		//build parameter
		svm_parameter param = new svm_parameter();
		int[] matchingIdx = new int[num_member+1];
		double[] matchingVal = new double[num_member+1];
		// default values
		param.svm_type = svm_parameter.C_SVC;
		param.kernel_type = svm_parameter.POLY;
		param.degree = 3;
		param.gamma = 1d/(double)(num_member);
		param.coef0 = 0;
		param.nu = 0.5;
		param.cache_size = 40;
		param.C = 1;
		param.eps = 1e-3;
		param.p = 0.1;
		param.shrinking = 1;
		param.probability = 0;
		param.nr_weight = 0;
		param.weight_label = new int[0];
		param.weight = new double[0];
		
		//build problem
		svm_problem prob = new svm_problem();
		
		int total_size = 0;
		int nonSpeechIdx = 0;
		double[] tsum = new double[num_member];
		int ct = 0;
		double varMin= Double.MAX_VALUE, var, varMean, mean;
		// Select non-speech (Pick least variance of volume)
		for(ArrayList<double[]> item : in) { 
			total_size += item.size();
			varMean = 0d;
			for(int i=0;i<num_member;i++)
				tsum[i] = 0;
			for(double[] feature : item) {
				var = 0;
				mean = 0;
				String tempString = "";
				for(int i=0;i<feature.length;i++) {
					var += feature[i]*feature[i];
					mean += feature[i];
					tempString += feature[i]+",";
					tsum[i] += feature[i];
				}
				Log.i("MYTAG",tempString);
				
				var -= mean*mean/(double)feature.length;
				var /= (double)feature.length; // V(X) = E(X^2) - E(X)^2
				varMean += var;
			}
			varMean /= (double)item.size();
			if(varMean < varMin) {
				varMin = varMean;
				nonSpeechIdx = ct;
				//matchingIdx[ct] = 0;
			}
			//else {
				// Temporary
				double max = -1d;
				int idx = 0;
				for(int i=0;i<num_member;i++) {
					if(max < tsum[i]){
						max = tsum[i];
						idx = i;
					}
				}
				//if(matchingIdx[ct] != 0) {				
					matchingIdx[ct] = idx + 1;
					matchingVal[ct] = max;
					Log.i("MYTAG2", "match : " + ct +", target : " + (idx +1) +", max : " + max +", var : " + varMean );
				/*}
				else if(max > matchingVal[ct]) {
					matchingVal[ct] = max;
					matchingIdx[ct] = -1;
					matchingIdx[ct] = idx + 1;
					Log.i("MYTAG", "match : " + ct +", target : " + (idx +1));
				}*/
			//}
			ct ++;
		}
		matchingIdx[nonSpeechIdx] = 0;
		
		Iterator<ArrayList<double[]>> iter = in.iterator();
		
		
		prob.l = total_size;
		prob.y = new double[prob.l];
		prob.x = new svm_node [prob.l][num_member];
		
		int index = 0;
		int group_index = 0;
		
		while(iter.hasNext()){
			Iterator<double[]> iter2 = iter.next().iterator();
			while(iter2.hasNext()){
				double[] d = iter2.next();
				
				for(int i=0; i<num_member; ++i){
					prob.x[index][i] = new svm_node();
					prob.x[index][i].index = i+1;
					prob.x[index][i].value = d[i];
				}
				//if(nonSpeechIdx == group_index) {
				//	prob.y[index] = 0;
				//}
				//else {
					//prob.y[index] = matchingIdx[group_index];
				//}
				
				if(nonSpeechIdx != 0) {
					if(group_index != nonSpeechIdx)
						prob.y[index] = matchingIdx[group_index];
						/*
						if (group_index == num_member) {
							prob.y[index] = nonSpeechIdx + 1;
						} else {
							prob.y[index] = group_index +1;
						}*/
					else
						prob.y[index] = 0;
				}
				else {
					prob.y[index] = matchingIdx[group_index];
					if(group_index == 0)
						prob.y[index] = 0;
					Log.i("MYTAG", " idx : " + index + " Group idx : " + group_index +" ydx : " +prob.y[index]);
				}
				
				
				++index;
			}
			++group_index;
		}
		
		// build model & classify
		model = svm.svm_train(prob, param);
	}
	
	private void BuildSVM_dep(ArrayList<ArrayList<double[]>> in){
		// TODO : Check SVM
		//build parameter
		svm_parameter param = new svm_parameter();

		
		
		// default values
		param.svm_type = svm_parameter.C_SVC;
		param.kernel_type = svm_parameter.POLY;
		param.degree = 3;
		param.gamma = 1d/(double)(num_member);
		param.coef0 = 0;
		param.nu = 0.5;
		param.cache_size = 40;
		param.C = 1;
		param.eps = 1e-3;
		param.p = 0.1;
		param.shrinking = 1;
		param.probability = 0;
		param.nr_weight = 0;
		param.weight_label = new int[0];
		param.weight = new double[0];
		
		
		/*param.svm_type = svm_parameter.C_SVC;
		param.kernel_type = svm_parameter.LINEAR;
		param.gamma = 1d/(double)(num_member);
		param.coef0 = 0;
		param.cache_size = 40;*/
		
		//build problem
		svm_problem prob = new svm_problem();
		
		int total_size = 0;
		int nonSpeechIdx = 0;
		int ct = 0;
		double varMin= Double.MAX_VALUE, var, varMean, mean, meanMean, meanMin = Double.MAX_VALUE, vec;
		// Select non-speech (Pick least variance of volume)
		for(ArrayList<double[]> item : in) { 
			total_size += item.size();
			varMean = 0d;
			meanMean = 0d;
			for(double[] feature : item) {
				var = 0;
				mean = 0;
				vec = 0;
				String tempString = "";
				for(int i=0;i<feature.length;i++) {
					var += feature[i]*feature[i];
					mean += feature[i];
					tempString += feature[i]+",";
				}
				if(debug)
					Log.i("MYTAG",tempString);
				vec = Math.sqrt(var);
				var -= mean*mean/(double)feature.length;
				var /= (double)feature.length; // V(X) = E(X^2) - E(X)^2
				varMean += var;
				meanMean += vec;
			}
			if(debug)
				Log.i("MYTAG"," ");
			meanMean /= (double)item.size(); 
			varMean /= (double)item.size();
			if(varMean < varMin) {
				varMin = varMean;
				nonSpeechIdx = ct;
			}
			/*if(meanMean < meanMin) {
				meanMin = meanMean;
				nonSpeechIdx = ct;
			}*/
			ct ++;
		}
		
		
		Iterator<ArrayList<double[]>> iter = in.iterator();
		
		
		prob.l = total_size;
		prob.y = new double[prob.l];
		prob.x = new svm_node [prob.l][num_member];
		
		int index = 0;
		int group_index = 0;
		
		while(iter.hasNext()){
			Iterator<double[]> iter2 = iter.next().iterator();
			while(iter2.hasNext()){
				double[] d = iter2.next();
				
				for(int i=0; i<num_member; ++i){
					prob.x[index][i] = new svm_node();
					prob.x[index][i].index = i+1;
					prob.x[index][i].value = d[i];
				}

				if(nonSpeechIdx != 0) {
					if(group_index != nonSpeechIdx) 
						if (group_index == num_member) {
							prob.y[index] = nonSpeechIdx + 1;
						} else {
							prob.y[index] = group_index +1;
						}
					else
						prob.y[index] = 0;
				}
				else {
					prob.y[index] = group_index;
					if(debug)
						Log.i("MYTAG", " idx : " + index + " Group idx : " + group_index);
				}
				
				
				++index;
			}
			++group_index;
		}
		
		// build model & classify
		model = svm.svm_train(prob, param);
	}
	
	private double predict(double[] d){
		svm_node[] x = new svm_node[num_member];
		for(int i=0; i<num_member; ++i){
			x[i] = new svm_node();
			x[i].index = i+1;
			x[i].value = d[i];
		}
		
		return svm.svm_predict(model, x);
	}
	
	private int patternBaseMatch(){
		double[] volumeVector = buffer.popFilled();
		int result = (int)predict(volumeVector);
		double variance = 0;
		double mean = 0;
		double max = Double.MIN_VALUE;
		for(int i=0;i<volumeVector.length;i++)
			mean += volumeVector[i];
		mean /= volumeVector.length;
		for(int i=0;i<volumeVector.length;i++) {
			variance += (volumeVector[i] - mean)*(volumeVector[i] - mean);
			if(max < volumeVector[i]) {
				max = volumeVector[i];
				//result = i + 1;
			}
		}
		variance /= volumeVector.length;
		if(variance < varianceThreshold) {
			result = 0;
		}
		if(max < volThreshold) {
			result = 0;
		}
		if(!usingJUnit) {
			mHandler.obtainMessage(SocioPhoneConstants.PROCESS_FINISHED, String.valueOf(result)).sendToTarget();
			//mHandler.obtainMessage(SocioPhoneConstants.PARAMETER_LOG, String.format("%.2f,%.2f", max, variance)).sendToTarget();
		}
		
		return result;
	}
	public static void main() {
		System.out.println("Hello world");
		
	}
}

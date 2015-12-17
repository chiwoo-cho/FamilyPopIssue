package com.nclab.sociophone_lib.test;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;

import android.os.Handler;
import android.os.Message;

import com.nclab.sociophone.processors.SoundProcessManager;

import junit.framework.TestCase;
import junit.framework.TestResult;

/**
 * Just a simple test case to evaluate classifier.
 * 
 * @author Chanyou
 *
 */
public class ClassifierTest extends TestCase {
	
	String[] fileName = {"E:\\experiment_data\\SocioPhone\\classifier_test\\1.raw", "E:\\experiment_data\\SocioPhone\\classifier_test\\2.raw", "E:\\experiment_data\\SocioPhone\\classifier_test\\3.raw"};
	int n=0;
	public void test1() {
		n = fileName.length;
		SoundProcessManager spm = new SoundProcessManager(null, n, false, false);
		double[] temp = new double[280*n];
		int bufferSize = (int)(16000d * 0.3 * 2);
		if(bufferSize % 2 == 1)
			bufferSize ++;
		byte[] buffer = new byte[bufferSize];
		
		Random rand = new Random(System.currentTimeMillis());
		System.out.println("-------------");
		for(int i=0;i<n;i++) {
			FileInputStream fis;
			try {
				fis = new FileInputStream(new File(fileName[i]));
				DataInputStream dataStream = new DataInputStream(fis);
				int nBytesRead = 1;
				for(int k=0;k<280;k++) {
					nBytesRead = dataStream.read(buffer, 0, bufferSize);
		       		double sum = 0;
		       		double rms;
		       		for(int j=0 ; j<nBytesRead; j+=2) {
		       			short p = (short) ((short)(buffer[j]&0xff) + (short)((short)(buffer[j+1]&0xff) << 8));
		       			sum += p*p;		       		
		       		}
		       		rms = sum / (nBytesRead /2);
		       		temp[k*3+i] = rms;// (double)sum / (bufferSize /2d);
		       		
				 }	
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//System.out.println(files[zz].getName());
		
		
	/*	double[] ref1 = {1500,100,300};
		double[] ref2 = {100,1800,200};
		double[] ref3 = {200,300,1900};
		for(int i=0;i<70;i++) {
			for(int j=0;j<3;j++) {
				temp[i*3+j] = ref1[j] + (rand.nextDouble()-0.5) * 400d;
			}
			
		}
		for(int i=70;i<140;i++) {
			
			for(int j=0;j<3;j++) {
				temp[i*3+j] = ref2[j] + (rand.nextDouble()-0.5) * 400d;				
			}
			
		}
		for(int i=140;i<210;i++) {
			
			for(int j=0;j<3;j++) {
				temp[i*3+j] = ref3[j] + (rand.nextDouble()-0.5) * 400d;
			}
			
		}
		for(int i=210;i<280;i++) {
			
			for(int j=0;j<3;j++) {
				temp[i*3+j] = 200 + (rand.nextDouble()-0.5) * 300d;
			}
			
		}*/
		for(int i=0;i<280;i++) {
			double psum = 0;
			for(int j=0;j<3;j++) {
				psum += temp[i*3+j]; 
			}
			psum /=3;
			//psum = Math.sqrt(psum);
			for(int j=0;j<3;j++)
				temp[i*3+j] = temp[i*3+j]/ psum * 20*Math.log10(temp[i*3+j]/20);
			
			spm.addData(0, temp[i*3], i);
			spm.addData(1, temp[i*3+1], i);
			spm.addData(2, temp[i*3+2], i);
		}
		System.out.println();
		System.out.println();
		System.out.println("--------Validation---------");
		for(int i=0;i<270;i++) {
			spm.addData(0, temp[i*3], i);
			spm.addData(1, temp[i*3+1], i);
			int x = spm.addData(2, temp[i*3+2], i);
			System.out.println((i*300)+":" +"a : " + temp[i*3] +", " +temp[i*3+1]+", " + temp[i*3+2]+", : " + x);
		}
		
	}
}

package com.nclab.sociophone.processors;

public class VolumeVectorBuffer {
	int size, dimension;
	double[][] buffer;
	long[] timestamps;
	int[] counts;
	int head = 0;
	int tail = 0;
	public boolean doNormalize = false;
	public int ready = 0;
	public VolumeVectorBuffer(int size, int dimension) {
		this.size = size;
		this.dimension = dimension;
		buffer = new double[size][dimension];
		timestamps = new long[size];
		counts = new int[size];
	}
	/**
	 * Put volume data from the phone
	 * @param idx
	 * @param value
	 * @param timestamp
	 * @return
	 */
	public boolean put(int idx, double value, long timestamp) {
		boolean filled = false;
		int timeMatchIdx = -1;
		if(head != tail) {
			if(timestamp < timestamps[head])
				// Skip old garbages
				return false;
		}
		for(int i=head;i!=tail;i++) {
			if(timestamps[i] == timestamp) {
				timeMatchIdx = i;
				break;
			}
			if(i == size -1)
				i = -1;
		}
		if(timeMatchIdx == -1) {
			// Non-existing time-window on the buffer
			if(nextIdx(tail) == head) {
				head = nextIdx(head);
			}
			timestamps[tail] = timestamp;
			buffer[tail][idx] = value;
			counts[tail] = 1;
			tail = nextIdx(tail);
		}
		else {
			// Existing timewindow
			counts[timeMatchIdx] ++;
			buffer[timeMatchIdx][idx] = value;
			if(counts[timeMatchIdx] == dimension) {
				ready ++;
				if(doNormalize)
					normalize(timeMatchIdx);
				else {
					//doLog(timeMatchIdx);
				}
				filled = true;
			}
		}
		return filled;
	}
	private void doLog(int index) {
		for(int i=0;i<dimension;i++) {
			buffer[index][i] = Math.log10(buffer[index][i]);
		}
	}
	private void normalize(int index) {
		double powsum = 0;
//	temp[i*3+j] = temp[i*3+j]/ psum * 20*Math.log10(temp[i*3+j]/20);
		for(int i=0;i<dimension;i++) 
			powsum += buffer[index][i];// * buffer[index][i];
		powsum /= dimension;
		
		for(int i=0;i<dimension;i++) 
			buffer[index][i] =  buffer[index][i] / powsum * 20 * Math.log10(buffer[index][i]/20);
	}
	public double[] popFromBottom() {
		double[] ret = null;
		int idx = -1;
		for(int i=head;i!=tail;i++) {
			
			if(counts[i] == dimension) {
				idx = i;
				break;
			}
			if(i == size -1)
				i = -1;
		}
		if(idx != -1) {
			head = nextIdx(idx);
			ret = buffer[idx];
			ready --;
		}
		
		return ret;
	}
	public double[] popFilled() {
		double[] ret = null;
		int idx = -1;
		for(int i=head;i!=tail;i++) {
			if(counts[i] == dimension) {
				idx = i;
			}
			if(i == size -1)
				i = -1;
		}
		if(idx != -1) {
			head = nextIdx(idx);
			ret = buffer[idx];
			ready --;
		}
		return ret;
	}
	private int nextIdx(int x) {
		x ++;
		if(x >= size)
			x = 0;
		return x;
	}
}

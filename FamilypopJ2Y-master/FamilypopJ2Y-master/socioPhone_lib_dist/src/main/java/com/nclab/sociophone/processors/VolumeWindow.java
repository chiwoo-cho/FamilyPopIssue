package com.nclab.sociophone.processors;

public class VolumeWindow {
	public long timestamp;
	public double power;
	public VolumeWindow(long timestamp, double power) {
		this.power = power;
		this.timestamp = timestamp;
	}
}

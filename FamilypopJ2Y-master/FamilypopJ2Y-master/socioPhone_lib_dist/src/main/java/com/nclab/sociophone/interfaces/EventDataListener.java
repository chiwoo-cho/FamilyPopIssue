package com.nclab.sociophone.interfaces;

public interface EventDataListener {
	public void onInteractionEventOccured(int speakerID, int eventType, long timestamp);
}

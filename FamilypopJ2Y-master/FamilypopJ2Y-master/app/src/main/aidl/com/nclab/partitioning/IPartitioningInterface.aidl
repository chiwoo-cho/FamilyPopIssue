package com.nclab.partitioning;

interface IPartitioningInterface {
	int registerQuery(String query);	
	
	int deregisterQuery(int queryId);
	
	int updateTaskType(String taskType);
	
	void startLogging(String filename);
	void stopLogging();
}
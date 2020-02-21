package com.awspaas.user.apps.cs.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.actionsoft.bpms.schedule.IJob;

public class TaskTimeoutAutoDoTaskJob implements IJob {

	@Override
	public void execute(JobExecutionContext jec) throws JobExecutionException {
		
	}

}

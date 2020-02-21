package com.awspaas.user.apps.cs.event;

import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.exception.AWSException;
import com.actionsoft.sdk.local.SDK;

public class CopyFileToFieldEvent extends ExecuteListener {
	public CopyFileToFieldEvent() {
	    this.setDescription("流程结束后，复制附件到指定字段");
	    this.setProvider("jl_Qin");
	    this.setVersion("1.0");
	  }
	@Override
	public void execute(ProcessExecutionContext pec) throws Exception {
		String proInstId = pec.getProcessInstance().getId();
		String taskInstId = pec.getTaskInstance().getId();
		try {
			//BO bo = SDK.getBOAPI().query(boName1).addQuery("BINDID = ", bindId).detail();
			//String file = bo.getString("FILE");
			SDK.getBOAPI().copyFileTo("6ce52a50-27e2-4283-a641-f6c78cce8703", "FILE", "2b8f7858-d3c0-473c-b14b-5d94df262715", "BO_CS_FZFJ2", "FILE", proInstId, taskInstId);
		} catch (AWSException e) {
			SDK.getLogAPI().consoleErr(e.getMessage());
		}
		System.out.println("----"+proInstId);
		System.out.println("----"+taskInstId);
	}

}

package com.awspaas.user.apps.cs.event;


import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.awspaas.user.apps.cs.biz.TaskBatchCompleteBiz;

public class EntrustImplementEvent extends ExecuteListener {
	public EntrustImplementEvent() {
	    this.setDescription("委托办理");
	    this.setProvider("jl_Qin");
	    this.setVersion("1.0");
	  }
	@Override
	public void execute(ProcessExecutionContext pec) throws Exception { 
		TaskBatchCompleteBiz.batchCompleteForCxdsl(pec);
	}
}

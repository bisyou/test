package com.awspaas.user.apps.cs.event;

import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.sdk.local.SDK;

public class PreemptiveImplementEvent extends ExecuteListener {
	public PreemptiveImplementEvent() {
	    this.setDescription("抢先办理");
	    this.setProvider("jl_Qin");
	    this.setVersion("1.0");
	  }
	@Override
	public void execute(ProcessExecutionContext pec) throws Exception {
		//获取当前流程实例id
		String processInstId = pec.getProcessInstance().getId();
		//获取当前节点定义id
		String activityDefId = pec.getTaskInstance().getActivityDefId();
		//获取当前登录用户id
		String uid = pec.getUserContext().getUID();
		//完成指定节点的所有任务
		SDK.getTaskAPI().completeTaskAll(processInstId, activityDefId, uid, true);
	}
}

package com.awspaas.user.apps.cs.event;

import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.exception.AWSException;
import com.actionsoft.exception.BPMNError;
import com.actionsoft.sdk.local.SDK;

public class AddSequenceEvent extends ExecuteListener {
	public AddSequenceEvent() {
	    this.setDescription("合同申请流程完成后，给合同编号字段赋值");
	    this.setProvider("jl_Qin");
	    this.setVersion("1.0");
	  }
	@Override
	public void execute(ProcessExecutionContext pec) throws Exception {
		//获取当前流程实例id
		String bindId = pec.getProcessInstance().getId();
		try {
			String boName = "BO_CS_XHYXX";
			//获取当前表单的主表数据
			BO bo = SDK.getBOAPI().query(boName).addQuery("BINDID = ", bindId).detail();
			// 执行一个混合有At公式的脚本
			String value = SDK.getRuleAPI().executeAtScript("@sequenceYear(测试编号有序性,4,0)");
			bo.set("SEQUENCE", value);
			SDK.getBOAPI().update(boName, bo);
		//异常捕获使用Exception,除非确定要处理所有异常，否则使用AWSException
		} catch (AWSException e) {
			//会在log文件中记录，不会给用户弹出提示
			SDK.getLogAPI().consoleErr(e.getMessage());
			//会在log文件中记录，会给用户弹出提示
			throw new BPMNError(e.getAPIErrorCode(),"XXX处理失败！"+e.getMessage());
		}
	}
}

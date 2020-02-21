package com.awspaas.user.apps.cs;


import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.server.bind.annotation.Controller;
import com.actionsoft.bpms.server.bind.annotation.Mapping;
import com.alibaba.fastjson.JSONArray;
import com.awspaas.user.apps.cs.biz.PushDataBiz;
import com.awspaas.user.apps.cs.biz.QueryGridDataToPage;
import com.awspaas.user.apps.cs.biz.ReceiveDataBiz;
import com.awspaas.user.apps.cs.web.CsWeb;

@Controller
public class CsController {

	@Mapping("com.awspaas.user.apps.cs_showTaskList")
	public String showTaskList(UserContext uc) {
		CsWeb cw =new CsWeb();
		return cw.queryTask(uc);
	}
	
	@Mapping("com.awspaas.user.apps.cs_queryDataToPage")
	public String queryDataToPage(UserContext uc) {
		CsWeb cw =new CsWeb();
		return cw.queryDataToPage(uc);
	}
	
	/**
	 * 需要在action.xml中对该CMD进行注册，同时声明info参数的type="body"
	 * @param info
	 * @return
	 */
	@Mapping(value = "com.awspaas.user.apps.cs_receiveData", 
			session = false, 
			noSessionEvaluate = "接收外部系统推送的数据并处理", 
			noSessionReason = "接收外部系统推送的数据并处理")
	public String mdm_org(String info) {
		ReceiveDataBiz biz = new ReceiveDataBiz();
		String result = biz.receiveTaskData(info);
		return result;
	}
	
	//获取审批记录的数据
	@Mapping("com.awspaas.user.apps.cs_dataSort")
	public JSONArray getOpinionData(UserContext uc, String arr) {
		
		CsWeb cw =new CsWeb();
		JSONArray result = cw.getOpinionData(uc, arr);
		//System.out.println(result);
		return result;
	}
	//获取子表数据
	@Mapping("com.awspaas.user.apps.cs_queryGridData")
	public String queryGridData(UserContext uc, String bindId) {
		QueryGridDataToPage qg =new QueryGridDataToPage();
		return qg.queryGridData(uc, bindId);
	}
	
	/**
	 * 需要在action.xml中对该CMD进行注册，同时声明info参数的type="body"
	 * @param info
	 * @return
	 */
	@Mapping(value = "com.awspaas.user.apps.cs_pushData", 
			session = false, 
			noSessionEvaluate = "获取内部系统数据处理并推送到外部系统", 
			noSessionReason = "获取内部系统数据处理并推送到外部系统")
	public String queryDataToExternalSystem(String uid) {
		PushDataBiz biz = new PushDataBiz();
		String result = biz.pushTaskData(uid);
		return result;
	}
}


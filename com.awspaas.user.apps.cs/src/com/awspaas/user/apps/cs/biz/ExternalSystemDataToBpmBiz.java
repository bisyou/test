package com.awspaas.user.apps.cs.biz;

import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.sdk.local.SDK;
import com.alibaba.fastjson.JSONObject;

public class ExternalSystemDataToBpmBiz {

	/**
	 * 处理外部系统推送的数据
	 * 
	 * @param item
	 * @return
	 */
	public static void exSysData2Bpm(JSONObject item) {
		String boName = "BO_CS_YGQZ";
		String code = item.getString("CODEID");// 唯一标识
		BO bo = SDK.getBOAPI().query(boName).addQuery("WBXTID=", code).detail();
		if(bo == null) {
			bo = new BO();
			bo.set("YYXTJC", item.getString("NAME"));// 应用系统简称
			bo.set("WBXTID", item.getString("CODEID"));// 外部系统id
			bo.set("CJRZH", item.getString("ACCOUNT"));// 创建人账户
			bo.set("ZXRZH", item.getString("CURRNTACCOUNT"));// 执行人账户
			bo.set("RWBT", item.getString("TITLE"));// 任务标题
			bo.set("RWSJ", item.getString("TASKDATE"));// 任务时间
			bo.set("RWURL", item.getString("ADDRESS"));// 任务url
			bo.set("STATUS", 0);// 任务状态
			SDK.getBOAPI().create(boName, bo, "", null);
		}else {
			bo.set("YYXTJC", item.getString("NAME"));// 应用系统简称
			bo.set("WBXTID", item.getString("CODEID"));// 外部系统id
			bo.set("CJRZH", item.getString("ACCOUNT"));// 创建人账户
			bo.set("ZXRZH", item.getString("CURRNTACCOUNT"));// 执行人账户
			bo.set("RWBT", item.getString("TITLE"));// 任务标题
			bo.set("RWSJ", item.getString("TASKDATE"));// 任务时间
			bo.set("RWURL", item.getString("ADDRESS"));// 任务url
			bo.set("STATUS", 1);// 任务状态
			SDK.getBOAPI().update(boName, bo);
		}
			
	}
}

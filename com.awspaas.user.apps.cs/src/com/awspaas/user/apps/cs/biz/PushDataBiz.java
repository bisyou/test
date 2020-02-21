package com.awspaas.user.apps.cs.biz;

import java.util.List;

import com.actionsoft.bpms.bpmn.constant.UserTaskRuntimeConst;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.TaskInstance;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.sdk.local.SDK;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.awspaas.user.apps.cs.util.CreateSid;

public class PushDataBiz {

	public String pushTaskData(String uid) {
		
		JSONArray jsonArray = new JSONArray();
		//获取指定用户的待办任务前10条，按照任务发起时间降序排列
		List<TaskInstance> list = SDK.getTaskQueryAPI().target(uid).orderByBeginTime().desc().listPage(0, 10);
		for (int i = 0; i < list.size(); i++) {
			TaskInstance task = list.get(i);
			//获取任务发起人
			String owner = UserContext.fromUID(task.getOwner()).getUserName();
			//获取任务发起时间
			String benginTime = task.getBeginTime().toString().substring(0, 19);
			//获取任务标题
			String title = task.getTitle();
			//获取任务链接
			//SDK.getFormAPI().getFormURL("../", sid, task.getProcessInstId(), task.getId(), UserTaskRuntimeConst.STATE_TYPE_TRANSACT, "", "", "");
			//String sid = SDK.getRuleAPI().executeAtScript("@sid");
			String sid = CreateSid.getUserSid(uid);
			String url = SDK.getFormAPI().getFormURL("../", sid, task.getProcessInstId(), task.getId(), UserTaskRuntimeConst.STATE_TYPE_TRANSACT, "", "", "", true);
			JSONObject josonObj =new JSONObject();
			josonObj.put("任务发起人", owner);
			josonObj.put("任务发起时间", benginTime);
			josonObj.put("任务标题", title);
			josonObj.put("任务链接", url);
			jsonArray.add(josonObj);
		}
		return jsonArray.toString();
	}

}

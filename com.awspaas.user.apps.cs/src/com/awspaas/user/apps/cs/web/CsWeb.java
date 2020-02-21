package com.awspaas.user.apps.cs.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.actionsoft.bpms.bpmn.constant.UserTaskRuntimeConst;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.TaskInstance;
import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.commons.htmlframework.HtmlPageTemplate;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class CsWeb {

	public String queryTask(UserContext uc) {
		String sid =uc.getSessionId();
		
		StringBuffer trHtmls =new StringBuffer();
		//获取当前用户的待办任务
		List<TaskInstance> list = SDK.getTaskQueryAPI().target(uc.getUID()).orderByBeginTime().desc().list();
		for (int i = 0; i < list.size(); i++) {
			TaskInstance task = list.get(i);
			//任务办理的url
			String url =SDK.getFormAPI().getFormURL("../", sid, task.getProcessInstId(), task.getId(), UserTaskRuntimeConst.STATE_TYPE_TRANSACT, "", "", "");
			//转换用户id为用户名称
			String user =UserContext.fromUID(task.getOwner()).getUserName();
			//转换日期
			String date =task.getBeginTime().toString().substring(0, 19);
			trHtmls.append("<tr class=\"papers\"  align=\"center\" onclick=\"openTaskForm('"+url+"')\">");
			trHtmls.append("<td>"+(i+1)+"</td>");
			trHtmls.append("<td>"+task.getTitle()+"</td>");
			trHtmls.append("<td>"+user+"</td>");
			trHtmls.append("<td>"+date+"</td>");
			trHtmls.append("</tr>");
		}
		Map<String, Object> map =new HashMap<>();
		map.put("sid", sid);
		map.put("trHtmls", trHtmls);
		
		return HtmlPageTemplate.merge("com.awspaas.user.apps.cs", "showTaskList.htm", map);
	}
	
	
	
	
	
	//查询数据到页面展示
	public String queryDataToPage(UserContext uc) {
		//获取当前用户id
		String uid =uc.getUID();
		//获取BO_CS_YGQZ表中执行人为当前用户的数据
		String sql ="select * from BO_CS_YGQZ where ZXRZH =?";
		List<RowMap> maps = DBSql.getMaps(sql, new Object[] {uid});
		
		StringBuffer trHtmls =new StringBuffer();
		for (int i = 0; i < maps.size(); i++) {
			RowMap rowMap = maps.get(i);
			trHtmls.append("<tr class=\"papers\"  align=\"center\" onclick=\"openTaskForm('"+rowMap.getString("RWURL")+"')\">");
			trHtmls.append("<td>"+(i+1)+"</td>");
			trHtmls.append("<td>"+rowMap.getString("RWBT")+"</td>");
			trHtmls.append("<td>"+rowMap.getString("CJRZH")+"</td>");
			trHtmls.append("<td>"+rowMap.getString("RWSJ")+"</td>");
			trHtmls.append("</tr>");
		}
		
		Map<String, Object> map =new HashMap<>();
		String sid =uc.getSessionId();
		map.put("sid", sid);
		map.put("trHtmls", trHtmls);
		
		return HtmlPageTemplate.merge("com.awspaas.user.apps.cs", "showDataPageList.htm", map);
	}
	
	
	//获取审批记录的数据并排列记录
	public JSONArray getOpinionData(UserContext uc, String arr) {
		//json数组
		JSONArray jsonArray = new JSONArray();
		String boName = "BO_CS_RYXXDZB";
		String sql ="select * from "+boName;
		//执行SQL语句，查询数据
		List<RowMap> list = DBSql.getMaps(sql, new Object[] {});
		//排序后的数据重新组装在map集合中
		//Map<Integer, List<Object>> mapss = new HashMap<>();
		Map<Integer, List<Object>> mapss = new TreeMap<>();
		//获取编号存放在list集合中
		List<Integer> numList = new ArrayList<>();
		//字符串解析成json数组
		JSONArray array = JSON.parseArray(arr);
		//存储用户账户的字符串
		//String userIds = "";
		Map<String, String> maps = new HashMap<>();
		if(list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				RowMap rowMap = list.get(i);
				//获取bo表中账户
				String userId = rowMap.getString("USERID");
				for(Object obj : array) {
					String str = JSON.toJSONString(obj);
					//转换成json对象
					JSONObject object = (JSONObject) JSONObject.parse(str);
					String userName = object.getString("UserName");
					//截取账户字符串
					String account = intercept(userName);
					//两个账户做对比
					if(userId.equals(account)) {
						//获取bo表中编号，作为map集合中的key
						String num = rowMap.getString("NUMBER");
						//判断map集合中是否存在该key
						if(mapss.containsKey(Integer.valueOf(num))) {
							mapss.get(Integer.valueOf(num)).add(obj);
						}else {
							List<Object> li =new ArrayList<>();
							li.add(obj);
							mapss.put(Integer.valueOf(num), li);
						}
					}
				}
				//userIds += " "+userId;
				maps.put(userId, " ");
				numList.add(Integer.valueOf(rowMap.getString("NUMBER")));
			}
		}
		
		//获取当前登录人的角色
		String role = SDK.getORGAPI().getRoleByUser(uc.getUID()).getName();
		if(role.equals("部首长") || role.equals("基地首长") || role.equals("部首长（副职）")) {
			
		}else {
			if(list.size() > 0) {
				//获取list集合中最大的数值并自增数字，作为map集合中的key
				Integer i = Integer.valueOf(Collections.max(numList)) + 1;
				for(Object obj : array) {
					String str = JSON.toJSONString(obj);
					//转换成json对象
					JSONObject object = (JSONObject) JSONObject.parse(str);
					String userName = object.getString("UserName");
					String account = intercept(userName);
					//判断字符串中是否包含另外一个字符串
					if (maps.containsKey(account)){
						
					}else {
						List<Object> li =new ArrayList<>();
						li.add(obj);
						mapss.put((i++), li);
					}
				}
			}else {
				int i = 1;
				for(Object obj : array) {
					List<Object> li =new ArrayList<>();
					li.add(obj);
					mapss.put((i++), li);
				}
			}
		}
		
		//获取map集合中的key
		Iterator<Integer> iter = mapss.keySet().iterator();
		//判断是否存在下一个key
		while(iter.hasNext()){
			Integer key=iter.next();
			//通过key得到对应的value集合
			List<Object> lis = mapss.get(key);
			for (Object li : lis) {
				
				JSONObject obj = JSON.parseObject(li.toString());
				jsonArray.add(obj);
			}
			
		}
		return jsonArray;
	}
	
	//截取账户字符串
	private static String intercept(String str) {
		int beginIndex = str.indexOf("'");
		int endIndex = str.indexOf("'", beginIndex+1);
		String result = str.substring(beginIndex+1,endIndex);
		return result;
	}
	
}
 
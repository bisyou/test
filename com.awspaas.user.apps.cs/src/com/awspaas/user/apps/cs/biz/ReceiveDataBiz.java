package com.awspaas.user.apps.cs.biz;

import com.actionsoft.bpms.util.UtilString;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class ReceiveDataBiz {

	/**
	 * 处理外部系统推送的数据
	 * 
	 * @param info
	 * @return
	 */ 
	public String receiveTaskData(String info) {
		
		JSONObject result = new JSONObject();
		JSONObject esb = new JSONObject();
		if (UtilString.isEmpty(info)) {
			esb.put("RESULT", "E");
			esb.put("DESC", "无法获取body中携带的info数据");
			result.put("ESB", esb);
		} else {
			JSONObject obj = JSONObject.parseObject(info);
			JSONObject esbObj = obj.getJSONObject("ESB");
			JSONObject dataObj = esbObj.getJSONObject("DATA");
			JSONObject dataInfosObj = dataObj.getJSONObject("DATAINFOS");
			JSONObject dataInfoObj = dataInfosObj.getJSONObject("DATAINFO");

			//String isSuccess = "S";
			//JSONArray dataInfo = new JSONArray();
			//for (int i = 0; i < dataInfoObj.size(); i++) {
				//JSONObject backObj = new JSONObject();
				//JSONObject item = dataInfoObj.getJSONObject(i);
				// 数据处理
				ExternalSystemDataToBpmBiz.exSysData2Bpm(dataInfoObj);
			//}
				/**
				try {
					

					backObj.put("CODE", item.getString("CODE"));
					backObj.put("UUID", item.getString("UUID"));
					backObj.put("SYNSTATUS", 0);// 0（成功）或者1（失败）
					backObj.put("SYNRESULT", "");

					dataInfo.add(backObj);
				} catch (Exception e) {
					e.printStackTrace();

					isSuccess = "E";
					backObj.put("CODE", item.getString("CODE"));
					backObj.put("UUID", item.getString("UUID"));
					backObj.put("SYNSTATUS", 1);// 0（成功）或者1（失败）
					backObj.put("SYNRESULT", e.getMessage());
				}
			}

			JSONObject dataInfos = new JSONObject();
			dataInfos.put("PUUID", dataInfosObj.getString("PUUID"));
			dataInfos.put("DATAINFO", dataInfo);

			JSONObject data = new JSONObject();
			data.put("DATAINFOS", dataInfos);

			esb.put("RESULT", isSuccess);
			esb.put("DESC", "");
			esb.put("DATA", data);

			result.put("ESB", esb);
			*/
		}
		//return result.toJSONString();
		return "";
		
	}

}

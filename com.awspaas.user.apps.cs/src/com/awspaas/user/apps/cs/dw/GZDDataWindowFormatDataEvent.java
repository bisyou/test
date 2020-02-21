package com.awspaas.user.apps.cs.dw;

import java.util.List;

import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.dw.design.event.DataWindowFormatDataEventInterface;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class GZDDataWindowFormatDataEvent implements DataWindowFormatDataEventInterface {

	@Override
	public void formatData(UserContext uc, JSONArray datas) {
		for (Object datao : datas) {
			JSONObject data = (JSONObject) datao;
			
			String sql ="SELECT * FROM BO_CS_GZD";
			List<RowMap> rows  =DBSql.getMaps(sql, new Object[] {});
			StringBuffer ss =new StringBuffer();
			String value = SDK.getRuleAPI().executeAtScript("@uid");
			for (int i = 0; i < rows.size(); i++) {
				
				RowMap rowData = rows.get(i);
				if(value.equals("admin")) {
					ss.append("****");
				}else {
					ss.append(rowData.getString("JBGZ")+" ");
				}
			}
			data.put("JBGZ", ss.toString());
		}
	}
}

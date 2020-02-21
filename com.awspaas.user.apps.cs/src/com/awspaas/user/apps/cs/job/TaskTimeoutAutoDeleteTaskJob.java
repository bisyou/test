package com.awspaas.user.apps.cs.job;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.schedule.IJob;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;

public class TaskTimeoutAutoDeleteTaskJob implements IJob {

	@Override
	public void execute(JobExecutionContext jec) throws JobExecutionException {

		Connection conn = null;
		String sql = "";
		String boName = "wfc_task";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			conn = DBSql.open();
			sql = "select readTime from "+boName+" where readState = ? and isTrash = ?";
			List<RowMap> datas = DBSql.getMaps(sql, new Object[] {"1", "0"});
			for (RowMap rowMap : datas) {
				String readTime = rowMap.getString("READTIME");
				//起草时间
				Long startTime = sdf.parse(readTime).getTime();
				//当前时间
				Long currentTime = new Date().getTime();
				
				//Job动态传入时间参数
				String param = SDK.getJobAPI().getJobParameter(jec);
				//执行超时动作
				if(currentTime - startTime >= Long.parseLong(param)) {
					sql = "delete from "+boName+" where readState = ? and isTrash = ?";
					DBSql.update(sql, new Object[] {"1", "0"});
				}
			}
		} catch (Exception e) {
			SDK.getLogAPI().consoleErr(e.getMessage());
		}finally {
			DBSql.close(conn);

		}
	}

}
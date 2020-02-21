package com.awspaas.user.apps.cs.biz;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.HistoryTaskInstance;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.TaskInstance;
import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.commons.security.delegation.model.DelegationModel;
import com.actionsoft.bpms.commons.security.delegation.model.DelegationScopeModel;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.sdk.local.SDK;

public class TaskBatchCompleteBiz {

	/**
	 * 并行多实例情况下委托任务批量办理
	 * 
	 * @param pec
	 */
	public static void batchCompleteForBxdsl(ProcessExecutionContext pec) {
		String uid = pec.getUserContext().getUID();
		String activityDefId = pec.getTaskInstance().getActivityDefId();

		// 获取当前用户在当前节点的所有委托任务
		List<TaskInstance> tasks = SDK.getTaskQueryAPI().processInstId(pec.getProcessInstance().getId())
				.activityDefId(activityDefId).target(uid).supportDelegateTask().activeTask().list();

		if (tasks.size() > 0) {
			TaskInstance task = tasks.get(0);
			SDK.getTaskAPI().completeTask(task.getId(), uid);
		}
	}

	/**
	 * 串行多实例情况下委托任务批量办理
	 * 
	 * @param pec
	 */
	public static void batchCompleteForCxdsl(ProcessExecutionContext pec) {
		// 判断当前任务的办理者，是否设置了任务委托，如果是，则获取当前办理者设置的委托信息
		Date now = new Date();
		List<String> uids = new ArrayList<>();
		List<DelegationModel> mods = SDK.getDelegationAPI().getListByApplicantUser(pec.getTaskInstance().getTarget());
		for (DelegationModel mod : mods) {
			// 判断委托是否失效
			if (mod.getEndTime().before(now)) {
				continue;
			}
			// 0:全部流程|1:部分流程|2:发起部门
			if (mod.getScopeType().equals("0")) {
				uids.add(mod.getDelegateUser());
			} else if (mod.getScopeType().equals("1")) {
				// 判断委托的流程是否包含当前流程
				for (DelegationScopeModel scope : mod.getScope()) {
					if (scope.getResourceId().equals(pec.getProcessDef().getId())) {
						uids.add(mod.getDelegateUser());
						continue;
					}
				}
			} else if (mod.getScopeType().equals("2")) {
				// 判断委托的流程发起部门是否包含当前流程的发起部门
				for (DelegationScopeModel scope : mod.getScope()) {
					if (scope.getResourceId().equals(pec.getProcessInstance().getCreateUserDeptId())) {
						uids.add(mod.getDelegateUser());
					}
				}
			}
		}
		uids.add(pec.getTaskInstance().getTarget());

		// 判断流程实例在当前节点的历史任务办理者，是否和委托用户有重复，如果有，则结束当前任务
		List<HistoryTaskInstance> tasks = SDK.getHistoryTaskQueryAPI()
				.activityDefId(pec.getTaskInstance().getActivityDefId()).processInstId(pec.getProcessInstance().getId())
				.parentTaskInstId(pec.getTaskInstance().getParentTaskInstId()).userTask().list();
		for (HistoryTaskInstance task : tasks) {
			// 如果历史任务是委托办理的，则使用任务DelegateUser判断
			String uid = task.getDelegateUser();
			if (UtilString.isNotEmpty(uid)) {
				boolean flag = autoComplete(pec.getTaskInstance().getId(), uids, task.getId(), uid);
				if (flag) {
					return;
				}
			}
			// 如果历史任务不是委托办理的，那么使用任务target判断
			else {
				uid = task.getTarget();
				boolean flag = autoComplete(pec.getTaskInstance().getId(), uids, task.getId(), uid);
				if (flag) {
					return;
				}
			}
		}
	}

	private static boolean autoComplete(String currentTaskId, List<String> uids, String historyTaskId, String uid) {
		UserContext uc = UserContext.fromUID(uid);
		if (uc == null) {
			return false;
		}
		try {
			// String roleName = uc.getRoleModel().getName();
			// if (roleName.indexOf("部首长") != -1 || roleName.indexOf("基地首长") != -1) {
			if (uids.indexOf(uc.getUID()) != -1) {
				String sql = "SELECT ACTIONNAME, MSG FROM WFC_COMMENT WHERE TASKINSTID=? UNION "
						+ "SELECT ACTIONNAME, MSG FROM WFC_COMMENTTEMP WHERE TASKINSTID=?";
				RowMap row = DBSql.getMap(sql, new Object[] { historyTaskId, historyTaskId });
				if (row != null) {
					// 写入意见留言
					// SDK.getTaskAPI().setComment(currentTaskId, row.getString("ACTIONNAME"), "");
				}
				SDK.getTaskAPI().completeTask(currentTaskId, uc.getUID());
				return true;
			}
			// }
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}
}

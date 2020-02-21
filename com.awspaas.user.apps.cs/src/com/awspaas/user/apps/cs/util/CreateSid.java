package com.awspaas.user.apps.cs.util;

import java.util.Iterator;

import com.actionsoft.bpms.commons.session.cache.SessionCache;
import com.actionsoft.bpms.commons.session.model.SessionModel;
import com.actionsoft.bpms.server.SSOUtil;
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.sdk.local.SDK;

public class CreateSid {
	/**
	 * 获取指定用户的有效sessionID
	 * 
	 * @param uid
	 * @return
	 */
	public static String getUserSid(String uid) {
		String sid = "";
		Iterator<SessionModel> iterator = SessionCache.getCache().iterator();
		while (iterator.hasNext()) {
			SessionModel mod = iterator.next();
			if (mod.getUid().equals(uid) && !mod.isClosed()) {
				sid = mod.getSessionId();
				break;
			}
		}
		if (UtilString.isNotEmpty(sid)) {
			SDK.getPortalAPI().refreshSession(sid);
		} else {
			// 创建session
			SSOUtil ssoUtil = new SSOUtil();
			sid = ssoUtil.registerClientSessionNoPassword(uid, "cn", "127.0.0.1", "pc");
		}
		return sid;
	}
}

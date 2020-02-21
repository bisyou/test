package com.awspaas.user.apps.cs.soap;

import javax.jws.WebParam;
import javax.jws.WebService;

import com.awspaas.user.apps.cs.biz.PushDataBiz;

@WebService(serviceName="PushDataToExternalSystem")
public class PushDataSoapWebServer {

	public String queryDataToExternalSystem(@WebParam(name="uid")String uid) {
		PushDataBiz biz = new PushDataBiz();
		String result = biz.pushTaskData(uid);
		return result;
	}
}

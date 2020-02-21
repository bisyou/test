package com.awspaas.user.apps.cs.webApi;

import com.actionsoft.bpms.server.bind.annotation.Controller;
import com.actionsoft.bpms.server.bind.annotation.HandlerType;
import com.actionsoft.bpms.server.bind.annotation.Mapping;

@Controller(type =HandlerType.OPENAPI,apiName = "HelloWord API",desc="HelloWord API 扩展演示" )
public class MyTestWebApi {

	@Mapping(value="startProcess")
	public String startProcess() {
		return "HelloWord";
	}
	@Mapping(value="updateStatus")
	public String updateStatus() {
		return "HelloWord";
	}
}

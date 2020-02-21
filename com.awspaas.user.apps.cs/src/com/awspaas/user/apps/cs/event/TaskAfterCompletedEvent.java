package com.awspaas.user.apps.cs.event;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.commons.formfile.FormFileUtil;
import com.actionsoft.bpms.commons.formfile.dao.FormFileDao;
import com.actionsoft.bpms.commons.formfile.model.FormFileModel;
import com.actionsoft.bpms.commons.formfile.model.delegate.FormFile;
import com.actionsoft.bpms.commons.mvc.view.ResponseObject;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.server.fs.DCContext;
import com.actionsoft.bpms.server.fs.dc.DCUtil;
import com.actionsoft.sdk.local.SDK;
import com.actionsoft.sdk.local.api.AppAPI;
import com.alibaba.fastjson.JSONObject;

public class TaskAfterCompletedEvent extends ExecuteListener {
	
	public TaskAfterCompletedEvent() {
		this.setDescription("任务完成后，把附件中的word格式文件转换成pdf格式文件");
		this.setProvider("jl_Qin");
		this.setVersion("1.0");
	}

	@Override
	public void execute(ProcessExecutionContext pec) throws Exception {
		UserContext uc = pec.getUserContext();
		String processInstId = pec.getProcessInstance().getId();
		String taskInstId = pec.getTaskInstance().getId();
		BO bo = SDK.getBOAPI().query("BO_CS_FZFJ1").bindId(processInstId).detail();
		List<FormFile> fileList = SDK.getBOAPI().getFiles(bo.getId(), "FILE");
		for (FormFile formFile : fileList) {
			DCContext doc = SDK.getBOAPI().getFileDCContext(formFile);
			String fileName = doc.getFileName();
			// 调用App 
			String sourceAppId = "com.awspaas.user.apps.cs";
			// aslp服务地址 
			String aslp = "aslp://com.actionsoft.apps.addons.onlinedoc/word2PDF";
			// 参数定义列表  
			Map<String, Object> params = new HashMap<String, Object>();
			//显示文件名,必填 
			params.put("fileNameOriginal", fileName);
			//原文件DC,必填 
			params.put("sourceDc", doc);
			//sessionid,必填 
			params.put("sid", uc.getSessionId());
			//文档是否加密,非必填 
			params.put("isEncrypt", false);
			AppAPI appAPI =  SDK.getAppAPI(); 
			//WORD文档转成PDF文件 
			ResponseObject ro = appAPI.callASLP(appAPI.getAppContext(sourceAppId), aslp, params);
			//System.out.println(ro.toString());
			//String url = ro.get("fileUrl").toString();
			//System.out.println(url);
			JSONObject obj = JSONObject.parseObject(ro.toString());
			JSONObject obj1 = obj.getJSONObject("data");
			JSONObject obj2 = obj1.getJSONObject("pdfDC");
			//获取加密文件相对路径（不含文件名称）
			String relativePath = obj2.getString("path");
			//获取加密文件名称
			String securityFileName = DCUtil.encryptFileName(obj2.getString("fileName"));
			relativePath = relativePath + securityFileName;
			System.out.println(relativePath);
			File file =new File(relativePath);
			Long fileSize = file.length();
			//String absolutePath = file.getCanonicalPath();
			//absolutePath = absolutePath.replaceAll("\\\\", "/");
			//System.out.println(absolutePath);
			//File f =new File(absolutePath);
			FileInputStream fis =new FileInputStream(file);
			fis.read();
			FormFileModel ffm = createFormFileModel(uc, sourceAppId, obj2.getString("fileName"), "BO_CS_FZFJ1", "FILE2", fileSize, bo.getId(), processInstId, taskInstId);
			if(ffm!=null) {
				SDK.getBOAPI().upFile(ffm, fis);
			}
			fis.close();
		}
	}
	
	private FormFileModel createFormFileModel(UserContext me, String appId, String fileName,String boName, String boItemName, long fileSize, String boId, 
            String processId, String taskId)
    {
        FormFileModel fileModel = new FormFileModel();
        fileModel.setAppId(appId);
        fileModel.setBoName(boName);
        fileModel.setBoItemName(boItemName);
        fileModel.setFileSize(fileSize);
        fileModel.setFileName(fileName);
        fileModel.setBoId(boId);
        fileModel.setCreateDate(new Timestamp((new Date()).getTime()));
        fileModel.setRemark("");
        fileModel.setCreateUser(me.getUID());
        fileModel.setExt1("");
        fileModel.setTaskInstId(taskId);
        fileModel.setProcessInstId(processId);
        FormFileDao fileDao = new FormFileDao();
        int flag = fileDao.insert(fileModel);
        if(flag > 0)
            return fileModel;
        else
            return null;
    }

	
}

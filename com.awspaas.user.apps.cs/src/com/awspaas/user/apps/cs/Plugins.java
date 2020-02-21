package com.awspaas.user.apps.cs;

import java.util.ArrayList;
import java.util.List;

import com.actionsoft.apps.listener.PluginListener;
import com.actionsoft.apps.resource.AppContext;
import com.actionsoft.apps.resource.plugin.profile.AWSPluginProfile;
import com.actionsoft.apps.resource.plugin.profile.AtFormulaPluginProfile;
import com.awspaas.user.apps.cs.at.GetRmbValueExpression;

public class Plugins implements PluginListener {
	public Plugins() {
    }

	@Override
	public List<AWSPluginProfile> register(AppContext context) {
        // 存放本应用的全部插件扩展点描述
        List<AWSPluginProfile> list = new ArrayList<AWSPluginProfile>();
        // 注册AT公式
        list.add(new AtFormulaPluginProfile("自定义", "@getRmbValueByNumber(*Name)", GetRmbValueExpression.class.getName(), "获取数值大写", "根据数值获取数值对应的大写"));
        return list;
    }

}

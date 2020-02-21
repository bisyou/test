package com.awspaas.user.apps.cs.at;

import com.actionsoft.bpms.commons.at.AbstExpression;
import com.actionsoft.bpms.commons.at.ExpressionContext;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.exception.AWSExpressionException;

public class GetRmbValueExpression extends AbstExpression {

	public GetRmbValueExpression(ExpressionContext atContext, String expressionValue) {
		super(atContext, expressionValue);
	}

	@Override
	public String execute(String expression) throws AWSExpressionException {
		// 取第1个参数
        String deptName = getParameter(expression, 1);
        String BoName ="ORGDEPARTMENT";
        String sql ="SELECT ID FROM "+BoName+" WHERE DEPARTMENTNAME=?";
        String depiId = DBSql.getString(sql, new Object[] {deptName});
        return depiId.toString();
	}

}

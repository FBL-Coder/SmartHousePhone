package cn.etsoft.smarthomephone.pullmi.common;

import cn.etsoft.smarthomephone.pullmi.app.GlobalVars;

public class DbOperator {

	private static DatabaseUtil dbUtil;
	
	static {
		dbUtil = DatabaseUtil.getInstance(GlobalVars.getContext());
	}
	
	public static DatabaseUtil getDbOperator() {
		return dbUtil;
	}

}

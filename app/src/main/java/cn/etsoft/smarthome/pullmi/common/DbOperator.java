package cn.etsoft.smarthome.pullmi.common;


import cn.etsoft.smarthome.NetMessage.GlobalVars;

public class DbOperator {

	private static DatabaseUtil dbUtil;
	
	static {
		dbUtil = DatabaseUtil.getInstance(GlobalVars.getContext());
	}
	
	public static DatabaseUtil getDbOperator() {
		return dbUtil;
	}

}

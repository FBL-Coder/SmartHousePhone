package cn.etsoft.smarthome.Helper;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;
import com.lidroid.xutils.util.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.etsoft.smarthome.MyApplication;
import cn.etsoft.smarthome.NetMessage.GlobalVars;
import cn.etsoft.smarthome.domain.Http_Result;
import cn.etsoft.smarthome.domain.WareData;
import cn.etsoft.smarthome.pullmi.utils.Data_Cache;
import cn.etsoft.smarthome.utils.AppSharePreferenceMgr;
import cn.etsoft.smarthome.utils.HttpGetDataUtils.HttpCallback;
import cn.etsoft.smarthome.utils.HttpGetDataUtils.NewHttpPort;
import cn.etsoft.smarthome.utils.HttpGetDataUtils.OkHttpUtils;
import cn.etsoft.smarthome.utils.HttpGetDataUtils.ResultDesc;
import cn.etsoft.smarthome.utils.ToastUtil;
import cn.semtec.community2.tool.Constants;

/**
 * Author：FBL  Time： 2017/9/28.
 */
public class LogoutHelper {
    public static void logout(final Activity activity) {
        MyApplication.mApplication.showLoadDialog(activity);
        Map<String, String> params = new HashMap<>();
        params.put("uid", GlobalVars.getUserid());
        OkHttpUtils.postAsyn(NewHttpPort.ROOT + NewHttpPort.LOCATION + NewHttpPort.LOGOUT, params, new HttpCallback() {
            @Override
            public void onSuccess(ResultDesc resultDesc) {
                super.onSuccess(resultDesc);
                Log.i("LOGOUT", "智能家居成功: " + resultDesc.getResult());
                Gson gson = new Gson();
                Http_Result result = gson.fromJson(resultDesc.getResult(), Http_Result.class);
                if (result.getCode() == 0) {
//                                        logout_yun();
                    logout_event(activity);
                } else {
                    MyApplication.mApplication.dismissLoadDialog();
                    Log.i("LOGOUT", "智能家具失败: " + resultDesc.getResult());
                    if ("".equals(result.getMsg()))
                        ToastUtil.showText("退出失败");
                    else
                        ToastUtil.showText(result.getMsg());
                }
            }

            @Override
            public void onFailure(int code, String message) {
                super.onFailure(code, message);
                MyApplication.mApplication.dismissLoadDialog();
                ToastUtil.showText("退出失败");
                Log.i("LOGOUT", "智能家具失败: ");
            }
        });
    }

    private void logout_yun(final Activity activity) {
        String url = Constants.CONTENT_LOGOUT;
        OkHttpUtils.postAsyn(url, null, new HttpCallback() {
            @Override
            public void onSuccess(ResultDesc resultDesc) {
                super.onSuccess(resultDesc);
                MyApplication.mApplication.dismissLoadDialog();
                String mResult = resultDesc.getResult().toString();
                try {
                    JSONObject jo = new JSONObject(mResult);
                    if (jo.getInt("returnCode") == 0) {
                        Log.i("LOGOUT", "云对讲服务器登出成功 ");
                        logout_event(activity);
                    } else {
                        Log.i("LOGOUT", "云对讲服务器登出失败 ");
                        ToastUtil.showText("登出失败");
                    }
                } catch (JSONException e) {
                    Log.i("LOGOUT", "云对讲服务器登出失败 ");
                    ToastUtil.showText("数据处理失败");
                }
            }

            @Override
            public void onFailure(int code, String message) {
                super.onFailure(code, message);
                MyApplication.mApplication.dismissLoadDialog();
                LogUtils.i("网络异常" + code + "------------" + message);
            }
        });
    }

    private static void logout_event(Activity activity) {
        ToastUtil.showText("登出成功");
        Data_Cache.writeFile(GlobalVars.getDevid(), new WareData());
        GlobalVars.setDevid("");
        GlobalVars.setDevpass("");
        GlobalVars.setUserid("");
        AppSharePreferenceMgr.put(GlobalVars.RCUINFOID_SHAREPREFERENCE, "");
        AppSharePreferenceMgr.put(GlobalVars.SAFETY_TYPE_SHAREPREFERENCE, 0);
        AppSharePreferenceMgr.put(GlobalVars.RCUINFOLIST_SHAREPREFERENCE, "");
        AppSharePreferenceMgr.put(GlobalVars.LOGOUT_SHAREPREFERENCE, true);
        MyApplication.mApplication.dismissLoadDialog();
        activity.startActivity(new Intent(activity, cn.semtec.community2.activity.LoginActivity.class));
        activity.finish();
    }
}

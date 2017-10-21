package cn.etsoft.smarthome.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;

import cn.etsoft.smarthome.NetMessage.GlobalVars;
import cn.etsoft.smarthome.domain.RcuInfo;
import cn.etsoft.smarthome.ui.Setting.NewWorkSetActivity;
import cn.etsoft.smarthome.utils.AppSharePreferenceMgr;
import cn.etsoft.smarthome.utils.HttpGetDataUtils.HttpCallback;
import cn.etsoft.smarthome.utils.HttpGetDataUtils.NewHttpPort;
import cn.etsoft.smarthome.utils.HttpGetDataUtils.OkHttpUtils;
import cn.etsoft.smarthome.utils.HttpGetDataUtils.ResultDesc;
import cn.etsoft.smarthome.utils.SendDataUtil;
import cn.etsoft.smarthome.utils.ToastUtil;
import cn.etsoft.smarthome.utils.UpdateManager;

import static cn.semtec.community2.exception.CrashHandler.TAG;


/**
 * Author：FBL  Time： 2017/6/13.
 * 欢迎界面
 */

public class WelcomeActivity extends Activity {

    private List<RcuInfo> mRcuInfos;
    private WelcomeHandler welcomeHandler = new WelcomeHandler(this);
    private String serverVersion;
    private UpdateManager mUpdateManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IsUpDataApk();
        try {
            //获取屏幕数据
            DisplayMetrics metric = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metric);
            // 屏幕宽度（像素）
            cn.semtec.community2.MyApplication.display_width = metric.widthPixels;
            // 屏幕高度（像素）
            cn.semtec.community2.MyApplication.display_height = metric.heightPixels;
            cn.semtec.community2.MyApplication.density = metric.density;
        } catch (Exception e1) {
        }
        initData();
    }

    private void IsUpDataApk() {
        HashMap<String, String> map = new HashMap<>();

        OkHttpUtils.postAsyn(NewHttpPort.ROOT + NewHttpPort.LOCATION + NewHttpPort.GetApkVersions,
                map, new HttpCallback() {
            @Override
            public void onSuccess(ResultDesc resultDesc) {
                super.onSuccess(resultDesc);
                //TODO 返回版本号后需要解析
                Log.i(TAG, "onSuccess: " + resultDesc.getResult());
                serverVersion = resultDesc.getResult();
                Message message = welcomeHandler.obtainMessage();
                message.what = 5;
                welcomeHandler.sendMessage(message);
            }

            @Override
            public void onFailure(int code, String message) {
                super.onFailure(code, message);
                Log.i(TAG, "onFailure: " + code + "-----" + message);
                Message message1 = welcomeHandler.obtainMessage();
                message1.what = 5;
                welcomeHandler.sendMessage(message1);
            }
        });


    }

    public void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    Message message2 = welcomeHandler.obtainMessage();
                    message2.what = 1;
                    welcomeHandler.sendMessage(message2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    static class WelcomeHandler extends Handler {
        WeakReference<WelcomeActivity> weakReference;

        WelcomeHandler(WelcomeActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (weakReference != null) {
                if (msg.what == 1) {

                    String json_RcuInfolist = (String) AppSharePreferenceMgr.get(GlobalVars.RCUINFOLIST_SHAREPREFERENCE, "");

                    String json_RcuinfoID = (String) AppSharePreferenceMgr.get(GlobalVars.RCUINFOID_SHAREPREFERENCE, "");


                    boolean IsLogin = (boolean) AppSharePreferenceMgr.get(GlobalVars.LOGIN_SHAREPREFERENCE, false);

                    if (!IsLogin) {
                        weakReference.get().startActivity(new Intent(weakReference.get(), cn.semtec.community2.activity.LoginActivity.class));
                        weakReference.get().finish();
                    }
                    if (IsLogin && "".equals(json_RcuinfoID)) {
                        weakReference.get().startActivity(new Intent(weakReference.get(), NewWorkSetActivity.class));
                        ToastUtil.showText("请选择联网模块");
                        weakReference.get().finish();
                    }
                    if (IsLogin && !"".equals(json_RcuinfoID)) {
                        weakReference.get().startActivity(new Intent(weakReference.get(), HomeActivity.class));
                        SendDataUtil.getNetWorkInfo();
                        weakReference.get().finish();
                    }
                } else if (msg.what == 5) {
                    new Thread() {
                        public void run() {
                            //这儿是耗时操作，完成之后更新UI；
                            weakReference.get().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //这里来检测版本是否需要更新
                                    Log.i("VER", "版本号:" + weakReference.get().serverVersion);
                                    if (weakReference.get().serverVersion.length() > 0) {
                                        weakReference.get().mUpdateManager = new UpdateManager(weakReference.get(), weakReference.get().serverVersion);
                                        weakReference.get().mUpdateManager.showNoticeDialog(weakReference.get().welcomeHandler);
                                    }
                                }
                            });
                        }
                    }.start();
                }
            }
        }
    }
}

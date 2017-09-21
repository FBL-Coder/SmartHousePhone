package cn.etsoft.smarthome.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;


import java.lang.ref.WeakReference;
import java.util.List;

import cn.etsoft.smarthome.NetMessage.GlobalVars;
import cn.etsoft.smarthome.R;
import cn.etsoft.smarthome.domain.RcuInfo;
import cn.etsoft.smarthome.utils.AppSharePreferenceMgr;
import cn.etsoft.smarthome.utils.SendDataUtil;
import cn.etsoft.smarthome.utils.ToastUtil;


/**
 * Author：FBL  Time： 2017/6/13.
 * 欢迎界面
 */

public class WelcomeActivity extends Activity{

    private List<RcuInfo> mRcuInfos;
    private WelcomeHandler welcomeHandler = new WelcomeHandler(this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

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



    public void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                welcomeHandler.sendMessage(welcomeHandler.obtainMessage());
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

                String json_RcuInfolist = (String) AppSharePreferenceMgr.get(GlobalVars.RCUINFOLIST_SHAREPREFERENCE, "");

                String json_RcuinfoID = (String) AppSharePreferenceMgr.get(GlobalVars.RCUINFOID_SHAREPREFERENCE, "");

                String UserID = (String) AppSharePreferenceMgr.get(GlobalVars.USERID_SHAREPREFERENCE, "");

                if ("".equals(UserID)) {
                    weakReference.get().startActivity(new Intent(weakReference.get(), cn.semtec.community2.activity.LoginActivity.class));
                    weakReference.get().finish();
                }
                if (!"".equals(UserID) && "".equals(json_RcuinfoID)) {
                    weakReference.get().startActivity(new Intent(weakReference.get(), NewWorkSetActivity.class));
                    ToastUtil.showText("请选择联网模块");
                    weakReference.get().finish();
                }
                if (!"".equals(UserID) && !"".equals(json_RcuinfoID)) {
                    SendDataUtil.getNetWorkInfo();
                    weakReference.get().startActivity(new Intent(weakReference.get(), HomeActivity.class));
                    weakReference.get().finish();
                }
            }
        }
    }
}

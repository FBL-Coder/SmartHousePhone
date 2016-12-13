package cn.etsoft.smarthomephone.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import cn.etsoft.smarthomephone.MyApplication;
import cn.etsoft.smarthomephone.R;
import cn.etsoft.smarthomephone.pullmi.app.GlobalVars;
import cn.etsoft.smarthomephone.pullmi.entity.RcuInfo;
import cn.etsoft.smarthomephone.pullmi.entity.WareData;
import cn.etsoft.smarthomephone.pullmi.utils.Dtat_Cache;
import cn.etsoft.smarthomephone.pullmi.utils.LogUtils;


/**
 * 作者：FBL  时间： 2016/8/31.
 */
public class WelcomeActivity extends Activity {

    private List<RcuInfo> mRcuInfos;
    private String TAG = "WelCome";
    private Handler mHandler, mDataHandler;
    private int OUTTIME_DOWNLOAD = 1111;
    private int OUTTIME_INITUID = 1000;
    private int count = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
    }

    @Override
    protected void onResume() {
        super.onResume();

        MyApplication.mInstance.setActivity(this);
        MyApplication.mInstance.setOnGetWareDataListener(new MyApplication.OnGetWareDataListener() {
            @Override
            public void upDataWareData(int what) {
            }
        });
        SharedPreferences sharedPreferences = this.getSharedPreferences("FirstRun", MODE_PRIVATE);
        boolean isFirstRun = sharedPreferences.getBoolean("isFirstRun", true);
        SharedPreferences.Editor editor = sharedPreferences.edit();


        mRcuInfos = getGwList();
        if (mRcuInfos != null && mRcuInfos.size() > 0) {

            GlobalVars.setDevid(mRcuInfos.get(mRcuInfos.size() - 1).getDevUnitID());
            GlobalVars.setDevpass(mRcuInfos.get(mRcuInfos.size() - 1).getDevUnitPass());
            mHandler = new Handler();
            MyApplication.mInstance.setHandler(mHandler);
            mDataHandler = new Handler() {

                @Override
                public void handleMessage(Message msg) {
                    if (msg.what == OUTTIME_INITUID) {
                        LogUtils.LOGE("", GlobalVars.getDstip() + "获取数据");
                        MyApplication.setRcuDevIDtoLocal();
                        /**
                         * 局域网内 300秒发送一次数据请求；
                         */
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    for (; ; ) {
//                                        Log.i("TIME", "局域网内数据请求-----------------");
                                        Thread.sleep(300 * 1000);
                                        if (MyApplication.getWareData().getRcuInfos() == null
                                                || MyApplication.getWareData().getRcuInfos().size() == 0) {
                                            GlobalVars.setDstip("127.0.0.1");
                                        } else {
                                            if (!MyApplication.getWareData().isDATA_LOCAL_FLAG())
                                                GlobalVars.setDstip("123.206.104.89");
                                            else
                                                GlobalVars.setDstip("127.0.0.1");
                                        }
                                        MyApplication.setRcuDevIDtoLocal();
                                    }
                                } catch (Exception e) {
                                }
                            }
                        }).start();
                    }
                    super.handleMessage(msg);
                }
            };
            MyApplication.mInstance.setAllHandler(mDataHandler);
            if (isFirstRun) {
                editor.putBoolean("isFirstRun", false);
                editor.commit();
                GlobalVars.setDevid(mRcuInfos.get(mRcuInfos.size() - 1).getDevUnitID());
                GlobalVars.setDevpass(mRcuInfos.get(mRcuInfos.size() - 1).getDevUnitPass());
                startActivity(new Intent(WelcomeActivity.this, HomeActivity.class));
                finish();
            } else {
                MyApplication.setWareData((WareData) Dtat_Cache.readFile());
                GlobalVars.setDevid(mRcuInfos.get(mRcuInfos.size() - 1).getDevUnitID());
                GlobalVars.setDevpass(mRcuInfos.get(mRcuInfos.size() - 1).getDevUnitPass());
                startActivity(new Intent(WelcomeActivity.this, HomeActivity.class));
                finish();
            }
        } else {
            startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
            finish();
        }
    }
    @Nullable
    private List<RcuInfo> getGwList() {
        SharedPreferences sharedPreferences = getSharedPreferences("profile",
                Context.MODE_PRIVATE);
        String jsondata = sharedPreferences.getString("list", "null");
        Gson gson = new Gson();
        if (!jsondata.equals("null")) {
            List<RcuInfo> list = gson.fromJson(jsondata, new TypeToken<List<RcuInfo>>() {
            }.getType());
            for (int i = 0; i < list.size(); i++) {
                RcuInfo p = list.get(i);
                System.out.println(p.getName());
            }
            return list;
        } else {
            return null;
        }
    }
}

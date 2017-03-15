package cn.etsoft.smarthomephone.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
 * 欢迎页面
 */
public class WelcomeActivity extends Activity {

    private List<RcuInfo> mRcuInfos;
    private String TAG = "WelCome";
    private Handler mHandler, mDataHandler;
    private int OUTTIME_DOWNLOAD = 1111;
    private int OUTTIME_INITUID = 1000;
    private int count = 1;
    private int LOGIN_OK = 1;

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
        int login = getIntent().getIntExtra("login", 0);

        MyApplication.mInstance.startSer();
        SharedPreferences sharedPreferences = getSharedPreferences("profile",
                Context.MODE_PRIVATE);
        String jsondata = sharedPreferences.getString("list", "");
        Gson gson = new Gson();
        if (!jsondata.equals("")) {
            mRcuInfos = gson.fromJson(jsondata, new TypeToken<List<RcuInfo>>() {
            }.getType());
        }
        if (mRcuInfos != null && mRcuInfos.size() == 1) {

            GlobalVars.setDevid(mRcuInfos.get(mRcuInfos.size() - 1).getDevUnitID());
            GlobalVars.setDevpass(mRcuInfos.get(mRcuInfos.size() - 1).getDevUnitPass());
            MyApplication.setWareData((WareData) Dtat_Cache.readFile(GlobalVars.getDevid()));
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
                                            GlobalVars.setDstip(MyApplication.LOCAL_IP);
                                        }
                                        MyApplication.setRcuDevIDtoLocal();
                                    }
                                } catch (Exception e) {
                                }
                            }
                        }).start();

//                        /**
//                         * 局域网内 30秒发送一次数据请求；
//                         */
//                        new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                try {
//                                    for (; ; ) {
////                                        Log.i("TIME", "局域网内数据请求-----------------");
//                                        Thread.sleep(30 * 1000);
//                                        if (MyApplication.getWareData().getRcuInfos() == null
//                                                || MyApplication.getWareData().getRcuInfos().size() == 0) {
//                                        }
//                                        MyApplication.getlife();
//                                    }
//                                } catch (Exception e) {
//                                }
//                            }
//                        }).start();
                    }
                    super.handleMessage(msg);
                }
            };
            MyApplication.mInstance.setAllHandler(mDataHandler);
            MyApplication.mInstance.setRcuInfo(mRcuInfos.get(0));
            startActivity(new Intent(WelcomeActivity.this, HomeActivity.class));
            finish();
        } else if (mRcuInfos != null && mRcuInfos.size() > 1) {
            SharedPreferences sharedPreferences1 = getSharedPreferences("profile",
                    Context.MODE_PRIVATE);
            String module_str = sharedPreferences1.getString("module_str", "");
            if (!"".equals(module_str)) {
                String DevID = module_str.substring(0, module_str.indexOf("-"));
                GlobalVars.setDevid(DevID);
                GlobalVars.setDevpass(module_str.substring(module_str.indexOf("-")+1));
                //读缓存数据
                MyApplication.setWareData((WareData) Dtat_Cache.readFile(DevID));
                if (MyApplication.getWareData().getDevs().size() > 0) {
                    for (int i = 0; i < MyApplication.getWareData().getDevs().size(); i++) {
                        Log.e("Exception", MyApplication.getWareData().getDevs().get(i).getDevName());
                    }
                }
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
                                                GlobalVars.setDstip(MyApplication.LOCAL_IP);
                                            }
                                            MyApplication.setRcuDevIDtoLocal();
                                        }
                                    } catch (Exception e) {
                                    }
                                }
                            }).start();

//                        /**
//                         * 局域网内 30秒发送一次数据请求；
//                         */
//                        new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                try {
//                                    for (; ; ) {
////                                        Log.i("TIME", "局域网内数据请求-----------------");
//                                        Thread.sleep(30 * 1000);
//                                        if (MyApplication.getWareData().getRcuInfos() == null
//                                                || MyApplication.getWareData().getRcuInfos().size() == 0) {
//                                        }
//                                        MyApplication.getlife();
//                                    }
//                                } catch (Exception e) {
//                                }
//                            }
//                        }).start();
                        }
                        super.handleMessage(msg);
                    }
                };
                for (int i = 0; i < mRcuInfos.size(); i++) {
                    if (DevID.equals(mRcuInfos.get(i).getDevUnitID())) {
                        MyApplication.mInstance.setRcuInfo(mRcuInfos.get(i));
                        break;
                    }
                }
                MyApplication.mInstance.setAllHandler(mDataHandler);

                startActivity(new Intent(WelcomeActivity.this, HomeActivity.class));
                finish();
            } else {
                startActivity(new Intent(WelcomeActivity.this, NetWorkActivity.class));
                finish();
            }
        } else {
            if (login == LOGIN_OK) {
                startActivity(new Intent(WelcomeActivity.this, NetWorkActivity.class));
                finish();
            } else {
                startActivity(new Intent(WelcomeActivity.this, cn.semtec.community2.activity.LoginActivity.class));
//                startActivity(new Intent(WelcomeActivity.this, HomeActivity.class));
                finish();
//                ToastUtil.showToast(WelcomeActivity.this,"跳转登陆页面");
            }
        }
    }
}

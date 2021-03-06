package cn.etsoft.smarthome.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import cn.etsoft.smarthome.MyApplication;
import cn.etsoft.smarthome.NetMessage.GlobalVars;
import cn.etsoft.smarthome.R;
import cn.etsoft.smarthome.adapter.GridViewAdapter_user;
import cn.etsoft.smarthome.domain.UdpProPkt;
import cn.etsoft.smarthome.domain.UserBean;
import cn.etsoft.smarthome.domain.WareAirCondDev;
import cn.etsoft.smarthome.domain.WareCurtain;
import cn.etsoft.smarthome.domain.WareFloorHeat;
import cn.etsoft.smarthome.domain.WareFreshAir;
import cn.etsoft.smarthome.domain.WareLight;
import cn.etsoft.smarthome.domain.WareSetBox;
import cn.etsoft.smarthome.domain.WareTv;
import cn.etsoft.smarthome.ui.HomeActivity;
import cn.etsoft.smarthome.ui.Setting.UserAddDevsActivty;
import cn.etsoft.smarthome.ui.UserInterface.Air_Control;
import cn.etsoft.smarthome.ui.UserInterface.Cur_Control;
import cn.etsoft.smarthome.ui.UserInterface.Flo_Control;
import cn.etsoft.smarthome.ui.UserInterface.Fre_Control;
import cn.etsoft.smarthome.utils.AppSharePreferenceMgr;
import cn.etsoft.smarthome.utils.HttpGetDataUtils.HttpCallback;
import cn.etsoft.smarthome.utils.HttpGetDataUtils.NewHttpPort;
import cn.etsoft.smarthome.utils.HttpGetDataUtils.OkHttpUtils;
import cn.etsoft.smarthome.utils.HttpGetDataUtils.ResultDesc;
import cn.etsoft.smarthome.utils.SendDataUtil;
import cn.etsoft.smarthome.utils.ToastUtil;

/**
 * Created by Say GoBay on 2016/9/1.
 * 用户界面
 */
public class UserInterface extends Fragment implements AdapterView.OnItemClickListener {

    private GridView gridView;
    private Activity mActivity;
    private GridViewAdapter_user adapter_user;
    private UserBean bean;
    private Handler handler;
    private int count;

    public UserInterface(Activity activity) {
        mActivity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        gridView = (GridView) view.findViewById(R.id.home_gv);
        if (MyApplication.getWareData().getUserBeen().getUser_bean().size() > 0) {
            MyApplication.mApplication.dismissLoadDialog();
            //初始化GridView
            initGridView(true);
        }
        return view;
    }

    @Override
    public void onResume() {
        if (!"".equals(GlobalVars.getDevid())) {
            if (MyApplication.getWareData().getSceneEvents().size() == 0)
                SendDataUtil.getSceneInfo();
            handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    MyApplication.mApplication.dismissLoadDialog();
                    if (msg.what == 0)
                        //初始化GridView
                        initGridView(true);
                    else {
                        ToastUtil.showText("用户数据获取失败");
                    }
                }
            };
            if (!MyApplication.mApplication.isVisitor())
                getUserData(handler);
        }
        HomeActivity.setHomeDataUpDataListener(new HomeActivity.HomeDataUpDataListener() {
            @Override
            public void getupData(int datType, int subtype1, int subtype2) {
                if (datType == 0) {
                    MyApplication.mApplication.dismissLoadDialog();
                    if (!MyApplication.mApplication.isVisitor())
                        getUserData(handler);
                }
                if (datType == 3 || datType == 4 || datType == 35) {
                    MyApplication.mApplication.dismissLoadDialog();
                    initGridView(false);
                }
            }
        });
        super.onResume();
    }

    private void getUserData(final Handler handler) {
        MyApplication.mApplication.showLoadDialog(mActivity, false);
        HashMap<String, String> map = new HashMap();
        map.put("userName", (String) AppSharePreferenceMgr.get(GlobalVars.USERID_SHAREPREFERENCE, ""));
        map.put("passwd", (String) AppSharePreferenceMgr.get(GlobalVars.USERPASSWORD_SHAREPREFERENCE, ""));
        map.put("devUnitID", (String) AppSharePreferenceMgr.get(GlobalVars.RCUINFOID_SHAREPREFERENCE, ""));

        OkHttpUtils.postAsyn(NewHttpPort.ROOT + NewHttpPort.LOCATION + NewHttpPort.USER_GET,
                map, new HttpCallback() {
                    @Override
                    public void onSuccess(ResultDesc resultDesc) {
                        super.onSuccess(resultDesc);
                        try {
                            Log.i("UserInterface", "onSuccess: " + resultDesc.getResult()
                            );
                            JSONObject object = new JSONObject(resultDesc.getResult());
                            int code = object.getInt("code");
                            if (code != 0) {
                                ToastUtil.showText(object.getInt("msg"));
                                return;
                            }
                            JSONArray array = object.getJSONArray("data");
                            JSONObject object1 = null;
                            if (array.length() > 0) {
                                object1 = array.getJSONObject(0);
                                Gson gson = new Gson();
                                UserBean userBean = gson.fromJson(object1.toString(), UserBean.class);
                                MyApplication.getWareData().setUserBeen(userBean);
                                Message message = handler.obtainMessage();
                                message.what = 0;
                                handler.sendMessage(message);
                            } else {
                                Message message = handler.obtainMessage();
                                message.what = 0;
                                handler.sendMessage(message);
                            }
                        } catch (Exception e) {
                            Log.i("UserInterface", "Exception: " + e);
                            return;
                        }
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        Log.i("USER_onFailure", "message: " + code + "--" + message);
                        Message msg = handler.obtainMessage();
                        msg.what = 1;
                        handler.sendMessage(msg);
                        super.onFailure(code, message);
                    }
                });
    }

    /**
     * 初始化GridView
     */
    private void initGridView(boolean isNew) {
        bean = MyApplication.getWareData().getUserBeen();
        if (isNew || adapter_user == null) {
            adapter_user = new GridViewAdapter_user(MyApplication.getWareData().getUserBeen(), mActivity);
            gridView.setAdapter(adapter_user);
        } else
            adapter_user.notifyDataSetChanged(MyApplication.getWareData().getUserBeen());
        gridView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TextView state = (TextView) view.findViewById(R.id.equip_style);
        if (position < bean.getUser_bean().size()) {
            //给点击按钮添加点击音效
            MyApplication.mApplication.getSp().play(MyApplication.mApplication.getMusic(), 1, 1, 0, 0, 1);
            UserBean.UserBeanBean beanBean = bean.getUser_bean().get(position);
            if (beanBean.isIsDev() == 1) {
                int type_dev = beanBean.getDevType();
                if (type_dev == 0) {
                    Intent intent = new Intent(mActivity, Air_Control.class);
                    intent.putExtra("cancpuid", beanBean.getCanCpuID());
                    intent.putExtra("devid", beanBean.getDevID());
                    mActivity.startActivity(intent);
                } else if (type_dev == 1) {
                    for (int j = 0; j < MyApplication.getWareData().getTvs().size(); j++) {
                        WareTv tv = MyApplication.getWareData().getTvs().get(j);
                        if (beanBean.getCanCpuID().equals(tv.getDev().getCanCpuId())
                                && beanBean.getDevID() == tv.getDev().getDevId()) {
                            SendDataUtil.controlDev(tv.getDev(), 0);
                        }
                    }
                } else if (type_dev == 2) {
                    for (int j = 0; j < MyApplication.getWareData().getStbs().size(); j++) {
                        WareSetBox box = MyApplication.getWareData().getStbs().get(j);
                        if (beanBean.getCanCpuID().equals(box.getDev().getCanCpuId())
                                && beanBean.getDevID() == box.getDev().getDevId()) {
                            SendDataUtil.controlDev(box.getDev(), 0);
                        }
                    }
                } else if (type_dev == 3) {
                    for (int j = 0; j < MyApplication.getWareData().getLights().size(); j++) {
                        WareLight Light = MyApplication.getWareData().getLights().get(j);
                        if (beanBean.getCanCpuID().equals(Light.getDev().getCanCpuId())
                                && beanBean.getDevID() == Light.getDev().getDevId()) {
                            if (Light.getbOnOff() == 0) {
                                SendDataUtil.controlDev(Light.getDev(), 0);
                            } else {
                                SendDataUtil.controlDev(Light.getDev(), 1);
                            }
                        }
                    }
                } else if (type_dev == 4) {
                    Intent intent = new Intent(mActivity, Cur_Control.class);
                    intent.putExtra("cancpuid", beanBean.getCanCpuID());
                    intent.putExtra("devid", beanBean.getDevID());
                    mActivity.startActivity(intent);
                } else if (type_dev == 7) {
                    Intent intent = new Intent(mActivity, Fre_Control.class);
                    intent.putExtra("cancpuid", beanBean.getCanCpuID());
                    intent.putExtra("devid", beanBean.getDevID());
                    mActivity.startActivity(intent);
                } else if (type_dev == 9) {
                    Intent intent = new Intent(mActivity, Flo_Control.class);
                    intent.putExtra("cancpuid", beanBean.getCanCpuID());
                    intent.putExtra("devid", beanBean.getDevID());
                    mActivity.startActivity(intent);
                }
            } else {
                SendDataUtil.executelScene(beanBean.getEventId());
            }
        } else {
            startActivity(new Intent(mActivity, UserAddDevsActivty.class));
        }
    }
}

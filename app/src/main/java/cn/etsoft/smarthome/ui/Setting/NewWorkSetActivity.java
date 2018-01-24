package cn.etsoft.smarthome.ui.Setting;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.githang.statusbar.StatusBarCompat;
import com.google.gson.Gson;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.etsoft.smarthome.Helper.Net_AddorDel_Helper;
import cn.etsoft.smarthome.MyApplication;
import cn.etsoft.smarthome.NetMessage.GlobalVars;
import cn.etsoft.smarthome.R;
import cn.etsoft.smarthome.adapter.NetWork_Adapter;
import cn.etsoft.smarthome.domain.Http_Result;
import cn.etsoft.smarthome.domain.RcuInfo;
import cn.etsoft.smarthome.domain.SearchNet;
import cn.etsoft.smarthome.domain.WareData;
import cn.etsoft.smarthome.pullmi.common.CommonUtils;
import cn.etsoft.smarthome.pullmi.utils.Data_Cache;
import cn.etsoft.smarthome.ui.HomeActivity;
import cn.etsoft.smarthome.utils.AppSharePreferenceMgr;
import cn.etsoft.smarthome.utils.HttpGetDataUtils.HTTPRequest_BackCode;
import cn.etsoft.smarthome.utils.HttpGetDataUtils.HttpCallback;
import cn.etsoft.smarthome.utils.HttpGetDataUtils.NewHttpPort;
import cn.etsoft.smarthome.utils.HttpGetDataUtils.OkHttpUtils;
import cn.etsoft.smarthome.utils.HttpGetDataUtils.ResultDesc;
import cn.etsoft.smarthome.utils.SendDataUtil;
import cn.etsoft.smarthome.utils.ToastUtil;
import cn.semtec.community2.activity.LoginActivity;

import static android.content.ContentValues.TAG;

/**
 * Author：FBL  Time： 2017/7/10.
 * 联网模块设置界面
 */

public class NewWorkSetActivity extends Activity {
    private TextView mDialogCancle, mDialogOk, mTitle, logout;
    private ImageView mBack, network_ref;
    private EditText mDialogName, mDialogID, mDialogPass;
    private NewModuleHandler mNewModuleHandler = new NewModuleHandler(this);
    private Gson gson = new Gson();
    private int mDeleteNet_Position = -1;
    private TextView mNetworkAdd, network_sousuo;
    private ListView mNetListView, sousuo_list;
    private NetWork_Adapter adapter, mSeekAdapter;
    private LinearLayout add_ref_LL;
    /**
     * 情景名称 :
     */
    private TextView mTitleName;
    /**
     * 请输入模块名称
     */
    private EditText mDialogAddSceneName;
    /**
     * 取消
     */
    private TextView mDialogAddSceneCancle;
    /**
     * 添加
     */
    private TextView mDialogAddSceneOk;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.AppTheme_color));

    }


    /**
     * 初始化页面组件
     */
    private void initView() {
        mBack = (ImageView) findViewById(R.id.title_bar_iv_back);
        logout = (TextView) findViewById(R.id.title_bar_tv_room);
        add_ref_LL = (LinearLayout) findViewById(R.id.add_ref_LL);
        network_ref = (ImageView) findViewById(R.id.network_ref);
        mTitle = (TextView) findViewById(R.id.title_bar_tv_title);
        network_sousuo = (TextView) findViewById(R.id.network_sousuo);
        mTitle.setVisibility(View.VISIBLE);
        mNetworkAdd = (TextView) findViewById(R.id.network_add);
        mNetListView = (ListView) findViewById(R.id.equi_list);
        sousuo_list = (ListView) findViewById(R.id.sousuo_list);
        mTitle.setText("模块设置");
        if (MyApplication.mApplication.getRcuInfoList().size() == 0) {
            logout.setVisibility(View.VISIBLE);
            logout.setTextColor(Color.BLACK);
            logout.setText("退出登录");
            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(NewWorkSetActivity.this, LoginActivity.class));
                    AppSharePreferenceMgr.put(GlobalVars.LOGIN_SHAREPREFERENCE, false);
                    finish();
                }
            });
        }
        if (MyApplication.mApplication.isVisitor()) {
            add_ref_LL.setVisibility(View.GONE);
        }
//        initSeekList();
        mTitle = (TextView) findViewById(R.id.title);
        mTitleName = (TextView) findViewById(R.id.title_name);
        mDialogAddSceneName = (EditText) findViewById(R.id.dialog_addScene_name);
        mDialogAddSceneCancle = (TextView) findViewById(R.id.dialog_addScene_cancle);
        mDialogAddSceneOk = (TextView) findViewById(R.id.dialog_addScene_ok);
    }

    @Override
    protected void onResume() {
        MyApplication.setOnGetWareDataListener(new MyApplication.OnGetWareDataListener() {
            @Override
            public void upDataWareData(int datType, int subtype1, int subtype2) {
                if (MyApplication.mApplication.isSeekNet() && datType == 0) {
                    MyApplication.mApplication.dismissLoadDialog();
                    initsousuoList();
                }
                if (datType == 1 && subtype1 == 1) {
                    MyApplication.mApplication.dismissLoadDialog();
                    if (subtype2 == 1) {
                        ToastUtil.showText("修改成功");
                        initView();
                    } else ToastUtil.showText("修改失败");
                }

            }
        });
        initView();
        initData();
        super.onResume();
    }


    /**
     * 初始化搜索联网模快列表
     */
    private void initsousuoList() {
        List<SearchNet> rcuInfo_SeekNet = MyApplication.getWareData().getSeekNets();
        List<RcuInfo> SeekListData = new ArrayList<>();
        for (int i = 0; i < rcuInfo_SeekNet.size(); i++) {
            RcuInfo info = new RcuInfo();
            info.setName(CommonUtils.getGBstr(CommonUtils.hexStringToBytes(rcuInfo_SeekNet.get(i).getRcu_rows().get(0).getName())));
            info.setCenterServ(rcuInfo_SeekNet.get(i).getRcu_rows().get(0).getCenterServ());
            info.setbDhcp(rcuInfo_SeekNet.get(i).getRcu_rows().get(0).getBDhcp());
            info.setDevUnitID(rcuInfo_SeekNet.get(i).getRcu_rows().get(0).getCanCpuID());
            info.setDevUnitPass(rcuInfo_SeekNet.get(i).getRcu_rows().get(0).getPass());
            info.setGateWay(rcuInfo_SeekNet.get(i).getRcu_rows().get(0).getGateway());
            info.setHwVversion(rcuInfo_SeekNet.get(i).getRcu_rows().get(0).getHwVersion());
            info.setSubMask(rcuInfo_SeekNet.get(i).getRcu_rows().get(0).getSubMask());
            info.setIpAddr(rcuInfo_SeekNet.get(i).getRcu_rows().get(0).getIpAddr());
            info.setMacAddr(rcuInfo_SeekNet.get(i).getRcu_rows().get(0).getMacAddr());
            info.setRoomNum(rcuInfo_SeekNet.get(i).getRcu_rows().get(0).getRoomNum());
            info.setSoftVersion(rcuInfo_SeekNet.get(i).getRcu_rows().get(0).getSoftVersion());
            SeekListData.add(info);
        }
        MyApplication.mApplication.setSeekRcuInfos(SeekListData);
        SeekNetClick(SeekListData);
    }

    /**
     * 搜索的联网模块在使用前需要输入密码确认
     *
     * @param SeekListData
     */
    private void showPassDialog(final List<RcuInfo> SeekListData, final int position) {
        final Dialog dialog = new Dialog(NewWorkSetActivity.this,android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);
        dialog.setContentView(R.layout.dialog_addscene);
        dialog.show();
        mDialogAddSceneName = (EditText) dialog.findViewById(R.id.dialog_addScene_name);
        mDialogAddSceneCancle = (TextView) dialog.findViewById(R.id.dialog_addScene_cancle);
        mDialogAddSceneOk = (TextView) dialog.findViewById(R.id.dialog_addScene_ok);
        mTitleName = (TextView) dialog.findViewById(R.id.title_name);
        mTitle = (TextView) dialog.findViewById(R.id.title);
        mTitle.setText("使用搜索");
        mDialogAddSceneName.setHint("请输入当前模块密码");
        mTitleName.setText("模块密码 :");
        mDialogAddSceneOk.setText("确定");
        mDialogAddSceneCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        mDialogAddSceneOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                String pass = mDialogAddSceneName.getText().toString();
                if (!pass.equals(SeekListData.get(position).getDevUnitPass())) {
                    ToastUtil.showText("密码不合适，请重新输入");
                } else {
                    ClickUseNet(position);
                }
            }
        });
    }

    /**
     * 搜索列表item点击事件
     *
     * @param seekListData 搜索数据
     */
    private void SeekNetClick(final List<RcuInfo> seekListData) {
        mSeekAdapter = new NetWork_Adapter(this, seekListData, NetWork_Adapter.SEEK);
        sousuo_list.setAdapter(mSeekAdapter);
        sousuo_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                MyApplication.mApplication.setSeekNet(false);
                if (!MyApplication.mApplication.isCanChangeNet()) {
                    ToastUtil.showText("正在加载数据，请等待几秒...");
                    return;
                }
                if (GlobalVars.getDevid().equals(MyApplication.mApplication.getSeekRcuInfos().get(position).getDevUnitID()))
                    ToastUtil.showText("联网模块正在使用中！");
                else {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(NewWorkSetActivity.this,
                            android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
                    dialog.setTitle("提示 :");
                    dialog.setMessage("您是否要使用此联网模块？");
                    dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    dialog.setPositiveButton("是的", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            showPassDialog(seekListData, position);
                        }
                    });
                    dialog.create().show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 5) {
            refNetLists();
        }
    }

    /**
     * 准备使用此联网模块==跳转页面前的准备
     *
     * @param position
     */
    private void ClickUseNet(final int position) {
        MyApplication.mApplication.showLoadDialog(NewWorkSetActivity.this, false);
        AppSharePreferenceMgr.put(GlobalVars.RCUINFOID_SHAREPREFERENCE,
                MyApplication.mApplication.getSeekRcuInfos().get(position).getDevUnitID());
        initListview();
        if (!MyApplication.mApplication.isVisitor()) {
            List<RcuInfo> list = MyApplication.mApplication.getRcuInfoList();
            boolean isExist = false;
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getDevUnitID().equals(MyApplication.mApplication.getSeekRcuInfos().get(position).getDevUnitID())) {
                    isExist = true;
                }
            }
            if (!isExist) {
                Net_AddorDel_Helper.addNew(mNewModuleHandler, NewWorkSetActivity.this
                        , MyApplication.mApplication.getSeekRcuInfos().get(position).getName()
                        , MyApplication.mApplication.getSeekRcuInfos().get(position).getDevUnitID()
                        , "");
                Log.i(TAG, "upDataWareData 搜索 使用  添加到服务器");
            }
        }
        MyApplication.setNewWareData();
        GlobalVars.setIsLAN(true);
        MyApplication.setOnGetWareDataListener(new MyApplication.OnGetWareDataListener() {
            @Override
            public void upDataWareData(int datType, int subtype1, int subtype2) {
                if (datType == 0 || datType == 3 || datType == 8) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(2000);
                                MyApplication.mApplication.dismissLoadDialog();
                                startActivity(new Intent(NewWorkSetActivity.this, HomeActivity.class));
                                finish();
                            } catch (InterruptedException e) {
                                MyApplication.mApplication.dismissLoadDialog();
                                startActivity(new Intent(NewWorkSetActivity.this, HomeActivity.class));
                                finish();
                            }
                        }
                    }).start();
                }
            }
        });
        SendDataUtil.getNetWorkInfo();
    }

    /**
     * 初始化账号下的联网模快列表
     */
    private void initListview() {
        adapter = new NetWork_Adapter(this, MyApplication.mApplication.getRcuInfoList(), NetWork_Adapter.LOGIN);
        mNetListView.setAdapter(adapter);
    }

    /**
     * 初始化页面数据
     */
    public void initData() {
        initListview();
        mNetworkAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyApplication.mApplication.isVisitor()) {
                    ToastUtil.showText("游客登录不能进行此操作");
                    return;
                }
                Dialog dialog = new Dialog(NewWorkSetActivity.this,android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);
                dialog.setContentView(R.layout.dialog_network);
                dialog.show();
                initAddNetModuleDialog(dialog);
            }
        });
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mNetListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                MyApplication.mApplication.setSeekNet(false);
                if (!MyApplication.mApplication.isCanChangeNet()) {
                    ToastUtil.showText("正在加载数据，请等待几秒...");
                    return;
                }
                if (GlobalVars.getDevid().equals(MyApplication.mApplication.getRcuInfoList().get(position).getDevUnitID()))
                    ToastUtil.showText("联网模块正在使用中！");
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(NewWorkSetActivity.this,
                            android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
                    builder.setTitle("提示 :");
                    builder.setMessage("您是否要使用此联网模块？");
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.setPositiveButton("是的", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            MyApplication.mApplication.showLoadDialog(NewWorkSetActivity.this, false);
                            AppSharePreferenceMgr.put(GlobalVars.RCUINFOID_SHAREPREFERENCE,
                                    MyApplication.mApplication.getRcuInfoList().get(position).getDevUnitID());
//                            WareData wareData = (WareData) Data_Cache.readFile(MyApplication.mApplication.getRcuInfoList().get(position).getDevUnitID());
//                            if (wareData == null) {
                            initListview();
                            MyApplication.setNewWareData();
                            GlobalVars.setIsLAN(true);
                            MyApplication.setOnGetWareDataListener(new MyApplication.OnGetWareDataListener() {
                                @Override
                                public void upDataWareData(int datType, int subtype1, int subtype2) {
                                    if (datType == 0) {
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    Thread.sleep(2000);
                                                    MyApplication.mApplication.dismissLoadDialog();
                                                    startActivity(new Intent(NewWorkSetActivity.this, HomeActivity.class));
                                                    finish();
                                                } catch (InterruptedException e) {
                                                    MyApplication.mApplication.dismissLoadDialog();
                                                    startActivity(new Intent(NewWorkSetActivity.this, HomeActivity.class));
                                                    finish();
                                                }
                                            }
                                        }).start();
                                    }
                                }
                            });
                            SendDataUtil.getNetWorkInfo();
//                            } else {
//                                SendDataUtil.getNetWorkInfo();
//                                MyApplication.mApplication.dismissLoadDialog();
//                                MyApplication.mWareData = wareData;
//                                startActivity(new Intent(NewWorkSetActivity.this, HomeActivity.class));
//                                finish();
//                            }
                        }
                    });
                    builder.create().show();
                }
            }
        });
        mNetListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                AlertDialog.Builder builder = new AlertDialog.Builder(NewWorkSetActivity.this,
                        android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
                builder.setTitle("删除");
                builder.setMessage("您是否要删除联网模块？");
                builder.setNegativeButton("不要", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton("是的", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (GlobalVars.getDevid().equals(
                                MyApplication.mApplication.getRcuInfoList().get(position).getDevUnitID())) {
                            if (MyApplication.mApplication.getRcuInfoList().size() > 0) {
                                AppSharePreferenceMgr.put(GlobalVars.RCUINFOID_SHAREPREFERENCE,
                                        MyApplication.mApplication.getRcuInfoList().get(0).getDevUnitID());
                                MyApplication.setNewWareData();
                                GlobalVars.setIsLAN(true);
                                SendDataUtil.getNetWorkInfo();
                            } else {
                                MyApplication.setNewWareData();
                            }
                        }
                        mDeleteNet_Position = position;
                        Net_AddorDel_Helper.deleteNew(mNewModuleHandler,
                                NewWorkSetActivity.this,
                                MyApplication.mApplication.getRcuInfoList().get(position).getDevUnitID());
                    }
                });
                builder.create().show();
                return true;
            }
        });

        network_ref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(NewWorkSetActivity.this,
                        android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
                builder.setTitle("提示");
                builder.setMessage("您确定要刷新联网模块列表？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        refNetLists();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.create().show();
            }
        });

        network_sousuo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApplication.mApplication.getSeekRcuInfos().clear();
                MyApplication.mApplication.getUdpServer().sendSeekNet(true);
                MyApplication.mApplication.showLoadDialog(NewWorkSetActivity.this);
            }
        });
    }

    /**
     * 刷新账号下的联网模快列表
     */
    private void refNetLists() {
        MyApplication.mApplication.showLoadDialog(NewWorkSetActivity.this);
        Map<String, String> param = new HashMap<>();
        param.put("userName", GlobalVars.getUserid());
        param.put("passwd", (String) AppSharePreferenceMgr.
                get(GlobalVars.USERPASSWORD_SHAREPREFERENCE, ""));
        OkHttpUtils.postAsyn(NewHttpPort.ROOT + NewHttpPort.LOCATION + NewHttpPort.NETLISTS, param, new HttpCallback() {
            @Override
            public void onSuccess(ResultDesc resultDesc) {
                super.onSuccess(resultDesc);
                MyApplication.mApplication.dismissLoadDialog();
                Log.i(TAG, "onSuccess: " + resultDesc.getResult());
                gson = new Gson();
                Http_Result result = gson.fromJson(resultDesc.getResult(), Http_Result.class);

                if (result.getCode() == HTTPRequest_BackCode.LOGIN_OK) {
                    // 刷新成功
                    setRcuInfoList(result);
                    ToastUtil.showText("操作成功");
                    initListview();
                } else {
                    // 刷新失败
                    ToastUtil.showText("操作失败，请稍后再试");
                }
            }

            @Override
            public void onFailure(int code, String message) {
                super.onFailure(code, message);
                Log.i(TAG, "刷新失败: " + code + "****" + message);
                //登陆失败
                MyApplication.mApplication.dismissLoadDialog();
                ToastUtil.showText("刷新失败，网络不可用或服务器异常");
            }
        });
    }


    /**
     * 刷新成功后改变列表数据
     *
     * @param result
     */
    public void setRcuInfoList(Http_Result result) {
        if (result == null)
            return;

        List<RcuInfo> rcuInfos = MyApplication.mApplication.getRcuInfoList();
        if (rcuInfos.size() <= result.getData().size()) {
            for (int i = 0; i < result.getData().size(); i++) {
                boolean isExist = false;
                for (int j = 0; j < rcuInfos.size(); j++) {
                    if (rcuInfos.get(j).getDevUnitID().equals(result.getData().get(i).getDevUnitID())) {
                        isExist = true;
                        rcuInfos.get(j).setCanCpuName(result.getData().get(i).getCanCpuName());
                        rcuInfos.get(j).setDevUnitID(result.getData().get(i).getDevUnitID());
                        rcuInfos.get(j).setOnline(result.getData().get(i).isOnline());
                    }
                }
                if (!isExist) {
                    RcuInfo info = new RcuInfo();
                    info.setCanCpuName(result.getData().get(i).getCanCpuName());
                    info.setDevUnitID(result.getData().get(i).getDevUnitID());
                    info.setOnline(result.getData().get(i).isOnline());
                    rcuInfos.add(info);
                }
            }
        } else {
            for (int j = 0; j < rcuInfos.size(); j++) {
                boolean isExist = false;
                for (int i = 0; i < result.getData().size(); i++) {
                    if (rcuInfos.get(j).getDevUnitID().equals(result.getData().get(i).getDevUnitID())) {
                        isExist = true;
                        rcuInfos.get(j).setCanCpuName(result.getData().get(i).getCanCpuName());
                        rcuInfos.get(j).setDevUnitID(result.getData().get(i).getDevUnitID());
                        rcuInfos.get(j).setOnline(result.getData().get(i).isOnline());
                    }
                }
                if (!isExist) {
                    rcuInfos.remove(j);
                }
            }
        }
        if (rcuInfos.size() == 0) {
            ToastUtil.showText("对不起。没有可用联网模块");
            AppSharePreferenceMgr.put(GlobalVars.RCUINFOID_SHAREPREFERENCE, "");
            MyApplication.setNewWareData();
        }
        AppSharePreferenceMgr.put(GlobalVars.RCUINFOLIST_SHAREPREFERENCE, gson.toJson(rcuInfos));
        initListview();
    }

    /**
     * 添加联网模快Dialog
     *
     * @param dialog
     */
    private void initAddNetModuleDialog(final Dialog dialog) {
        mDialogName = (EditText) dialog.findViewById(R.id.network_et_name);
        mDialogID = (EditText) dialog.findViewById(R.id.network_et_id);
        mDialogPass = (EditText) dialog.findViewById(R.id.network_et_pwd);
        mDialogCancle = (TextView) dialog.findViewById(R.id.network_btn_cancel);
        mDialogOk = (TextView) dialog.findViewById(R.id.network_btn_sure);
        mDialogCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        mDialogOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Net_AddorDel_Helper.addNew(mNewModuleHandler,
                        NewWorkSetActivity.this, mDialogName.getText().toString(),
                        mDialogID.getText().toString(), mDialogPass.getText().toString());
            }
        });
    }

    public static class NewModuleHandler extends Handler {

        WeakReference<NewWorkSetActivity> weakReference;

        public NewModuleHandler(NewWorkSetActivity fragment) {
            weakReference = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (weakReference != null) {
                if (msg.what == Net_AddorDel_Helper.ADDNEWMODULE_OK) {
                    // 添加成功
                    weakReference.get().refNetLists();
                } else if (msg.what == Net_AddorDel_Helper.DELNEWMODULE_OK) {
                    weakReference.get().refNetLists();
                    //删除成功
                } else if (msg.what == Net_AddorDel_Helper.EDITNEWMODULE_OK) {
                    //修改成功
                    weakReference.get().refNetLists();
                }
            }
        }
    }
}

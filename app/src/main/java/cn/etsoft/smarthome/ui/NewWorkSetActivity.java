package cn.etsoft.smarthome.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.TextView;

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
import cn.etsoft.smarthome.utils.AppSharePreferenceMgr;
import cn.etsoft.smarthome.utils.HttpGetDataUtils.HTTPRequest_BackCode;
import cn.etsoft.smarthome.utils.HttpGetDataUtils.HttpCallback;
import cn.etsoft.smarthome.utils.HttpGetDataUtils.NewHttpPort;
import cn.etsoft.smarthome.utils.HttpGetDataUtils.OkHttpUtils;
import cn.etsoft.smarthome.utils.HttpGetDataUtils.ResultDesc;
import cn.etsoft.smarthome.utils.SendDataUtil;
import cn.etsoft.smarthome.utils.ToastUtil;
import cn.etsoft.smarthome.weidget.SwipeListView;

import static android.content.ContentValues.TAG;

/**
 * Author：FBL  Time： 2017/7/10.
 * 联网模块设置界面
 */

public class NewWorkSetActivity extends Activity {
    private TextView mDialogCancle, mDialogOk,mTitle;
    private ImageView mSousuo, mBack,network_ref;
    private EditText mDialogName, mDialogID, mDialogPass;
    private NewModuleHandler mNewModuleHandler = new NewModuleHandler(this);
    private Gson gson = new Gson();
    private int mDeleteNet_Position = -1;
    private TextView mNetworkAdd;
    private SwipeListView mNetListView;
    private NetWork_Adapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network);
        initView();
        initData();
    }

    private void initView() {
        mBack = (ImageView) findViewById(R.id.title_bar_iv_back);
        mSousuo = (ImageView) findViewById(R.id.title_bar_iv_or);
        network_ref = (ImageView) findViewById(R.id.network_ref);
        mTitle = (TextView) findViewById(R.id.title_bar_tv_title);
        mTitle.setVisibility(View.VISIBLE);
        mSousuo.setImageResource(R.drawable.net_search);
        mSousuo.setVisibility(View.VISIBLE);
        mNetworkAdd = (TextView) findViewById(R.id.network_add);
        mNetListView = (SwipeListView) findViewById(R.id.equi_list);
        mTitle.setText("模块设置");
    }

    private void initListview() {
        if (adapter == null)
            adapter = new NetWork_Adapter(this);
        else adapter.notifyDataSetChanged();
        mNetListView.setAdapter(adapter);

    }
    public void initData() {
        initListview();
        mNetworkAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyApplication.mApplication.isVisitor()){
                    ToastUtil.showText("游客登录不能进行此操作");
                    return;
                }
                Dialog dialog = new Dialog(NewWorkSetActivity.this, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);
                dialog.setContentView(R.layout.dialog_network);
                dialog.setTitle("添加联网模块");
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
                if (GlobalVars.getDevid().equals(MyApplication.mApplication.getRcuInfoList().get(position).getDevUnitID()))
                    ToastUtil.showText("联网模块正在使用中！");
                else {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(NewWorkSetActivity.this);
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
                            AppSharePreferenceMgr.put(GlobalVars.RCUINFOID_SHAREPREFERENCE,
                                    MyApplication.mApplication.getRcuInfoList().get(position).getDevUnitID());
                            MyApplication.setNewWareData();
                            SendDataUtil.getNetWorkInfo();
                            adapter.notifyDataSetChanged();
                            dialog.dismiss();
                            startActivity(new Intent(NewWorkSetActivity.this, HomeActivity.class));
                            finish();
                        }
                    });
                    dialog.create().show();
                }
            }
        });
        mNetListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                AlertDialog.Builder builder = new AlertDialog.Builder(NewWorkSetActivity.this);
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
                        mDeleteNet_Position = position;
                        MyApplication.mApplication.showLoadDialog(NewWorkSetActivity.this);
                        Net_AddorDel_Helper.deleteNew(mNewModuleHandler,
                                NewWorkSetActivity.this,
                                MyApplication.mApplication.getRcuInfoList().get(position).getDevUnitID());
                    }
                });
                builder.create().show();
                return true;
            }
        });


        mSousuo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(NewWorkSetActivity.this,
                        SeekActivity.class), 0);
            }
        });


        network_ref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(NewWorkSetActivity.this);
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
    }


    private void refNetLists() {
        MyApplication.mApplication.showLoadDialog(NewWorkSetActivity.this);
        Map<String, String> param = new HashMap<>();
        param.put("userName", GlobalVars.getUserid());
        param.put("passwd", (String) AppSharePreferenceMgr.
                get(GlobalVars.USERPASSWORD_SHAREPREFERENCE, ""));
        OkHttpUtils.postAsyn(NewHttpPort.ROOT + NewHttpPort.LOCATION + NewHttpPort.LOGIN, param, new HttpCallback() {
            @Override
            public void onSuccess(ResultDesc resultDesc) {
                Log.i("LOGIN", resultDesc.getResult());
                MyApplication.mApplication.dismissLoadDialog();
                super.onSuccess(resultDesc);
                Log.i(TAG, "onSuccess: " + resultDesc.getResult());
                gson = new Gson();
                Http_Result result = gson.fromJson(resultDesc.getResult(), Http_Result.class);

                if (result.getCode() == HTTPRequest_BackCode.LOGIN_OK) {
                    // 刷新成功
                    setRcuInfoList(result);
                    ToastUtil.showText("刷新成功");
                    initListview();
                } else {
                    // 刷新失败
                    ToastUtil.showText("刷新失败，请稍后再试");
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


    public void setRcuInfoList(Http_Result result) {
        if (result == null)
            return;

        List<RcuInfo> rcuInfos = new ArrayList<>();
        for (int i = 0; i < result.getData().size(); i++) {
            RcuInfo rcuInfo = new RcuInfo();
            rcuInfo.setCanCpuName(result.getData().get(i).getCanCpuName());
            rcuInfo.setDevUnitID(result.getData().get(i).getDevUnitID());
            rcuInfo.setDevUnitPass(result.getData().get(i).getDevPass());
            rcuInfos.add(rcuInfo);
        }
        AppSharePreferenceMgr.put(GlobalVars.RCUINFOLIST_SHAREPREFERENCE, gson.toJson(rcuInfos));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            int position = data.getIntExtra("yes", -1);
            if (position != -1) {
                if (position == -5) {
                    adapter.notifyDataSetChanged();
                } else {
                    SearchNet net = MyApplication.getWareData().getSeekNets().get(position);
                    Log.i("SeekNet", "onActivityResult: " + net.getRcu_rows().get(0).getName() + "--"
                            + net.getRcu_rows().get(0).getCanCpuID() + "--" + net.getRcu_rows().get(0).getDevUnitPass());
                    Net_AddorDel_Helper.addNew(mNewModuleHandler, NewWorkSetActivity.this,
                            net.getRcu_rows().get(0).getName(), net.getRcu_rows().get(0).getCanCpuID(),
                            net.getRcu_rows().get(0).getDevUnitPass());
                }
            }
        }
    }

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

    @Override
    public void onBackPressed() {
        backEvent();
        super.onBackPressed();

    }

    private void backEvent() {
        if (!"".equals(AppSharePreferenceMgr.get(GlobalVars.RCUINFOID_SHAREPREFERENCE, ""))) {
            SendDataUtil.getNetWorkInfo();
            startActivity(new Intent(NewWorkSetActivity.this, HomeActivity.class));
        }
        finish();
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
                    RcuInfo info = new RcuInfo();
                    info.setDevUnitID(weakReference.get().mDialogID.getText().toString());
                    info.setCanCpuName(weakReference.get().mDialogName.getText().toString());
                    info.setDevUnitPass(weakReference.get().mDialogPass.getText().toString());

                    List<RcuInfo> json_rcuinfolist = MyApplication.mApplication.getRcuInfoList();
                    json_rcuinfolist.add(info);
                    AppSharePreferenceMgr.put(GlobalVars.RCUINFOLIST_SHAREPREFERENCE, weakReference.get().gson.toJson(json_rcuinfolist));
                    weakReference.get().initListview();
                    ToastUtil.showText("添加成功");
                } else if (msg.what == Net_AddorDel_Helper.DELNEWMODULE_OK) {
                    MyApplication.mApplication.dismissLoadDialog();
                    List<RcuInfo> list = MyApplication.mApplication.getRcuInfoList();
                    list.remove(weakReference.get().mDeleteNet_Position);
                    MyApplication.mApplication.setRcuInfoList(list);
                    weakReference.get().initListview();
                    ToastUtil.showText("删除成功");
                    //删除成功
                } else if (msg.what == Net_AddorDel_Helper.EDITNEWMODULE_OK) {
                    //修改成功
                    MyApplication.mApplication.dismissLoadDialog();
                }
            }
        }
    }
}

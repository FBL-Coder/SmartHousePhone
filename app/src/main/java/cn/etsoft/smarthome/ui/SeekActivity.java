package cn.etsoft.smarthome.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import cn.etsoft.smarthome.MyApplication;
import cn.etsoft.smarthome.NetMessage.GlobalVars;
import cn.etsoft.smarthome.R;
import cn.etsoft.smarthome.adapter.SeekListAdapter;
import cn.etsoft.smarthome.domain.RcuInfo;
import cn.etsoft.smarthome.utils.AppSharePreferenceMgr;
import cn.etsoft.smarthome.utils.SendDataUtil;
import cn.etsoft.smarthome.utils.ToastUtil;

/**
 * Author：FBL  Time： 2017/7/24.
 * 搜索联网模块
 */

public class SeekActivity extends Activity {

    private ListView mSeekNetDataList;
    private TextView mSeekNetNullData;
    private TextView mSeekNetBack;
    private SeekListAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seekmode);
        initView();
        initData();
        initList();
    }

    public void initView() {
        mSeekNetBack = (TextView) findViewById(R.id.SeekNet_Back);
        mSeekNetDataList = (ListView) findViewById(R.id.SeekNet_DataList);
        mSeekNetNullData = (TextView) findViewById(R.id.SeekNet_NullData);
        mSeekNetDataList.setEmptyView(mSeekNetNullData);
    }

    public void initData() {
        MyApplication.setOnGetWareDataListener(new MyApplication.OnGetWareDataListener() {
            @Override
            public void upDataWareData(int datType, int subtype1, int subtype2) {
                if (MyApplication.mApplication.isSeekNet() && datType == 0) {
                    MyApplication.mApplication.setSeekNet(false);
                    MyApplication.mApplication.dismissLoadDialog();
                    initList();
                }
            }
        });
        SendDataUtil.SeekNet();
        MyApplication.mApplication.showLoadDialog(this);
        mSeekNetBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(1, getIntent().putExtra("yes", -1));
                finish();
            }
        });

        mSeekNetDataList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(SeekActivity.this);
                builder.setTitle("提示");
                builder.setMessage("是否添加此联网模块？");
                builder.setPositiveButton("是的", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if ("".equals(AppSharePreferenceMgr.get(GlobalVars.USERID_SHAREPREFERENCE, ""))) {
                            GlobalVars.setDevid(MyApplication.getWareData().getSeekNets().get(position).getDevUnitID());
                            RcuInfo info = new RcuInfo();
                            info.setDevUnitID(MyApplication.getWareData().getSeekNets().get(position).getDevUnitID());
                            info.setCanCpuName(MyApplication.getWareData().getSeekNets().get(position).getRcu_rows().get(0).getName());
                            info.setDevUnitPass(MyApplication.getWareData().getSeekNets().get(position).getRcu_rows().get(0).getDevUnitPass());
                            info.setName(MyApplication.getWareData().getSeekNets().get(position).getRcu_rows().get(0).getName());
                            info.setCenterServ(MyApplication.getWareData().getSeekNets().get(position).getRcu_rows().get(0).getCenterServ());
                            info.setbDhcp(MyApplication.getWareData().getSeekNets().get(position).getRcu_rows().get(0).getBDhcp());
                            info.setGateWay(MyApplication.getWareData().getSeekNets().get(position).getRcu_rows().get(0).getGateway());
                            info.setHwVversion(MyApplication.getWareData().getSeekNets().get(position).getRcu_rows().get(0).getHwVersion());
                            info.setIpAddr(MyApplication.getWareData().getSeekNets().get(position).getRcu_rows().get(0).getIpAddr());
                            info.setMacAddr(MyApplication.getWareData().getSeekNets().get(position).getRcu_rows().get(0).getMacAddr());
                            info.setRoomNum(MyApplication.getWareData().getSeekNets().get(position).getRcu_rows().get(0).getRoomNum());
                            info.setSoftVersion(MyApplication.getWareData().getSeekNets().get(position).getRcu_rows().get(0).getSoftVersion());
                            info.setSubMask(MyApplication.getWareData().getSeekNets().get(position).getRcu_rows().get(0).getSubMask());

                            for (int i = 0; i < MyApplication.getWareData().getRcuInfos().size(); i++) {
                                if (MyApplication.getWareData().getRcuInfos().get(i).getDevUnitID().equals(info.getDevUnitID())){
                                    ToastUtil.showText("这个联网模块已存在");
                                    return;
                                }
                            }
                            MyApplication.getWareData().getRcuInfos().add(info);
                            MyApplication.mApplication.setRcuInfoList(MyApplication.getWareData().getRcuInfos());
                            setResult(1, getIntent().putExtra("yes", -5));
                        } else {
                            setResult(1, getIntent().putExtra("yes", position));
                        }
                        finish();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("算了", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        });
    }

    private void initList() {
        if (adapter == null) {
            adapter = new SeekListAdapter(this);
            mSeekNetDataList.setAdapter(adapter);
        } else adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        setResult(1, getIntent().putExtra("yes", -1));
        super.onBackPressed();
    }
}

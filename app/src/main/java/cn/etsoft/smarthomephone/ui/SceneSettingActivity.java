package cn.etsoft.smarthomephone.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import cn.etsoft.smarthomephone.MyApplication;
import cn.etsoft.smarthomephone.R;
import cn.etsoft.smarthomephone.UiUtils.ToastUtil;
import cn.etsoft.smarthomephone.adapter.ParlourGridViewAdapter;
import cn.etsoft.smarthomephone.adapter.Room_Select_Adapter;
import cn.etsoft.smarthomephone.pullmi.app.GlobalVars;
import cn.etsoft.smarthomephone.pullmi.common.CommonUtils;
import cn.etsoft.smarthomephone.pullmi.entity.WareAirCondDev;
import cn.etsoft.smarthomephone.pullmi.entity.WareCurtain;
import cn.etsoft.smarthomephone.pullmi.entity.WareData;
import cn.etsoft.smarthomephone.pullmi.entity.WareDev;
import cn.etsoft.smarthomephone.pullmi.entity.WareLight;
import cn.etsoft.smarthomephone.pullmi.entity.WareSceneEvent;
import cn.etsoft.smarthomephone.pullmi.entity.WareSetBox;
import cn.etsoft.smarthomephone.pullmi.entity.WareTv;
import cn.etsoft.smarthomephone.pullmi.utils.Dtat_Cache;
import cn.etsoft.smarthomephone.view.Circle_Progress;
import cn.etsoft.smarthomephone.weidget.CustomDialog;
import cn.etsoft.smarthomephone.weidget.CustomDialog_comment;

/**
 * Created by Say GoBay on 2016/9/2.
 * 情景设置页面二
 */
public class SceneSettingActivity extends Activity implements View.OnClickListener {
    private TextView title, title_bar_tv_room;
    private ImageView back, title_bar_iv_or;
    private List<WareDev> mWareDev;
    private List<WareDev> listViewItems;
    private byte eventId;
    private boolean IsCanClick = false;
    private int position_room = -1;
    private List<String> room_list;
    private GridView gridView;
    private ParlourGridViewAdapter parlourGridViewAdapter;
    private Handler mHandler;
    private Dialog mDialog;
    private byte sceneid = 0;


    //自定义加载进度条
    private void initDialog(String str) {
        Circle_Progress.setText(str);
        mDialog = Circle_Progress.createLoadingDialog(this);
        //允许返回
        mDialog.setCancelable(true);
        //显示
        mDialog.show();
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                mDialog.dismiss();
            }
        };
        //加载数据进度条，5秒数据没加载出来自动消失
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                    if (mDialog.isShowing()) {
                        handler.sendMessage(handler.obtainMessage());
                    }
                } catch (Exception e) {
                    System.out.println(e + "");
                }
            }
        }).start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sceneset_listview);
        ReadWrite();
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                //初始化标题栏
                initTitleBar();
                upData();

                super.handleMessage(msg);
            }
        };


        MyApplication.mInstance.setOnGetWareDataListener(new MyApplication.OnGetWareDataListener() {
            @Override
            public void upDataWareData(int what) {
                if (mDialog != null)
                    mDialog.dismiss();
                if (what == 24) {
                    ToastUtil.showToast(SceneSettingActivity.this, "保存成功");
                    finish();
                }
            }
        });
    }

    private void ReadWrite() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Dtat_Cache.writeFile(GlobalVars.getDevid(), MyApplication.getWareData());
                MyApplication.setWareData_Scene((WareData) Dtat_Cache.readFile(GlobalVars.getDevid()));
                mHandler.sendMessage(mHandler.obtainMessage());
            }
        }).start();
    }

    /**
     * 初始化标题栏
     */
    private void initTitleBar() {
        title = (TextView) findViewById(R.id.title_bar_tv_title);
        back = (ImageView) findViewById(R.id.title_bar_iv_back);
        title_bar_tv_room = (TextView) findViewById(R.id.title_bar_tv_room);
        title_bar_tv_room.setVisibility(View.VISIBLE);
        title_bar_tv_room.setTextColor(0xff000000);
        title_bar_iv_or = (ImageView) findViewById(R.id.title_bar_iv_or);
        title_bar_iv_or.setVisibility(View.VISIBLE);
        title_bar_iv_or.setImageResource(R.drawable.fj);
        title.setText(getIntent().getBundleExtra("bundle").getString("sceneName"));
        back.setOnClickListener(this);
        eventId = getIntent().getBundleExtra("bundle").getByte("eventId");
        title_bar_iv_or.setOnClickListener(this);
    }

    /**
     * 初始化GridView
     */
    private void initGridView(List<WareDev> listViewItems) {
        gridView = (GridView) findViewById(R.id.parlour_gv);
        if (parlourGridViewAdapter != null) {
            parlourGridViewAdapter.notifyDataSetChanged(listViewItems);
        } else {
            parlourGridViewAdapter = new ParlourGridViewAdapter(this, listViewItems, eventId);
            gridView.setAdapter(parlourGridViewAdapter);
        }
    }

    /**
     * 初始化自定义dialog
     */
    CustomDialog dialog;

    public void getRoomDialog() {
        ListView dia_listview;
        dialog = new CustomDialog(this, R.style.customDialog_null, R.layout.air_select_item);
        //获得当前窗体
        Window window = dialog.getWindow();
        //重新设置
        WindowManager.LayoutParams lp = window.getAttributes();
        window.setGravity(Gravity.RIGHT | Gravity.TOP);
        lp.x = 0; // 新位置X坐标
        lp.y = 65; // 新位置Y坐标
        lp.width = 300; // 宽度
        lp.height = 300; // 高度
        // dialog.onWindowAttributesChanged(lp);
        //(当Window的Attributes改变时系统会调用此函数)
        window.setAttributes(lp);
        dialog.show();

        TextView textView = (TextView) dialog.findViewById(R.id.select_room);
        textView.setText("请选择房间");
        dia_listview = (ListView) dialog.findViewById(R.id.air_select);
        dia_listview.setAdapter(new Room_Select_Adapter(SceneSettingActivity.this, room_list));

        dia_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialog.dismiss();
                position_room = position;
                upData();
            }
        });
    }

    private void upData() {
        if (MyApplication.getRoom_list().size() == 0 || MyApplication.getWareData_Scene().getDevs() == null ||
                MyApplication.getWareData_Scene().getDevs().size() == 0)
            return;
        IsCanClick = true;
        listViewItems = MyApplication.getWareData_Scene().getDevs();
        mWareDev = new ArrayList<>();
        //房间集合
        room_list = MyApplication.getRoom_list();
        //房间名称；
        if (position_room != -1)
            title_bar_tv_room.setText(MyApplication.getRoom_list().get(position_room));
        else
            title_bar_tv_room.setText(MyApplication.getRoom_list().get(getIntent().getIntExtra("viewpage_num", 0)));
        //根据房间id获取设备；
        for (int i = 0; i < listViewItems.size(); i++) {
            if (listViewItems.get(i).getRoomName().equals(title_bar_tv_room.getText())) {
                mWareDev.add(listViewItems.get(i));
            }
        }
        //初始化GridView
        initGridView(mWareDev);
    }

    public void save() {
        CustomDialog_comment.Builder builder = new CustomDialog_comment.Builder(SceneSettingActivity.this);
        builder.setTitle("提示");
        builder.setMessage("您要保存这些设置吗？");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
         builder.setPositiveButton("保存", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                initDialog("正在保存...");
                WareSceneEvent Sceneevent = null;
                for (int i = 0; i < MyApplication.getWareData().getSceneEvents().size(); i++) {
                    if (sceneid == MyApplication.getWareData().getSceneEvents().get(i).getEventld()) {
                        Sceneevent = MyApplication.getWareData().getSceneEvents().get(i);
                        break;
                    }
                }
                int num = 0;
                String div;
                String more_data = "";
                String data_str = "";
                for (int i = 0; i < MyApplication.getWareData_Scene().getDevs().size(); i++) {//循环所有设备
                    WareDev dev = MyApplication.getWareData_Scene().getDevs().get(i);//拿到其中一个设备
                    div = ",";
                    if (dev.getType() == 0) {
                        for (int j = 0; j < MyApplication.getWareData_Scene().getAirConds().size(); j++) {
                            WareAirCondDev AirCondDev = MyApplication.getWareData_Scene().getAirConds().get(j);
                            if (dev.getCanCpuId().equals(AirCondDev.getDev().getCanCpuId()) && dev.getDevId() == AirCondDev.getDev().getDevId()) {
                                if (AirCondDev.getbOnOff() == 1) {
                                    data_str = "{" +
                                            "\"uid\":\"" + dev.getCanCpuId() + "\"," +
                                            "\"devType\":" + dev.getType() + "," +
                                            "\"devID\":" + dev.getDevId() + "," +
                                            "\"bOnOff\":" + AirCondDev.getbOnOff() + "," +
                                            "\"lmVal\":0," +
                                            "\"rev2\":0," +
                                            "\"rev3\":0," +
                                            "\"param1\":0," +
                                            "\"param2\":0}" + div;
                                    num++;
                                }
                            }
                        }
                    } else if (dev.getType() == 1) {
                        for (int j = 0; j < MyApplication.getWareData_Scene().getTvs().size(); j++) {
                            WareTv tv = MyApplication.getWareData_Scene().getTvs().get(j);
                            if (dev.getCanCpuId().equals(tv.getDev().getCanCpuId()) && dev.getDevId() == tv.getDev().getDevId()) {
                                if (tv.getbOnOff() == 1) {
                                    data_str = "{" +
                                            "\"uid\":\"" + dev.getCanCpuId() + "\"," +
                                            "\"devType\":" + dev.getType() + "," +
                                            "\"devID\":" + dev.getDevId() + "," +
                                            "\"bOnOff\":" + tv.getbOnOff() + "," +
                                            "\"lmVal\":0," +
                                            "\"rev2\":0," +
                                            "\"rev3\":0," +
                                            "\"param1\":0," +
                                            "\"param2\":0}" + div;
                                    num++;

                                }
                            }
                        }
                    } else if (dev.getType() == 2) {
                        for (int j = 0; j < MyApplication.getWareData_Scene().getStbs().size(); j++) {
                            WareSetBox box = MyApplication.getWareData_Scene().getStbs().get(j);
                            if (dev.getCanCpuId().equals(box.getDev().getCanCpuId()) && dev.getDevId() == box.getDev().getDevId()) {
                                if (box.getbOnOff() == 1) {
                                    data_str = "{" +
                                            "\"uid\":\"" + dev.getCanCpuId() + "\"," +
                                            "\"devType\":" + dev.getType() + "," +
                                            "\"devID\":" + dev.getDevId() + "," +
                                            "\"bOnOff\":" + box.getbOnOff() + "," +
                                            "\"lmVal\":0," +
                                            "\"rev2\":0," +
                                            "\"rev3\":0," +
                                            "\"param1\":0," +
                                            "\"param2\":0}" + div;
                                    num++;
                                }
                            }
                        }
                    } else if (dev.getType() == 3) {
                        for (int j = 0; j < MyApplication.getWareData_Scene().getLights().size(); j++) {
                            WareLight Light = MyApplication.getWareData_Scene().getLights().get(j);
                            if (dev.getCanCpuId().equals(Light.getDev().getCanCpuId()) && dev.getDevId() == Light.getDev().getDevId()) {
                                if (Light.getbOnOff() == 1) {
                                    data_str = "{" +
                                            "\"uid\":\"" + dev.getCanCpuId() + "\"," +
                                            "\"devType\":" + dev.getType() + "," +
                                            "\"devID\":" + dev.getDevId() + "," +
                                            "\"bOnOff\":" + Light.getbOnOff() + "," +
                                            "\"lmVal\":0," +
                                            "\"rev2\":0," +
                                            "\"rev3\":0," +
                                            "\"param1\":0," +
                                            "\"param2\":0}" + div;
                                    num++;
                                }
                            }
                        }
                    } else if (dev.getType() == 4) {
                        for (int j = 0; j < MyApplication.getWareData_Scene().getCurtains().size(); j++) {
                            WareCurtain Curtain = MyApplication.getWareData_Scene().getCurtains().get(j);
                            if (dev.getCanCpuId().equals(Curtain.getDev().getCanCpuId()) && dev.getDevId() == Curtain.getDev().getDevId()) {
                                if (Curtain.getbOnOff() == 1) {
                                    data_str = "{" +
                                            "\"uid\":\"" + dev.getCanCpuId() + "\"," +
                                            "\"devType\":" + dev.getType() + "," +
                                            "\"devID\":" + dev.getDevId() + "," +
                                            "\"bOnOff\":" + Curtain.getbOnOff() + "," +
                                            "\"lmVal\":0," +
                                            "\"rev2\":0," +
                                            "\"rev3\":0," +
                                            "\"param1\":0," +
                                            "\"param2\":0}" + div;
                                    num++;
                                }
                            }
                        }
                    }
                    more_data += data_str;
                    data_str = "";
                }
                byte[] nameData = {0};
                try {
                    nameData = title.getText().toString().getBytes("GB2312");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                String str_gb = CommonUtils.bytesToHexString(nameData);
                Log.e("情景模式名称:%s", str_gb);
                try {
                    more_data = more_data.substring(0, more_data.lastIndexOf(","));
                } catch (Exception e) {
                    System.out.println(e + "");
                }
                //这就是要上传的字符串:data_hoad
                String data_hoad = "{" +
                        "\"devUnitID\":\"" + GlobalVars.getDevid() + "\"," +
                        "\"sceneName\":\"" + str_gb + "\"," +
                        "\"datType\":24" + "," +
                        "\"subType1\":0" + "," +
                        "\"subType2\":0" + "," +
                        "\"eventId\":" + eventId + "," +
                        "\"devCnt\":" + num + "," +
                        "\"itemAry\":[" + more_data + "]}";
                Log.e("情景模式测试:", data_hoad);
                MyApplication.sendMsg(data_hoad);
            }
        });
        builder.create().show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            save();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View view) {
        if (view == back)
            save();
        if (view == title_bar_iv_or)
            if (IsCanClick) {
                if (room_list != null && room_list.size() > 0)
                    getRoomDialog();
            }
    }
}


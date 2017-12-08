package cn.etsoft.smarthome.ui.Setting;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.githang.statusbar.StatusBarCompat;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import cn.etsoft.smarthome.Helper.WareDataHliper;
import cn.etsoft.smarthome.MyApplication;
import cn.etsoft.smarthome.NetMessage.GlobalVars;
import cn.etsoft.smarthome.R;
import cn.etsoft.smarthome.adapter.ParlourGridViewAdapter;
import cn.etsoft.smarthome.adapter.PopupWindowAdapter2;
import cn.etsoft.smarthome.adapter.Room_Select_Adapter;
import cn.etsoft.smarthome.domain.WareDev;
import cn.etsoft.smarthome.domain.WareSceneDevItem;
import cn.etsoft.smarthome.domain.WareSceneEvent;
import cn.etsoft.smarthome.pullmi.common.CommonUtils;
import cn.etsoft.smarthome.utils.ToastUtil;
import cn.etsoft.smarthome.weidget.CustomDialog;
import cn.etsoft.smarthome.weidget.CustomDialog_comment;

/**
 * Created by Say GoBay on 2016/9/2.
 * 情景设置页面二
 */
public class SceneSettingActivity extends Activity implements View.OnClickListener {
    private TextView title, title_bar_tv_room, sceneSetSave, safety_type;
    private ImageView back, title_bar_iv_or;
    private List<WareDev> mWareDev;
    private List<WareDev> listViewItems;
    private int eventId;
    private boolean IsCanClick = false;
    private int position_room = 0;
    private List<String> room_list;
    private GridView gridView;
    private ParlourGridViewAdapter parlourGridViewAdapter;
    private WareSceneEvent sceneEvent;
    private PopupWindow popupWindow;
    private List<String> safetytypes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sceneset_listview);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.AppTheme_color));

        MyApplication.mApplication.setOnGetWareDataListener(new MyApplication.OnGetWareDataListener() {
            @Override
            public void upDataWareData(int datType, int subtype1, int subtype2) {
                if (datType == 24 && subtype2 == 1) {
                    ToastUtil.showText("保存成功");
                    MyApplication.mApplication.dismissLoadDialog();
                    finish();
                }
            }
        });
        //初始化标题栏
        initTitleBar();
        listViewItems = WareDataHliper.initCopyWareData().getCopyDevs();
        for (int i = 0; i < listViewItems.size(); i++) {
            listViewItems.get(i).setSelect(false);
        }
        title_bar_tv_room.setText("全部");
        upData();
    }

    /**
     * 初始化标题栏
     */
    private void initTitleBar() {
        title = (TextView) findViewById(R.id.title_bar_tv_title);
        back = (ImageView) findViewById(R.id.title_bar_iv_back);
        title_bar_tv_room = (TextView) findViewById(R.id.title_bar_tv_room);
        safety_type = (TextView) findViewById(R.id.safety_type);
        title_bar_tv_room.setVisibility(View.VISIBLE);
        title_bar_tv_room.setTextColor(0xff000000);
        title_bar_iv_or = (ImageView) findViewById(R.id.title_bar_iv_or);
        title_bar_iv_or.setVisibility(View.VISIBLE);
        title_bar_iv_or.setImageResource(R.drawable.fj);
        title.setText(getIntent().getBundleExtra("bundle").getString("sceneName"));
        back.setOnClickListener(this);
        eventId = getIntent().getBundleExtra("bundle").getInt("eventId", 0);
        title_bar_iv_or.setOnClickListener(this);
        sceneSetSave = (TextView) findViewById(R.id.sceneSetSave);
        sceneSetSave.setOnClickListener(this);
        safety_type.setOnClickListener(this);

        //房间集合
        room_list = new ArrayList<>();
        room_list.add("全部");
        room_list.addAll(MyApplication.getWareData().getRooms());

        safetytypes = new ArrayList<>();
        safetytypes.add("24小时布防");
        safetytypes.add("在家布防");
        safetytypes.add("外出布防");
        safetytypes.add("全部撤防");
    }

    /**
     * 初始化GridView
     */
    private void initGridView(List<WareDev> listViewItems) {
        gridView = (GridView) findViewById(R.id.parlour_gv);
        if (parlourGridViewAdapter != null) {
            parlourGridViewAdapter.notifyDataSetChanged(listViewItems);
        } else {
            parlourGridViewAdapter = new ParlourGridViewAdapter(this, listViewItems);
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
                title_bar_tv_room.setText(room_list.get(position));
                upData();
            }
        });
    }

    private void upData() {
        if (MyApplication.getWareData().getRooms().size() == 0 ||
                WareDataHliper.initCopyWareData().getCopyDevs().size() == 0)
            return;
        IsCanClick = true;
        List<WareSceneDevItem> items = new ArrayList<>();
        try {
            for (int i = 0; i < WareDataHliper.initCopyWareData().getCopyScenes().size(); i++) {
                if (WareDataHliper.initCopyWareData().getCopyScenes().get(i).getEventId() == eventId) {
                    sceneEvent = WareDataHliper.initCopyWareData().getCopyScenes().get(i);
                    items = WareDataHliper.initCopyWareData().getCopyScenes().get(i).getItemAry();
                }
            }
        } catch (Exception e) {
            ToastUtil.showText("情景选择出错");
            finish();
        }
        if (0 == sceneEvent.getExeSecu())
            safety_type.setText("24小时布防");
        if (1 == sceneEvent.getExeSecu())
            safety_type.setText("在家布防");
        if (2 == sceneEvent.getExeSecu())
            safety_type.setText("外出布防");
        if (255 == sceneEvent.getExeSecu())
            safety_type.setText("撤防状态");

        for (int i = 0; i < items.size(); i++) {
            for (int j = 0; j < listViewItems.size(); j++) {
                if (items.get(i).getDevID() == listViewItems.get(j).getDevId()
                        && items.get(i).getCanCpuID().equals(listViewItems.get(j).getCanCpuId())
                        && items.get(i).getDevType() == listViewItems.get(j).getType()) {
                    listViewItems.get(j).setSelect(true);
                    listViewItems.get(j).setbOnOff(items.get(i).getbOnOff());
                }
            }
        }
        mWareDev = new ArrayList<>();
        if (position_room == 0) {
            mWareDev.addAll(listViewItems);
        } else {
            //根据房间id获取设备；
            for (int i = 0; i < listViewItems.size(); i++) {
                if (listViewItems.get(i).getRoomName().equals(title_bar_tv_room.getText())) {
                    mWareDev.add(listViewItems.get(i));
                }
            }
        }
        //初始化GridView
        initGridView(mWareDev);
    }

    public void save() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SceneSettingActivity.this, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
        builder.setTitle("提示");
        builder.setMessage("您要保存这些设置吗？");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("保存", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                MyApplication.mApplication.showLoadDialog(SceneSettingActivity.this);
                int num = 0;
                String div;
                String more_data = "";
                String data_str = "";
                for (int i = 0; i < listViewItems.size(); i++) {//循环所有设备
                    WareDev dev = listViewItems.get(i);//拿到其中一个设备
                    div = ",";
                    if (dev.isSelect()) {
                        data_str = "{" +
                                "\"canCpuId\":\"" + dev.getCanCpuId() + "\"," +
                                "\"devType\":" + dev.getType() + "," +
                                "\"devID\":" + dev.getDevId() + "," +
                                "\"bOnOff\":" + dev.getbOnOff() + "," +
                                "\"lmVal\":0," +
                                "\"rev2\":0," +
                                "\"rev3\":0," +
                                "\"param1\":0," +
                                "\"param2\":0}" + div;
                        num++;
                    }
                    more_data += data_str;
                    data_str = "";
                }
                byte[] nameData = {0};
                try {
                    nameData = title.getText().toString().getBytes("GB2312");
                } catch (Exception e) {
                    MyApplication.mApplication.dismissLoadDialog();
                    ToastUtil.showText("发生未知错误");
                    return;
                }
                String str_gb = CommonUtils.bytesToHexString(nameData);
                Log.e("情景模式名称:%s", str_gb);

                if ("".equals(more_data)) {
                    MyApplication.mApplication.dismissLoadDialog();
                    ToastUtil.showText("没有选择设备，不可保存");
                    return;

                }
                try {
                    more_data = more_data.substring(0, more_data.lastIndexOf(","));
                } catch (Exception e) {
                    System.out.println(e + "");
                    MyApplication.mApplication.dismissLoadDialog();
                    ToastUtil.showText("数据不合适，请重新编辑");
                    return;
                }
                //这就是要上传的字符串:data_hoad
                String data_hoad = "{" +
                        "\"devUnitID\":\"" + GlobalVars.getDevid() + "\"," +
                        "\"sceneName\":\"" + str_gb + "\"," +
                        "\"datType\":24" + "," +
                        "\"subType1\":0" + "," +
                        "\"subType2\":0" + "," +
                        "\"eventId\":" + eventId + "," +
                        "\"exeSecu\":" + sceneEvent.getExeSecu() + "," +
                        "\"devCnt\":" + num + "," +
                        "\"itemAry\":[" + more_data + "]}";
                Log.e("情景模式测试:", data_hoad);
                MyApplication.mApplication.getUdpServer().send(data_hoad, 24);
            }
        });
        builder.create().show();
    }

    @Override
    public void onClick(View view) {
        if (view == back)
            finish();
        if (view == title_bar_iv_or)
            if (IsCanClick) {
                if (room_list != null && room_list.size() > 0)
                    getRoomDialog();
            }
        if (view == safety_type) {
            initRadioPopupWindow(view, safetytypes);
            popupWindow.showAsDropDown(view, 0, 0);
        }
        if (view == sceneSetSave) {
            save();
        }
    }

    /**
     * 初始化自定义设备的状态以及设备PopupWindow
     */
    private void initRadioPopupWindow(final View view_parent, final List<String> text) {

        //获取自定义布局文件pop.xml的视图
        final View customView = view_parent.inflate(this, R.layout.listview_popupwindow_equipment, null);
        customView.setBackgroundResource(R.drawable.selectbg);
        customView.setFocusable(true);
        customView.setFocusableInTouchMode(true);
        customView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (popupWindow != null) {
                        popupWindow.dismiss();
                    }
                }
                return false;
            }
        });
        // 创建PopupWindow实例
        popupWindow = new PopupWindow(customView, view_parent.getWidth(), 200);
        popupWindow.setContentView(customView);
        ListView list_pop = (ListView) customView.findViewById(R.id.popupWindow_equipment_lv);
        PopupWindowAdapter2 adapter = new PopupWindowAdapter2(text, this);
        list_pop.setAdapter(adapter);
        list_pop.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView) view_parent;
                tv.setText(text.get(position));
                sceneEvent.setExeSecu(position == 3 ? 255 : position);
                popupWindow.dismiss();
            }
        });
        //popupwindow页面之外可点
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.update();
        // 自定义view添加触摸事件
        customView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                    popupWindow = null;
                }
                return false;
            }
        });
    }
}


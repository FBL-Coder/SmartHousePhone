package cn.etsoft.smarthomephone.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import cn.etsoft.smarthomephone.MyApplication;
import cn.etsoft.smarthomephone.R;
import cn.etsoft.smarthomephone.adapter.ModuleEditAdapter;
import cn.etsoft.smarthomephone.adapter.PopupWindowAdapter2;
import cn.etsoft.smarthomephone.pullmi.app.GlobalVars;
import cn.etsoft.smarthomephone.pullmi.common.CommonUtils;
import cn.etsoft.smarthomephone.pullmi.entity.WareBoardKeyInput;
import cn.etsoft.smarthomephone.utils.ToastUtil;
import cn.etsoft.smarthomephone.view.Circle_Progress;
import cn.etsoft.smarthomephone.weidget.CustomDialog_comment;

/**
 * Created by Say GoBay on 2017/8/4.
 */
public class EditModuleActivity extends Activity implements View.OnClickListener {
    private ImageView back;
    private TextView title, save, room, count, style, backlight;
    private EditText name;
    private GridView gridView_key;
    private String uid;
    private int keyInput_position = 0;
    private List<String> input_name;
    private int style_position = 0;
    private int backLight_position = 0;
    private int countNumber = 0;
    private List<String> home_text;
    private int count_position = 0;
    private int room_position = 0;
    private List<String> key_style;
    private List<String> key_backLight;
    private List<String> count_text;
    private List<String> listData;
    private byte countData = 0;
    private String[] keyName;
    private ModuleEditAdapter moduleEditAdapter1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_module);
        //初始化标题栏
        initTitleBar();
        //初始化组件
        initView();
        initData();
        MyApplication.mInstance.setOnGetWareDataListener(new MyApplication.OnGetWareDataListener() {
            @Override
            public void upDataWareData(int what) {
                if (mDialog != null)
                    mDialog.dismiss();
                if (what == 9 && MyApplication.getWareData().getKyeInputResult() != null
                        && MyApplication.getWareData().getKyeInputResult().getSubType2() == 1) {
                    Toast.makeText(EditModuleActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                } else if (what == 9 && MyApplication.getWareData().getKyeInputResult() != null
                        && MyApplication.getWareData().getKyeInputResult().getSubType2() == 0) {
                    Toast.makeText(EditModuleActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 初始化标题栏
     */
    private void initTitleBar() {
        title = (TextView) findViewById(R.id.title_bar_tv_title);
        back = (ImageView) findViewById(R.id.title_bar_iv_back);
        title.setTextColor(0xffffffff);
        title.setText("");
        title.setHint(getIntent().getStringExtra("title"));
        title.setHintTextColor(0xffffffff);
        keyInput_position = getIntent().getIntExtra("keyInput_position", keyInput_position);
        back.setImageResource(R.drawable.return2);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    /**
     * 初始化组件
     */
    private void initView() {
        save = (TextView) findViewById(R.id.save);
        room = (TextView) findViewById(R.id.room);
        count = (TextView) findViewById(R.id.count);
        style = (TextView) findViewById(R.id.style);
        backlight = (TextView) findViewById(R.id.backlight);
        name = (EditText) findViewById(R.id.name);
        gridView_key = (GridView) findViewById(R.id.gridView_key);

        save.setOnClickListener(this);
        room.setOnClickListener(this);
        count.setOnClickListener(this);
        style.setOnClickListener(this);
        backlight.setOnClickListener(this);
        name.setOnClickListener(this);
    }

    /**
     * 初始化数据
     */
    String roomName;

    public void initData() {
        name.setText("");
        name.setHint(MyApplication.getWareData().getKeyInputs().get(keyInput_position).getBoardName());
        roomName = MyApplication.getWareData().getKeyInputs().get(keyInput_position).getRoomName();
        style_position = MyApplication.getWareData().getKeyInputs().get(keyInput_position).getbResetKey();
        backLight_position = MyApplication.getWareData().getKeyInputs().get(keyInput_position).getLedBkType();
        countNumber = MyApplication.getWareData().getKeyInputs().get(keyInput_position).getKeyCnt();

        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                title.setText(name.getText().toString());
            }
        });
        home_text = MyApplication.getRoom_list();
        count_position = countNumber;
        if (home_text != null && home_text.size() > 0) {
            room.setText(home_text.get(room_position));
        }
        count_text = new ArrayList<>();
        count_text.add(0, String.valueOf(0));
        count_text.add(1, String.valueOf(1));
        count_text.add(2, String.valueOf(2));
        count_text.add(3, String.valueOf(3));
        count_text.add(4, String.valueOf(4));
        count_text.add(5, String.valueOf(5));
        count_text.add(6, String.valueOf(6));
        count_text.add(7, String.valueOf(7));
        count_text.add(8, String.valueOf(8));
        count.setText(count_text.get(count_position));
        count.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                initGridView(keyInput_position);
            }
        });

        key_style = new ArrayList<>();
        key_style.add(0, "复位");
        key_style.add(1, "非复位");
        if (style_position == 0 || style_position == 1) {
            style.setText(key_style.get(style_position));
        } else {
            style.setText("未知");
        }
        key_backLight = new ArrayList<>();
        key_backLight.add(0, "随灯状态变化");
        key_backLight.add(1, "常亮");
        key_backLight.add(2, "不亮");
        if (backLight_position == 0 || backLight_position == 1 || backLight_position == 2) {
            backlight.setText(key_backLight.get(backLight_position));
        } else {
            backlight.setText("未知");
        }
        initGridView(keyInput_position);

    }

    /**
     * 初始化GridView
     */
    public void initGridView(int keyInput_position) {
        keyName = MyApplication.getWareData().getKeyInputs().get(keyInput_position).getKeyName();
        //按键数目
        try {
            countData = Byte.parseByte(count_text.get(count_position));
        } catch (Exception e) {
            return;
        }
        //按键名称集合
        listData = new ArrayList<>();

        if (countData > keyName.length) {
            for (int i = 0; i < countData; i++) {
                if (i >= keyName.length) {
                    listData.add("按键" + i);
                } else {
                    listData.add(keyName[i]);
                }
            }
        } else {
            for (int i = 0; i < countData; i++) {
                listData.add(keyName[i]);
            }
        }
        moduleEditAdapter1 = new ModuleEditAdapter(EditModuleActivity.this, listData);
        gridView_key.setAdapter(moduleEditAdapter1);
        gridView_key.setSelector(new ColorDrawable(Color.TRANSPARENT));

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.style:
                initPopupWindow(style, key_style, 1);
                popupWindow.showAsDropDown(view, 0, 0);
                break;
            case R.id.backlight:
                initPopupWindow(backlight, key_backLight, 2);
                popupWindow.showAsDropDown(view, 0, 0);
                break;
            case R.id.room:
                initPopupWindow(room, home_text, 3);
                popupWindow.showAsDropDown(view, 0, 0);
                break;
            case R.id.count:
                initPopupWindow(count, count_text, 4);
                popupWindow.showAsDropDown(view, 0, 0);
                break;
            case R.id.save:
                CustomDialog_comment.Builder builder = new CustomDialog_comment.Builder(EditModuleActivity.this);
                builder.setTitle("提示 :");
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
                        WareBoardKeyInput wareBoardKeyInput = null;
                        String data_str = "";
                        String div;
                        String more_data = "";
                        div = ",";
                        wareBoardKeyInput = MyApplication.getWareData().getKeyInputs().get(keyInput_position);
                        //输入板名称
                        byte[] nameData = {0};
                        if ("".equals(name.getText().toString())) {
                            try {
                                nameData = wareBoardKeyInput.getBoardName().getBytes("GB2312");
                            } catch (Exception e) {
                            }
                        } else {
                            try {
                                nameData = name.getText().toString().getBytes("GB2312");
                            } catch (UnsupportedEncodingException e) {
                                ToastUtil.showToast(EditModuleActivity.this, "输入面板名称不合适");
                                return;
                            }
                        }
                        if (name.getText().toString().length() > 24) {
                            ToastUtil.showToast(EditModuleActivity.this, "输入面板名称不能过长");
                            return;
                        }
                        String str_gb = CommonUtils.bytesToHexString(nameData);

                        //房间名称
                        byte[] roomData = {0};
                        try {
                            roomData = home_text.get(room_position).getBytes("GB2312");
                        } catch (Exception e) {
                            return;
                        }
                        String str_gb_room = CommonUtils.bytesToHexString(roomData);

                        //按键名称
                        String name_rows = "";
                        for (int i = 0; i < countData; i++) {
                            if (i < countData - 1)
                                try {
                                    name_rows += "\"" + CommonUtils.bytesToHexString(listData.get(i).getBytes("GB2312")) + "\",";
                                } catch (Exception e) {
                                    System.out.println(e + "");
                                }
                            else
                                try {
                                    name_rows += "\"" + CommonUtils.bytesToHexString(listData.get(i).getBytes("GB2312")) + "\"";
                                } catch (Exception e) {
                                    System.out.println(e + "");
                                }
                        }
                        name_rows = "[" + name_rows + "]";


                        //KeyAllCtrlType
                        String key_rows = "";
                        for (int i = 0; i < wareBoardKeyInput.getKeyAllCtrlType().length; i++) {
                            if (i < wareBoardKeyInput.getKeyAllCtrlType().length - 1)
                                key_rows += wareBoardKeyInput.getKeyAllCtrlType()[i] + ",";
                            else key_rows += wareBoardKeyInput.getKeyAllCtrlType()[i] + "";
                        }
                        key_rows = "[" + key_rows + "]";


                        data_str = "{" +
                                "\"canCpuID\":\"" + wareBoardKeyInput.getCanCpuID() + "\"," +
                                "\"boardName\":\"" + str_gb + "\"," +
                                "\"boardType\":" + wareBoardKeyInput.getBoardType() + "," +
                                "\"keyCnt\":" + countData + "," +
                                "\"bResetKey\":" + style_position + "," +
                                "\"ledBkType\":" + backLight_position + "," +
                                "\"keyName_rows\":" + name_rows + "," +
                                "\"keyAllCtrlType_rows\":" + key_rows + "," +
                                "\"roomName\":\"" + str_gb_room
                                + "\"}" + div;
                        more_data += data_str;

                        try {
                            more_data = more_data.substring(0, more_data.lastIndexOf(","));
                        } catch (Exception e) {
                            System.out.println(e + "");
                        }
                        initDialog("正在保存...");
                        //这就是要上传的字符串:data_hoad
                        String data_hoad = "{" +
                                "\"devUnitID\":\"" + GlobalVars.getDevid() + "\"," +
                                "\"datType\":9" + "," +
                                "\"subType1\":0" + "," +
                                "\"subType2\":1" + "," +
                                "\"keyinput\":" + wareBoardKeyInput.getKeyinput() + "," +
                                "\"keyinput_rows\":[" + more_data + "]}";
                        Log.e("情景模式测试:", data_hoad);
                        MyApplication.sendMsg(data_hoad);
                    }
                });
                builder.create().show();
                break;
        }
    }

    private Dialog mDialog;

    //自定义加载进度条
    private void initDialog(String str) {
        Circle_Progress.setText(str);
        mDialog = Circle_Progress.createLoadingDialog(EditModuleActivity.this);
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

    /**
     * 初始化自定义设备的状态以及设备PopupWindow
     */
    private PopupWindow popupWindow;

    private void initPopupWindow(final View view_parent, final List<String> text, final int tag) {
        //获取自定义布局文件pop.xml的视图
        final View customView = view_parent.inflate(EditModuleActivity.this, R.layout.popupwindow_equipment_listview, null);
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
        popupWindow = new PopupWindow(view_parent.findViewById(R.id.popupWindow_equipment_sv), view_parent.getWidth(), 150);
        popupWindow.setContentView(customView);
        ListView list_pop = (ListView) customView.findViewById(R.id.popupWindow_equipment_lv);
        PopupWindowAdapter2 adapter = new PopupWindowAdapter2(text, EditModuleActivity.this);
        list_pop.setAdapter(adapter);
        list_pop.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view_p, int position, long id) {
                TextView tv = (TextView) view_parent;
                tv.setText(text.get(position));
                if (tag == 1) {
                    style_position = position;
                } else if (tag == 2) {
                    backLight_position = position;
                } else if (tag == 3) {
                    room_position = position;
                } else if (tag == 4) {
                    count_position = position;
                    initGridView(keyInput_position);
                }
                popupWindow.dismiss();
            }
        });
        //popupWindow页面之外可点
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

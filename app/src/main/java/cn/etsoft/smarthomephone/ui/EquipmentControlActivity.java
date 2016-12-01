package cn.etsoft.smarthomephone.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.etsoft.smarthomephone.MyApplication;
import cn.etsoft.smarthomephone.R;
import cn.etsoft.smarthomephone.adapter.EquipmentAdapter;
import cn.etsoft.smarthomephone.adapter.PopupWindowAdapter;
import cn.etsoft.smarthomephone.pullmi.entity.WareDev;


/**
 * Created by Say GoBay on 2016/8/25.
 */
public class EquipmentControlActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private ImageView equipment_id,equipment, room,back;
    private TextView title, TvEquipment, TvRoom,id_kz;
    private ScrollView sv;
    private ListView dev_lv;
    private int[] image = {R.drawable.select, R.drawable.select, R.drawable.selected, R.drawable.select,};
    private PopupWindow popupWindow;
    private String[] text = {"电视", "灯光", "空调", "窗帘"};
    private List<String> dev_type_text ;
    private List<String> dev_name ;
    private List<String> id_kz_text ;
    private List<String> home_text ;
    private List<WareDev> mWareDev_room;
    private List<WareDev> mWareDev;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipment);
        //初始化标题栏
        initTitleBar();
        //初始化控件
        initView();
        //初始化ListView
        initListView();
    }

    /**
     * 初始化标题栏
     */
    private void initTitleBar() {
        title = (TextView) findViewById(R.id.title_bar_tv_title);
        title.setText(getIntent().getStringExtra("title"));
        back = (ImageView) findViewById(R.id.title_bar_iv_back);
        back.setOnClickListener(this);
        dev_type_text = new ArrayList<>();
        dev_type_text.add("空调");
        dev_type_text.add("灯光");
        dev_type_text.add("窗帘");
        dev_type_text.add("电视");
        dev_name = new ArrayList<>();
        id_kz_text = new ArrayList<>();
        home_text = new ArrayList<>();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        equipment = (ImageView) findViewById(R.id.equipment_lamplight);
        room = (ImageView) findViewById(R.id.equipment_parlour);
        equipment_id = (ImageView) findViewById(R.id.equipment_id);
        TvEquipment = (TextView) findViewById(R.id.tv_equipment_lamplight);
        TvRoom = (TextView) findViewById(R.id.tv_equipment_parlour);
        id_kz = (TextView) findViewById(R.id.tv_equipment_id);

        for (int i = 0; i < MyApplication.getWareData().getBoardChnouts().size(); i++) {
            id_kz_text.add(MyApplication.getWareData().getBoardChnouts().get(i).getDevUnitID());
        }
        mWareDev_room = new ArrayList<>();
        mWareDev = new ArrayList<>();

        for (int i = 0; i < MyApplication.getWareData().getDevs().size(); i++) {
            mWareDev_room.add(MyApplication.getWareData().getDevs().get(i));
            mWareDev.add(MyApplication.getWareData().getDevs().get(i));
        }

        for (int i = 0; i < mWareDev_room.size() - 1; i++) {
            for (int j = mWareDev_room.size() - 1; j > i; j--) {
                if (mWareDev_room.get(i).getRoomName().equals(mWareDev_room.get(j).getRoomName())
                        || !(mWareDev_room.get(i).getCanCpuId()).equals(MyApplication.getWareData().getBoardChnouts().get(j).getDevUnitID())) {
                    mWareDev_room.remove(j);
                }
            }
        }

        for (int i = 0; i < mWareDev_room.size(); i++) {
            home_text.add(mWareDev_room.get(i).getRoomName()) ;
        }

        equipment.setOnClickListener(this);
        equipment_id.setOnClickListener(this);
        room.setOnClickListener(this);
        TvEquipment.setText(dev_type_text.get(0));
//        TvRoom.setText(home_text.get(0));
        id_kz.setText(id_kz_text.get(0));

    }

    /**
     * 初始化ListView
     */
    private void initListView() {
        dev_lv = (ListView) findViewById(R.id.equipment_lv);
        sv = (ScrollView) findViewById(R.id.equipment_sv);
        sv.smoothScrollTo(0, 0);
        EquipmentAdapter adapter = new EquipmentAdapter(dev_name, image, this);
        dev_lv.setAdapter(adapter);
    }

    /**
     * 初始化自定义PopupWindow
     */
    private void initPopupWindow(List<String> text) {
        //获取自定义布局文件pop.xml的视图
        final View customView = getLayoutInflater().from(this).inflate(R.layout.popupwindow_equipment_listview, null);
        customView.setBackgroundResource(R.drawable.selectbg);
        // 创建PopupWindow实例
        popupWindow = new PopupWindow(findViewById(R.id.popupWindow_equipment_sv),
                160, 312
        );
        popupWindow.setContentView(customView);
        ListView pop_lv = (ListView) customView.findViewById(R.id.popupWindow_equipment_lv);
        PopupWindowAdapter adapter = new PopupWindowAdapter(text, this);
        pop_lv.setAdapter(adapter);
        pop_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TvEquipment.setText(dev_type_text.get(i));

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


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TvEquipment.setText(text[position]);
        TvRoom.setText(text[position]);
        popupWindow.dismiss();
    }


    @Override
    public void onClick(View v) {
        int widthOff = getWindow().getWindowManager().getDefaultDisplay().getWidth() / 4;
        switch (v.getId()) {
            case R.id.equipment_lamplight:
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                    popupWindow = null;
                } else {
                    initPopupWindow(dev_type_text);
                    popupWindow.showAsDropDown(v, -widthOff, 0);
                }
                break;
            case R.id.equipment_parlour:
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                    popupWindow = null;
                } else {
                    initPopupWindow(home_text);
                    popupWindow.showAsDropDown(v, -widthOff, 0);
                }
                break;
            case R.id.equipment_id:
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                    popupWindow = null;
                } else {
                    initPopupWindow(id_kz_text);
                    popupWindow.showAsDropDown(v, -widthOff, 0);
                }
                break;
            case R.id.title_bar_iv_back:
                finish();
        }
    }
}

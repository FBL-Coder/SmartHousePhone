package cn.etsoft.smarthomephone.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.etsoft.smarthomephone.MyApplication;
import cn.etsoft.smarthomephone.R;
import cn.etsoft.smarthomephone.adapter.PopupWindowAdapter;
import cn.etsoft.smarthomephone.pullmi.entity.WareBoardKeyInput;
import cn.etsoft.smarthomephone.pullmi.entity.WareDev;

/**
 * Created by fbl on 16-11-17.
 */
public class Add_Dev_Activity extends Activity implements View.OnClickListener {

    private TextView title, add_dev_type, add_dev_room, add_dev_board, add_dev_save;
    private EditText add_dev_name, add_dev_way;
    private ImageView back;
    private PopupWindow popupWindow;
    private List<String> Board_text;
    private List<WareBoardKeyInput> list_board;
    private List<String> home_text;
    private List<String> type_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_dev);

        initView();
    }

    private void initView() {

        title = (TextView) findViewById(R.id.title_bar_tv_title);
        add_dev_type = (TextView) findViewById(R.id.add_dev_type);
        add_dev_room = (TextView) findViewById(R.id.add_dev_room);
        add_dev_save = (TextView) findViewById(R.id.add_dev_save);
        add_dev_board = (TextView) findViewById(R.id.add_dev_board);
        add_dev_name = (EditText) findViewById(R.id.add_dev_name);
        add_dev_way = (EditText) findViewById(R.id.add_dev_way);
        back = (ImageView) findViewById(R.id.title_bar_iv_back);


        back.setOnClickListener(this);
        add_dev_type.setOnClickListener(this);
        add_dev_room.setOnClickListener(this);
        add_dev_board.setOnClickListener(this);
        add_dev_save.setOnClickListener(this);


        Board_text = new ArrayList<>();
        list_board = MyApplication.getWareData().getKeyInputs();
        for (int i = 0; i < list_board.size(); i++) {
            Board_text.add(list_board.get(i).getBoardName());
        }

        // 创建PopupWindow实例
        home_text = new ArrayList<>();
        List<WareDev> mWareDev_room = new ArrayList<>();

        for (int i = 0; i < MyApplication.getWareData().getDevs().size(); i++) {
            mWareDev_room.add(MyApplication.getWareData().getDevs().get(i));
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
            home_text.add(mWareDev_room.get(i).getRoomName());
        }

        type_text = new ArrayList<>();
        type_text.add("空调");
        type_text.add("电视");
        type_text.add("机顶盒");
        type_text.add("灯光");
        type_text.add("窗帘");
        title.setText("添加设备");
        add_dev_type.setText(type_text.get(0));
        add_dev_board.setText(Board_text.get(0));
        add_dev_room.setText(home_text.get(0));

    }

    @Override
    public void onClick(View v) {
        int widthOff = getWindow().getWindowManager().getDefaultDisplay().getWidth() / 500;
        switch (v.getId()) {
            case R.id.title_bar_iv_back:
                finish();
                break;
            case R.id.add_dev_type:
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                    popupWindow = null;
                } else {
                    initPopupWindow(type_text, 1);
                    popupWindow.showAsDropDown(v, -widthOff, 0);
                }
                break;
            case R.id.add_dev_room:
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                    popupWindow = null;
                } else {
                    initPopupWindow(home_text, 2);
                    popupWindow.showAsDropDown(v, -widthOff, 0);
                }
                break;
            case R.id.add_dev_board:
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                    popupWindow = null;
                } else {
                    initPopupWindow(Board_text, 3);
                    popupWindow.showAsDropDown(v, -widthOff, 0);
                }
                break;
            case R.id.add_dev_save:

                String name = add_dev_name.getText().toString();
                String type = add_dev_type.getText().toString();
                String room = add_dev_room.getText().toString();
                String board = add_dev_board.getText().toString();
                String way = add_dev_way.getText().toString();

                if ("".equals(name) || "".equals(type) || "".equals(room) || "".equals(board) || "".equals(way))
                    Toast.makeText(Add_Dev_Activity.this, "信息输入不完整", Toast.LENGTH_SHORT).show();
                else {
                    int type_index = type_text.indexOf(type);
                    int way_int = Integer.parseInt(way);
                    //----待和服务器交互；
                }
                break;
        }
    }

    /**
     * 初始化自定义设备的状态以及设备PopupWindow
     */

    private void initPopupWindow(final List<String> text, final int type) {
        //获取自定义布局文件pop.xml的视图
        final View customView = getLayoutInflater().from(this).inflate(R.layout.popupwindow_equipment_listview, null);
        customView.setBackgroundResource(R.drawable.selectbg);

        // 创建PopupWindow实例
        if (type == 1)
            popupWindow = new PopupWindow(findViewById(R.id.popupWindow_equipment_sv), 320, 160);
        else if (type == 0)
            popupWindow = new PopupWindow(findViewById(R.id.popupWindow_equipment_sv), 320, 300);
        else
            popupWindow = new PopupWindow(findViewById(R.id.popupWindow_equipment_sv), 320, 120);

        popupWindow.setContentView(customView);
        ListView list_pop = (ListView) customView.findViewById(R.id.popupWindow_equipment_lv);
        PopupWindowAdapter adapter = new PopupWindowAdapter(text, this);
        list_pop.setAdapter(adapter);
        list_pop.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (type == 1) {
                    add_dev_type.setText(text.get(position));
                } else if (type == 2) {
                    add_dev_room.setText(text.get(position));
                } else if (type == 3) {
                    add_dev_board.setText(text.get(position));
                }
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



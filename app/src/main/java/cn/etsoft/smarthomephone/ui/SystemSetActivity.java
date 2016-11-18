package cn.etsoft.smarthomephone.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.etsoft.smarthomephone.R;
import cn.etsoft.smarthomephone.adapter.GridViewAdapter;

/**
 * Created by Say GoBay on 2016/9/2.
 */
public class SystemSetActivity extends Activity implements AdapterView.OnItemClickListener{
    private GridView gridView;
    private LinearLayout ll;
    private int[] image = {R.drawable.systemsetup2, R.drawable.systemsetup3, R.drawable.systemsetup4, R.drawable.systemsetup5,
            R.drawable.ip, R.drawable.equipmentcontrol};
    private String[] text = {"联网模块", "房间设置", "情景设置", "组合设置","远程IP","设备控制"};
    private ImageView back;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lights);
        //初始化标题栏
        initTitleBar();
        //初始化控件
        initView();
        //初始化GridView
        initGridView();
    }
    /**
     * 初始化标题栏
     */
    private void initTitleBar() {
        back = (ImageView) findViewById(R.id.title_bar_iv_back);
        title = (TextView) findViewById(R.id.title_bar_tv_title);
        title.setText(getIntent().getStringExtra("title"));
        title.setTextColor(0xffffffff);
        back.setImageResource(R.drawable.return2);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 初始化控件
     */
    private void initView() {
        ll = (LinearLayout) findViewById(R.id.ll);
        ll.setBackgroundResource(R.drawable.tu7);
    }

    /**
     * 初始化GridView
     */
    private void initGridView() {
        gridView = (GridView) findViewById(R.id.light_gv);
        gridView.setAdapter(new GridViewAdapter(image, text, this));
        gridView.setSelector(R.drawable.selector_gridview_item);
        gridView.setOnItemClickListener(this);
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent;
        switch (position) {
            case 0:
                intent = new Intent(SystemSetActivity.this,NetWorkActivity.class);
                intent.putExtra("title",text[0]);
                startActivity(intent);
                break;
            case 1:
                intent = new Intent(SystemSetActivity.this,RoomActivity.class);
                intent.putExtra("title",text[1]);
                startActivity(intent);
                break;
            case 2:
                intent = new Intent(SystemSetActivity.this,SceneSetActivity.class);
                intent.putExtra("title",text[2]);
                startActivity(intent);
                break;
            case 3:
                intent = new Intent(SystemSetActivity.this, GroupActivity.class);
                intent.putExtra("title",text[3]);
                startActivity(intent);
                break;
            case 4:
                intent = new Intent(SystemSetActivity.this, Add_long_ip.class);
                intent.putExtra("title",text[4]);
                startActivity(intent);
                break;
            case 5:
                intent = new Intent(SystemSetActivity.this, Equipment_control.class);
                intent.putExtra("title",text[5]);
                startActivity(intent);
                break;
        }
    }
}

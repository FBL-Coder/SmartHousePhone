package cn.etsoft.smarthomephone.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cn.etsoft.smarthomephone.MyApplication;
import cn.etsoft.smarthomephone.R;
import cn.etsoft.smarthomephone.adapter.SceneAdapter;
import cn.etsoft.smarthomephone.pullmi.entity.WareData;
import cn.etsoft.smarthomephone.pullmi.entity.WareSceneEvent;

/**
 * Created by Say GoBay on 2016/9/1.
 * 情景页面
 */
public class SceneActivity extends Activity {
    private GridView gridView;
    private LinearLayout ll;
    private ImageView back;
    private TextView title, name_cur;

    private boolean IsCanClick = false;
    private WareData wareData;
    private List<WareSceneEvent> mSceneEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initEvent();
//        MyApplication.getSceneInfo();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scene);
        //初始化标题栏
        initTitleBar();
        //初始化控件
        intView();
        //初始化GridView
        initGridView();
    }

    /**
     * 初始化标题栏
     */
    private void initTitleBar() {
        back = (ImageView) findViewById(R.id.title_bar_iv_back);
        title = (TextView) findViewById(R.id.title_bar_tv_title);
        title.setText(getIntent().getStringExtra("title") + "控制");
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
    private void intView() {
        ll = (LinearLayout) findViewById(R.id.ll);
        ll.setBackgroundResource(R.drawable.tu5);
        name_cur = (TextView) findViewById(R.id.name_cur);
        name_cur.setVisibility(View.GONE);
    }

    /**
     * 初始化GridView
     */
    private void initGridView() {
        gridView = (GridView) findViewById(R.id.light_gv);
        gridView.setSelector(R.drawable.selector_gridview_item);

        MyApplication.mInstance.setOnGetWareDataListener( new MyApplication.OnGetWareDataListener() {
            @Override
            public void upDataWareData(int what) {
                if (MyApplication.getWareData().getSceneEvents() != null && MyApplication.getWareData().getSceneEvents().size() > 0) {
                    initEvent();
                    IsCanClick = true;
                    gridView.setAdapter(new SceneAdapter(getListData(), SceneActivity.this));
                    gridView.setSelector(R.drawable.selector_gridview_item);
                } else {
                    Toast.makeText(SceneActivity.this, "没有找到情景模式", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (MyApplication.getWareData().getSceneEvents() != null && MyApplication.getWareData().getSceneEvents().size() > 0) {
            initEvent();
            IsCanClick = true;
            gridView.setAdapter(new SceneAdapter(getListData(), SceneActivity.this));
            gridView.setSelector(R.drawable.selector_gridview_item);
        } else {
            Toast.makeText(SceneActivity.this, "没有找到情景模式", Toast.LENGTH_SHORT).show();
        }
    }

    private void initEvent() {
        wareData = MyApplication.getWareData();
    }

    private List<WareSceneEvent> getListData() {
        mSceneEvents = wareData.getSceneEvents();
        return mSceneEvents;
    }
}

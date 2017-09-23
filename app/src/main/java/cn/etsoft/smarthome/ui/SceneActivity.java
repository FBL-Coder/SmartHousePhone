package cn.etsoft.smarthome.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.etsoft.smarthome.MyApplication;
import cn.etsoft.smarthome.R;
import cn.etsoft.smarthome.adapter.SceneAdapter;
import cn.etsoft.smarthome.domain.WareData;
import cn.etsoft.smarthome.domain.WareSceneEvent;
import cn.etsoft.smarthome.utils.SendDataUtil;

/**
 * Created by Say GoBay on 2016/9/1.
 * 情景页面
 */
public class SceneActivity extends Activity {
    private GridView gridView;
    private LinearLayout ll;
    private ImageView back;
    private TextView title, name_cur;
    private List<WareSceneEvent> mSceneEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        MyApplication.getSceneInfo();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scene);
        if (MyApplication.getWareData().getSceneEvents().size() == 0) {
            SendDataUtil.getSceneInfo();
            MyApplication.mApplication.showLoadDialog(this);
        }
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
        MyApplication.mApplication.setOnGetWareDataListener(new MyApplication.OnGetWareDataListener() {
            @Override
            public void upDataWareData(int datType, int subtype1, int subtype2) {
                if (datType == 22 && MyApplication.getWareData().getSceneEvents() != null
                        && MyApplication.getWareData().getSceneEvents().size() > 0) {
                    MyApplication.mApplication.dismissLoadDialog();
                    gridView.setAdapter(new SceneAdapter(getListData(), SceneActivity.this));
                    gridView.setSelector(R.drawable.selector_gridview_item);
                }
            }
        });

        if (MyApplication.getWareData().getSceneEvents().size() > 0) {
            gridView.setAdapter(new SceneAdapter(getListData(), SceneActivity.this));
            gridView.setSelector(R.drawable.selector_gridview_item);
        }
    }

    private List<WareSceneEvent> getListData() {
        mSceneEvents = new ArrayList<>();
        WareSceneEvent event = new WareSceneEvent();
        event.setEventId(0);
        event.setSceneName("全开模式");
        mSceneEvents.add(event);
        WareSceneEvent event1 = new WareSceneEvent();
        event1.setEventId(1);
        event1.setSceneName("全关模式");
        mSceneEvents.add(event1);
        for (int i = 0; i < MyApplication.getWareData().getSceneEvents().size(); i++) {
            mSceneEvents.add(MyApplication.getWareData().getSceneEvents().get(i));
        }
        return mSceneEvents;
    }
}

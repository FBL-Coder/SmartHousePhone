package cn.etsoft.smarthome.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.etsoft.smarthome.MyApplication;
import cn.etsoft.smarthome.R;
import cn.etsoft.smarthome.adapter.IClick;
import cn.etsoft.smarthome.adapter.TextDeployAdapter;
import cn.etsoft.smarthome.utils.ToastUtil;

/**
 * Created by Say GoBay on 2017/5/31.
 * 组合设置--按键情景
 */
public class KeySceneActivity extends Activity implements AdapterView.OnItemClickListener {
    private TextView mTitle;
    private ImageView back;
    private ScrollView sv;
    private ListView lv;
    private int sceneId = 0;
    private int keyInput_position = 0;
    private String title;

    List<String> LTitle;
    List<String> LText;
    List<String> LDeploy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parlour_four_out_listview);
        //初始化标题栏
        initTitleBar();
        //初始化ListView
        initListView();
    }


    /**
     * 初始化标题栏
     */
    private void initTitleBar() {
        mTitle = (TextView) findViewById(R.id.title_bar_tv_title);
        title = getIntent().getExtras().getString("sceneName");
        back = (ImageView) findViewById(R.id.title_bar_iv_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTitle.setText(title);
        sceneId = getIntent().getExtras().getInt("sceneId",0);
        //名称
        LTitle = new ArrayList<>();
        //测试
        LText = new ArrayList<>();
        //配置
        LDeploy = new ArrayList<>();
    }

    /**
     * 初始化ListView
     */
    private void initListView() {
        lv = (ListView) findViewById(R.id.out_lv);
        sv = (ScrollView) findViewById(R.id.out_sv);
        sv.smoothScrollTo(0, 0);
        if (MyApplication.getWareData().getKeyInputs().size() == 0) {
            ToastUtil.showText("没有收到输入板数据");
            return;
        }
        int size = MyApplication.getWareData().getKeyInputs().size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                LTitle.add(MyApplication.getWareData().getKeyInputs().get(i).getBoardName());
                LText.add("测试");
                LDeploy.add("配置");
            }

            lv.setAdapter(new TextDeployAdapter(LTitle, LText, LDeploy, this, mListener));
            lv.setOnItemClickListener(this);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    }

    /**
     * 实现类，响应按钮点击事件
     */
    private IClick mListener = new IClick() {
        @Override
        public void listViewItemClick(int position, View v) {
            switch (v.getId()) {
                case R.id.parlour_four_text:
                    break;
                case R.id.parlour_four_deploy:
                    Intent intent = new Intent(KeySceneActivity.this, KeySceneActivity_dev.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("title", LTitle.get(position));
                    bundle.putInt("sceneId", sceneId);
                    bundle.putInt("keyInput_position", keyInput_position);
                    keyInput_position = position;
                    intent.putExtras(bundle);
                    startActivity(intent);
                    break;
            }
        }
    };
}

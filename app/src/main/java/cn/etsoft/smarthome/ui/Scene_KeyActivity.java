package cn.etsoft.smarthome.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import cn.etsoft.smarthome.R;
import cn.etsoft.smarthome.adapter.Scene_KeyAdapter;
import cn.etsoft.smarthome.ui.Setting.KeySceneActivity_dev;

/**
 * Author：FBL  Time： 2017/10/24.
 * 情景配按键点击后的按键板列表页面
 */

public class Scene_KeyActivity extends Activity {

    private ListView mSceneKeys;
    private ImageView back;
    private TextView title;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keys_activity);
        initView();
    }

    private void initView() {
        back = (ImageView) findViewById(R.id.title_bar_iv_back);
        title = (TextView) findViewById(R.id.title_bar_tv_title);
        mSceneKeys = (ListView) findViewById(R.id.scene_keys);
        mSceneKeys.setAdapter(new Scene_KeyAdapter(this));
        title.setText("请选择按键板");
        mSceneKeys.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(Scene_KeyActivity.this, KeySceneActivity_dev.class);
                intent.putExtra("title", getIntent().getStringExtra("title"));
                intent.putExtra("sceneId", getIntent().getIntExtra("SceneID", 0));
                intent.putExtra("keyInput_position", i);
                startActivity(intent);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}

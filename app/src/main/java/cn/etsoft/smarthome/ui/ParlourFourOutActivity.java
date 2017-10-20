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
import cn.etsoft.smarthome.domain.WareDev;
import cn.etsoft.smarthome.ui.Setting.EquipmentDeployActivity;

/**
 * Created by Say GoBay on 2016/8/26.
 * 组合设置--输出板配置页面
 */
public class ParlourFourOutActivity extends Activity implements AdapterView.OnItemClickListener {
    private TextView mTitle;
    private ImageView back;
    private ScrollView sv;
    private ListView lv;
    private String uid;
    private String title;

    List<String> LTitle;
    List<String> LText;
    List<String> LDeploy;

    private List<WareDev> devs;

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
        title = getIntent().getExtras().getString("title");
        back = (ImageView) findViewById(R.id.title_bar_iv_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTitle.setText(title);
        uid = getIntent().getExtras().getString("uid");
        //名称
        LTitle = new ArrayList<>();
        //测试
        LText = new ArrayList<>();
        //配置
        LDeploy = new ArrayList<>();
        //设备
        devs = new ArrayList<>();
    }

    /**
     * 初始化ListView
     */
    private void initListView() {
        lv = (ListView) findViewById(R.id.out_lv);
        sv = (ScrollView) findViewById(R.id.out_sv);
        int size = MyApplication.getWareData().getDevs().size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                if (uid.equals(MyApplication.getWareData().getDevs().get(i).getCanCpuId())) {
                    LTitle.add(MyApplication.getWareData().getDevs().get(i).getDevName());
                    LText.add("测试");
                    LDeploy.add("配置");
                    devs.add(MyApplication.getWareData().getDevs().get(i));
                }
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
                    Intent intent = new Intent(ParlourFourOutActivity.this, EquipmentDeployActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("title", LTitle.get(position));
                    bundle.putString("uid", uid);
                    bundle.putInt("devType", devs.get(position).getType());
                    bundle.putInt("devID", devs.get(position).getDevId());
                    intent.putExtras(bundle);
                    startActivity(intent);
                    break;
            }
        }
    };
}

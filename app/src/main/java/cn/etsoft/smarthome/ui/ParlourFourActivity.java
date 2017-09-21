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

import cn.etsoft.smarthome.MyApplication;
import cn.etsoft.smarthome.R;
import cn.etsoft.smarthome.adapter.IClick;
import cn.etsoft.smarthome.adapter.TextDeployAdapter;


/**
 * Created by Say GoBay on 2016/8/24.
 * 输入板配置页面
 */
public class ParlourFourActivity extends Activity implements AdapterView.OnItemClickListener {
    private TextView title;
    private ImageView back;
    private ScrollView sv;
    private ListView lv;

    private String[] text = {"测试"};
    private String[] deploy = {"配置"};
    private int index = -1;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parlour_four_listview);
        //初始化标题栏
        initTitleBar();
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
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        uid = getIntent().getExtras().getString("uid");
    }

    /**
     * 初始化ListView
     */
    private void initListView() {
        lv = (ListView) findViewById(R.id.parlour_four_lv);
        sv = (ScrollView) findViewById(R.id.parlour_four_sv);
        sv.smoothScrollTo(0, 0);
        if (MyApplication.getWareData().getKeyInputs().size() > 0) {
            int size = MyApplication.getWareData().getKeyInputs().size();
            if (size > 0) {
                for (int i = 0; i < size; i++)
                    if (title.getText().toString().equals(MyApplication.getWareData().getKeyInputs().get(i).getBoardName())) {
                        index = i;
                        lv.setAdapter(new TextDeployAdapter(
                                MyApplication.getWareData().getKeyInputs().get(i).getKeyName(),
                                MyApplication.getWareData().getKeyInputs().get(i).getKeyCnt(),
                                text, deploy, this, mListener));
                        lv.setOnItemClickListener(this);
                    }
            }
        }
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
                    Intent intent = new Intent(ParlourFourActivity.this, AddEquipmentControlActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("key_index", position);
                    bundle.putString("title", MyApplication.getWareData().getKeyInputs().get(index).getKeyName()[position]);
                    bundle.putString("uid", uid);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    break;
            }
        }
    };

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
}

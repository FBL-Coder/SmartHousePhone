package cn.etsoft.smarthomephone.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import cn.etsoft.smarthomephone.MyApplication;
import cn.etsoft.smarthomephone.R;
import cn.etsoft.smarthomephone.adapter.SwipeAdapter;
import cn.etsoft.smarthomephone.pullmi.entity.WareDev;
import cn.etsoft.smarthomephone.weidget.CustomDialog_comment;

/**
 * Created by fbl on 16-11-17.
 */
public class Equipment_control extends Activity implements View.OnClickListener {
    private ListView equi_control;
    private TextView title, add_equi;
    private ImageView back;
    private Dev_Adapter adapter;
    private List<WareDev> devs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.equi_cont);

        initView();

        event();
    }

    @Override
    protected void onRestart() {
        if (adapter != null)
            adapter.notifyDataSetChanged();
        else {
            adapter = new Dev_Adapter();
            equi_control.setAdapter(adapter);
        }
        super.onRestart();
    }

    private void event() {
        title.setText("设 备 控 制");
        devs = MyApplication.getWareData().getDevs();
        adapter = new Dev_Adapter();
        equi_control.setAdapter(adapter);
        add_equi.setOnClickListener(this);
        back.setOnClickListener(this);

        equi_control.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(Equipment_control.this, Devs_Detail_Activity.class).putExtra("id",position));
            }
        });

        equi_control.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                CustomDialog_comment.Builder builder = new CustomDialog_comment.Builder(Equipment_control.this);
                builder.setTitle("提示 :");
                builder.setMessage("您确定要删除此设备吗？");
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        devs.remove(position);
                        if (adapter != null)
                            adapter.notifyDataSetChanged();
                        else {
                            adapter = new Dev_Adapter();
                            equi_control.setAdapter(adapter);
                        }

                        //----------服务器段交互
                        dialog.dismiss();
                    }
                });
                builder.create().show();
                return true;
            }
        });
    }

    private void initView() {
        equi_control = (ListView) findViewById(R.id.equi_cont_list);
        add_equi = (TextView) findViewById(R.id.add_equi);
        title = (TextView) findViewById(R.id.title_bar_tv_title);
        back = (ImageView) findViewById(R.id.title_bar_iv_back);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_bar_iv_back:
                finish();
                break;
            case R.id.add_equi:
                startActivity(new Intent(Equipment_control.this,Add_Dev_Activity.class));
                break;
        }
    }

    class Dev_Adapter extends BaseAdapter {

        @Override
        public int getCount() {
            return devs.size();
        }

        @Override
        public Object getItem(int position) {
            return devs.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int[] image = new int[]{R.drawable.kongtiao, R.drawable.tv_0,
                    R.drawable.jidinghe, R.drawable.dengguang, R.drawable.chuanglian};
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(Equipment_control.this).inflate(R.layout.equipment_listview_control_item, null);
                viewHolder = new ViewHolder();

                viewHolder.title = (TextView) convertView.findViewById(R.id.equipment_tv);
                viewHolder.image = (ImageView) convertView.findViewById(R.id.equipment_iv);

                convertView.setTag(viewHolder);
            } else
                viewHolder = (ViewHolder) convertView.getTag();

            viewHolder.title.setText(devs.get(position).getDevName());

            if (devs.get(position).getType() == 0)
                viewHolder.image.setImageResource(image[0]);
            else if (devs.get(position).getType() == 1)
                viewHolder.image.setImageResource(image[1]);
            else if (devs.get(position).getType() == 2)
                viewHolder.image.setImageResource(image[2]);
            else if (devs.get(position).getType() == 3)
                viewHolder.image.setImageResource(image[3]);
            else
                viewHolder.image.setImageResource(image[4]);

            return convertView;
        }

        public class ViewHolder {
            public TextView title;
            public ImageView image;
        }
    }
}

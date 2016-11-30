package cn.etsoft.smarthomephone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.etsoft.smarthomephone.MyApplication;
import cn.etsoft.smarthomephone.R;
import cn.etsoft.smarthomephone.pullmi.entity.IcLick_Tag;
import cn.etsoft.smarthomephone.pullmi.entity.WareAirCondDev;
import cn.etsoft.smarthomephone.pullmi.entity.WareCurtain;
import cn.etsoft.smarthomephone.pullmi.entity.WareKeyOpItem;
import cn.etsoft.smarthomephone.pullmi.entity.WareLight;
import cn.etsoft.smarthomephone.pullmi.entity.WareSetBox;
import cn.etsoft.smarthomephone.pullmi.entity.WareTv;
import cn.etsoft.smarthomephone.weidget.SwipeItemLayout;

/**
 * Created by Say GoBay on 2016/8/30.
 */
public class SwipeAdapter extends BaseAdapter {
    private Context mContext = null;
    private IClick_PZ mListener;
    private List<WareKeyOpItem> keyOpItems;
    String[] text;
    private int[] image = new int[]{R.drawable.kongtiao, R.drawable.tv,R.drawable.jidinghe,R.drawable.dengguang, R.drawable.chuanglian};

    public SwipeAdapter(Context context, List<WareKeyOpItem> lst, IClick_PZ listener) {
        this.mContext = context;
        mListener = listener;
        keyOpItems = lst;
//        System.out.println(lst.get(0).getDevId() +"---------"+lst.get(1).getDevId() +"---------"+lst.get(2).getDevId());
    }

    @Override
    public int getCount() {
        if (keyOpItems != null)
            return keyOpItems.size();
        else
            return 0;
    }

    @Override
    public Object getItem(int position) {

        if (keyOpItems != null)
            return keyOpItems.get(position);
        else
            return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View contentView, ViewGroup arg2) {
        ViewHolder viewHolder;
        if (contentView == null) {
            View contentView1 = LayoutInflater.from(mContext).inflate(R.layout.equipmentdeploy_listview_item, null);
            View contentView2 = LayoutInflater.from(mContext).inflate(R.layout.equipmentdeploy_listview_item_add, null);
            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) contentView1.findViewById(R.id.deploy_tv);
            viewHolder.deploy_iv = (ImageView) contentView1.findViewById(R.id.deploy_iv);
            viewHolder.choose = (TextView) contentView1.findViewById(R.id.deploy_choose);
            viewHolder.choose1 = (TextView) contentView1.findViewById(R.id.deploy_choose1);
            viewHolder.delete = (TextView) contentView2.findViewById(R.id.deploy_delete);
            contentView = new SwipeItemLayout(contentView1, contentView2, null, null);
            contentView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) contentView.getTag();
        }

        if (keyOpItems.get(position).getDevType() == 0) {
            List<WareAirCondDev> list = MyApplication.getWareData().getAirConds();
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getDev().getDevId() == keyOpItems.get(position).getDevId()) {
                    viewHolder.title.setText(list.get(i).getDev().getDevName() + "");
                }
            }
            text = new String[]{"未设置","开关", "模式", "风速", "温度+", "温度-"};
            viewHolder.choose.setText(text[keyOpItems.get(position).getKeyOpCmd() ]);
            viewHolder.deploy_iv.setImageResource(image[0]);
            if (keyOpItems.get(position).getKeyOp() == 1) {
                viewHolder.choose1.setText("弹起");
            } else if (keyOpItems.get(position).getKeyOp() == 0){
                viewHolder.choose1.setText("按下");
            }else {
                viewHolder.choose1.setText("未设置");
            }

        } else if (keyOpItems.get(position).getDevType() == 1) {
            List<WareTv> list = MyApplication.getWareData().getTvs();
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getDev().getDevId() == keyOpItems.get(position).getDevId()) {
                    viewHolder.title.setText(list.get(i).getDev().getDevName() + "");
                }
            }
            viewHolder.deploy_iv.setImageResource(image[1]);
            text = new String[]{};
            viewHolder.choose.setText("无操作");
            if (keyOpItems.get(position).getKeyOp() == 1) {
                viewHolder.choose1.setText("弹起");
            } else if (keyOpItems.get(position).getKeyOp() == 0){
                viewHolder.choose1.setText("按下");
            }else {
                viewHolder.choose1.setText("未设置");
            }
        } else if (keyOpItems.get(position).getDevType() == 2) {
            List<WareSetBox> list = MyApplication.getWareData().getStbs();
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getDev().getDevId() == keyOpItems.get(position).getDevId()) {
                    viewHolder.title.setText(list.get(i).getDev().getDevName() + "");
                }
            }
            text = new String[]{};
            viewHolder.deploy_iv.setImageResource(image[2]);
            viewHolder.choose.setText("无操作");
            if (keyOpItems.get(position).getKeyOp() == 1) {
                viewHolder.choose1.setText("弹起");
            } else if (keyOpItems.get(position).getKeyOp() == 0){
                viewHolder.choose1.setText("按下");
            }else {
                viewHolder.choose1.setText("未设置");
            }
        } else if (keyOpItems.get(position).getDevType() == 3) {
            List<WareLight> list = MyApplication.getWareData().getLights();
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getDev().getDevId() == keyOpItems.get(position).getDevId()) {
                    viewHolder.title.setText(list.get(i).getDev().getDevName() + "");
                }
            }
            viewHolder.deploy_iv.setImageResource(image[3]);
            text = new String[]{"未设置","打开", "关闭", "开关", "变暗", "变亮"};
            viewHolder.choose.setText(text[keyOpItems.get(position).getKeyOpCmd() ]);

            if (keyOpItems.get(position).getKeyOp() == 1) {
                viewHolder.choose1.setText("弹起");
            } else if (keyOpItems.get(position).getKeyOp() == 0){
                viewHolder.choose1.setText("按下");
            }else {
                viewHolder.choose1.setText("未设置");
            }
        } else if (keyOpItems.get(position).getDevType() == 4) {
            List<WareCurtain> list = MyApplication.getWareData().getCurtains();
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getDev().getDevId() == keyOpItems.get(position).getDevId()) {
                    viewHolder.title.setText(list.get(i).getDev().getDevName() + "");
                }
            }
            viewHolder.deploy_iv.setImageResource(image[4]);
            text = new String[]{"未设置","打开", "关闭", "停止", "开关停"};
            viewHolder.choose.setText(text[keyOpItems.get(position).getKeyOpCmd()]);
            if (keyOpItems.get(position).getKeyOp() == 1) {
                viewHolder.choose1.setText("弹起");
            } else if (keyOpItems.get(position).getKeyOp() == 0){
                viewHolder.choose1.setText("按下");
            }else {
                viewHolder.choose1.setText("未设置");
            }
        }

        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyOpItems.remove(position);
                notifyDataSetChanged();
            }
        });

        IcLick_Tag tag = new IcLick_Tag();
        tag.setPosition(position);
        tag.setType(keyOpItems.get(position).getDevType());
        tag.setText(text);

        viewHolder.choose.setOnClickListener(mListener);
        viewHolder.choose.setTag(tag);
        viewHolder.choose1.setOnClickListener(mListener);
        viewHolder.choose1.setTag(tag);
        viewHolder.delete.setOnClickListener(mListener);
        viewHolder.delete.setTag(tag);


        return contentView;
    }

    class ViewHolder {
        TextView choose, delete, choose1;
        TextView title;
        ImageView deploy_iv;
    }
}

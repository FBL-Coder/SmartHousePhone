package cn.etsoft.smarthomephone.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;

import cn.etsoft.smarthomephone.R;
import cn.etsoft.smarthomephone.UiUtils.Contacts;
import cn.etsoft.smarthomephone.utils.SharePreferenceUtil;

/**
 * Created by Say GoBay on 2016/9/2.
 * 安装引导页面
 */
public class GuideActivity extends Activity implements View.OnClickListener {
    private Button experience;
    private ViewPager viewPager;
    /**
     * 图片资源的id数组
     */
    private int[] mImageIds = new int[] { R.drawable.guide1, R.drawable.guide2, R.drawable.guide3 };
    private ArrayList<ImageView> mImageViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guid);
        //初始化控件
        initView();
        //设置显示信息
        setMessage();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        viewPager = (ViewPager) findViewById(R.id.guide_viewpager);
        experience = (Button) findViewById(R.id.experience);
        mImageViews = new ArrayList<ImageView>();
    }

    /**
     * 设置显示信息
     */
    private void setMessage() {

        // 根据图片个数，创建imageView的个数
        for (int i = 0; i < mImageIds.length; i++) {
            // 创建ImageView
            ImageView imageView = new ImageView(this);
            // 将图片设置给imageView
            imageView.setBackgroundResource(mImageIds[i]);
            // 将创建的imageView存放到集合或者数组中
            mImageViews.add(imageView);
        }
        viewPager.setAdapter(new ViewPagerAdapter());
        viewPager.setOnPageChangeListener(onPageChangeListener);
    }
    // viewpager界面切换监听
    ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        // 当界面切换完成调用的方法
        @Override
        public void onPageSelected(int position) {
            // 当切换到第三个界面，显示button按钮，第一和第二个界面不显示
            if (position == mImageViews.size() - 1) {
                // 显示button按钮
                experience.setVisibility(View.VISIBLE);
                experience.setOnClickListener(GuideActivity.this);
            } else {
                // 隐藏button按钮
                experience.setVisibility(View.INVISIBLE);
                // 取消button的点击事件
                experience.setOnClickListener(null);
            }
        }

        // 滑动的时候调用的方法
        @Override
        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels) {

        }

        // 滑动状态改变的时候
        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
    private class ViewPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return mImageViews.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = mImageViews.get(position);
            container.addView(imageView);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.experience:
                // 跳转主界面
                startActivity(new Intent(GuideActivity.this, HomeActivity.class));
                // 保存不是第一次进入的状态
                SharePreferenceUtil.saveBoolean(getApplicationContext(), Contacts.FIRST_ENTER, false);
                finish();
                break;
            default:
                break;
        }
    }
}

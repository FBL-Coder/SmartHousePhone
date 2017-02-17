package cn.etsoft.smarthomephone.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;

import cn.etsoft.smarthomephone.R;
import cn.etsoft.smarthomephone.UiUtils.Contacts;
import cn.etsoft.smarthomephone.utils.SharePreferenceUtil;

/**
 * Created by Say GoBay on 2016/9/2.
 * 可以删除的页面
 */
public class SplashActivity extends Activity {
    private RelativeLayout mRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //初始化控件
        initView();
        //设置动画
        setAnimation();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        mRoot = (RelativeLayout) findViewById(R.id.rel_splash_root);
    }

    /**
     * 设置动画
     */
    private void setAnimation() {
        // 1.旋转动画
        // fromDegrees : 开始的角度
        // toDegrees : 结束的角度
        // pivotXType, pivotXValue, pivotYType, pivotYValue : 旋转的类型和坐标
        RotateAnimation rotateAnimation = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        rotateAnimation.setDuration(1000);// 设置持续时间
        rotateAnimation.setFillAfter(true);// 保持结束的位置
        // 2.缩放动画
        // fromX, toX, fromY, toY : 表示从那里开始缩放，缩放到那里
        // pivotXType, pivotXValue, pivotYType, pivotYValue : 旋转的类型和坐标
        ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 0, 1,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        scaleAnimation.setDuration(1000);
        scaleAnimation.setFillAfter(true);
        // 3.渐变动画
        //fromAlpha, toAlpha : 从透明到不透明
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(1000);
        alphaAnimation.setFillAfter(true);
        //4.组合动画
        //shareInterpolator : 是否公用动画插补器，true：共用   false：不共用
        AnimationSet animationSet = new AnimationSet(false);
        //设置添加动画
        animationSet.addAnimation(rotateAnimation);
        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(alphaAnimation);
        //执行动画
        mRoot.startAnimation(animationSet);
        //动画设置监听
        animationSet.setAnimationListener(animationListener);
    }

    //动画监听
    Animation.AnimationListener animationListener = new Animation.AnimationListener() {

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {

            //跳转操作
            //第一次进入，跳转引导界面，不是第一次，跳转主界面,splash界面只是判断是否是第一次进入，更改操作放到其他界面
            boolean isFirst_enter = SharePreferenceUtil.getBoolean(getApplicationContext(), Contacts.FIRST_ENTER, true);
            if (isFirst_enter) {
                //跳转引导界面
                startActivity(new Intent(SplashActivity.this, GuideActivity.class));
            } else {
                //跳转主界面
                startActivity(new Intent(SplashActivity.this, HomeActivity.class));
            }
            //当跳转到其他界面的时候，点击返回键是直接退出程序，而不是跳回splash界面
            finish();
        }
    };
}

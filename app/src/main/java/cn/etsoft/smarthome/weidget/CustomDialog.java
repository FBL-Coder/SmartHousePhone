package cn.etsoft.smarthome.weidget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

public class CustomDialog extends Dialog {
    int layoutRes;
    Context context;
    public CustomDialog(Context context) {
        super(context);
        this.context = context;
    }
    /**
     * 自定义布局的构造方法
     * @param context
     * @param resLayout
     */
    public CustomDialog(Context context, int resLayout){
        super(context);
        this.context = context;
        this.layoutRes=resLayout;
    }
    /**
     * 自定义主题及布局的构造方法
     * @param context
     * @param theme
     * @param resLayout
     */
    public CustomDialog(Context context, int theme, int resLayout){
        super(context, theme);
        this.context = context;
        this.layoutRes=resLayout;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(layoutRes);
    }

    public View getView(){
        return View.inflate(context,layoutRes,null);
    }
}

package cn.semtec.community2.util;


import android.app.Service;
import android.os.Vibrator;

import cn.semtec.community2.MyApplication;

/**
 * 手机震动工具类
 *
 * @author Administrator
 */
public class VibratorUtil {
    private static Vibrator vib;

    /**
     * final Activity activity ：调用该方法的Activity实例 long milliseconds ：震动的时长，单位是毫秒
     * long[] pattern ：自定义震动模式 。数组中数字的含义依次是[静止时长，震动时长，静止时长，震动时长。。。]时长的单位是毫秒
     * boolean isRepeat ： 是否反复震动，如果是true，反复震动，如果是false，只震动一次
     */

    public static void Vibrate(long milliseconds) {
        if (vib == null) {
            vib = (Vibrator) MyApplication.instance.getSystemService(Service.VIBRATOR_SERVICE);
        }
        vib.vibrate(milliseconds);
    }

    public static void Vibrate(long[] pattern,
                               boolean isRepeat) {
        if (vib == null) {
            vib = (Vibrator) MyApplication.instance.getSystemService(Service.VIBRATOR_SERVICE);
        }
        vib.vibrate(pattern, isRepeat ? 1 : -1);
    }

}
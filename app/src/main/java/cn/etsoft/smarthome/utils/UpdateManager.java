package cn.etsoft.smarthome.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import cn.etsoft.smarthome.MyApplication;
import cn.etsoft.smarthome.R;
import cn.etsoft.smarthome.utils.HttpGetDataUtils.NewHttpPort;
import cn.etsoft.smarthome.weidget.CustomDialog_comment;
import cn.etsoft.smarthome.weidget.CustomDialog_upload;
import cn.etsoft.smarthome.weidget.NumberProgressBar;

/**
 * 版本更新
 */
public class UpdateManager {
    private Context mContext; //上下文

    private String apkUrl = NewHttpPort.DownLoad_Apk;//apk下载地址
    private static final String savePath = "/sdcard/ZNJJAPK/"; //apk保存到SD卡的路径
    private static final String saveFileName = savePath + "apkName.apk"; //完整路径名

    private NumberProgressBar mProgress; //下载进度条控件
    private static final int DOWNLOADING = 1; //表示正在下载
    private static final int DOWNLOADED = 2; //下载完毕
    private static final int DOWNLOAD_FAILED = 3; //下载失败
    private int progress; //下载进度
    private boolean cancelFlag = false; //取消下载标志位

    private int serverVersion; //从服务器获取的版本号
    private String VersionName; //从服务器获取的版本名
    private String Info; //从服务器获取的描述
    private int clientVersion; //客户端当前的版本号
    private String updateDescription = "是否现在更新"; //更新内容描述信息
    private boolean forceUpdate = true; //是否强制更新
    private Handler mHandler;
    private CustomDialog_comment alertDialog1;
    private CustomDialog_upload alertDialog2;

    /**
     * 构造函数
     */
    public UpdateManager(Context context, String serverVersion, String VersionName, String Info) {
        this.mContext = context;
        this.VersionName = VersionName;
        this.Info = Info;
        this.serverVersion = Integer.parseInt(serverVersion);


        PackageManager packageManager = context.getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(MyApplication.getContext().getPackageName(), 0);
            clientVersion = packInfo.versionCode;
            Log.i("UpAPP", "客户端版本号:" + clientVersion);
        } catch (PackageManager.NameNotFoundException e) {
            Log.i("UpAPP", "检查更新异常:" + e);
        }

        /**
         * 更新UI的handler
         */
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                switch (msg.what) {
                    case DOWNLOADING:
//                        mProgress.incrementProgressBy(progress);
                        mProgress.setProgress(progress);
                        break;
                    case DOWNLOADED:
                        if (alertDialog2 != null)
                            alertDialog2.dismiss();
                        installAPK();
                        break;
                    case DOWNLOAD_FAILED:
                        ToastUtil.showText("网络断开或更新异常，请稍候再试");
                        break;
                    default:
                        break;
                }
            }
        };
    }

    /**
     * 显示更新对话框
     */
    public void showNoticeDialog(final Handler handler) {
        //如果版本最新，则不需要更新
        if (serverVersion <= clientVersion) {
            Message message = handler.obtainMessage();
            message.what = 1;
            handler.sendMessage(message);
            return;
        }

        CustomDialog_comment.Builder dialog = new CustomDialog_comment.Builder(mContext);
        dialog.setTitle("发现新版本 ：" + VersionName);
        dialog.setMessage(updateDescription + "\n  描述：" + Info);
        dialog.setPositiveButton("更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                // TODO Auto-generated method stub
                arg0.dismiss();
                showDownloadDialog();
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Message message = handler.obtainMessage();
                message.what = 1;
                handler.sendMessage(message);
            }
        });

        alertDialog1 = dialog.create();
        alertDialog1.setCancelable(false);
        alertDialog1.show();
        TextView view = (TextView) alertDialog1.findViewById(R.id.hint);
        view.setVisibility(View.VISIBLE);

    }

    private NumberProgressBar.OnProgressBarListener listener;

    /**
     * 显示进度条对话框
     */
    public void showDownloadDialog() {
        alertDialog2 = new CustomDialog_upload(mContext, R.style.customDialog, R.layout.dialog_progressbar_layout);
        alertDialog2.show();
        mProgress = (NumberProgressBar) alertDialog2.findViewById(R.id.update_progress);
        alertDialog2.setCancelable(false);
        //下载apk
        downloadAPK();
    }

    /**
     * 下载apk的线程
     */
    public void downloadAPK() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    URL url = new URL(apkUrl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestProperty("Accept-Encoding", "identity");
                    conn.setRequestMethod("POST");
                    conn.connect();
                    System.setProperty("http.keepAlive", "false");

                    int length = conn.getContentLength();
                    InputStream is = conn.getInputStream();

                    File file = new File(savePath);
                    if (!file.exists()) {
                        file.mkdir();
                    }
                    String apkFile = saveFileName;
                    File ApkFile = new File(apkFile);
                    FileOutputStream fos = new FileOutputStream(ApkFile);


                    int count = 0;
                    byte buf[] = new byte[1024*50];

                    do {
                        int numread = is.read(buf);
                        count += numread;
                        progress = (int) (((float) count / length) * 100);
                        //更新进度
                        mHandler.sendEmptyMessage(DOWNLOADING);
                        if (numread <= 0) {
                            //下载完成通知安装
                            mHandler.sendEmptyMessage(DOWNLOADED);
                            break;
                        }
                        fos.write(buf, 0, numread);
                    } while (!cancelFlag); //点击取消就停止下载.
                    fos.close();
                    is.close();
                } catch (Exception e) {
//                    Log.i("UpApp", "更新应用异常:" + e);
//                    mHandler.sendEmptyMessage(DOWNLOAD_FAILED);
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 下载完成后自动安装apk
     */
    public void installAPK() {
        File apkFile = new File(saveFileName);
        if (!apkFile.exists()) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.parse("file://" + apkFile.toString()), "application/vnd.android.package-archive");
        mContext.startActivity(intent);
    }
}

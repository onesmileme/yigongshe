package com.weikan.app.update;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.alibaba.fastjson.JSON;
import com.weikan.app.R;
import platform.http.responsehandler.ConfusedJsonResponseHandler;
import platform.http.responsehandler.RootObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * ���汾����
 *
 * @author sahara
 */
public class UpdateManager {

    private static final String UPDATE_MUST = "1";// ǿ�Ƹ���
    private static final String UPDATE_MAN = "0";// �ֶ�����

    private static final int NEWAPK = 3;
    private static final int SHOWDIALOG = 0;
    /* ������ */
    private static final int DOWNLOAD = 1;
    /* ���ؽ��� */
    private static final int DOWNLOAD_FINISH = 2;

    private static final int DOWNLOAD_ERROR = 10;
    /* ���ر���·�� */
    private String mSavePath;
    /* ��¼���������� */
    private int progress;

    private Activity mContext;
    /* ���½����� */
    private ProgressBar mProgress;
    private Dialog mDownloadDialog;

    /**
     * ���µ�ַ
     */
    private String apkURL;
    /**
     * ��������
     */
    private String updateContents;
    /**
     * �Ƿ�ǿ�Ƹ��� 0:�� 1:��
     */

    private String updateType = UPDATE_MAN;// Ĭ���ֶ�����

    // �ж��ж��Ƿ���Լ���
    public boolean getIsCanContinue() {

        if (updateType.contains(UPDATE_MAN)) {
            return true;
        }
        return false;// ǿ�Ƹ������޷�����
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case NEWAPK:
                    if (flag != 0)
                        Toast.makeText(mContext, mContext.getResources().getString(R.string.soft_not_update_info_half_before),
                                Toast.LENGTH_SHORT).show();
                    break;
                case SHOWDIALOG:
                    if (msg.arg1 == 0) {
                        showNoticeDialog();
                    }
                    break;
                // ��������
                case DOWNLOAD:
                    // ���ý�����λ��
                    mProgress.setProgress(progress);
                    break;
                case DOWNLOAD_FINISH:
                    // ��װ�ļ�
                    if (msg.obj != null)
                        installApk(msg.obj.toString());
                    break;
                case 99:
                    // Tools.showToast(mContext,"��ǰ�������°汾!");
                    // Intent intent = new
                    // Intent(mContext,Home_Tab5_VersionCheck_Activity.class);
                    // mContext.startActivity(intent);
                    break;
                case 10:
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.soft_update_error), Toast.LENGTH_LONG);
                    break;
                default:
                    break;
            }
        }

        ;
    };

    int flag = 0;

    public UpdateManager(Activity context, int flag_) {
        flag = flag_;
        this.mContext = context;
    }

    // ��õ�ǰ�汾��
    private int getVersionCode(Context context) {
        int versionCode = 0;
        try {
            // ��ȡ����汾�ţ���ӦAndroidManifest.xml��android:versionCode
            versionCode = context.getPackageManager().getPackageInfo("com.hunwaterplatform.app", 0).versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    public void checkUpdate() {
        isShow = true;
        isShowUpdate();
    }

    public void firstCheckUpdate() {
        isShow = false;
        isShowUpdate();
    }

    /**
     * ����������
     */
    public boolean isShow = false;

    public void isShowUpdate() {

//        Uri.Builder builder = new Uri.Builder();
//        builder.scheme(URLDefine.SCHEME);
//        builder.encodedAuthority(URLDefine.HOST_API);
//        builder.encodedPath("1/version/");
//
//        Map<String, String> params = new HashMap<String, String>();
//
//        HttpUtils.get(builder.build().toString(), params, httpHandler);
    }


    private ConfusedJsonResponseHandler httpHandler = new ConfusedJsonResponseHandler<RootObject>() {

        @Override
        public void success(@NonNull RootObject data) {
            try {
                com.alibaba.fastjson.JSONObject resultJsonObject =JSON.parseObject(data.data) ;

                if (resultJsonObject == null
                        || resultJsonObject.getString("version") == null
                        || resultJsonObject.getString("url") == null) {
                    return;
                }
                int serviceCode = Integer.parseInt(resultJsonObject.getString("version"));
                int versionCode = getVersionCode(mContext);
                if (serviceCode > versionCode) {
                    apkURL = resultJsonObject.getString("url");
                    if (apkURL != null) {
                        Message msg = new Message();
                        msg.what = SHOWDIALOG;
                        msg.arg1 = 0;
                        mHandler.sendMessage(msg);
                    }
                } else {
                    if (isShow) {
                        Toast.makeText(mContext, R.string.soft_update_no, Toast.LENGTH_LONG).show();
                    }
                }
            } catch (com.alibaba.fastjson.JSONException e) {
                e.printStackTrace();
            } catch (NumberFormatException e) {

            }
        }
    };


    /**
     * 显示软件更新对话框
     */
    private void showNoticeDialog() {
        // 构造对话框
        AlertDialog.Builder builder = new Builder(mContext);
        builder.setTitle(R.string.soft_update_title);
        if (!TextUtils.isEmpty(updateContents)) {
            builder.setMessage(updateContents);
        } else {
            builder.setMessage("检测到新的版本!");
        }

        builder.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                    mContext.finish();
                }
                return false;
            }
        });

        // 更新
        builder.setPositiveButton(R.string.soft_update_updatebtn, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                // 显示下载对话框
                showDownloadDialog();
            }
        });

        if (updateType.contains(UPDATE_MAN)) {// 手动
            // 稍后更新
            builder.setNegativeButton(R.string.soft_update_later, new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }
        Dialog noticeDialog = builder.create();
        noticeDialog.setCanceledOnTouchOutside(false);
        noticeDialog.show();
    }

    /**
     * 显示软件下载对话框
     */
    private void showDownloadDialog() {
        // 构造软件下载对话框
        AlertDialog.Builder builder = new Builder(mContext);

        builder.setTitle(R.string.soft_updating);
        // 给下载对话框增加进度条
        final LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.softupdate_progress, null);
        mProgress = (ProgressBar) v.findViewById(R.id.update_progress);
        builder.setView(v);
        // 取消更新
        builder.setNegativeButton(R.string.soft_update_cancel, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                // 设置取消状态
                // cancelUpdate = true;
                if (updateType.contains(UPDATE_MUST)) {
                    mContext.finish();
                }
            }
        });
        mDownloadDialog = builder.create();
        mDownloadDialog.setCanceledOnTouchOutside(false);
        mDownloadDialog.setCancelable(false);
        mDownloadDialog.show();
        // 现在文件
        downloadApk();
    }

    /**
     * 下载apk文件
     */
    private void downloadApk() {
        // 启动新线程下载软件
        new downloadApkThread().start();
    }

    /**
     * 下载文件线程
     */
    private class downloadApkThread extends Thread {
        @Override
        public void run() {
            if (TextUtils.isEmpty(apkURL))
                return;

            try {
                // 判断SD卡是否存在，并且是否具有读写权限
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    // 获得存储卡的路径
                    if (apkURL == null)
                        return;
                    String sdpath = Environment.getExternalStorageDirectory() + "/";
                    mSavePath = sdpath + "download";
                    URL url = new URL(apkURL);
                    int count = 0;
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(5000);
                    int apklength = connection.getContentLength();
                    FileOutputStream fileOutputStream = null;
//					String filaname = apkURL.substring(apkURL.lastIndexOf("/") + 1) + ".apk";
                    File file = new File(mSavePath);

                    // 判断文件目录是否存在
                    if (!file.exists()) {
                        file.mkdir();
                    }
                    InputStream inputStream;
                    File apkFile = new File(mSavePath, "lanjingapp.apk");
                    if (connection.getResponseCode() == 200) {
                        inputStream = connection.getInputStream();

                        if (inputStream != null) {
                            fileOutputStream = new FileOutputStream(apkFile);
                            byte[] buffer = new byte[1024];
                            int length = 0;

                            while ((length = inputStream.read(buffer)) != -1) {
                                count += length;
                                // // 计算进度条位置
                                progress = (int) (((float) count / apklength) * 100);
                                // // 更新进度
                                mHandler.sendEmptyMessage(DOWNLOAD);
                                fileOutputStream.write(buffer, 0, length);

                            }
                            fileOutputStream.close();
                            fileOutputStream.flush();
                        }
                        inputStream.close();
                        if (count == apklength) {
                            Message msg = new Message();
                            msg.what = DOWNLOAD_FINISH;
                            msg.obj = apkFile.getPath();
                            mHandler.sendMessage(msg);
                        } else {

                        }
                    }

                }
            } catch (MalformedURLException e) {
                mHandler.sendEmptyMessage(DOWNLOAD_ERROR);

                e.printStackTrace();
            } catch (IOException e) {
                mHandler.sendEmptyMessage(DOWNLOAD_ERROR);
                e.printStackTrace();
            }
            // 取消下载对话框显示
            mDownloadDialog.dismiss();
        }
    }

    ;

    /**
     * 安装APK文件
     */
    private void installApk(String filepath) {
        File apkfile = new File(filepath);
        if (!apkfile.exists()) {
            return;
        }
        // 通过Intent安装APK文件
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
        mContext.startActivity(i);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

}

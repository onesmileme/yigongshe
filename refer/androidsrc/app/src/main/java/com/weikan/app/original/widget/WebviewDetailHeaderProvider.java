package com.weikan.app.original.widget;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.http.SslError;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.*;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;
import com.weikan.app.WebshellActivity;
import com.weikan.app.original.bean.OriginalDetailItem;
import com.weikan.app.push.PushDefine;
import com.weikan.app.push.PushManager;
import com.weikan.app.util.LToast;
import com.weikan.app.util.StorageUtil;
import com.weikan.app.util.URLDefine;
import com.weikan.app.widget.photoviewpager.BitmapPersistence;
import com.weikan.app.widget.photoviewpager.PhotoViewPagerActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Webview实现详情页头部
 * Created by liujian on 16/3/21.
 */
public class WebviewDetailHeaderProvider extends AbsDetailHeaderProvider<OriginalDetailItem> {
    private Context context;
    private String[] menuItems = new String[]{"保存到手机"};

    @Override
    public View getDetailHeaderView(final Context context, View originalView, ViewGroup parent, OriginalDetailItem object) {
        this.context = context;
        if (originalView == null) {
            originalView = new WebView(context);
        }
        WebView webView = (WebView) originalView;
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        if (object != null) {
            String webContent = object.content;
            if (!TextUtils.isEmpty(webContent)) {
                webView.loadDataWithBaseURL("about:blank", webContent, "text/html", "utf-8", null);
            } else {
                String url = "about:blank";
                if (!TextUtils.isEmpty(object.url)) {
                    url = object.url;
                }
//                webView.loadDataWithBaseURL("http://mp.weixin.qq.com", webContent, "text/html", "utf-8", null);
                webView.loadUrl(url);
            }
        }
        webView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                WebView.HitTestResult result = ((WebView) view).getHitTestResult();
                int type = result.getType();
                // 增加图片长按保存
                if (type == WebView.HitTestResult.IMAGE_TYPE
                        || type == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
                    final String imgurl = result.getExtra();
                    new AlertDialog.Builder(context)
                            .setItems(menuItems, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case 0:
                                            final ProgressDialog pd = new ProgressDialog(context);
                                            pd.show();
                                            RequestCreator requestCreator = Picasso.with(context).load(Uri.parse(imgurl));
                                            requestCreator.into(new Target() {
                                                @Override
                                                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                                                    File file = StorageUtil.saveImageToGallery(context, bitmap);
                                                    LToast.showToast("图片已保存到" + file.getAbsolutePath());
                                                    pd.dismiss();
                                                }

                                                @Override
                                                public void onBitmapFailed(Drawable errorDrawable) {
                                                    LToast.showToast("图片保存失败。");
                                                    pd.dismiss();
                                                }

                                                @Override
                                                public void onPrepareLoad(Drawable placeHolderDrawable) {
                                                }
                                            });
                                            break;
                                    }
                                }
                            })
                            .show();
                }
                return false;
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {

        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // Log.d("baidumap", "shouldOverrideUrlLoading url:"+url);
                if (url == null) {
                    return false;
                }

                if (url.startsWith("tel") || url.startsWith("mailto")) {
                    Uri uri = Uri.parse(url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    context.startActivity(intent);
                    return true;
                }

                if (url.startsWith("http://") || url.startsWith("https://")) {
                    // h5页面
                    Intent intent = new Intent(context, WebshellActivity.class);
                    intent.putExtra(URLDefine.URL, url);
                    context.startActivity(intent);
                    return true;
                }

                if (url.startsWith(PushDefine.SCHEMA)) {
                    // 通用协议
                    PushManager.getInstance().executePushJump(context, url);
                    return true;
                }

                if (url.startsWith("kankaninternal://")) {
                    // android内部自己用的协议
                    if (url.startsWith("kankaninternal://imgclick")) {
                        showBigImages(url);
                    }
                    return true;
                }

                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                addImageClickListner(view);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }
        });
        return originalView;
    }

    @Override
    public void onPause(View view) {
        try {
            ((WebView) view).getClass().getMethod("onPause").invoke(view, (Object[]) null);
        } catch (Exception e) {

        }
        super.onPause(view);
    }

    @Override
    public void onResume(View view) {
        try {
            ((WebView) view).getClass().getMethod("onResume").invoke(view, (Object[]) null);
        } catch (Exception e) {

        }
        super.onResume(view);
    }


    /**
     * 注入webview，遍历所有的img节点，并添加onclick函数，
     * 函数的功能是在图片点击的时候调用url跳转，传递图片的url，方便截获处理
     */
    private void addImageClickListner(WebView webView) {
        webView.loadUrl("javascript:(function(){" +
                "var objs = document.getElementsByTagName(\"img\"); " +
                "var urls =\"\";" +
                "for(var i=0;i<objs.length;i++)" +
                "{"
                + "objs[i].i = i;"
                + "if(i==0){"
                + "  urls = urls +objs[i].src;"
                + "}else{"
                + "  urls = urls+\",\"+objs[i].src;"
                + "}"
                + "if(!objs[i].onclick) {"
                + "    objs[i].onclick=function()  " +
                "    {  "
                + "        location.href = 'kankaninternal://imgclick?urls='+urls+'&index='+this.i;  " +
                "    }  " +
                "}}" +
                "})()");
    }

    private void showBigImages(String url) {
        if (url != null) {
            String[] strings = url.split("\\?");
            if (strings.length == 2) {
                String[] items = strings[1].split("&");
                int index = -1;
                ArrayList<String> urls = new ArrayList<>();

                for (String s : items) {
                    String[] kv = s.split("=");
                    if (kv.length == 2) {
                        if (kv[0].equals("index")) {
                            index = Integer.parseInt(kv[1]);
                        } else if (kv[0].equals("urls")) {
                            Collections.addAll(urls, kv[1].split(","));
                        }
                    }
                }

                if (urls.size() != 0 && index >= 0 && index < urls.size() && context != null) {
                    BitmapPersistence.getInstance().clean();
                    BitmapPersistence.getInstance().mDrawableUrl.addAll(urls);
                    Intent intent = new Intent(context, PhotoViewPagerActivity.class);
                    intent.putExtra("bitmaps_index", index);
                    context.startActivity(intent);
                }

            }
        }
    }

}

package com.weikan.app.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.StatFs;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Downloader;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;

/**
 * Created with IntelliJ IDEA.
 * User: liujian06
 * Date: 2015/1/25
 * Time: 19:40
 */
public class ImageLoaderUtil {
    private static final int KEY_PADDING = 50; // Determined by exact science.
    static final char KEY_SEPARATOR = '\n';
    private static MyOKHttpDownloader downloader;
    private static LruCache memoryCache;
    private static Picasso picasso;

    /**
     * 初始化imageloader，必须在程序启动的时候调用
     *
     * @param context
     */
    public static void initImageLoaderUtil(Context context) {
        memoryCache = new LruCache(context);
        downloader = new MyOKHttpDownloader(context);
        picasso = new Picasso.Builder(context)
                .memoryCache(memoryCache)
                .downloader(downloader)
                .build();
    }

    /**
     * 设置图片后url会放在tag中，再次设置判断url一样就不会重复刷新
     *
     * @param ivIcon
     * @param url
     */
    public static void updateImageBetweenUrl(final ImageView ivIcon, final String url) {
        updateImageBetweenUrl(ivIcon, url, 0);
    }

    public static void updateImageBetweenUrl(final ImageView ivIcon, final String url, final int defaultid) {
        updateImageBetweenUrl(ivIcon, url, defaultid, 0);

    }

    public static void updateImageBetweenUrl(final ImageView ivIcon, final String url, final int defaultid, final int errorid) {
        if (picasso == null) {
            return;
        }
        if (ivIcon == null) {
            return;
        }
        // url 为空，直接设置为默认图
        if (TextUtils.isEmpty(url)) {
            if (defaultid > 0) {
                ivIcon.setImageResource(defaultid);
            }
            ivIcon.setTag(null);
            return;
        }
        // 展现过的url一样，直接返回
        String innerUrl = ivIcon.getTag() != null ? (String) ivIcon.getTag() : "";
        if (innerUrl.equals(url)) {
            return;
        }

        RequestCreator rc = picasso.load(url);
        if (defaultid > 0) {
            rc.placeholder(defaultid);
        }
        if (errorid > 0) {
            rc.error(errorid);
        } else if (defaultid > 0) {
            rc.error(defaultid);
        }

        // 判断ImageView如果已经有固定大小，就用picasso的fit和centercrop，如果没有固定大小，就不能用
        if ((ivIcon.getHeight() != 0 && ivIcon.getWidth() != 0) || (ivIcon.getLayoutParams() != null && ivIcon.getLayoutParams().height > 0 && ivIcon.getLayoutParams().width > 0)) {
            switch (ivIcon.getScaleType()) {
                case FIT_XY:
                    rc.fit();
                    break;
                case CENTER_CROP:
                    rc.fit();
                    rc.centerCrop();
                    break;
                case CENTER_INSIDE:
                    rc.fit();
                    rc.centerInside();
                    break;
                default:
                    rc.fit();
                    break;
            }
        }
        rc.config(Bitmap.Config.RGB_565)
                .into(ivIcon, new Callback() {
                    @Override
                    public void onSuccess() {
                        ivIcon.setTag(url);
                    }

                    @Override
                    public void onError() {

                    }
                });
    }


    public static void updateImage(ImageView ivIcon, String url) {
        updateImage(ivIcon, url, 0);
    }

    public static void updateImage(final ImageView ivIcon, final String url, final int defaultid) {
        updateImage(ivIcon, url, defaultid, 0, null);

    }

    public static void updateImage(final ImageView ivIcon, final String url, final int defaultid, final int errorid, Callback callback) {
        if (picasso == null || TextUtils.isEmpty(url)) {
            return;
        }
        if (ivIcon == null) {
            return;
        }

        if (TextUtils.isEmpty(url)) {
            if (defaultid > 0) {
                ivIcon.setImageResource(defaultid);
            }
            return;
        }

        RequestCreator rc = picasso.load(url);
        if (defaultid > 0) {
            rc.placeholder(defaultid);
        }
        if (errorid > 0) {
            rc.error(errorid);
        } else if (defaultid > 0) {
            rc.error(defaultid);
        }

        // 判断ImageView如果已经有固定大小，就用picasso的fit和centercrop，如果没有固定大小，就不能用
        if ( (ivIcon.getHeight() != 0 && ivIcon.getWidth() != 0) || (ivIcon.getLayoutParams() != null && ivIcon.getLayoutParams().height != ViewGroup.LayoutParams.WRAP_CONTENT && ivIcon.getLayoutParams().width != ViewGroup.LayoutParams.WRAP_CONTENT)) {
            switch (ivIcon.getScaleType()) {
                case FIT_XY:
                    rc.fit();
                    break;
                case CENTER_CROP:
                    rc.fit();
                    rc.centerCrop();
                    break;
                case CENTER_INSIDE:
                    rc.fit();
                    rc.centerInside();
                    break;
                default:
                    rc.fit();
                    break;
            }
        }
        rc.config(Bitmap.Config.RGB_565)
                .into(ivIcon, callback != null ? callback :new Callback() {
                    @Override
                    public void onSuccess() {
                        ivIcon.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError() {
                    }
                });
    }

    public static void loadImageCustom(final String url, final Target target) {
        if (picasso == null || TextUtils.isEmpty(url)) {
            return;
        }

        if (TextUtils.isEmpty(url)) {
            return;
        }

        RequestCreator rc = picasso.load(url);
        rc.into(target);
    }

    /**
     * 下载并缓存图片，不进行任何显示
     *
     * @param uri
     */
    public static void loadImage(String uri) {
        loadImage(uri, null);
    }

    /**
     * 下载并缓存图片，不进行任何显示
     *
     * @param uri
     */

    public static void loadImage(String uri, Callback callback) {
        if (picasso == null || TextUtils.isEmpty(uri)) {
            return;
        }

        picasso.load(uri).fetch(callback);
    }

    /**
     * 判断图片是否已经缓存，内存或者磁盘有缓存都会返回true
     *
     * @param uri
     * @return
     */
    public static boolean isImageAvailableInCache(String uri) {
        if (picasso == null || TextUtils.isEmpty(uri)) {
            return false;
        }

        com.squareup.picasso.Request req = new com.squareup.picasso.Request.Builder(Uri.parse(uri)).build();
        String cacheKey = createKey(req, new StringBuilder());

        if (memoryCache.get(cacheKey) != null) {
            return true;
        }

        try {
            Iterator<String> diskUrls = downloader.getClient().cache().urls();
            while (diskUrls.hasNext()) {
                String tmp = diskUrls.next();

                if (tmp.equals(uri)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }


    /**
     * 计算picasso的cache所用的key，从picasso源代码扣出来的
     *
     * @param data
     * @param builder
     * @return
     */
    static String createKey(com.squareup.picasso.Request data, StringBuilder builder) {
        if (data.stableKey != null) {
            builder.ensureCapacity(data.stableKey.length() + KEY_PADDING);
            builder.append(data.stableKey);
        } else if (data.uri != null) {
            String path = data.uri.toString();
            builder.ensureCapacity(path.length() + KEY_PADDING);
            builder.append(path);
        } else {
            builder.ensureCapacity(KEY_PADDING);
            builder.append(data.resourceId);
        }
        builder.append(KEY_SEPARATOR);

        if (data.rotationDegrees != 0) {
            builder.append("rotation:").append(data.rotationDegrees);
            if (data.hasRotationPivot) {
                builder.append('@').append(data.rotationPivotX).append('x').append(data.rotationPivotY);
            }
            builder.append(KEY_SEPARATOR);
        }
        if (data.hasSize()) {
            builder.append("resize:").append(data.targetWidth).append('x').append(data.targetHeight);
            builder.append(KEY_SEPARATOR);
        }
        if (data.centerCrop) {
            builder.append("centerCrop").append(KEY_SEPARATOR);
        } else if (data.centerInside) {
            builder.append("centerInside").append(KEY_SEPARATOR);
        }

        if (data.transformations != null) {
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = data.transformations.size(); i < count; i++) {
                builder.append(data.transformations.get(i).key());
                builder.append(KEY_SEPARATOR);
            }
        }

        return builder.toString();
    }

    /**
     * picasso 用 okhttp 下载和缓存图片
     */
    static class MyOKHttpDownloader implements Downloader {
        static final int DEFAULT_READ_TIMEOUT_MILLIS = 20 * 1000; // 20s
        static final int DEFAULT_WRITE_TIMEOUT_MILLIS = 20 * 1000; // 20s
        static final int DEFAULT_CONNECT_TIMEOUT_MILLIS = 15 * 1000; // 15s
        private static final String PICASSO_CACHE = "picasso-cache";
        private static final int MIN_DISK_CACHE_SIZE = 5 * 1024 * 1024; // 5MB
        private static final int MAX_DISK_CACHE_SIZE = 50 * 1024 * 1024; // 50MB

        private static OkHttpClient defaultOkHttpClient(okhttp3.Cache cache) {
            OkHttpClient.Builder b = new OkHttpClient.Builder();
            b.connectTimeout(DEFAULT_CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
            b.readTimeout(DEFAULT_READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
            b.writeTimeout(DEFAULT_WRITE_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
            if (cache != null) {
                b.cache(cache);
            }
            return b.build();
        }

        private final OkHttpClient client;

        static File createDefaultCacheDir(Context context) {
            File cache = new File(context.getApplicationContext().getCacheDir(), PICASSO_CACHE);
            if (!cache.exists()) {
                //noinspection ResultOfMethodCallIgnored
                cache.mkdirs();
            }
            return cache;
        }


        static long calculateDiskCacheSize(File dir) {
            long size = MIN_DISK_CACHE_SIZE;

            try {
                StatFs statFs = new StatFs(dir.getAbsolutePath());
                long available = ((long) statFs.getBlockCount()) * statFs.getBlockSize();
                // Target 2% of the total space.
                size = available / 50;
            } catch (IllegalArgumentException ignored) {
            }

            // Bound inside min/max size for disk cache.
            return Math.max(Math.min(size, MAX_DISK_CACHE_SIZE), MIN_DISK_CACHE_SIZE);
        }

        /**
         * Create new downloader that uses OkHttp. This will install an image cache into your application
         * cache directory.
         */
        public MyOKHttpDownloader(final Context context) {
            this(createDefaultCacheDir(context));
        }

        /**
         * Create new downloader that uses OkHttp. This will install an image cache into the specified
         * directory.
         *
         * @param cacheDir The directory in which the cache should be stored
         */
        public MyOKHttpDownloader(final File cacheDir) {
            this(cacheDir, calculateDiskCacheSize(cacheDir));
        }

        /**
         * Create new downloader that uses OkHttp. This will install an image cache into your application
         * cache directory.
         *
         * @param maxSize The size limit for the cache.
         */
        public MyOKHttpDownloader(final Context context, final long maxSize) {
            this(createDefaultCacheDir(context), maxSize);
        }

        /**
         * Create new downloader that uses OkHttp. This will install an image cache into the specified
         * directory.
         *
         * @param cacheDir The directory in which the cache should be stored
         * @param maxSize  The size limit for the cache.
         */
        public MyOKHttpDownloader(final File cacheDir, final long maxSize) {
            this(defaultOkHttpClient(new okhttp3.Cache(cacheDir, maxSize)));
        }

        /**
         * Create a new downloader that uses the specified OkHttp instance. A response cache will not be
         * automatically configured.
         */
        public MyOKHttpDownloader(OkHttpClient client) {
            this.client = client;
        }

        protected final OkHttpClient getClient() {
            return client;
        }

        @Override
        public Response load(Uri uri, int networkPolicy) throws IOException {
            CacheControl cacheControl = null;
            if (networkPolicy != 0) {
                if (NetworkPolicy.isOfflineOnly(networkPolicy)) {
                    cacheControl = CacheControl.FORCE_CACHE;
                } else {
                    CacheControl.Builder builder = new CacheControl.Builder();
                    if (!NetworkPolicy.shouldReadFromDiskCache(networkPolicy)) {
                        builder.noCache();
                    }
                    if (!NetworkPolicy.shouldWriteToDiskCache(networkPolicy)) {
                        builder.noStore();
                    }
                    cacheControl = builder.build();
                }
            }

            Request.Builder builder = new Request.Builder().url(uri.toString());
            if (cacheControl != null) {
                builder.cacheControl(cacheControl);
            }

            okhttp3.Response response = client.newCall(builder.build()).execute();
            int responseCode = response.code();
            if (responseCode >= 300) {
                response.body().close();
                throw new ResponseException(responseCode + " " + response.message(), networkPolicy,
                        responseCode);
            }

            boolean fromCache = response.cacheResponse() != null;

            ResponseBody responseBody = response.body();
            return new Response(responseBody.byteStream(), fromCache, responseBody.contentLength());
        }

        @Override
        public void shutdown() {
            okhttp3.Cache cache = client.cache();
            if (cache != null) {
                try {
                    cache.close();
                } catch (IOException ignored) {
                }
            }
        }
    }


}

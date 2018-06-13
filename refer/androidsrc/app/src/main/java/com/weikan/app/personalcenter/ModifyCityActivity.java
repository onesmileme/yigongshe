package com.weikan.app.personalcenter;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.weikan.app.R;
import com.weikan.app.base.BaseActivity;
import com.weikan.app.common.widget.SimpleNavigationView;
import com.weikan.app.personalcenter.adapter.ModifyCityAdapter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import rx.functions.Action1;

/**
 * abstract of class/interface and so on
 *
 * @author kailun on 15/12/14
 */
public class ModifyCityActivity extends BaseActivity {

    private static final String BUNDLE_CITY = "city";
    private static final String BUNDLE_RESULT = "result";
    private static final int REQUEST_MODIFY_CITY = 2002;

    SimpleNavigationView navigation;
    ListView lv;

    @NonNull
    // SimpleProgress simpleProgress = new SimpleProgress(this);
    ModifyCityAdapter adapter = new ModifyCityAdapter(this);
    LoadCitiesTask task = new LoadCitiesTask();

    public static void newActivityForResult(Activity activity, @NonNull String city) {
        Intent intent = new Intent(activity, ModifyCityActivity.class);
        intent.putExtra(BUNDLE_CITY, city);
        activity.startActivityForResult(intent, REQUEST_MODIFY_CITY);
    }

    public static boolean processActivityResult(int requestCode,
                                                int resultCode,
                                                @Nullable Intent data,
                                                Action1<String> callback) {
        if (requestCode == REQUEST_MODIFY_CITY && resultCode == RESULT_OK) {
            if (data != null) {
                String result = data.getStringExtra(BUNDLE_RESULT);
                callback.call(result);
            }
            return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_city);
        parseIntent();
        initViews();
        task.execute();
    }

    private void parseIntent() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String city = extras.getString(BUNDLE_CITY, "");
            adapter.setChooseCity(city);
        }
    }

    private void initViews() {
        navigation = (SimpleNavigationView) findViewById(R.id.navigation);
        navigation.setLeftOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        navigation.setRightOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onConfirm();
            }
        });

        lv = (ListView) findViewById(R.id.lv);
        lv.setAdapter(adapter);
    }

    private void onConfirm() {

        final String chooseCity = adapter.getChooseCity();
        if (TextUtils.isEmpty(chooseCity)) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(BUNDLE_RESULT, chooseCity);
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * 读取Assets的citylist.json
     *
     * @return 返回为字符串
     */
    private String readAssets() {
        String str = "";

        InputStream is = null;
        ByteArrayOutputStream os = null;
        try {
            is = getResources().getAssets().open("citylist.json");
            os = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];

            int l = is.read(buffer);
            while (l > 0) {
                os.write(buffer, 0, l);
                l = is.read(buffer);
            }

            str = os.toString("utf8");

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception ignored) {
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (Exception ignored) {
                }
            }
        }

        return str;
    }

    private Map<Character, List<String>> parseCityList(@NonNull String s) {
        // 使用有序的TreeMap
        Map<Character, List<String>> cities = new TreeMap<>();

        JSONObject obj = (JSONObject) JSONObject.parse(s);

        for (char c = 'A'; c <= 'Z'; c++) {
            List<String> list = parseArray(obj, c);

            // 只放非空的首字母
            if (list.size() != 0) {
                cities.put(c, list);
            }
        }

        return cities;
    }

    private List<String> parseArray(JSONObject obj, Character c) {
        List<String> list = new ArrayList<>();

        JSONArray arr = (JSONArray) obj.get(Character.toString(c));
        if (arr != null) {
            for (Object anArr : arr) {
                String s = null;
                try {
                    s = anArr.toString();
                } catch (JSONException ignored) {
                }

                if (s != null) {
                    list.add(s);
                }
            }
        }

        return list;
    }

    /**
     * 读取Assets内城市列表的异步任务
     */
    private class LoadCitiesTask extends AsyncTask<Object, Object, Object> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // simpleProgress.show();
        }

        @Override
        protected Object doInBackground(Object... params) {

            String assetsContent = readAssets();
            Map<Character, List<String>> stringListMap = parseCityList(assetsContent);

            return stringListMap;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            // noinspection unchecked
            Map<Character, List<String>> map = (Map<Character, List<String>>) o;
            if (map != null) {
                adapter.setCityList(map);
                adapter.notifyDataSetChanged();
            }
            // simpleProgress.dismiss();
        }
    }
}

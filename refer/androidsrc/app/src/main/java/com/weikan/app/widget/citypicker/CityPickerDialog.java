package com.weikan.app.widget.citypicker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import com.weikan.app.R;
import com.weikan.app.widget.wheel.widget.OnWheelChangedListener;
import com.weikan.app.widget.wheel.widget.WheelView;
import com.weikan.app.widget.wheel.widget.adapters.ArrayWheelAdapter;

/**
 * Created with IntelliJ IDEA.
 * User: liujian06
 * Date: 2015/4/12
 * Time: 21:58
 */
public class CityPickerDialog extends CityPickerModel implements OnWheelChangedListener {
    private WheelView mViewProvince;
    private WheelView mViewCity;
    private WheelView mViewDistrict;
    private Context context;
    private CityPickerListener listener;

    public CityPickerDialog(Context c){
        context = c;
    }

    public void setListener( CityPickerListener l){
        listener = l;
    }

    public void show(){
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.dialog_citypicker, null);

        setUpViews(layout);
        setUpListener();
        setUpData();

        new AlertDialog.Builder(context).setTitle("选择城市").setView(layout)

                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (listener != null) {
                            listener.onSelected(mCurrentProviceName, mCurrentCityName, mCurrentDistrictName, mCurrentZipCode);
                        }
                    }
                })
                .setNegativeButton("取消", null).show();
    }


    private void setUpViews(View view) {
        mViewProvince = (WheelView) view.findViewById(R.id.id_province);
        mViewCity = (WheelView)view. findViewById(R.id.id_city);
        mViewDistrict = (WheelView) view.findViewById(R.id.id_district);
    }

    private void setUpListener() {
        mViewProvince.addChangingListener(this);
        mViewCity.addChangingListener(this);
        mViewDistrict.addChangingListener(this);
    }

    private void setUpData() {
        initProvinceDatas(context.getAssets());
        mViewProvince.setViewAdapter(new ArrayWheelAdapter<String>(context, mProvinceDatas));
        mViewProvince.setVisibleItems(7);
        mViewCity.setVisibleItems(7);
        mViewDistrict.setVisibleItems(7);
        updateCities();
        updateAreas();
    }

    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        // TODO Auto-generated method stub
        if (wheel == mViewProvince) {
            updateCities();
        } else if (wheel == mViewCity) {
            updateAreas();
        } else if (wheel == mViewDistrict) {
            mCurrentDistrictName = mDistrictDatasMap.get(mCurrentCityName)[newValue];
            mCurrentZipCode = mZipcodeDatasMap.get(mCurrentDistrictName);
        }
    }

    /**
     * ��ݵ�ǰ���У�������WheelView����Ϣ
     */
    private void updateAreas() {
        int pCurrent = mViewCity.getCurrentItem();
        mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName)[pCurrent];
        String[] areas = mDistrictDatasMap.get(mCurrentCityName);

        if (areas == null) {
            areas = new String[] { "" };
        }
        mViewDistrict.setViewAdapter(new ArrayWheelAdapter<String>(context, areas));
        mViewDistrict.setCurrentItem(0);
    }

    /**
     * ��ݵ�ǰ��ʡ��������WheelView����Ϣ
     */
    private void updateCities() {
        int pCurrent = mViewProvince.getCurrentItem();
        mCurrentProviceName = mProvinceDatas[pCurrent];
        String[] cities = mCitisDatasMap.get(mCurrentProviceName);
        if (cities == null) {
            cities = new String[] { "" };
        }
        mViewCity.setViewAdapter(new ArrayWheelAdapter<String>( context, cities));
        mViewCity.setCurrentItem(0);
        updateAreas();
    }

    public interface CityPickerListener{
        public void onSelected(String province, String city, String area, String zipcode);
    }
}

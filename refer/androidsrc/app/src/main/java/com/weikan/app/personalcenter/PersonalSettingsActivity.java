package com.weikan.app.personalcenter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.weikan.app.R;
import com.weikan.app.account.AccountManager;
import com.weikan.app.base.BaseActivity;
import com.weikan.app.bean.UserInfoObject;
import com.weikan.app.common.widget.SimpleNavigationView;
import com.weikan.app.original.OriginalAgent;
import com.weikan.app.original.bean.ImageObject;
import com.weikan.app.original.bean.UploadImageObject;
import com.weikan.app.util.LToast;
import com.weikan.app.widget.citypicker.CityPickerDialog;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import platform.http.responsehandler.JsonResponseHandler;
import platform.http.responsehandler.SimpleJsonResponseHandler;
import platform.http.result.FailedResult;
import platform.photo.util.PhotoUtils;
import rx.functions.Action1;

/**
 * @author kailun on 16/7/27.
 */
public class PersonalSettingsActivity extends BaseActivity {

    private SimpleNavigationView navigationView;

    private ImageView ivAvatar;
    private TextView tvAvatar;

    private EditText etNick;
    private TextView tvSex;
    private TextView tvBirth;
    private TextView tvCity;

    private EditText etSign;
    private TextView tvWordCount;

    private int minNickLength = 2;
    private int maxNickLength = 16;
    private int maxSignLength = 30;

    private static final CharSequence[] sexArray = new CharSequence[]{"女", "男", "保密"};

    private UserInfoObject.UserInfoContent userInfo =
            new UserInfoObject.UserInfoContent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_settings);
        initViews();
        initValues();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        PhotoUtils.processActivityResult(requestCode, resultCode, data, new PhotoUtils.ProcessListener() {
            @Override
            public void onResult(List<String> result) {
                if (result.size() == 0) {
                    return;
                }

                String r = result.get(0);
                OriginalAgent.newUploadAvatar(new File(r), new UploadAvatarResponseHandler());
            }
        });
    }

    private void initViews() {
        navigationView = (SimpleNavigationView) findViewById(R.id.navigation);
        navigationView.setLeftOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        navigationView.setRightOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onConfirmClick();
            }
        });

        ivAvatar = (ImageView) findViewById(R.id.iv_avatar);
        ivAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAvatarClick();
            }
        });
        tvAvatar = (TextView) findViewById(R.id.tv_avatar);
        tvAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAvatarClick();
            }
        });

        etNick = (EditText) findViewById(R.id.et_nick);
        etNick.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                userInfo.nick_name = s.toString();
            }
        });

        tvSex = (TextView) findViewById(R.id.tv_sex);
        tvSex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSexClick();
            }
        });

        tvBirth = (TextView) findViewById(R.id.tv_birth);
        tvBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBirthClick();
            }
        });

        tvCity = (TextView) findViewById(R.id.tv_city);
        tvCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CityPickerDialog cpd = new CityPickerDialog(PersonalSettingsActivity.this);
                cpd.setListener(new CityPickerDialog.CityPickerListener() {
                    @Override
                    public void onSelected(String province, String city, String area, String zipcode) {
                        tvCity.setText(province + " " + city);
                        userInfo.province = province;
                        userInfo.city = city;
                    }
                });
                cpd.show();
            }
        });

        tvWordCount = (TextView) findViewById(R.id.tv_word_count);

        etSign = (EditText) findViewById(R.id.et_sign);
        etSign.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void afterTextChanged(Editable s) {
                userInfo.autograph = s.toString();
                tvWordCount.setText(userInfo.autograph.length() + "字");
            }
        });

    }

    private void initValues() {
        UserInfoObject.UserInfoContent info = AccountManager.getInstance().getUserData();
        if (info != null) {
            userInfo.headimgurl = info.headimgurl;
            userInfo.nick_name = info.nick_name;
            userInfo.sex = info.sex;
            userInfo.birthday = info.birthday;
            userInfo.autograph = info.autograph;
            userInfo.province = info.province;
            userInfo.city = info.city;
        }

        refreshViews();
    }

    @SuppressLint("SetTextI18n")
    private void refreshViews() {
        if (!TextUtils.isEmpty(userInfo.headimgurl)) {
            Picasso.with(this).load(userInfo.headimgurl).into(ivAvatar);
        } else {
            ivAvatar.setImageResource(R.drawable.user_default);
        }

        // 昵称
        etNick.setText(userInfo.nick_name);

        // 性别
        CharSequence sex = "";
        if (userInfo.sex >= 0 && userInfo.sex < sexArray.length) {
            sex = sexArray[userInfo.sex];
        }
        tvSex.setText(sex);

        // 生日
        Date date = new Date(userInfo.birthday * 1000L);
        tvBirth.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(date));

        // 个性签名
        etSign.setText(userInfo.autograph);
        tvWordCount.setText(userInfo.autograph.length() + "字");
        tvCity.setText(userInfo.province + " " + userInfo.city);
    }

    private void onAvatarClick() {
        PhotoUtils.gotoPending(this, 1, true);
    }

    private void onSexClick() {
        final AlertDialog dialog = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT)
                .setTitle("选择性别")
                .setSingleChoiceItems(sexArray, userInfo.sex, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        dialog.dismiss();
                        userInfo.sex = item;
                        refreshViews();
                    }
                })
                .create();
        dialog.show();
    }

    private void onBirthClick() {
        DatePickerFragment fragment = new DatePickerFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(DatePickerFragment.ARGUMENT_DATE, userInfo.birthday);

        fragment.setArguments(bundle);
        fragment.actionConfirmed = new Action1<Integer>() {
            @Override
            public void call(Integer i) {
                userInfo.birthday = i;
                refreshViews();
            }
        };
        fragment.show(getSupportFragmentManager(), "datePicker");
    }

    private void onConfirmClick() {
        if (TextUtils.isEmpty(userInfo.nick_name)) {
            LToast.showToast("昵称不能为空");
            return;
        }
        if (userInfo.nick_name.length() < minNickLength || userInfo.nick_name.length() > maxNickLength) {
            LToast.showToast(getResources().getString(R.string.nick_beyond, minNickLength, maxNickLength));
            return;
        }
        if (userInfo.autograph.length() > maxSignLength) {
            LToast.showToast(getResources().getString(R.string.sign_beyond, maxSignLength));
            return;
        }

        PersonalAgent.postUserInfo(userInfo, new SimpleJsonResponseHandler() {
            @Override
            public void success() {
                LToast.showToast("修改用户信息成功！");
                AccountManager.getInstance().updateUserInfo();
                finish();
            }

            @Override
            public void begin() {
                showLoadingDialog();
            }

            @Override
            public void end() {
                hideLoadingDialog();
            }
        });
    }

    public static class DatePickerFragment extends DialogFragment {

        public static final String ARGUMENT_DATE = "argument_date";

        public Action1<Integer> actionConfirmed = null;

        private int year = 0;
        private int month = 0;
        private int day = 0;

        /**
         * 子类化，避免在DateChanged的时候，标题被修改
         */
        public class InnerDatePickerDialog extends DatePickerDialog {

            public InnerDatePickerDialog(@NonNull Context context,
                                         @StyleRes int theme,
                                         @Nullable DatePickerDialog.OnDateSetListener listener,
                                         int year, int monthOfYear, int dayOfMonth) {
                super(context, theme, listener, year, monthOfYear, dayOfMonth);
            }

            @Override
            public void onDateChanged(DatePicker view, int year, int month, int day) {
                view.init(year, month, day, this);
                DatePickerFragment.this.year = year;
                DatePickerFragment.this.month = month;
                DatePickerFragment.this.day = day;
            }
        }

        @NonNull
        @SuppressWarnings({"ResourceType", "unchecked"})
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            // 从传入参数中读取时间
            long date = getArguments().getInt(ARGUMENT_DATE) * 1000L;
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(date);

            this.year = calendar.get(Calendar.YEAR);
            this.month = calendar.get(Calendar.MONTH);
            this.day = calendar.get(Calendar.DAY_OF_MONTH);

            final InnerDatePickerDialog dialog = new InnerDatePickerDialog(getActivity(), AlertDialog.THEME_HOLO_LIGHT,
                    null, this.year, this.month, this.day);
            dialog.setTitle("选择日期");
            dialog.setButton(DialogInterface.BUTTON_POSITIVE, getResources().getString(android.R.string.ok),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            onConfirmClick();
                        }
                    });
            dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getResources().getString(android.R.string.cancel),
                    (DialogInterface.OnClickListener) null);

            return dialog;
        }

        private void onConfirmClick() {
            if (actionConfirmed != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);

                int time = (int) (calendar.getTimeInMillis() / 1000);
                actionConfirmed.call(time);
            }
        }
    }

    private class UploadAvatarResponseHandler extends JsonResponseHandler<UploadImageObject> {
        @Override
        public void success(@NonNull UploadImageObject data) {
            ImageObject n = data.img.n;
            if (n != null) {
                userInfo.headimgurl = n.url;
                Picasso.with(PersonalSettingsActivity.this)
                        .load(n.url)
                        .into(ivAvatar);
            }
            LToast.showToast("上传头像成功");
        }

        @Override
        protected void failed(FailedResult r) {
            hideLoadingDialog();
            r.setIsHandled(true);
            LToast.showToast("上传头像失败");
        }

        @Override
        public void begin() {
            showLoadingDialog();
        }

        @Override
        public void end() {
            hideLoadingDialog();
        }
    }
}

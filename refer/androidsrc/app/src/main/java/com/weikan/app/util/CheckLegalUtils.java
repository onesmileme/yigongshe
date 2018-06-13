package com.weikan.app.util;

import android.text.TextUtils;

import com.weikan.app.R;

import java.util.regex.Pattern;

/**
 * Created by Lee on 2016/12/8.
 */
public class CheckLegalUtils {
    /**
     * 检查手机号码合法性
     *
     * @param phone
     * @return
     */
    public static boolean checkPhone(String phone) {
        if (!Pattern.matches("[1][3-8]\\d{9}", phone)) {
            LToast.showToast(R.string.msg_invalid_phone_number);
            return false;
        }
        return true;
    }

    /**
     * 检查验证码合法性
     *
     * @param code
     * @return
     */
    public static boolean checkCode(String code) {
        if (TextUtils.isEmpty(code)) {
            LToast.showToast(R.string.msg_invalid_captcha);
            return false;
        }
        return true;
    }

    /**
     * 检查密码合法性
     *
     * @param pwd
     * @return
     */
    public static boolean checkPwd(String pwd) {
        if (pwd.length() < 6 || pwd.length() > 15) {
            LToast.showToast(R.string.msg_invalid_pass_length);
            return false;
        }
        // 检查是否包含中文
        boolean isPassAllEn = true;
        for (int i = 0; i < pwd.length(); i++) {
            int c = pwd.charAt(i);
            if (c > 127 || c < 32) {
                isPassAllEn = false;
                break;
            }
        }
        if (!isPassAllEn) {
            LToast.showToast(R.string.msg_pass_not_only_english);
            return false;
        }
        return true;
    }

    /**
     * 检查用户名合法性
     *
     * @param name
     * @return
     */
    public static boolean checkName(String name) {
        Pattern p = Pattern.compile("[A-Za-z0-9_\\u4e00-\\u9fa5]{2,16}");
        if (!p.matcher(name).matches()) {
            LToast.showToast(R.string.msg_invalid_nickname);
            return false;
        }
        return true;
    }
}

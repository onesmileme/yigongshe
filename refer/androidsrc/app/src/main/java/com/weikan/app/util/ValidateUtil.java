package com.weikan.app.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.os.Environment;
import android.text.TextUtils;

/**
 * 验证工具类
 * 
 * @author
 * 
 */
public class ValidateUtil {

	/**
	 * 验证身份证
	 * 
	 * @param idCardNumber
	 * @return
	 */
	public static boolean checkIDCardNumber(String idCardNumber) {
		if (TextUtils.isEmpty(idCardNumber)) {
			return false;
		} else if (idCardNumber.length() != 18) {
			return false;
		}
		return true;
	}

	/**
	 * 验证手机号
	 * 
	 * @param phone
	 * @return
	 */
	public static boolean checkPhoneNumber(String phone) {
		if (phone == null || phone.equals("null"))
			return false;
		boolean isValid = false;
		String expression = "^\\(?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{5})$";
		String expression2 = "^\\(?(\\d{3})\\)?[- ]?(\\d{4})[- ]?(\\d{4})$";
		CharSequence inputStr = phone;
		Pattern pattern = Pattern.compile(expression);
		Matcher matcher = pattern.matcher(inputStr);
		Pattern pattern2 = Pattern.compile(expression2);
		Matcher matcher2 = pattern2.matcher(inputStr);
		if (matcher.matches() || matcher2.matches()) {
			isValid = true;
		}
		return isValid;
	}

	/**
	 * 验证银行卡号
	 * 
	 * @param phone
	 * @return
	 */
	public static boolean checkBankCardNumber(String bankCardNumber) {
		if (TextUtils.isEmpty(bankCardNumber)) {
			return false;
		} else if (bankCardNumber.length() < 12 || bankCardNumber.length() > 19) {
			return false;
		}
		return true;
	}

	/**
	 * 验证邮箱
	 * 
	 * @param phone
	 * @return
	 */
	public static boolean checkEmail(String email) {
		if (TextUtils.isEmpty(email)) {
			return false;
		} else {
			String pattern = "^[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}$";
			Pattern p = Pattern.compile(pattern);
			Matcher m = p.matcher(email);
			return m.matches();
		}
	}

	public static boolean isSDCardExists() {
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
	}
}

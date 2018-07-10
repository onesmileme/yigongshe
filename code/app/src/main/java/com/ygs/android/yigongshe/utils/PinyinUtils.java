package com.ygs.android.yigongshe.utils;

import com.ygs.android.yigongshe.utils.HanziToPinyin.Token;
import java.util.ArrayList;

/**
 * Created by ruichao on 2018/7/7.
 */

public class PinyinUtils {
  // 输入汉字返回拼音的通用方法函数。
  public static String getPinYin(String hanzi) {
    ArrayList<Token> tokens = HanziToPinyin.getInstance().get(hanzi);
    StringBuilder sb = new StringBuilder();
    if (tokens != null && tokens.size() > 0) {
      for (Token token : tokens) {
        if (Token.PINYIN == token.type) {
          sb.append(token.target);
        } else {
          sb.append(token.source);
        }
      }
    }

    return sb.toString().toUpperCase();
  }
}

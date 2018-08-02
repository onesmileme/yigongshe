package com.ygs.android.yigongshe.bean.response;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ruichao on 2018/6/28.
 */

public class ActivityDetailResponse implements Serializable {
  public int activityid;
  public String title;//"第11条",
  public String budget; //活动预算
  public String start_date; //活动开始日期
  public String end_date;//活动结束日期
  public int year_projectid;//年度项目id
  public String desc;//活动描述
  public String content; //活动详情，富文本
  public String pic;//"",
  public int cur_call_num;//当前call值
  public int target_call_num;//目标call值
  public String progress;//活动进程
  public String create_at;//"2018-05-01",创建时间
  public String create_name;//"admin",创建者姓名
  public int is_call; //是否打call
  public int is_register;//是否报名
  public int is_signin;//是否签到
  public int is_like; //是否点赞
  public int is_store;//是否收藏
  public int is_end;//活动是否结束
  public List<String> videos;//应援小视频
  public int participate_count;
}

package com.ygs.android.yigongshe.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import java.io.Serializable;
import java.util.List;

/**
 * Created by ruichao on 2018/7/26.
 */

public class MeCorporationBean implements Serializable {
  public static final int TYPE_ITEM_0 = 0; //没有文字
  public static final int TYPE_ITEM_1 = 1; //一组文字
  public static final int TYPE_ITEM_2 = 2; //2组文字
  public static final int TYPE_ITEM_3 = 3; //3组文字
  public static final int TYPE_ITEM_4 = 4; //图文组合
  public String assciation_name;
  public String assciation_desc;
  public List<UserInfo> user_list;

  public static class UserInfo implements Serializable {
    public String username;
    public String school;
    public String role;
  }

  public static class MeCorporationTransItemBean0 implements MultiItemEntity {
    @Override public int getItemType() {
      return TYPE_ITEM_0;
    }
  }

  public static class MeCorporationTransItemBean1 implements MultiItemEntity {
    public String name1;

    public MeCorporationTransItemBean1(String name1) {
      this.name1 = name1;
    }

    @Override public int getItemType() {
      return TYPE_ITEM_1;
    }
  }

  public static class MeCorporationTransItemBean2 implements MultiItemEntity {
    public String name1;
    public String name2;

    public MeCorporationTransItemBean2(String name1, String name2) {
      this.name1 = name1;
      this.name2 = name2;
    }

    @Override public int getItemType() {
      return TYPE_ITEM_2;
    }
  }

  public static class MeCorporationTransItemBean3 implements MultiItemEntity {
    public String name1;
    public String name2;
    public String name3;

    public MeCorporationTransItemBean3(String name1, String name2, String name3) {
      this.name1 = name1;
      this.name2 = name2;
      this.name3 = name3;
    }

    public MeCorporationTransItemBean3(UserInfo userInfo) {
      this.name1 = userInfo.username;
      this.name2 = userInfo.school;
      this.name3 = userInfo.role;
    }

    @Override public int getItemType() {
      return TYPE_ITEM_3;
    }
  }

  public static class MeCorporationTransItemBean4 implements MultiItemEntity {
    public int activityid;
    public String title;//"第11条",
    public String desc;//活动描述
    public String pic;//"",
    public String create_at;//"2018-05-01",创建时间
    public String link;

    public MeCorporationTransItemBean4(ActivityItemBean bean) {
      this.activityid = bean.activityid;
      this.title = bean.title;
      this.desc = bean.desc;
      this.pic = bean.pic;
      this.create_at = bean.create_at;
      this.link = bean.link;
    }

    @Override public int getItemType() {
      return TYPE_ITEM_4;
    }
  }
}



package com.ygs.android.yigongshe.bean;

public class SortModel {
  public SortModel() {
  }

  public SortModel(String name, String sortLetters) {
    this.name = name;
    this.sortLetters = sortLetters;
  }

  public String name;
  public String sortLetters;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getSortLetters() {
    return sortLetters;
  }

  public void setSortLetters(String sortLetters) {
    this.sortLetters = sortLetters;
  }
}

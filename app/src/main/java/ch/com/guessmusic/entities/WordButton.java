package ch.com.guessmusic.entities;

import android.widget.Button;

/**
 * 待选的字
 * Created by Chenhao on 2016/5/19.
 */
public class WordButton {

  private int index;//文字索引
  private boolean mIsVisiable;//是否可见
  private String mWord;//显示文字
  private Button mButton;//点击的button

  public WordButton() {
    mIsVisiable = true;
    mWord = "";
  }

  public WordButton(int index,String mWord, Button mButton, boolean mIsVisiable) {
    this.index = index;
    this.mWord = mWord;
    this.mButton = mButton;
    this.mIsVisiable = mIsVisiable;
  }

  public int getIndex() {
    return index;
  }

  public void setIndex(int index) {
    this.index = index;
  }

  public String getmWord() {
    return mWord;
  }

  public void setmWord(String mWord) {
    this.mWord = mWord;
  }

  public Button getmButton() {
    return mButton;
  }

  public void setmButton(Button mButton) {
    this.mButton = mButton;
  }

  public boolean ismIsVisiable() {
    return mIsVisiable;
  }

  public void setmIsVisiable(boolean mIsVisiable) {
    this.mIsVisiable = mIsVisiable;
  }

  @Override public String toString() {
    return "WordButton{" +
        "mIsVisiable=" + mIsVisiable +
        ", mWord='" + mWord + '\'' +
        ", mButton=" + mButton +
        '}';
  }

}

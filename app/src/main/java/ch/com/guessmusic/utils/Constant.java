package ch.com.guessmusic.utils;

import android.os.Environment;

/**
 * Created by Chenhao on 2016/5/20.
 */
public class Constant {
  public static final String WXAPP_ID ="wx8d9688cf767799f5";//微信key
  public static final int THUMB_SIZE = 120;//微信缩略图大小
  public static final int PROMPT_DIALOG = 1;//提示对话框
  public static final int DELETE_DIALOG = PROMPT_DIALOG+1;//删除对话框
  public static final int BUYCOIN_DIALOG = DELETE_DIALOG+1;//添加金币对话框

  public static final int CURRENT_LEVEL = 10;//初始关卡
  public static final int TOTAL_COINS = 999;//初始金币数量
  public static final int SELECT_WORD_LENGTH = 24;//待选字个数
  public static final String SCREEN_PATH =
      Environment.getExternalStorageDirectory().getAbsolutePath()+ "/GuessScreens";//截图保存路径

  public static final String DB_NAME = "music.db";//数据库名称
  public static final int DB_VERSION = 1;//数据库版本
  public static final String TABLE_NAME = "songs";//歌曲表名
  public static final String songName = "songName";//歌曲名称列名
  public static final String songUrl = "songUrl";//歌曲url列名
  public static final String songLength = "songLength";//歌曲长度列名

}

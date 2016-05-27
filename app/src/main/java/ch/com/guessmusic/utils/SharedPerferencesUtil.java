package ch.com.guessmusic.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by Chenhao on 2016/5/23.
 */
public class SharedPerferencesUtil {

  public static final String TOATL_COINS = "total_coins";//总金币
  public static final String CURRENT_INDEX = "current_index";//当前关卡

  public static final HashMap<String,Object> perfencesData = new HashMap<String,Object>();
  /**
   * 保存整型数据
   * @param context
   * @param key
   * @param coins
   */
  public static void savePerfencesData(Context context ,String key,Object coins){
    SharedPreferences preferences = context.getSharedPreferences("gameData", Activity.MODE_PRIVATE);
    SharedPreferences.Editor editor = preferences.edit();
    if (coins instanceof  Integer)
      editor.putInt(key,(int)coins);
    else if(coins instanceof  String)
      editor.putString(key,(String) coins);
    else if(coins instanceof  Boolean)
      editor.putBoolean(key,(Boolean) coins);
    editor.commit();
    perfencesData.put(key,coins);
  }

  /**
   * 获取int数据
   * @param context
   * @param key
   * @param defaultData
   * @return
   */
  public static Object getPerfencesData(Context context ,String key,Object defaultData){
    SharedPreferences preferences = context.getSharedPreferences("gameData", Activity.MODE_PRIVATE);
    Object result = null;
    if (defaultData instanceof  Integer)
      result = preferences.getInt(key,(int)defaultData);
    else if(defaultData instanceof  String)
      result = preferences.getString(key,(String)defaultData);
    else if(defaultData instanceof  Boolean)
      result = preferences.getBoolean(key,(Boolean)defaultData);
    return result;
  }

  public static void resetSharedPerferencesData(Context context){
    SharedPreferences preferences = context.getSharedPreferences("gameData", Activity.MODE_PRIVATE);
    SharedPreferences.Editor editor = preferences.edit();
    Iterator iter = perfencesData.entrySet().iterator();
    while (iter.hasNext()) {
      Map.Entry entry = (Map.Entry) iter.next();
      Object key = entry.getKey();
      Object val = entry.getValue();
      if (((String)val).equals(TOATL_COINS))
          editor.putInt((String)key,Constant.TOTAL_COINS);
      else if (((String)val).equals(CURRENT_INDEX))
        editor.putInt((String)key,Constant.CURRENT_LEVEL);
    }
    editor.commit();
  }
}

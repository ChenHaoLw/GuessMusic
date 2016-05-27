package ch.com.guessmusic.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import ch.com.guessmusic.db.DbSongsImp;
import ch.com.guessmusic.entities.Song;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 获取歌曲类
 * Created by Chenhao on 2016/5/20.
 */
public class SongUtil {

  public static ArrayList<Song> generateSongDatas(Context context){
    ArrayList<Song> datas = new ArrayList<Song>();
    HashMap<String,String> mapSongs = new HashMap<String,String>();//用于过滤相同的歌名

    ContentResolver resolver = context.getContentResolver();
    Cursor cursor = resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
        MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
    while(cursor.moveToNext()){
      Song song = new Song();
      String url = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
      song.setUrl(url);
      String name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
      name = getChineseString(name);
      song.setName(name);

      //只获取中文
      song.setLength(name.length());
      if (null != name && ""!= name && null != url && ""!= url){
        if (!mapSongs.containsKey(name)) {
          datas.add(song);
          mapSongs.put(name,url);
        }
      }
    }
    //释放资源
    cursor.close();
    cursor = null;
    mapSongs.clear();
    mapSongs = null;

    return datas;
  }

  /**
   * 匹配中文
   * @param str
   * @return
   */
  public static String getChineseString(String str)
  {
    String regex=".*[\\u4E00-\\u9FA5]+.*";//chinese

    Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);

    Matcher m = p.matcher(str);

    StringBuffer ret = new StringBuffer();
    while(m.find())
    {
      ret.append(m.group());
  }
    //获取第一次匹配的连续中文
    String result = "";
    boolean isfind = false;
    for (int j = 0; j <ret.toString().length() ; j ++){
      if (isChinese(ret.toString().charAt(j))){
        isfind = true;
        result += ret.toString().charAt(j);
      }else{
        if (isfind){
          return result;
        }
      }
    }
    return result.replaceAll("\\s*","");
  }

  /**
   * 输入的字符是否是汉字
   * @param a char
   * @return boolean
   */
  public static boolean isChinese(char a) {
    int v = (int)a;
    return (v >=19968 && v <= 171941);
  }

  /**
   * 插入数据库
   * @param imp
   * @param songs
   */
  public static void insertSongsDb(final DbSongsImp imp ,final List<Song> songs){
    new Thread(new Runnable() {
      @Override public void run() {
        for (int i = 0; i <songs.size() ;i ++){
          imp.insertSongs(songs.get(i));
        }
      }
    }).start();
  }
}

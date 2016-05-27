package ch.com.guessmusic.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import ch.com.guessmusic.entities.Song;
import ch.com.guessmusic.utils.Constant;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chenhao on 2016/5/26.
 */
public class DbSongsImp implements IDbDao {

  public static DbSongsImp dbSongsImp = null;
  private DbHepler dbHepler = null;
  public static DbSongsImp getInstance(Context context){
    if (dbSongsImp == null)
       dbSongsImp = new DbSongsImp(context);

    return dbSongsImp;
  }
  public DbSongsImp(Context context) {
    dbHepler = new DbHepler(context);
  }

  /**
   * 插入一首歌曲
   * @param song
   */
  @Override public void insertSongs(Song song) {
    SQLiteDatabase db = dbHepler.getWritableDatabase();
    String insertSql = "insert into "+ Constant.TABLE_NAME+""
        + "("+Constant.songName+","+Constant.songUrl+","+Constant.songLength+")"
        + " values(?,?,?)";
    db.execSQL(insertSql,new Object[]{song.getName(),song.getUrl(),song.getLength()});
    db.close();
  }

  /**
   * 删除一首歌曲
   * @param songName
   * @param songUrl
   */
  @Override public void deleteSongs(String songName, String songUrl) {
    SQLiteDatabase db = dbHepler.getWritableDatabase();
    String deleteSql = "delete from "+ Constant.TABLE_NAME+""
        + " where "+Constant.songName+" = ? and "+Constant.songUrl+" = ?";
    db.execSQL(deleteSql,new Object[]{songName,songUrl});
    db.close();
  }

  /**
   * 清空表
   * @param tableName
   */
  @Override public void clearSongs(String tableName) {
    SQLiteDatabase db = dbHepler.getWritableDatabase();
    String clearSql = "delete from "+tableName+"";
    db.execSQL(clearSql);
    db.close();
  }

  /**
   * 查询所有歌曲
   * @param tableName
   * @return
   */
  @Override public List<Song> querySongs(String tableName) {
    List<Song> list = new ArrayList<Song>();
    SQLiteDatabase db = dbHepler.getWritableDatabase();
    String queryAllSql = "select * from "+tableName+"";
    Cursor cursor = db.rawQuery(queryAllSql,new String[]{});
    while(cursor.moveToNext()){
      Song song = new Song();
      song.setName(cursor.getString(cursor.getColumnIndex(Constant.songName)));
      song.setUrl(cursor.getString(cursor.getColumnIndex(Constant.songUrl)));
      song.setLength(cursor.getInt(cursor.getColumnIndex(Constant.songLength)));
      list.add(song);
    }
    cursor.close();
    db.close();
    return list;
  }

  /**
   * 查询一首歌曲
   * @param songName
   * @param songUrl
   * @return
   */
  @Override public Song querySong(String songName, String songUrl) {
    Song song  = null;
    SQLiteDatabase db = dbHepler.getWritableDatabase();
    String querySql = "select * from "+Constant.TABLE_NAME+" where "+Constant.songName+" =? and "+Constant.songUrl+"=?";
    Cursor cursor = db.rawQuery(querySql,new String[]{songName,songUrl});
    if (cursor.moveToFirst()){
      song.setName(cursor.getString(cursor.getColumnIndex(Constant.songName)));
      song.setUrl(cursor.getString(cursor.getColumnIndex(Constant.songUrl)));
      song.setLength(cursor.getInt(cursor.getColumnIndex(Constant.songLength)));
    }
    cursor.close();
    db.close();
    return song;
  }

  /**
   * 更新歌名
   * @param songName
   * @param songUrl
   * @param updateName
   */
  @Override public void updateSong(String songName, String songUrl, String updateName) {
    SQLiteDatabase db = dbHepler.getWritableDatabase();
    String updateSql = "update "+ Constant.TABLE_NAME+" set "+Constant.songName+" =? where "+Constant.songName+" = ? and "+Constant.songUrl+" = ?";
    db.execSQL(updateSql,new Object[]{updateName,songName,songUrl});
    db.close();
  }

  /**
   * 判断数据库是否为空
   * @param tableName
   * @return
   */
  @Override public boolean isEmpty(String tableName) {
    boolean isEmpty = false;
    SQLiteDatabase db = dbHepler.getWritableDatabase();
    String queryAllSql = "select * from "+tableName+"";
    Cursor cursor = db.rawQuery(queryAllSql,new String[]{});
    isEmpty = cursor.moveToNext()?false:true;
    cursor.close();
    db.close();
    return isEmpty;
  }
}

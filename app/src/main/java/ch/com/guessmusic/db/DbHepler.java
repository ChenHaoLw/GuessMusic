package ch.com.guessmusic.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import ch.com.guessmusic.utils.Constant;

/**
 * Created by Chenhao on 2016/5/26.
 */
public class DbHepler extends SQLiteOpenHelper {

  private String create_table = "create table "+Constant.TABLE_NAME+"(_id integer primary key autoincrement,"+Constant.songName+" text not null,"+Constant.songUrl+" text unique,"+Constant.songLength+" integer)";//创建表
  private String drop_table = "delete table if exists "+Constant.TABLE_NAME+"";
  public DbHepler(Context context) {
    super(context, Constant.DB_NAME, null,  Constant.DB_VERSION);
  }

  @Override public void onCreate(SQLiteDatabase db) {
    db.execSQL(create_table);
  }

  @Override public void onUpgrade(SQLiteDatabase db, int i, int i1) {
    db.execSQL(drop_table);
    onCreate(db);
  }
}

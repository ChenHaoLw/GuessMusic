package ch.com.guessmusic.db;

import ch.com.guessmusic.entities.Song;
import java.util.List;

/**
 * Created by Chenhao on 2016/5/26.
 */
public interface IDbDao {

  void insertSongs(Song song);//插入歌曲
  void deleteSongs(String songName,String songUrl);//根据歌名和url删除一首歌
  void clearSongs(String tableName);//根据表名清空数据
  List<Song> querySongs(String tableName);//根据表名查询所有数据
  Song querySong(String songName,String songUrl);//查询某一首歌
  void updateSong(String songName,String songUrl,String updateName);//更新歌曲名称
  boolean isEmpty(String tableName);//数据库是否为空
}

package ch.com.guessmusic.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by Chenhao on 2016/5/19.
 */
public class LayoutUtil {

  public static View getView(Context context,int id){
    LayoutInflater inflater =
        (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    return inflater.inflate(id,null,false);
  }
}

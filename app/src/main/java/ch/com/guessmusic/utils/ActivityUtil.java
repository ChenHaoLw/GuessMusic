package ch.com.guessmusic.utils;

import android.content.Context;
import android.content.Intent;

/**
 * Created by Chenhao on 2016/5/25.
 */
public class ActivityUtil {

  public static void startActivity(Context context,Class dexclass){
    Intent intent = new Intent(context,dexclass);
    context.startActivity(intent);
  }
}

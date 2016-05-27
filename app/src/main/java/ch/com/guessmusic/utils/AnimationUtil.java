package ch.com.guessmusic.utils;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;

/**
 * Created by Chenhao on 2016/5/19.
 */
public class AnimationUtil {

  /**
   *
   * @param context
   * @param view 需要设置动画的view
   * @param id   动画的id
   * @param after 是否保持动画后效果
   * @param repeatCount 动画持续时间
   */
  public static Animation rotate(Context context,View view,int id,boolean after,int repeatCount) {
    Animation animation = AnimationUtils.loadAnimation(context, id);
    LinearInterpolator interpolator = new LinearInterpolator();
    animation.setInterpolator(interpolator);
    animation.setFillAfter(after);
    animation.setRepeatCount(repeatCount);
    view.startAnimation(animation);
    return animation;
  }
}

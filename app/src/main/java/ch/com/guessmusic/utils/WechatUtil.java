package ch.com.guessmusic.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXImageObject;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.platformtools.Util;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * 微信相关
 * Created by Chenhao on 2016/5/25.
 */
public class WechatUtil {
  public static IWXAPI iwxapi;//微信api

  /**
   * 向微信注册应用
   */
  public static void regToWx(Context context) {
    iwxapi = WXAPIFactory.createWXAPI(context, Constant.WXAPP_ID, true);
    //向微信注册应用
    iwxapi.registerApp(Constant.WXAPP_ID);
  }

  /**
   * 截屏
   */
  public static String getScreenImage(View view) {
    //截屏
    //View view = getWindow().getDecorView().getRootView();
    String bitmapPath = "";
    view.setDrawingCacheEnabled(true);
    view.buildDrawingCache();
    Bitmap bitmap = view.getDrawingCache();
    //此处会回收bitmap 导致bitmap compress时对象为空
    //view.setDrawingCacheEnabled(false);
    if (Environment.isExternalStorageEmulated()) {
      File pathfile = new File(Constant.SCREEN_PATH);
      if (!pathfile.exists()) {
        pathfile.mkdirs();
      }
      File file = new File(pathfile, System.currentTimeMillis() + ".png");
      try {
        FileOutputStream outputStream = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        outputStream.flush();
        outputStream.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
      bitmapPath = file.getAbsolutePath();
    }
    return bitmapPath;
  }

  /**
   * 清除本地分享的数据
   */

  public static void clearSharedLocalImages(){
    File file = new File(Constant.SCREEN_PATH);
    if (file != null && file.exists() && file.isDirectory()) {
      for (File item : file.listFiles()) {
        item.delete();
      }
        //file.delete(); //文件夹不删除
    }
  }

  /**
   *
   * @param bitmap
   * @param toUserOrSession :true 分享朋友圈 false:会话
   */
  public static void shareImagesToWx(Bitmap bitmap, boolean toUserOrSession) {
    WXImageObject imgObj = new WXImageObject(bitmap);

    WXMediaMessage msg = new WXMediaMessage();
    msg.mediaObject = imgObj;

    //缩略图
    Bitmap thumbBmp =
        Bitmap.createScaledBitmap(bitmap, Constant.THUMB_SIZE, Constant.THUMB_SIZE, false);

    for (int imageSize = 0, quality = 100; imageSize > 32; quality--) {
         msg.thumbData = bmpToByteArray(thumbBmp, false, quality); // 设置缩略图
         imageSize = msg.thumbData.length / 1024;
    }

    thumbBmp.recycle();
    SendMessageToWX.Req req = new SendMessageToWX.Req();
    req.transaction = buildTransaction("img");
    req.message = msg;
    //发送个指定用户还是朋友圈
    req.scene =
        toUserOrSession ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
    iwxapi.sendReq(req);
  }

  /**
   * 唯一标识符
   */
  private static String buildTransaction(final String type) {
    return (type == null) ? String.valueOf(System.currentTimeMillis())
        : type + System.currentTimeMillis();
  }

  /**
   * bitmap转数组
   * @param bmp
   * @param needRecycle
   * @return
   */
  public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle ,int quality) {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    bmp.compress(Bitmap.CompressFormat.JPEG, quality,output);
    if (needRecycle) {
      bmp.recycle();
    }
    byte[] result = output.toByteArray();
    try {
      output.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return result;
  }
}

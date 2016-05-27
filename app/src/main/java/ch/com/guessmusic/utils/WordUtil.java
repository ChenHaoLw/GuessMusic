package ch.com.guessmusic.utils;

import android.support.annotation.NonNull;
import android.util.Log;
import java.io.UnsupportedEncodingException;
import java.util.Random;

/**
 * 生成随机汉字
 * Created by Chenhao on 2016/5/20.
 */
public class WordUtil {

  public static String[] generateRandomWord(int size) throws UnsupportedEncodingException {
    if(size < 0){
      return null;
    }
    String[] words = new String [size];
    for(int i = 0; i < size ;i ++){
      String word = generateOneWord();
      words[i] = word;
    }
    return words;
  }

  @NonNull private static String generateOneWord() throws UnsupportedEncodingException {
    Random random = new Random();
    //一个汉字有2个字节  高位和低位
    int highPos = (176 + Math.abs(random.nextInt(39)));
    int lowPos = (161 + Math.abs(random.nextInt(93)));
    byte[] bytes = new byte[2];
    bytes[0] = (Integer.valueOf(highPos)).byteValue();
    bytes[1] = (Integer.valueOf(lowPos)).byteValue();
    return new String(bytes,"GBK");
  }
}

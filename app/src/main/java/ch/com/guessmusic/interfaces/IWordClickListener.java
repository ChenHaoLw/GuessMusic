package ch.com.guessmusic.interfaces;

import android.content.DialogInterface;
import ch.com.guessmusic.entities.WordButton;

/**
 * Created by Chenhao on 2016/5/20.
 */
public interface IWordClickListener extends DialogInterface.OnClickListener{

  void OnWordClick(WordButton wordButton);
}

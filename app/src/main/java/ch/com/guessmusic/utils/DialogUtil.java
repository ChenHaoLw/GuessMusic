package ch.com.guessmusic.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import ch.com.guessmusic.R;
import ch.com.guessmusic.interfaces.IDialogClickListener;

/**
 * 对话框工具
 * Created by Chenhao on 2016/5/23.
 */
public class DialogUtil {

  private static IDialogClickListener mListener;

  /**
   * 显示对话框
   */
  public static void showDialog(Context context, String title, String message,
      IDialogClickListener listener, final int whichDialog) {
    mListener = listener;
    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
    View view = LayoutUtil.getView(context, R.layout.dialog);
    TextView titleTv = (TextView) view.findViewById(R.id.id_dialog_title);
    titleTv.setText(title);
    TextView msgTv = (TextView) view.findViewById(R.id.id_dialog_message);
    msgTv.setText(message);
    ImageButton noBtn = (ImageButton) view.findViewById(R.id.id_no_prompt);
    noBtn.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        mListener.onClick(false, whichDialog);
        view.getRootView().setVisibility(View.GONE);
      }
    });
    ImageButton yesBtn = (ImageButton) view.findViewById(R.id.id_yes_prompt);
    yesBtn.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        mListener.onClick(true, whichDialog);
        view.getRootView().setVisibility(View.GONE);
      }
    });

    dialog.setView(view);
    dialog.create();
    dialog.show();
  }
}

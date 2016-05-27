package ch.com.guessmusic.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ch.com.guessmusic.R;
import ch.com.guessmusic.utils.WechatUtil;

/**
 * Created by Chenhao on 2016/5/25.
 */
public class FinishedActivity extends Activity {
  @BindView(R.id.id_fl_1) FrameLayout fl;
  @BindView(R.id.id_pass_eamil) ImageButton passEmail;
  @BindView(R.id.id_pass_wechat) ImageButton passWechat;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.finished);
    ButterKnife.bind(this);
    initViews();
  }

  /**
   * 初始化控件
   */
  private void initViews() {
    fl.setVisibility(View.INVISIBLE);
  }

  @OnClick({R.id.id_pass_wechat,R.id.id_pass_eamil,R.id.id_back})
  public void OnClick(View view){
    switch (view.getId()){
      case R.id.id_back:
        onBackPressed();
        break;
      case R.id.id_pass_eamil:
        break;
      case R.id.id_pass_wechat:
        //截屏
        View viewScreen = getWindow().getDecorView().getRootView();
        Bitmap bitmap = BitmapFactory.decodeFile(WechatUtil.getScreenImage(viewScreen));
        if (bitmap != null) {
          //分享微信
          WechatUtil.shareImagesToWx(bitmap, false);
        }
        break;
    }
  }
}

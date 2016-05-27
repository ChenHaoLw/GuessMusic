package ch.com.guessmusic.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ch.com.guessmusic.R;
import ch.com.guessmusic.db.DbSongsImp;
import ch.com.guessmusic.entities.Song;
import ch.com.guessmusic.entities.WordButton;
import ch.com.guessmusic.interfaces.IDialogClickListener;
import ch.com.guessmusic.interfaces.IWordClickListener;
import ch.com.guessmusic.myview.WordGridView;
import ch.com.guessmusic.utils.AnimationUtil;
import ch.com.guessmusic.utils.Constant;
import ch.com.guessmusic.utils.DialogUtil;
import ch.com.guessmusic.utils.SharedPerferencesUtil;
import ch.com.guessmusic.utils.SongUtil;
import ch.com.guessmusic.utils.WechatUtil;
import ch.com.guessmusic.utils.WordUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends Activity implements IWordClickListener, IDialogClickListener {

  @BindView(R.id.id_back) ImageButton back;//返回按钮
  /**
   * 音乐相关控件
   */
  @BindView(R.id.id_play) ImageButton play;//播放音乐按钮
  @BindView(R.id.id_pin) ImageView playPin;
  @BindView(R.id.id_disc) ImageView playDisc;
  /**
   * 答案相关控件
   */
  @BindView(R.id.id_gridword) WordGridView gridView;//选择答案24个按钮控件
  @BindView(R.id.id_name_select) LinearLayout selectLinearlayout;//答案框框控件
  @BindView(R.id.id_right_answer) LinearLayout rightAnswerLinearlayout;//正确答案显示控件
  /**
   * 金币相关控件
   */
  @BindView(R.id.id_coins) TextView coinTv;//总金币数量
  @BindView(R.id.id_addCoin) ImageButton addCoinButton;//添加金币按钮
  @BindView(R.id.id_delete) ImageButton deleteButton;//删除按钮
  @BindView(R.id.id_prompt) ImageButton promptButton;//提示按钮
  @BindView(R.id.id_share) ImageButton shareButton;//分享按钮

  /**
   * 关卡相关控件
   */
  @BindView(R.id.id_game_level) TextView floatGameLevel;//游戏时显示当前关卡
  @BindView(R.id.id_ranking) TextView currentRanking;//当前排名
  @BindView(R.id.id_current_level) TextView currentLevelTv;//过关显示当前关卡
  @BindView(R.id.id_show_right_answer) TextView rightAnswerTv;//过关歌名
  @BindView(R.id.id_reward_count) TextView rewardCountTv;//过关奖励金币数(3)
  @BindView(R.id.id_next_level) ImageButton nextLevelButton;//下一关
  @BindView(R.id.id_share_wechat) ImageButton shareWechatButton;//分享微信(截屏-分享)

  /**
   * 金币相比变量
   */
  private int total_coins = 0;//初始金币数量
  private int delete_price = 0;//删除价格
  private int prompt_price = 0;//提示价格
  private int deleteCount = 0;//记录已删除个数,防止堆栈溢出

  /**
   * 选字以及答案相关变量
   */
  private ArrayList<WordButton> list = new ArrayList<WordButton>();//待选字集合
  private ArrayList<WordButton> listSelect = new ArrayList<WordButton>(); //答案集合
  private int selectCount = 0; //已选择答案个数
  private int currentIndex = Constant.CURRENT_LEVEL;//当前关卡

  /**
   * 音乐相关变量
   */
  private ArrayList<Song> songs = new ArrayList<Song>(); //所有关卡歌曲集合
  private Song currentSong = null;//当前歌曲
  private MediaPlayer mediaPlay = null;//音乐播放器

  /**
   * 错误答案闪烁效果变量
   */
  private static final int MSG_CHANGE_COLOR = 1;
  private static final int MSG_CHANGE_COLOR_DELAY = 150;//变化时间间隔
  private boolean isChangeColor = false;
  private int CHANGE_COLOR_TIMES = 5;//总共闪烁次数
  private int countTimes = 0;//已变化次数

  /**
   * finishedActivity 请求码
   */
  private static final int REQUEST_CODE = 0x11;

  /**
   * 是否通关
   */
  //private boolean isPassed = Constant.IS_PASSED;
  /**
   * 数据库操作对象
   */
  private DbSongsImp songsImp = null;

  /**
   * 控制动画顺序播放 ： 左 中 右 顺序播放
   */
  private Animation pinLeftAnima = null;
  private Animation discAnima = null;
  private Animation pinRightAnima = null;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);
    WechatUtil.regToWx(getApplicationContext());
    songsImp = new DbSongsImp(this);
    initSongs();
    initViews();
    oprPlayMusic();
  }

  /**
   * 初始化控件相关
   */
  private void initViews() {
    rightAnswerLinearlayout.setVisibility(View.GONE);
    coinTv.setText(total_coins + "");
    delete_price = getConfigDeletePrice();
    prompt_price = getConfigPromptPrice();
    floatGameLevel.setText((currentIndex + 1) + "/" + songs.size());
    //自动播放音乐
    oprPlayMusic();
  }

  /**
   * 从配置获取删除价格
   */
  private int getConfigDeletePrice() {
    return this.getResources().getInteger(R.integer.price_delete);
  }

  /**
   * /**
   * 从配置获取提示价格
   */
  private int getConfigPromptPrice() {
    return this.getResources().getInteger(R.integer.price_prompt);
  }

  /**
   * 1.初始化歌曲信息
   */
  private void initSongs() {
    //金币赋值
    total_coins = (int)SharedPerferencesUtil.getPerfencesData(MainActivity.this,
        SharedPerferencesUtil.TOATL_COINS, Constant.TOTAL_COINS);
    //关卡赋值
    currentIndex = (int)SharedPerferencesUtil.getPerfencesData(MainActivity.this,
        SharedPerferencesUtil.CURRENT_INDEX, Constant.CURRENT_LEVEL);

    /**
     * 1.本地数据库是否为空？ 非空直接拿数据
     *                      为空查找系统数据库 -- 并插入本地数据库
     */
    if (songsImp.isEmpty(Constant.TABLE_NAME)){
      songs = SongUtil.generateSongDatas(MainActivity.this);
      SongUtil.insertSongsDb(songsImp,songs);
    }else{
      songs = (ArrayList<Song>) songsImp.querySongs(Constant.TABLE_NAME);
    }

    if (songs.size() > 0 && null != songs.get(currentIndex)) {
      currentSong = songs.get(currentIndex);
      initData();
      initSelectData();
    }
  }

  /**
   * 2.初始化待选文字
   */
  private void initData() {
    if (null == currentSong || TextUtils.isEmpty(currentSong.getName())) {
      return;//没有歌曲数据 或者已越界
    }

    try {
      for (int j = 0; j < currentSong.getLength(); j++) {
        WordButton word = new WordButton(j, currentSong.getName().charAt(j) + "", null, true);
        list.add(word);
      }
      String[] words = WordUtil.generateRandomWord(Constant.SELECT_WORD_LENGTH);
      for (int i = currentSong.getLength(); i < Constant.SELECT_WORD_LENGTH; i++) {
        WordButton word = new WordButton(i, words[i], null, true);
        list.add(word);
      }

      /**
       *  Collections.shuffle(list);//打乱数据 这种方式打乱之后 数据的index和显示对不上 不便于还原
       */
      randomMusicName(list);
      gridView.updateWords(list);
      gridView.registerWordClickListener(this);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * 打乱歌曲名再选择框的位置
   */
  private void randomMusicName(ArrayList<WordButton> list) {
    for (int i = 0; i < currentSong.getLength(); i++) {
      int index = (int) (Math.random() * Constant.SELECT_WORD_LENGTH);
      WordButton b = list.get(index);
      list.get(i).setIndex(b.getIndex());
      b.setIndex(i);
      list.set(index, list.get(i));
      list.set(i, b);
    }
  }

  /**
   * 3.初始化选择字
   */
  private void initSelectData() {
    //先清空
    selectLinearlayout.removeAllViews();

    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(140, 140);
    for (int i = 0; i < currentSong.getLength(); i++) {
      final Button b = new Button(MainActivity.this);
      b.setLayoutParams(new LinearLayout.LayoutParams(140, 140));
      b.setTextSize(15);
      b.setTextColor(Color.WHITE);
      b.setBackgroundResource(R.drawable.grid_item_selector);
      final WordButton word = new WordButton(i, "", b, true);
      word.setmButton(b);
      b.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
          if (!TextUtils.isEmpty(word.getmButton().getText())) {
            clearAnswer(word);
          }
        }
      });
      word.getmButton().setText(word.getmWord());
      word.getmButton().setTextColor(Color.WHITE);
      word.getmButton().setBackgroundResource(R.drawable.game_wordblank);
      listSelect.add(word);
      selectLinearlayout.addView(word.getmButton(), params);
    }
  }

  /**
   * 清空答案
   */
  private void clearAnswer(WordButton word) {
    selectCount--;
    word.getmButton().setText("");
    word.setmWord("");
    word.setmIsVisiable(false);
    //设置待选文字可见性
    setListWord(list.get(word.getIndex()), View.VISIBLE);
  }

  @OnClick({
      R.id.id_back, R.id.id_addCoin, R.id.id_play, R.id.id_delete, R.id.id_prompt, R.id.id_share,
      R.id.id_next_level, R.id.id_share_wechat
  }) public void onClick(View view) {
    switch (view.getId()) {
      case R.id.id_back:
        //返回
        onBackPressed();
        break;
      case R.id.id_addCoin:
        //添加金币
        oprAddCoin();
        break;
      case R.id.id_delete:
        //删除一个答案
        oprDelete();
        break;
      case R.id.id_prompt:
        //提示一个答案
        oprPrompt();
        break;
      case R.id.id_next_level:
        //下一关
        oprNextLevel();
        break;
      case R.id.id_share_wechat:
      case R.id.id_share:
        //分享微信
        oprShare();
        break;
      case R.id.id_play:
        //播放音乐
        oprPlayMusic();
        break;
    }
  }

  /**
   * 下一关
   */
  private void oprNextLevel() {
    currentIndex++;
    if (songs.size() == currentIndex) {
      /**
       * 通关界面
       * 新建一个activity处理 这里面逻辑和游戏主界面关联很小
       */
      songsImp.clearSongs(Constant.TABLE_NAME);
      Collections.shuffle(songs);//打乱数据
      SongUtil.insertSongsDb(songsImp,songs);

      rightAnswerLinearlayout.setVisibility(View.INVISIBLE);//隐藏下一关界面
      Intent intent = new Intent(MainActivity.this,FinishedActivity.class);
      startActivityForResult(intent,REQUEST_CODE);
    } else {
      SharedPerferencesUtil.savePerfencesData(MainActivity.this,
          SharedPerferencesUtil.CURRENT_INDEX, currentIndex);
      initSongs();
      initViews();
    }
  }

  /**
   * 下一关时先清空之前的数据
   */
  private void clearListData() {
    listSelect.clear();
    list.clear();
  }

  /**
   * 添加金币操作
   */
  private void oprAddCoin() {
  }

  /**
   * 删除答案操作
   */
  private void oprDelete() {
    if (total_coins - delete_price > 0) {
      total_coins = total_coins - delete_price;
      //View view = LayoutUtil.getView(MainActivity.this,R.layout.dialog);
      //金币足够删除
      DialogUtil.showDialog(MainActivity.this, "删除", "是否花费" + delete_price + "金币去除一个无关文字?",
          MainActivity.this, Constant.DELETE_DIALOG);
    } else {
      //显示addCoin对话框
      DialogUtil.showDialog(MainActivity.this, "金币不足", "是否立即购买金币?", MainActivity.this,
          Constant.BUYCOIN_DIALOG);
    }
  }

  /**
   * 删除一个非答案文字
   */
  private void deleteOneWord(int index) {
    if (deleteCount == (list.size() - currentSong.getLength())) {
      Toast.makeText(MainActivity.this, "无法继续删除", Toast.LENGTH_LONG).show();
      return;
    }
    WordButton wordBtn = list.get(index);
    if (wordBtn.ismIsVisiable() && !TextUtils.isEmpty(wordBtn.getmWord())) {
      boolean result = isRightAnswerWord(wordBtn);
      if (!result) {
        //非答案
        deleteCount++;
        wordBtn.getmButton().setVisibility(View.INVISIBLE);
        wordBtn.setmIsVisiable(false);
        return;
      } else {
        index++;//找下一个
        if (list.size() <= index || index < 0) {
          index = 0;
        }
        deleteOneWord(index);
      }
    } else {
      index++;//找下一个
      if (list.size() <= index || index < 0) {
        index = 0;
      }
      deleteOneWord(index);
    }
  }

  /**
   * 判断word是否是答案的一部分
   */
  private boolean isRightAnswerWord(WordButton wordBtn) {
    return currentSong.getName().contains(wordBtn.getmWord());
  }

  /**
   * 提示答案操作
   */
  private void oprPrompt() {
    if (total_coins - prompt_price > 0) {
      total_coins = total_coins - prompt_price;
      //金币足够提示
      DialogUtil.showDialog(MainActivity.this, "提示", "是否花费" + prompt_price + "金币提示一个答案?",
          MainActivity.this, Constant.PROMPT_DIALOG);
    } else {
      //显示addCoin对话框
      DialogUtil.showDialog(MainActivity.this, "金币不足", "是否立即购买金币?", MainActivity.this,
          Constant.BUYCOIN_DIALOG);
    }
  }

  /**
   * 提示一个答案
   */
  private void promptOneAnswer(int index) {
    /**
     * 思路：根据随机的答案word去list中找到对应的WordBtn
     *   如果找到值为null：则说明->改答案已经在listselect中显示了
     *                   ->判断该字是否为单字
     *              是单字：判断该字在listSelect中的位置是否正确
     *                   正确：取别的字 迭代prompOneAnser(index++)
     *                   错误：clearAnswer oprOnClick()
     *             不是单字：所以该字的位置是否都正确
     *                   正确 取别的字迭代prompOneAnser(index++)
     *                   错误：找到第一个非正确位置的字，替换到正确位置（判断正确位置是否有内容  有clear oprOnClick() ---无 oprOnClick()）
     *        非null: 找到listSelect中index位置的值 判断是否有值
     *              有值：判断是否相等
     *                  相等：取别的字 迭代prompOneAnser(index++)
     *                  不相等：clearAnswer oprOnClick()
     *              无值：oprOnClick()
     */

    WordButton wordBtn = null;
    String word = String.valueOf(currentSong.getName().charAt(index));

    for (int i = 0; i < list.size(); i++) {
      if (list.get(i).getmWord().equals(word) && list.get(i).ismIsVisiable()) {
        wordBtn = list.get(i);//正确提示字
        break;
      }
    }

    if (wordBtn == null)//值为null
    {
      //判断是否为单字
      int[] indexs = new int[currentSong.getLength()];//用于保存相同word的位置
      int count = 0;//默认word个数
      for (int j = 0; j < currentSong.getLength(); j++) {
        if (String.valueOf(currentSong.getName().charAt(j)).equals(word)) {
          indexs[count] = j;
          count++;
        }
      }
      if (count == 1) {
        //单字
        int listSelectIndex = -1;//得到该字在listSelect中的索引位置
        for (int j = 0; j < listSelect.size(); j++) {
          if (listSelect.get(j).getmWord().equals(word)) {
            listSelectIndex = j;
          }
        }
        if (listSelectIndex == indexs[0]) {
          //位置正确 取别的字
          index++;
          if (index < 0 || currentSong.getLength() <= index) {
            index = 0;
          }
          //迭代
          promptOneAnswer(index);
        } else {
          //位置不正确 替换到正确的位置
          wordBtn = list.get(listSelect.get(listSelectIndex).getIndex());//带提示的字
          WordButton replaceWord = list.get(listSelect.get(indexs[0]).getIndex());//占错位置的字
          //clearAnswer(listSelect.get(listSelectIndex));
          selectCount--;
          oprOnWordClick(wordBtn, indexs[0], true);//走到值非null的流程中去了
          selectCount--;
          oprOnWordClick(replaceWord, listSelectIndex, true);
        }
      } else if (count > 1) {
        boolean isNeedChange = false;
        int hasChangeCount = -1;
        //多字
        for (int j = 0; j < listSelect.size(); j++) {
          if (listSelect.get(j).getmWord().equals(word)) {
            hasChangeCount++;
            for (int i = count; i > 0; i--) {
              if (j == indexs[i - 1]) {
                isNeedChange = true;
              }
            }

            if (isNeedChange) {
              //需要交换
              wordBtn = list.get(listSelect.get(j).getIndex());//带提示的字
              WordButton replaceWord =
                  list.get(listSelect.get(indexs[hasChangeCount]).getIndex());//占错位置的字
              //clearAnswer(listSelect.get(j));
              selectCount--;
              oprOnWordClick(wordBtn, indexs[hasChangeCount], true);//走到值非null的流程中去了
              selectCount--;
              oprOnWordClick(replaceWord, j, true);
            }
            break;
          }
        }
        //多个字位置都正确
        if (!isNeedChange) {
          //位置正确 取别的字
          index++;
          if (index < 0 || currentSong.getLength() <= index) {
            index = 0;
          }
          //迭代
          promptOneAnswer(index);
        }
      }
    } else//值非null
    {
      if (!TextUtils.isEmpty(listSelect.get(index).getmWord())) {
        //index位置值非空
        if (listSelect.get(index).getmWord().equals(word)) {
          //且相等
          //位置正确 取别的字
          index++;
          if (index < 0 || currentSong.getLength() <= index) {
            index = 0;
          }
          //迭代
          promptOneAnswer(index);
        } else {
          clearAnswer(listSelect.get(index));
          oprOnWordClick(wordBtn, index, false);
        }
      } else {
        oprOnWordClick(wordBtn, index, false);
      }
    }
  }

  /**
   * 分享操作
   */
  private void oprShare() {
    //截屏
    View view = getWindow().getDecorView().getRootView();
    String bitmapPath = WechatUtil.getScreenImage(view);
    Bitmap bitmap = BitmapFactory.decodeFile(bitmapPath);
    if (bitmap != null) {
      //分享微信
      WechatUtil.shareImagesToWx(bitmap, false);
    }
  }

  /**
   * 音乐操作
   */
  private void oprPlayMusic() {
    playAnimation();
    if (null != mediaPlay && mediaPlay.isPlaying()) {
      return;
    } else {
      playMusic();
    }
  }

  /**
   * 播放音乐
   */
  private void playMusic() {
    if (mediaPlay == null) {
      mediaPlay = new MediaPlayer();
    }

    try {
      if (currentSong == null || null == currentSong.getUrl() || "" == currentSong.getUrl()) {
        return;
      }
      Uri uri = Uri.parse(currentSong.getUrl());
      mediaPlay.setDataSource(MainActivity.this, uri);
      mediaPlay.prepare();
      mediaPlay.start();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void playAnimation() {
    pinLeftAnima = AnimationUtil.rotate(this, playPin, R.anim.rotate_left, true, 0);
    pinLeftAnima.setAnimationListener(new Animation.AnimationListener() {
      @Override public void onAnimationStart(Animation animation) {
        play.setVisibility(View.INVISIBLE);
      }

      @Override public void onAnimationEnd(Animation animation) {
        discAnima = AnimationUtil.rotate(MainActivity.this, playDisc, R.anim.rotate, false, 5/*Animation.INFINITE*/);
        discAnima.setAnimationListener(new Animation.AnimationListener() {
          @Override public void onAnimationStart(Animation animation) {

          }

          @Override public void onAnimationEnd(Animation animation) {
            pinRightAnima =
                AnimationUtil.rotate(MainActivity.this, playPin, R.anim.rotate_right, true, 0);
            pinRightAnima.setAnimationListener(new Animation.AnimationListener() {
              @Override public void onAnimationStart(Animation animation) {

              }

              @Override public void onAnimationEnd(Animation animation) {
                stopPlayMusic();
              }

              @Override public void onAnimationRepeat(Animation animation) {
              }
            });
          }

          @Override public void onAnimationRepeat(Animation animation) {

          }
        });
      }

      @Override public void onAnimationRepeat(Animation animation) {

      }
    });
  }

  /**
   * 结束音乐
   */
  private void stopPlayMusic() {
    play.setVisibility(View.VISIBLE);
    //动画结束 音乐停止
    if (mediaPlay != null) {
      mediaPlay.seekTo(0);
      mediaPlay.stop();
      mediaPlay.release();
      mediaPlay = null;
    }
  }

  @Override protected void onResume() {
    super.onResume();
  }

  @Override protected void onPause() {
    super.onPause();
  }

  @Override protected void onStop() {
    super.onStop();
    stopAnimations();
    saveDataToSharedPerference(total_coins,currentIndex);
  }

  /**
   * 保存数据
   */
  private void saveDataToSharedPerference(int total_coins,int currentIndex) {
    //保存数据
    SharedPerferencesUtil.savePerfencesData(MainActivity.this, SharedPerferencesUtil.TOATL_COINS,
        total_coins);
    SharedPerferencesUtil.savePerfencesData(MainActivity.this, SharedPerferencesUtil.CURRENT_INDEX,
        currentIndex);
  }

  /**
   * 停止动画
   */
  private void stopAnimations() {
    if (null != pinLeftAnima) pinLeftAnima.cancel();
    if (null != discAnima) discAnima.cancel();
    if (null != pinRightAnima) pinRightAnima.cancel();
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    WechatUtil.clearSharedLocalImages();//删除文件
  }

  @Override public void OnWordClick(WordButton wordButton) {
    oprOnWordClick(wordButton, -1, false);
  }

  /**
   * 执行选字流程
   *
   * @param index 提示的不从第一个开始赋值
   */
  private void oprOnWordClick(WordButton wordButton, int index, boolean isPrompt) {
    if (selectCount < currentSong.getLength()) {
      setWordClickEvent(wordButton, index, isPrompt);
      selectCount++;
      //判断是否输入结束
      if (selectCount == currentSong.getLength()) {
        //判断答案是否正确
        String answer = "";
        for (int i = 0; i < listSelect.size(); i++) {
          answer += listSelect.get(i).getmWord();
        }

        if (answer.equals(currentSong.getName())) {
          //显示过关界面
          //Toast.makeText(MainActivity.this,"恭喜你答对了",Toast.LENGTH_LONG).show();
          showRightAnswerUi();
        } else {
          //字体闪烁
          //Toast.makeText(MainActivity.this,"再试试",Toast.LENGTH_LONG).show();
          mHandler.obtainMessage(MSG_CHANGE_COLOR).sendToTarget();
        }
      } else {
        for (int i = 0; i < listSelect.size(); i++) {
          if (!TextUtils.isEmpty(listSelect.get(i).getmButton().getText())) {
            listSelect.get(i).getmButton().setTextColor(Color.WHITE);
          }
        }
      }
    }
  }

  /**
   * 显示过关界面
   */
  private void showRightAnswerUi() {
    //清空计数器
    selectCount = 0;
    deleteCount = 0;
    //停止UI
    stopPlayMusic();
    stopAnimations();
    //清空数据
    clearListData();

    rightAnswerLinearlayout.setVisibility(View.VISIBLE);
    /*
     *计算全国排名 当前level/总关卡数
     */
    currentRanking.setText(String.format(getResources().getString(R.string.ranking),
        (double) (currentIndex * 100 / (songs.size()-1)) + "%"));
    currentLevelTv.setText(currentIndex + "");
    rightAnswerTv.setText(currentSong.getName());
    int rewardCount = currentIndex + 1;
    rewardCountTv.setText(rewardCount + "");

    total_coins += rewardCount;//总金币增加
    SharedPerferencesUtil.savePerfencesData(MainActivity.this, SharedPerferencesUtil.TOATL_COINS,
        total_coins);
  }

  /**
   * 处理待选文字点击事件
   */
  private void setWordClickEvent(WordButton wordButton, int index, boolean isPrompt) {
    //设置答案
    if (index == -1) {
      for (int i = 0; i < listSelect.size(); i++) {
        if (TextUtils.isEmpty(listSelect.get(i).getmWord())) {
          listSelect.get(i).setmWord(wordButton.getmWord());
          listSelect.get(i).getmButton().setText(wordButton.getmWord());
          listSelect.get(i).setmIsVisiable(wordButton.ismIsVisiable());
          listSelect.get(i).setIndex(wordButton.getIndex());
          break;
        }
      }
    } else {
      listSelect.get(index).setmWord(wordButton.getmWord());
      listSelect.get(index).getmButton().setText(wordButton.getmWord());
      listSelect.get(index).setmIsVisiable(wordButton.ismIsVisiable());
      listSelect.get(index).setIndex(wordButton.getIndex());
    }

    //设置待选文字可见性
    if (!isPrompt) setListWord(wordButton, View.INVISIBLE);
  }

  /**
   * 设置待选文字显示
   */
  private void setListWord(WordButton wordButton, int invisible) {
    wordButton.getmButton().setVisibility(invisible);
    wordButton.setmIsVisiable(invisible == View.VISIBLE ? true : false);
  }

  Handler mHandler = new Handler() {
    @Override public void handleMessage(Message msg) {
      switch (msg.what) {
        case MSG_CHANGE_COLOR:
          if (countTimes < CHANGE_COLOR_TIMES) {
            countTimes++;
            for (int i = 0; i < listSelect.size(); i++) {
              listSelect.get(i).getmButton().setTextColor(isChangeColor ? Color.RED : Color.WHITE);
            }
            isChangeColor = !isChangeColor;
            mHandler.sendEmptyMessageDelayed(MSG_CHANGE_COLOR, MSG_CHANGE_COLOR_DELAY);
          } else {
            mHandler.removeMessages(MSG_CHANGE_COLOR);
            countTimes = 0;
            isChangeColor = false;
          }
          break;
      }
    }
  };

  /**
   * 对话框点击事件处理
   */
  @Override public void onClick(boolean yesOrNo, int whichDialog) {
    if (yesOrNo)//点击yes
    {
      switch (whichDialog) {
        case Constant.PROMPT_DIALOG://提示一个答案
          coinTv.setText(total_coins + "");
          int indexPrompt = (int) (((Math.random() * currentSong.getLength())));
          promptOneAnswer(indexPrompt);
          break;
        case Constant.DELETE_DIALOG://删除一个答案
          coinTv.setText(total_coins + "");
          int indexDelete = (int) (((Math.random() * Constant.SELECT_WORD_LENGTH - 2)) + 1);
          deleteOneWord(indexDelete);
          break;
        case Constant.BUYCOIN_DIALOG://购买金币
          oprAddCoin();
          break;
      }
    }
  }

  @Override public void onClick(DialogInterface dialogInterface, int i) {
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode == 0){
      if (requestCode == REQUEST_CODE){
        //从结束界面返回
        //清空计数器
        selectCount = 0;
        deleteCount = 0;
        //停止UI
        stopPlayMusic();
        stopAnimations();
        //清空数据
        clearListData();
        //初始化数据
        currentIndex = 0;
        saveDataToSharedPerference(total_coins,currentIndex);

        //重现开始
        initSongs();
        initViews();
        //退出程序
        //finish();
        //清楚截屏的图片
      }
    }
  }
}

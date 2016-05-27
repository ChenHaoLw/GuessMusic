package ch.com.guessmusic.myview;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import ch.com.guessmusic.R;
import ch.com.guessmusic.entities.WordButton;
import ch.com.guessmusic.interfaces.IWordClickListener;
import ch.com.guessmusic.utils.AnimationUtil;
import ch.com.guessmusic.utils.LayoutUtil;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chenhao on 2016/5/19.
 */
public class WordGridView extends GridView{

  private Context mContext = null;
  private List<WordButton> words = new ArrayList<WordButton>();//数据
  private WordAdapter mAdapter = new WordAdapter();
  private IWordClickListener iWordClickListener = null;

  public WordGridView(Context context, AttributeSet attrs) {
    super(context, attrs);
    mContext = context;
  }

  /**
   * 更新数据
   * @param list
   */
  public void updateWords(ArrayList<WordButton> list){
     words = list;
    //设置数据源
     setAdapter(mAdapter);
  }

  /**
   * 适配器
   */
  class WordAdapter extends BaseAdapter{
    @Override public int getCount() {
      return words.size();
    }

    @Override public Object getItem(int i) {
      return words.get(i);
    }

    @Override public long getItemId(int i) {
      return i;
    }

    @Override public View getView(final int i, View view, ViewGroup viewGroup) {
      WordButton viewHolder = null;
      if(view == null){
        viewHolder = words.get(i);
        view = LayoutUtil.getView(mContext, R.layout.grid_item);
        viewHolder.setmButton((Button) view.findViewById(R.id.id_grid_button));
        view.setTag(viewHolder);
      }else{
        viewHolder = (WordButton) view.getTag();
      }
      viewHolder.getmButton().setText(viewHolder.getmWord());
      //添加动画
      Animation scaleAnima = AnimationUtil.rotate(mContext,view,R.anim.scale,true,0);
      scaleAnima.setStartOffset(i*100);
      viewHolder.getmButton().setOnClickListener(new OnClickListener() {
        @Override public void onClick(View view) {
          if (iWordClickListener != null){
            iWordClickListener.OnWordClick(words.get(i));
          }
        }
      });
      return view;
    }
  }

  /**
   * 注册点击事件回调
   * @param listener
   */
  public void registerWordClickListener(IWordClickListener listener){
    this.iWordClickListener = listener;
  }
}

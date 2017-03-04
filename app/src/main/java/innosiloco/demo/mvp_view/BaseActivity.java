package innosiloco.demo.mvp_view;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import de.greenrobot.event.EventBus;
import innosiloco.demo.R;
import innosiloco.demo.utils.DialogCreatUtil;

/**
 * Created by ron on 2016/5/18.
 */
public abstract class BaseActivity extends Activity
{

    protected DialogCreatUtil dialogCreatUtil;
    protected boolean ViewHadDestory = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        dialogCreatUtil = new DialogCreatUtil();
//        setWindowParams();
        setContentView(getContentView());
        findViews();
        initViews();
        initLisenter();
        ViewHadDestory = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }
    protected void  setWindowParams()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {//5.0 全透明实现
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);//calculateStatusColor(Color.WHITE, (int) alphaValue)
        } else
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {//4.4 全透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

    }


    /**
     * <p>使用findViewById方法，找到所有的Views</p>
     * <hr/>
     * <p>主要是为了方便优化维护</p>
     */
    public abstract  void findViews();

    /**
     * <p>初始化Views</p>
     * <hr/>
     * <p>主要是为了方便优化维护</p>
     */
    public abstract  void initViews();

    /**
     * <p>设置Views的监听事件</p>
     * <hr/>
     * <p>主要是为了方便优化维护</p>
     */
    public abstract  void initLisenter();

    /**
     * <p>设置setContentView的Res文件ID号</p>
     * @return
     */
    public abstract int getContentView();


    public void onHeadBack(View v)
    {
        this.finish();
    }

    /**
     * <p>设置标题</p>
     * @param title
     */
    public void setTitle(String title)
    {
        TextView textView = (TextView)findViewById(R.id.tv_head_title);
        textView.setText(title);
    }

    /**
     * <p>结束当前的Activity</p>
     */
    public void exit()
    {
        this.finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        exit();
    }

    @Override
    protected void onDestroy()
    {
        ViewHadDestory = true;
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}

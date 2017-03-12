package innosiloco.demo.utils;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import innosiloco.demo.MyApp;
import innosiloco.demo.R;

/**
 * Created by ronya on 2017/3/12.
 */

public class CheckKeyUIUtil
{
    private TextView textView;
    private Context context;
    private boolean isSuccess;
    private String key;
    private String titleLabel;
    private final int ShowTime = 2000;
    public  CheckKeyUIUtil(TextView textView,Context context,String titleLabel)
    {
        this.textView = textView;
        this.titleLabel = titleLabel;
        this.context = context;
    }

    public void setCheckResult(boolean isSuccess)
    {
//        Toast.makeText(MyApp.getSingleApp(),"Result:" + isSuccess,Toast.LENGTH_LONG).show();
        this.isSuccess = isSuccess;

//        handler.sendEmptyMessageDelayed(3,ShowTime);
    };

    public void onDestory()
    {
        handler.removeMessages(1);
        handler.removeMessages(2);
        handler.removeMessages(3);
        handler.removeMessages(4);
    }

    private Handler handler = new Handler(Looper.getMainLooper())
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case 1:
                    break;
                case 2:
                    pufCheck(isSuccess);

                    break;
                case 3:
                    checkResult(isSuccess);
                    handler.sendEmptyMessageDelayed(4,ShowTime);
                    break;
                case 4:
                    checkOver(isSuccess);
                    break;
            }
        }
    };

    public void beginCheck(boolean isSucess,String key)
    {
        this.isSuccess = isSucess;
        this.key = key;
        textView.setTextColor(Color.BLACK);
        if(AppConfig.isServce)
        {
            this.textView.setText(context.getString(R.string.Label_Server_Begin));
        }else
        {
            this.textView.setText(context.getString(R.string.Label_Client_begin));
        }
        handler.sendEmptyMessageDelayed(2,ShowTime);
    }

    public void pufCheck(boolean isSuccess)
    {
        if(AppConfig.isServce)
        {
            if(isSuccess)
            {
                this.textView.setText(context.getString(R.string.Label_CheckKey_SuccessPuf));
            }else
            {
                this.textView.setText(context.getString(R.string.Label_CheckKey_ErrPuf));
            }

        }else
        {
            this.textView.setText(context.getString(R.string.Label_Client_Checking));
        }

        handler.sendEmptyMessageDelayed(3,ShowTime);
    }

    public void checkResult(boolean isSuccess)
    {
        if(AppConfig.isServce)
        {
            if(isSuccess)
                this.textView.setText(context.getString(R.string.Label_Server_CheckSuccess));
            else
                this.textView.setText(context.getString(R.string.Label_Server_CheckErr));
        }else
        {
            if(isSuccess)
                this.textView.setText(context.getString(R.string.Label_Client_CheckSuccess));
            else
                this.textView.setText(context.getString(R.string.Label_Client_CheckErr));
        }
    }

    public void checkOver(boolean isSuccess)
    {
        if(!isSuccess)
        {
            textView.setText(R.string.keyIsloss);
            textView.setTextColor(Color.RED);
        }else
        {
            if(AppConfig.isServce )
            {
                if(TextUtils.isEmpty(AESKeyUitl.getSingleton().getEncode_key()))
                {
                    textView.setText(R.string.keyIsloss);
                    textView.setTextColor(Color.RED);
                }
            }
            textView.setText(titleLabel);
            textView.setTextColor(Color.BLACK);
        }
    }

}

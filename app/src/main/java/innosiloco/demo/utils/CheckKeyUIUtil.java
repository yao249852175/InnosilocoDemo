package innosiloco.demo.utils;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import innosiloco.demo.MyApp;
import innosiloco.demo.R;

/**
 * Created by ronya on 2017/3/12.
 */

public class CheckKeyUIUtil
{
    private Context context;
    private boolean isSuccess;
    private final int ShowTime = 2000;
    private ListView listView;
    private Button button;
    private String key;
    private List<SpannableStringBuilder> data;
    private Button bottomBtn;
    public  CheckKeyUIUtil(Context context, ListView listView, Button button,Button bottomBtn)
    {
        this.bottomBtn = bottomBtn;
        this.context = context;
        this.listView = listView;
        this.button = button;
        this.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.clear();
                baseAdapter.notifyDataSetChanged();
            }
        });
        this.data = new ArrayList<>();
        listView.setAdapter(baseAdapter);
    }

    public void setCheckResult(boolean isSuccess)
    {
//        Toast.makeText(MyApp.getSingleApp(),"Result:" + isSuccess,Toast.LENGTH_LONG).show();
        this.isSuccess = isSuccess;

//        handler.sendEmptyMessageDelayed(3,ShowTime);
    };

    public void addNewLog(SpannableStringBuilder log)
    {
        data.add(log);
        listView.setSelection(baseAdapter.getCount() -1);
        baseAdapter.notifyDataSetChanged();
    }

    public void addNewLog(String log)
    {

        data.add(new SpannableStringBuilder(log));
        baseAdapter.notifyDataSetChanged();
        listView.setSelection(baseAdapter.getCount() -1);
    }

    private BaseAdapter baseAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            View v = LayoutInflater.from(context).inflate(R.layout.item_log,null);
            TextView logText =(TextView) v
                    .findViewById(R.id.tv_Log);
            logText.setText(data.get(position));
            return v;
        }
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
                    break;
            }
        }
    };

    public void beginCheck(boolean isSucess,String key)
    {
        this.isSuccess = isSucess;
        this.key = key;
        if(AppConfig.isServce)
        {
           addNewLog(context.getString(R.string.Label_Server_Begin));
        }else
        {
//           addNewLog(context.getString(R.string.Label_Client_begin));
        }
        handler.sendEmptyMessageDelayed(2,ShowTime);
    }

    public void pufCheck(boolean isSuccess)
    {
        if(AppConfig.isServce)
        {
            if(isSuccess)
            {
                addNewLog(context.getString(R.string.Label_CheckKey_SuccessPuf));
            }else
            {
                addNewLog(context.getString(R.string.Label_CheckKey_ErrPuf));
            }

        }else
        {
            addNewLog(context.getString(R.string.Label_Client_Checking));
        }

        handler.sendEmptyMessageDelayed(3,ShowTime);
    }

    public void checkResult(boolean isSuccess)
    {
        if(AppConfig.isServce)
        {
            if(isSuccess)
                addNewLog(context.getString(R.string.Label_Server_CheckSuccess));
            else
                addNewLog(context.getString(R.string.Label_Server_CheckErr));
        }else
        {
            if(isSuccess)
                addNewLog(context.getString(R.string.Label_Client_CheckSuccess));
            else
                addNewLog(context.getString(R.string.Label_Client_CheckErr));
        }
        bottomBtn.setEnabled(true);
    }


}

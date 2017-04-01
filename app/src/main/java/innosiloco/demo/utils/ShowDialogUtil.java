package innosiloco.demo.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import innosiloco.demo.R;

/**
 * Created by Administrator on 2017/4/1.
 */
public class ShowDialogUtil
{
    private Dialog dialog;
    private ListView listView;
    private Button clear;
    private Activity activity;

    private List<String> logsList;

    private boolean hadGetLog;

    private Thread thread;

    /**************
     * ron*************
     * 显示日志的弹框
     *
     * @param activity
     */
    public void showLogDialog(Activity activity) {
        logsList = new ArrayList<>();
        hadGetLog = true;
        this.activity = activity;
        thread = new Thread(runnable);
        thread.start();
        dialog = new Dialog(activity, R.style.myDialog);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialogWindow.setAttributes(lp);
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_showlog, null);
        listView = (ListView) view.findViewById(R.id.list_showLog);
        clear = (Button) view.findViewById(R.id.clear_showLog);
        listView.setAdapter(baseAdapter);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logsList.clear();
                baseAdapter.notifyDataSetChanged();
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                hadGetLog = false;
                thread.interrupt();
            }
        });
        dialog.setContentView(view);
        dialog.show();
    }

    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            baseAdapter.notifyDataSetChanged();
        }
    };

    private BaseAdapter baseAdapter = new BaseAdapter() {
        @Override
        public int getCount()
        {
            return logsList.size();
        }

        @Override
        public Object getItem(int position)
        {
            return position;
        }

        @Override
        public long getItemId(int position)
        {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            View view = LayoutInflater.from(activity).inflate(R.layout.item_showlog,null);
            TextView textView = (TextView) view.findViewById(R.id.txt_showLog);
            textView.setText(logsList.get(position));
            return view;
        }
    };

    /*********ron**************
     *  获取
     */
    private Runnable runnable = new Runnable()
    {
        @Override
        public void run()
        {

            while (hadGetLog)
            {
                try {

                    while (true)
                    {
                        if(RonLog.sendLogs.size()< 1)
                        {
                            break;
                        }
                        String log =  RonLog.sendLogs.poll();
                        if(!TextUtils.isEmpty(log))
                        {
                            logsList.add(log);
                            if(logsList.size() > 500)
                            {
                                logsList.remove(0);
                            }
                        }

                    }
                    handler.sendEmptyMessage(0);
                   Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };
}

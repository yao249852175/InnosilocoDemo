package innosiloco.demo.utils;



import android.app.Activity;
import android.app.Dialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import innosiloco.demo.R;

public class DialogCreatUtil
{
	
	
	/**
	 * 描述：是否显示 可以杀死app的 tag
	 */
	public final String HintCanKillappTag= "HintCanKillappTag"; 
	
	
	/**
	 * 描述：退出app
	 */
	public final String ExitTag = "ExitTag";
	
	/**
	 * 描述：重试同步数据
	 */
	public final String ReSysDataTag ="ReSysDataTag";
	
	
	/**
	 * 描述：重试连接蓝牙
	 */
	public final String ReTryConnBleTAG = "ReTryConnBleTAG" ;
	
	
	private Dialog singleBtnDialog;

	public void showSingleBtnDialog(String title, String intro, Activity activity)
	{
		showSingleBtnDialog(title,intro,activity,null);
	}

	/**
	 * 描述：单按钮提示dialog
	 * @param title
	 * @param intro
	 */
	public void showSingleBtnDialog(String title, String intro, Activity activity, final OnClickListener onClickListener)
	{
		if(singleBtnDialog != null && singleBtnDialog.isShowing())
		{
			singleBtnDialog.dismiss();
			singleBtnDialog = null;
		}
		singleBtnDialog =  new Dialog(activity, R.style.myDialog);
		View v = LayoutInflater.from(activity).inflate(R.layout.dialog_layout, null);
		TextView dialogT = (TextView) v.findViewById(R.id.tv_dialog_title);
		if(title == null)
		{
			dialogT.setVisibility(View.GONE);
		}else
		{
			dialogT.setText(title);
		}
		TextView dContent = (TextView) v.findViewById(R.id.tv_dialog_intro);
		dContent.setText(intro);
		View ok = v.findViewById(R.id.tv_ok);
		ok.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0) 
			{
				singleBtnDialog.dismiss();
				if(onClickListener != null )
				{
					onClickListener.onClick(arg0);
				}
			}
		});

		
		singleBtnDialog.setContentView(v);
		singleBtnDialog.setCanceledOnTouchOutside(false);
		singleBtnDialog.show();
		
	}
	
	
	private Dialog progressDialog;
	

	
	public boolean cancelProgressDialog()
	{
		if( progressDialog != null && progressDialog.isShowing() )
		{
			progressDialog.dismiss();
			progressDialog = null;
			return true;
		}else
		{
			return false;
		}
	}
	
	/**
	 * 描述：判断当前的网络是否已经连接
	 * @return
	 */
	public boolean ProgressDialogIsShow()
	{
		if( progressDialog != null && progressDialog.isShowing() )
		{
			return true;
		}else
		{
			return false;
		}
	}
	
	/**
	 * 描述：是不是还有其他的dialog弹出
	 * @return
	 */
	public boolean haveSomeDialogShow()
	{
		if(progressDialog != null && progressDialog.isShowing() )
			return true;
		else if(singleBtnDialog != null && singleBtnDialog.isShowing())
		{
			return true;
		}else if(DialogWith2Btn != null && DialogWith2Btn.isShowing())
		{
			return true;
		}else
		{
			return false;
		}
	}
	
	/**
	 * 描述：连接蓝牙 重试的次数记录
	 */
	public byte TryTimes = 0;
	
	
	
	
	/**
	 * 描述：同步失败的弹出框
	 */
	private Dialog DialogWith2Btn;
	
	/**
	 * 描述：记录当前的showDialogWith2Btn 的Tag
	 */
	public String DialogWith2Btn_TAG = "";
	
	
	/**
	 * 描述：
	 * @param contentTxt
	 * @param cancelTxt
	 * @param okTxt
	 * @param onClickLis
	 */
	public void showDialogWith2Btn(int titleTxt,int contentTxt,int cancelTxt,
			int okTxt,String tag,OnClickListener onClickLis,Activity activity)
	{
		if(activity==null|| activity.isFinishing()) return;
		if(DialogWith2Btn !=null && DialogWith2Btn.isShowing())
		{
			DialogWith2Btn.dismiss();
			DialogWith2Btn = null;
		}
		
		DialogWith2Btn_TAG =  tag ;
		
		DialogWith2Btn = new Dialog(activity, R.style.myDialog);
		View content = LayoutInflater.from(activity).inflate(R.layout.dialog_defaultlayout, null);
		TextView title = (TextView) content.findViewById(R.id.tv_title);
		
		TextView con = (TextView) content.findViewById(R.id.tv_content);
		/*if(tag.equals(HintCanKillappTag)&& SleepUtill.isZHVersion(activity))
		{
			con.setVisibility(View.GONE);
			WebView web = (WebView) content.findViewById(R.id.tv_content1);
			web.setVisibility(View.VISIBLE);
			web.setVerticalScrollBarEnabled(false);
			web.setBackgroundColor(0);
			String info = SleepUtill.getWebViewContent(activity.getString(contentTxt), Constant.HTML_TEXT_SIZE,Color.BLACK, 100);
			web.loadDataWithBaseURL("", info, "text/html", "utf-8", ""); 
		}else
		{
		
		}*/
		
		if(tag.equals(HintCanKillappTag))
		{
			con.setGravity(Gravity.LEFT);
		}else
		{
			con.setGravity(Gravity.CENTER);
		}
		
		con.setText(contentTxt);
		TextView close = (TextView) content.findViewById(R.id.tv_close);
		close.setText(cancelTxt);
		close.setTag(tag);
		close.setOnClickListener(onClickLis);
		TextView ok = (TextView) content.findViewById(R.id.tv_ok);
		ok.setTag(tag);
		ok.setOnClickListener(onClickLis);
		ok.setText(okTxt);
		
		if(titleTxt ==0)
			title.setVisibility(View.GONE);
		else
			title.setText(titleTxt);
		DialogWith2Btn.setCanceledOnTouchOutside(false);
		DialogWith2Btn.setContentView(content);
		DialogWith2Btn.show();
		
		
		
	}
	
	public void cancel2BtnDialog()
	{
		if(DialogWith2Btn !=null && DialogWith2Btn.isShowing())
		{
			DialogWith2Btn.dismiss();
			DialogWith2Btn = null;
		}
	}
	
	
	private Dialog iconDialog;
	

	
	public void cancelIconDialog()
	{
		if( iconDialog != null && iconDialog.isShowing() )
		{
			iconDialog.dismiss();
			iconDialog = null;
		}
	}
	
}

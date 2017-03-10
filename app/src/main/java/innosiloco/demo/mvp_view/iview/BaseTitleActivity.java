package innosiloco.demo.mvp_view.iview;


import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import innosiloco.demo.R;

/**
 * 
 * @ClassName: BaseTitleActivity
 * @Description: 带有标题栏的界面的基类
 * @author:WH
 * @date: 2015年7月16日 上午9:15:57
 */
public abstract class BaseTitleActivity extends FragmentActivity {
//	private final int				MENUE_REQUEST=0x0001000;
	private ImageButton			leftBtn;
	protected TextView			titleTxt;
	protected Button			rightBtn;
	private ImageButton			rightImgButton;
	private ImageButton			rightSecondBtn;
	public  boolean              isCheck=true;
	private Dialog 	            waitDlg;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getLayout());
		initUi();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	@Override
	protected void onResume() {
		super.onResume();
		checkIsCrash(isCheck);
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		checkIsCrash(isCheck);
	}
	
	protected void checkIsCrash(boolean isCheck){
	}
	
	/**
	 * 初始化控件
	 */
	protected void initUi(){
		leftBtn=(ImageButton) findViewById(R.id.title_bar_back_btn);
		rightBtn=(Button) findViewById(R.id.title_bar_right_btn);
		titleTxt=(TextView) findViewById(R.id.tile_bar_title_txt);
		rightImgButton=(ImageButton) findViewById(R.id.title_bar_right_img_btn);
		rightSecondBtn=(ImageButton) findViewById(R.id.title_bar_right_second_btn);
		
		rightSecondBtn.setOnClickListener(baseOnclick);
		rightImgButton.setOnClickListener(baseOnclick);
		leftBtn.setOnClickListener(baseOnclick);
		rightBtn.setOnClickListener(baseOnclick);
		String[] texts=getTitles();
		titleTxt.setText(texts[0]);
		if(texts[1].length()<=0)
			rightBtn.setVisibility(View.GONE);
		else
			rightBtn.setText(texts[1]);
		
	}
	
	/**
	 * 顶部按钮点击事件
	 */
	private View.OnClickListener baseOnclick=new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.title_bar_back_btn:
				titleLeftBtnClick();
				break;
			case R.id.title_bar_right_btn:
			case R.id.title_bar_right_img_btn:
				titleRightBtnClick();
				break;
			case R.id.title_bar_right_second_btn:
				titleSecondBtnClick();
				break;
			}
		}
	};
	
	protected	String[] getStrings(int res[]){
		String[] strs=new String[res.length];
		for(int i=0;i<res.length;i++){
			if(res[i]==-1)
				strs[i]="";
			else
				strs[i]=getResources().getString(res[i]);
		}
		return strs;
	}
	
	/**
	 * 获取界面layout
	 */
	abstract int getLayout();
	/**
	 * 获取界面顶部显示的标题文字
	 */
	abstract String[] getTitles();
	/**
	 * 顶部标题栏左侧按钮点击事件
	 */
	abstract void titleLeftBtnClick();
	/**
	 * 顶部标题栏右侧点击按钮
	 */
	protected void titleRightBtnClick(){};
	
	/**
	 * 顶部右边第二个按钮点击
	 */
	protected void titleSecondBtnClick(){};
	
	
	/**
	 * 设置标题栏右边按钮的文字
	 */
	protected void setTitleRightTxt(String text){
		rightBtn.setText(text);
	}
	
	protected void setRightBtnEnable(boolean enable){
		rightBtn.setEnabled(enable);
		if(enable==false)
			rightBtn.setTextColor(getResources().getColor(R.color.disable_color));
		else
			rightBtn.setTextColor(getResources().getColor(R.color.white));
	}
	
	/**
	 * 在聊天模式设置显示状态
	 */
	protected void setChatMode(boolean isGroup){
	}
	
	protected void setRightBtnInvisible(){
		this.rightSecondBtn.setVisibility(View.GONE);
		this.rightImgButton.setVisibility(View.GONE);
	}
	
	/**
	 * 显示底部菜单
	 */
	protected void createMenue(String[] menus,int [] ids){
	}
	
	/**
	 * 创建自定义上下文菜单
	 */
	protected void createContextMenu(String[] menues,int[] ids,final Object object){
	}
	
	/**
	 * 菜单选择事件
	 */
	protected void onMenuSelected(int menuId){}
	
	/**
	 * 自定义上下文菜单选择事件
	 */
	protected void onContexMenueSelected(int menueId,Object object){}
	
	protected void setTitleTextColor(int color){
		titleTxt.setTextColor(color);
	}
	
	protected void setTitleTxt(int res){
		titleTxt.setText(res);
	}
	
	protected void setTitleTxt(String res){
		titleTxt.setText(res);
	}
	
	protected void setTitleTextSize(int textSize){
		titleTxt.setTextSize(textSize);
	}
	
	protected void setTitleBarTransparent(){
		leftBtn.setBackgroundColor(Color.TRANSPARENT);
	}
	
	protected void setLeftBtnVisible(int visible){
		leftBtn.setVisibility(visible);
	}
	
	protected void setRightBtnVisible(int visible){
		rightBtn.setVisibility(View.GONE);
	}
	
	protected   void showWaitDialog(int strRes){
	}
	
	protected void dissmissWaitDlg(){
		if(waitDlg!=null){
			waitDlg.dismiss();
			waitDlg=null;
		}
	}
	
	protected void cancelDlg(){}


	
}

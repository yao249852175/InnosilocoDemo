package innosiloco.demo.utils;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import innosiloco.demo.R;

/**
 * 
 * @ClassName: ChattingFootLayout
 * @Description: 聊天界面底部弹出界面组件
 * @author:ZCS
 * @date: 2015年7月23日 上午9:52:55
 */
public class ChattingFootLayout extends LinearLayout implements View.OnClickListener,OnItemClickListener{
	private 			Context		context;
	private 			int 				current = 0;
	private				Button			voiceModeBtn;
	private 			Button			keyboardModeBtn;
	private				Button			sendBtn;
	private				Button			moreBtn;
	private				TextView		pressTotalkBtn;
	private				EditText		msgEdt;
	private				ViewPager	viewPager;
	private 			LinearLayout	pointLayout;
	private				RelativeLayout	edtLayout;
	private				View					moreLayout;
	private 			View					faceListLayout;
	private 			boolean			isEmojiChecked=false;
	private				boolean           isMoreChecked=false;
	private 			ArrayList<View> 					pageViews;
	private 			ArrayList<ImageView> 			pointViews;
	
	public ChattingFootLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context=context;
	}
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		//初始化控件
		voiceModeBtn=(Button) findViewById(R.id.voice_mode_btn);
		voiceModeBtn.setOnClickListener(this);
		keyboardModeBtn=(Button) findViewById(R.id.keyboard_mode_btn);
		keyboardModeBtn.setOnClickListener(this);
		sendBtn=(Button) findViewById(R.id.send_msg_btn);
		pressTotalkBtn=(TextView) findViewById(R.id.press_to_speak_btn);
		moreBtn=(Button) findViewById(R.id.more_btn);
		moreBtn.setOnClickListener(this);
		msgEdt=(EditText) findViewById(R.id.message_edt);
		moreBtn.setOnClickListener(this);
		edtLayout=(RelativeLayout) findViewById(R.id.edittext_layout);
		viewPager=(ViewPager) findViewById(R.id.pager);
		pointLayout=(LinearLayout) findViewById(R.id.point_layout);
		moreLayout=findViewById(R.id.more_layout);
		faceListLayout=findViewById(R.id.face_list_layout);
		int defaultHeight=BitmapUtils.dip2px(context, 210);
		int height=defaultHeight;
		LayoutParams params=(LayoutParams) faceListLayout.getLayoutParams();
		params.height=height;
		faceListLayout.setLayoutParams(params);
		
		params=(LayoutParams) moreLayout.getLayoutParams();
		params.height=height;
		moreLayout.setLayoutParams(params);
		
		
		//监听EditText 的焦点变化情况
		msgEdt.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					edtLayout.setBackgroundResource(R.drawable.input_bar_bg_active);
				} else {
					edtLayout.setBackgroundResource(R.drawable.input_bar_bg_normal);
				}
			}
		});
		
		//监听EditText 的输入变化情况
		msgEdt.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(TextUtils.isEmpty(s)){
					moreBtn.setVisibility(VISIBLE);
					sendBtn.setVisibility(GONE);
					return;
				}
				
				moreBtn.setVisibility(GONE);
				sendBtn.setVisibility(VISIBLE);
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {}
			@Override
			public void afterTextChanged(Editable s) {	}
		});
		
		msgEdt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				editRequireFoucus();
				isMoreChecked=false;
				isEmojiChecked=false;
				showKeyboard(v);
				moreLayout.postDelayed(new Runnable() {
					@Override
					public void run() {
						((Activity)context).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE|
								WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
						moreLayout.setVisibility(GONE);
						faceListLayout.setVisibility(GONE);
					}
				}, 300);
			}
		});
		
		//初始化ViewPager
//		initViewpager();
//		//初始化indicator
//		Init_Point();
//		//初始化数据
//		Init_Data();
		
	}
	
	public void hideLayout(){
		((Activity)context).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE|
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		isMoreChecked=false;
		isEmojiChecked=false;
		moreLayout.setVisibility(GONE);
		faceListLayout.setVisibility(GONE);
		hideKeyboardForce(msgEdt);
	}
	

	private void editRequireFoucus(){
		msgEdt.setFocusable(true);
		msgEdt.setFocusableInTouchMode(true);
		msgEdt.requestFocus();
	}
	
	private void setEditVisible(){
		voiceModeBtn.setVisibility(VISIBLE);
		edtLayout.setVisibility(VISIBLE);
		pressTotalkBtn.setVisibility(GONE);
		keyboardModeBtn.setVisibility(GONE);
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.voice_mode_btn:
			((Activity)context).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE|
			WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
			hideKeyboardForce(msgEdt);
			voiceModeBtn.setVisibility(GONE);
			edtLayout.setVisibility(GONE);
			pressTotalkBtn.setVisibility(VISIBLE);
			keyboardModeBtn.setVisibility(VISIBLE);
			isMoreChecked=false;
			moreLayout.setVisibility(GONE);
			isEmojiChecked=false;
			faceListLayout.setVisibility(GONE);
			break;
		case R.id.keyboard_mode_btn:
			setEditVisible();
			hideKeyboard();
			break;
		case R.id.more_btn:
			((Activity)context).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
			moreLayout.setVisibility(VISIBLE);
			setEditVisible();
			if(isMoreChecked==true){
				editRequireFoucus();
				moreBtn.setFocusable(false);
				showKeyboard(msgEdt);
				moreLayout.postDelayed(new Runnable() {
					@Override
					public void run() {
						((Activity)context).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE|
								WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
						moreLayout.setVisibility(GONE);
					}
				}, 300);
			}else{
				msgEdt.setFocusable(false);
				moreBtn.setFocusable(true);
				hideKeyboardForce(msgEdt);
			}
			isMoreChecked=!isMoreChecked;
			isEmojiChecked=false;
			faceListLayout.setVisibility(GONE);
			hideKeyboard();
			break;
		}
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,long id) {


	}
	
	/**
	 * 隐藏软键盘
	 */
	private void hideKeyboard() {
		InputMethodManager manager = (InputMethodManager) ((Activity)context).getSystemService(Context.INPUT_METHOD_SERVICE);
		if (((Activity)context).getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (((Activity)context).getCurrentFocus() != null)
				manager.hideSoftInputFromWindow(((Activity)context).getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}
	
	public void hideKeyboardForce(View v){
		InputMethodManager manager = (InputMethodManager) ((Activity)context).getSystemService(Context.INPUT_METHOD_SERVICE);
		manager.hideSoftInputFromWindow( v.getApplicationWindowToken( ) , 0 );   
	}
	private void showKeyboard(View v){
		InputMethodManager manager = (InputMethodManager) ((Activity)context).getSystemService(Context.INPUT_METHOD_SERVICE);
		manager.showSoftInput(v,InputMethodManager.SHOW_FORCED);    
	}
	
	/**
	 * @ClassName: OnCorpusSelectedListener
	 * @Description: 表情选择监听类
	 * @author:ZCS
	 * @date: 2015年8月24日 下午4:51:19
	 */

	
}

package innosiloco.demo.utils;

import innosiloco.demo.MyApp;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

/**
 * 
 * @ClassName: SharedPreferenceUtils
 * @Description: 操作sharedpreference的工具类
 * @author:WH
 * @date: 2015年7月16日 上午10:47:04
 */
public class SharedPreferenceUtils {
	private static SharedPreferences getLocalSharedPreference(){
		return PreferenceManager.getDefaultSharedPreferences(MyApp.getSingleApp());
	}
	
	public static String getSharedPreferenceStringValue(String key){
		SharedPreferences localSharedPreference=getLocalSharedPreference();
		String str="";
		if(localSharedPreference!=null){
			str=localSharedPreference.getString(key, "");
		}
		return str;
	}
	
	public static int getSharedPreferenceIntValue(String key){
		SharedPreferences localSharedPreference=getLocalSharedPreference();
		int value=0;
		if(localSharedPreference!=null){
			value=localSharedPreference.getInt(key, 0);
		}
		return value;
	}
	
	public static int getSharedPreferenceIntValue(String key,int defaultValue){
		SharedPreferences localSharedPreference=getLocalSharedPreference();
		int value = defaultValue;
		if(localSharedPreference!=null){
			value=localSharedPreference.getInt(key, defaultValue);
		}
		return value;
	}
	public static void setSharedPreferenceValue(String key,int value){
		SharedPreferences localSharedPreference=getLocalSharedPreference();
		Editor editor=localSharedPreference.edit();
		editor.putInt(key, value);
		editor.commit();
	}
	

	public static void setSharedPreferenceValue(String key,String value){
		SharedPreferences localSharedPreference=getLocalSharedPreference();
		Editor editor=localSharedPreference.edit();
		editor.putString(key, value);
		editor.commit();
	}
	
	public static void setSharedPreferenceValue(String key,boolean value){
		SharedPreferences localSharedPreference=getLocalSharedPreference();
		Editor editor=localSharedPreference.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}
	
	public static boolean getSharedPreferenceBooleanValue(String key,boolean defaultValue){
		SharedPreferences localSharedPreference=getLocalSharedPreference();
		boolean value = defaultValue;
		if(localSharedPreference!=null){
			value=localSharedPreference.getBoolean(key, defaultValue);
		}
		return value;
	}
}

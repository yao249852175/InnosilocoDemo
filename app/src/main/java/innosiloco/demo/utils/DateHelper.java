package innosiloco.demo.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import innosiloco.demo.MyApp;
import innosiloco.demo.R;


/**
 *
* @Title: 					DateHelper.java
* @Package 				duoduo.app.utils
* @Description: 		���ڴ������ڸ�ʽ���Ĺ�����
* @author 				ZCS
* @date 					2015��3��14�� ����10:34:16
 */
public class DateHelper {
	private static String serverTime="";
	@SuppressLint("SimpleDateFormat")
	public static String getCurrentDate(){
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//�������ڸ�ʽ
		return df.format(new Date());
	}

	@SuppressLint("SimpleDateFormat")
	public static String getTime(String dateStr) {
		if(dateStr==null)
			return null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date;
		try {
			date = sdf.parse(dateStr);
			Calendar old = Calendar.getInstance();
			old.setTime(date);

			Date nowDate = new Date();
			Calendar now = Calendar.getInstance();
			now.setTime(nowDate);
			int y=old.get(Calendar.YEAR);
			int m=old.get(Calendar.MONTH)+1;
			int d=old.get(Calendar.DAY_OF_MONTH);
			int h=old.get(Calendar.HOUR_OF_DAY);
			int mi=old.get(Calendar.MINUTE);
			int curY=now.get(Calendar.YEAR);
			String time="";
			if(y==curY){
				int xcts = now.get(Calendar.DAY_OF_YEAR)-old.get(Calendar.DAY_OF_YEAR);
				if(xcts==0)
					time= MyApp.getSingleApp().getResources().getString(R.string.today)+" "+(h<10? "0"+h:h)+":"+(mi<10? "0"+mi:mi);
				else if(xcts==1)
					time=MyApp.getSingleApp().getResources().getString(R.string.yesterday)+" "+(h<10? "0"+h:h)+":"+(mi<10? "0"+mi:mi);
				else
					time =(m<10? "0"+m:m)+MyApp.getSingleApp().getResources().getString(R.string.month)+(d<10? "0"+d:d)+"��";
			}else{
				time=y+MyApp.getSingleApp().getResources().getString(R.string.year)+(m<10? "0"+m:m)+MyApp.getSingleApp().getResources().getString(R.string.month)+(d<10? "0"+d:d)+MyApp.getSingleApp().getResources().getString(R.string.day);
			}

			return time;
		} catch (ParseException e) {
			e.printStackTrace();
			return dateStr;
		}
	}

	/**
     *
     * @param timeLength Millisecond
     * @return
     */
    public static String toTime(int timeLength) {
        timeLength /= 1000;
        int minute = timeLength / 60;
        int hour = 0;
        if (minute >= 60) {
            hour = minute / 60;
            minute = minute % 60;
        }
        int second = timeLength % 60;
        // return String.format("%02d:%02d:%02d", hour, minute, second);
        return String.format("%02d:%02d", minute, second);
    }

	public static String formatRecordTime(int len){
		if(len<60)
			return String.format("%d''", len);
		else{
			int m=len/60;
			int s=len%60;
			return String.format("%d'%d''", m,s);
		}
	}

	/**
	 * ��ʽ��ͨ��ʱ��
	 */
	public static String millionsCountFromat(int ticCount){
		int second=ticCount/1000;
		int minute=second/60;
		int s=second-minute*60;
		return String.format("%s:%s", minute>9?minute:"0"+minute,s>9?s:"0"+s);

	}

	public static String formatDateString(Context context, long time) {
        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(context);
        DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(context);
        Date date = new Date(time);
        return dateFormat.format(date) + " " + timeFormat.format(date);
    }

	/**
	 * ��ʱ������һ��
	 */
	@SuppressLint("SimpleDateFormat") public static String  incTime(String time){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			long millionSeconds=sdf.parse(time).getTime();
			millionSeconds+=1000;
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(millionSeconds);
			String strTime=sdf.format(calendar.getTime());
			return strTime;
		} catch (ParseException e) {
			e.printStackTrace();
			return time;
		}
	}
}

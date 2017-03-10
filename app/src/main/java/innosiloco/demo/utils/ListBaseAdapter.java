package innosiloco.demo.utils;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 
 * @ClassName: ListBaseAdapter
 * @Description: 所有ListView 数据适配器的基类
 * @author:WH
 * @date: 2015年7月21日 上午10:00:32
 * @param <T>
 */
public abstract class ListBaseAdapter<T> extends BaseAdapter{
	protected Context	context;
	protected List<T> 	datas;
	public ListBaseAdapter(Context context) {
		this.context=context;
	}
	@Override
	public int getCount() {
		return datas.size();
	}

	@Override
	public Object getItem(int position) {
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return getRealView(position, convertView, parent);
	}
	
	protected View getInflate(int resLayout){
		return LayoutInflater.from(context).inflate(resLayout, null);
	}
	
	public  boolean setText(View view, int id, String text) {
        TextView textView = (TextView) view.findViewById(id);
        if (textView == null)
            return false;

        textView.setText(text);
        return true;
    }
	
	public abstract void setData(List<T> datas);
	protected abstract View getRealView(int position, View convertView, ViewGroup parent);

}

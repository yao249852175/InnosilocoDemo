package innosiloco.demo.mvp_view.iview;

import java.util.List;

import android.view.View;
import android.widget.ListView;

import innosiloco.demo.R;
import innosiloco.demo.utils.ListBaseAdapter;

/**
 * 
 * @ClassName: ListBaseActivity
 * @Description: 所有列表界面的基类
 * @author:ZCS
 * @date: 2015年7月21日 下午1:48:11
 * @param <T>
 */
public abstract class ListBaseActivity<T> extends BaseTitleActivity{
	protected ListView 						listView;
	private ListBaseAdapter<T> adapter;
	private      List<T>						datas;
	@Override
	protected void initUi() {
		super.initUi();
		listView=(ListView) findViewById(R.id.listview);
		if(getListHeaderView()!=null)
			listView.addHeaderView(getListHeaderView());
		if(getListFooterView()!=null)
			listView.addFooterView(getListFooterView());
		datas=getData();
		adapter=getAdapter();
		adapter.setData(datas);
		listView.setAdapter(adapter);
		DatasetNotifychanged();
	}
	protected abstract ListBaseAdapter<T> getAdapter();
	protected abstract List<T> getData();
	
	protected void DatasetNotifychanged(){
		adapter.notifyDataSetChanged();
	}
	
	protected T getItem(int pos) {
		return datas.get(pos);
	}
	
	protected ListView getListView() {
		return listView;
	}
	
	protected View getListHeaderView(){return null;};
	
	protected View getListFooterView(){return null;};
}

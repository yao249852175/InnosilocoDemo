package innosiloco.demo.mvp_view.iview;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import innosiloco.demo.R;
import innosiloco.demo.beans.FileInfo;
import innosiloco.demo.utils.FileUtils;
import innosiloco.demo.utils.FilelistAdapterExt;
import innosiloco.demo.utils.ListBaseAdapter;

/**
 * 
 * @ClassName: FileListSelector
 * @Description: 文件列表
 * @author:ZCS
 * @date: 2015年11月26日 下午4:12:48
 */
public class FileListSelector extends ListBaseActivity<FileInfo> implements OnItemClickListener {
	private 					 	int fileFlag = 0;
	private List<String>     fileData = new ArrayList<String>();
	private ArrayList<FileInfo> fileList;
	private FilelistAdapterExt  fileListAdapter;
	
	
	@Override
	protected ListBaseAdapter<FileInfo> getAdapter() {
		return fileListAdapter;
	}
	@Override
	protected List<FileInfo> getData() {
		if (getFileList(getExternalStorageDirectory(fileFlag)) != null) {
			fileList =getFileList(getExternalStorageDirectory(fileFlag));
			Collections.sort(fileList, comparator);
		}
		fileData.add(getExternalStorageDirectory(fileFlag).getAbsolutePath());
		return fileList;
	}

	@Override
	int getLayout() {
		return R.layout.file_upload_layout;
	}

	@Override
	String[] getTitles() {
		if (fileFlag == 0) {
			return new String[] { getResources().getString(R.string.phone_memory), "" };
		} else {
			return new String[] { getResources().getString(R.string.sdk), "" };
		}

	}

	@Override
	void titleLeftBtnClick() {
		onBackPressed();
	}

	/**
	 * 获取文件列表
	 */
	private ArrayList<FileInfo> getFileList(File file) {
		if (file.isDirectory()) {
			return getFileList(file.getAbsolutePath());
		} else {
			return null;
		}
	}

	/**
	 * 获取文件列表
	 */
	private ArrayList<FileInfo> getFileList(String filePath) {
		File file = new File(filePath);
		ArrayList<FileInfo> fileList=null;
		if (file.exists()) {
			if (file.isDirectory()) {
				fileList=filterFile(file.listFiles());
			}
		}
		return fileList;
	}
	
	private ArrayList<FileInfo> filterFile(File[] files){
		ArrayList<FileInfo> fileList=new ArrayList<>();
		for (File child : files) {
			String absolutePath = child.getAbsolutePath();
            if (FileUtils.isNormalFile(absolutePath) && FileUtils.shouldShowFile(absolutePath)) {
                FileInfo lFileInfo = FileUtils.GetFileInfo(child,null,false);
                if (lFileInfo != null) {
                    fileList.add(lFileInfo);
                }
            }
		}
		return fileList;
	}

	/**
	 * 获取根目录
	 * 
	 * @param flag
	 * @return
	 */
	private File getExternalStorageDirectory(int flag) {
		if (flag == 1) {
			return Environment.getExternalStorageDirectory();
		} else {
			return Environment.getRootDirectory();
		}
	}

	@Override
	protected void initUi() {
		fileFlag = getIntent().getIntExtra("fileFlag", 0);
		fileListAdapter = new FilelistAdapterExt(this);
		super.initUi();
		getListView().setOnItemClickListener(this);
		getListView().setDivider(new ColorDrawable(Color.parseColor("#c8c7cc")));
		getListView().setDividerHeight(1);
	}

	@Override
	public void onBackPressed() {
		if (fileData.size() <= 1) {
			super.onBackPressed();
		} else {
			fileData.remove(fileData.size() - 1);
			fileList.clear();
			getFileList(fileData.get(fileData.size() - 1));
			fileList.addAll(getFileList(fileData.get(fileData.size() - 1)));
			Collections.sort(fileList, comparator);
			DatasetNotifychanged();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		final FileInfo file = (FileInfo) arg0.getItemAtPosition(arg2);
		if (file.IsDir) {
			if(getFileList(file.filePath)==null)
				return;
			fileData.add(file.filePath);
			fileList.clear();
			fileList.addAll(getFileList(file.filePath));
			Collections.sort(fileList, comparator);
			DatasetNotifychanged();
		} else {
			Intent intent=new Intent();
			intent.setData(Uri.fromFile(new File(file.filePath)));
			setResult(0, intent);
			this.finish();
		}
	}

	/**
	 * 对文件进行排序
	 */
	private Comparator<FileInfo> comparator = new Comparator<FileInfo>() {
		@Override
		public int compare(FileInfo f1, FileInfo f2) {
			if (f1 == null || f2 == null) {
				if (f1 == null) {
					return -1;
				} else {
					return 1;
				}
			} else {
				if (f1.IsDir == true && f2.IsDir == true) { 
					return f1.fileName.compareToIgnoreCase(f2.fileName);
				} else {
					if ((f1.IsDir && !f2.IsDir) == true) {
						return -1;
					} else if ((f2.IsDir && !f1.IsDir) == true) {
						return 1;
					} else {
						return f1.fileName.compareToIgnoreCase(f2.fileName);// 最后比较文件
					}
				}
			}
		}
	};
	
	
	
}

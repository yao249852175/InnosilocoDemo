package innosiloco.demo.utils;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import innosiloco.demo.R;
import innosiloco.demo.beans.FileInfo;

public class FilelistAdapterExt extends ListBaseAdapter<FileInfo>{

	public FilelistAdapterExt(Context context) {
		super(context);
	}

	@Override
	public void setData(List<FileInfo> datas) {
		this.datas=datas;
	}

	@Override
	protected View getRealView(int position, View convertView, ViewGroup parent) {
		View view = null;
        if (convertView != null) {
            view = convertView;
        } else {
            view =getInflate(R.layout.file_browser_item);
        }
        FileInfo lFileInfo = (FileInfo) getItem(position);
        setupFileListItemInfo(context, view, lFileInfo);
        return view;
	}
	
	public  void setupFileListItemInfo(Context context, View view,FileInfo fileInfo) {
		setText(view, R.id.file_name, fileInfo.fileName);
        setText(view, R.id.file_count, fileInfo.IsDir ? "(" + fileInfo.Count + ")" : "");
        setText(view, R.id.modified_time, DateHelper.formatDateString(context, fileInfo.ModifiedDate));
        setText(view, R.id.file_size, (fileInfo.IsDir ? "" : FileUtils.convertStorage(fileInfo.fileSize)));
        ImageView lFileImage = (ImageView) view.findViewById(R.id.file_image);
        ImageView lFileImageFrame = (ImageView) view.findViewById(R.id.file_image_frame);
        if (fileInfo.IsDir) {
            lFileImageFrame.setVisibility(View.GONE);
            lFileImage.setImageResource(R.drawable.file_icon);
        }else{
//        	FileUtils.setImageViewIcon(lFileImage, fileInfo.fileName);
            lFileImage.setImageResource(R.drawable.file_icon);
        }

    }
}

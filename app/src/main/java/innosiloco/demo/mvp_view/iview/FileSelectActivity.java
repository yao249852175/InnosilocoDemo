package innosiloco.demo.mvp_view.iview;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Intent;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import innosiloco.demo.R;
import innosiloco.demo.utils.FileUtils;

/**
 * 文件选择器
 */
public class FileSelectActivity extends BaseTitleActivity implements View.OnClickListener { 
	private final int REQUEST_FILE_LOCAL=3;
	@Override
	int getLayout() {
		return R.layout.allfiles_activity;
	}

	@Override
	String[] getTitles() {
		return new String[] { "全部文件", "" };
	}

	@Override
	void titleLeftBtnClick() {
		finish();
	}

	@Override
	public void onClick(View arg0) {
		Intent intent = new Intent(this, FileListSelector.class);
		switch (arg0.getId()) {
		case R.id.afa_phone:
			intent.putExtra("fileFlag", 0);
			break;
		case R.id.afa_sd:
			if(FileUtils.isExitsSdcard())
				intent.putExtra("fileFlag", 1);
			else
				Toast.makeText(this,"sd卡不存在",Toast.LENGTH_SHORT).show();
			break;
		}
		startActivityForResult(intent, REQUEST_FILE_LOCAL);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(data!=null){
			setResult(0, data);
			this.finish();
		}
	}

	protected void initUi() {
		super.initUi();
		findViewById(R.id.afa_phone).setOnClickListener(this);
		if(FileUtils.isExitsSdcard()==true){
			findViewById(R.id.afa_sd).setOnClickListener(this);
		}else{
			findViewById(R.id.afa_sd).setVisibility(View.GONE);
		}	
	}
}

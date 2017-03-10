package innosiloco.demo.mvp_view;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import innosiloco.demo.R;
import innosiloco.demo.beans.SecretKeyBean;
import innosiloco.demo.beans.TalkBean;
import innosiloco.demo.utils.RonLog;

/**
 * Created by Administrator on 2017/3/10.
 */
public class ImageActivity extends BaseActivity
{
    private ImageView imageView;

    public final static String PATH = "PATH";
    @Override
    public void findViews()
    {
        imageView= (ImageView) findViewById(R.id.iv_showImg);
    }

    @Override
    public void initViews()
    {
        String path = getIntent().getStringExtra(PATH);
        try {
           Bitmap bitmap = BitmapFactory.decodeFile(path);
            imageView.setImageBitmap(bitmap);
        }catch (Exception e)
        {
            imageView.setImageResource(R.drawable.dud);
        }
    }

    @Override
    public void initLisenter()
    {

    }

    @Override
    public int getContentView() {
        return R.layout.activity_img;
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void secretKeyChange(SecretKeyBean secretKeyBean)
    {

    }
}

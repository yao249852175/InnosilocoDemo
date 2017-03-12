package innosiloco.demo.mvp_presenter;

import android.content.Context;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ronya on 2017/3/12.
 */

public class InnoDatabaseHelper extends SQLiteOpenHelper{
    private static final int version = 1; //数据库版本
    private static final String name = "count"; //数据库名称
    public InnoDatabaseHelper(Context context) {

        //第三个参数CursorFactory指定在执行查询时获得一个游标实例的工厂类,设置为null,代表使用系统默认的工厂类

        super(context, name, null, version);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE IF NOT EXISTS keyTabel (_id integer primary key autoincrement, key varchar)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


    }
}

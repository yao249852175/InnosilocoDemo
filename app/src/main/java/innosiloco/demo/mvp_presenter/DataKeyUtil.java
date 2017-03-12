package innosiloco.demo.mvp_presenter;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.List;

import innosiloco.demo.utils.RonLog;

/**
 * Created by ronya on 2017/3/12.
 */

public class DataKeyUtil
{
    private InnoDatabaseHelper databaseHelper;
    private SQLiteDatabase sqLiteDatabase;
    public DataKeyUtil(Context context)
    {
        databaseHelper = new InnoDatabaseHelper(context);
    }

    public void insert(String key)
    {
        if(checkKeyIsExit(key))
        {
            return;
        }
        if(sqLiteDatabase == null )
        {
            sqLiteDatabase = databaseHelper.getWritableDatabase();
        }
        sqLiteDatabase.execSQL("INSERT INTO keyTabel(key)"+
                " VALUES ('"+ key +"')");
    }

    public List<String> queryAllKey()
    {
        if(sqLiteDatabase == null )
        {
            sqLiteDatabase = databaseHelper.getWritableDatabase();
        }
        String sql = "select key from keyTabel";
        Cursor result=sqLiteDatabase.rawQuery(sql,new String[]{});
        result.moveToFirst();
        List<String> keys = new ArrayList<>();
        while (!result.isAfterLast()) {
            String name=result.getString(0);
            keys.add(name);
            result.moveToNext();
        }
        result.close();
        sqLiteDatabase.close();
        sqLiteDatabase = null;
        return keys;
    }

    /********************
     * 检查key 是否存在
     * @param key
     * @return
     */
    public boolean checkKeyIsExit(String key )
    {
        RonLog.LogE("key:" + key);
        List<String > keys = queryAllKey();
        for (String key_s: keys)
        {
            RonLog.LogE("key_s:" + key_s);
            if(key_s.equals(key))
            {
                return true;
            }
        }
        return false;
    }
}

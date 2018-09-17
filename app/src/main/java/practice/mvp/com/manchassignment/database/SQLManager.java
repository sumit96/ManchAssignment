package practice.mvp.com.manchassignment.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.util.ArrayList;

import practice.mvp.com.manchassignment.utilities.ContextHolder;
import practice.mvp.com.manchassignment.types.PostItem;

/**
 * Created by admin on 9/17/18.
 */

public class SQLManager  {

    private SQLDataHelper sqlHelper;
    private Context context = ContextHolder.getInstance().getContext();
    private SQLiteDatabase database;
    public static SQLManager instance;

    private SQLManager() {
        sqlHelper = new SQLDataHelper(context,
                SQLConstants.DATA_BASE_NAME, null,
                SQLConstants.DATA_BASE_VERSION);
        database = sqlHelper.getWritableDatabase();
    }

    public static SQLManager getInstance() {
        if (instance == null) {
            instance = new SQLManager();
        }
        return instance;
    }

    public long savePost(PostItem item, String postTable) {
        if (item == null) {
            return -1;
        }
        long rowId = -1;

        ContentValues values = new ContentValues();
        values.put(SQLConstants.FIELD_TITLE, item.getTitle());
        values.put(SQLConstants.FIELD_DESCRIPTION, item.getDescription());
        values.put(SQLConstants.FIELD_IMAGE_PATH, item.getImagePath());

        rowId = database.insert(postTable, null, values);

        return rowId;
    }

    public ArrayList<PostItem> getPostData()
    {

        Cursor cursor = null;
        ArrayList<PostItem> list = new ArrayList<PostItem>();
        try {

            cursor = database.query(SQLConstants.POST_TABLE, null,
                    null,
                    null, null, null, null);

            if (cursor != null && cursor.getCount() > 0) {

                for (int i = 0; i < cursor.getCount(); i++) {

                    cursor.moveToPosition(i);

                    String title=cursor.getString(cursor
                            .getColumnIndex(SQLConstants.FIELD_TITLE));
                    String desc=cursor.getString(cursor
                            .getColumnIndex(SQLConstants.FIELD_DESCRIPTION));
                    String imagePath=cursor.getString(cursor
                            .getColumnIndex(SQLConstants.FIELD_IMAGE_PATH));
                    PostItem item = new PostItem(title,desc,imagePath);


                    list.add(item);
                }

            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return list;
    }

}

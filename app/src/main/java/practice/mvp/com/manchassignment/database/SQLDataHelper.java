package practice.mvp.com.manchassignment.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by admin on 9/17/18.
 */

public class SQLDataHelper extends SQLiteOpenHelper{

    public SQLDataHelper(Context context, String name,
                         SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase dataBase) {
        createTables(dataBase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private void createTables(SQLiteDatabase dataBase) {
        // these tables are added in version no.1 and 2
        dataBase.execSQL(CREATE_VENDOR_TABLE);
    }
    private String CREATE_VENDOR_TABLE = "CREATE TABLE IF NOT EXISTS "
            + SQLConstants.POST_TABLE + "( " + SQLConstants.FIELD_ID
            + " INTEGER," + SQLConstants.FIELD_TITLE + " VARCHAR,"
            + SQLConstants.FIELD_DESCRIPTION + " VARCHAR,"
            + SQLConstants.FIELD_IMAGE_PATH + " VARCHAR);";

}

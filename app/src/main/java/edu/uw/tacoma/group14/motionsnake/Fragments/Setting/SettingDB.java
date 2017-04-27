package edu.uw.tacoma.group14.motionsnake.Fragments.Setting;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import edu.uw.tacoma.group14.motionsnake.R;

/**
 * Create and handle the SQLite database function.
 * Created by Justin Arnett Son Vu on 5/31/2016.
 */
public class SettingDB {

    class SettingDBHelper extends SQLiteOpenHelper {

        private final String CREATE_SETTING_SQL;

        private final String DROP_SETTING_SQL;

        public SettingDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
            CREATE_SETTING_SQL = context.getString(R.string.CREATE_SETTING_SQL);
            DROP_SETTING_SQL = context.getString(R.string.DROP_SETTING_SQL);

        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(CREATE_SETTING_SQL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL(DROP_SETTING_SQL);
            onCreate(sqLiteDatabase);
        }
    }

    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "Setting.db";

    private SettingDBHelper mSettingDBHelper;
    private SQLiteDatabase mSQLiteDatabase;
    private static final String SETTING_TABLE = "Setting";

    //Set the helper class
    public SettingDB(Context context) {
        mSettingDBHelper = new SettingDBHelper(
                context, DB_NAME, null, DB_VERSION);
        mSQLiteDatabase = mSettingDBHelper.getWritableDatabase();
    }

    //insert records to the database
    public boolean insertSetting(String angle, String color, boolean debug) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("angle", angle);
        contentValues.put("color", color);
        int flag = (debug)? 1 : 0;
        contentValues.put("debug", flag);

        long rowId = mSQLiteDatabase.insert("Setting", null, contentValues);
        return rowId != -1;
    }

    /**
     * retrieve the records from database
     * if no records found, load the default information.
     */
    public String getSetting(){
        String[] columns = {"angle","color","debug"};
        Cursor c = mSQLiteDatabase.query(SETTING_TABLE,columns,null,null,null,null,null);
        c.moveToFirst();
        StringBuilder sb = new StringBuilder();
        try {
            sb.append(c.getString(0));
            sb.append(",");
            sb.append(c.getString(1));
            sb.append(",");
            sb.append(c.getString(2));
        }catch(CursorIndexOutOfBoundsException e){
            sb.append("0,red,0");
        }
        return sb.toString();
    }

    //closed database connection
    public void closeDB() {
        mSQLiteDatabase.close();
    }

    //delete database
    public void deleteSetting(){
        mSQLiteDatabase.delete(SETTING_TABLE,null,null);
    }

}

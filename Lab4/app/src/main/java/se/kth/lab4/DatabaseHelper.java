package se.kth.lab4;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.ArrayList;

import static se.kth.lab4.DatabaseHelper.InvitationReaderContract.Invite.*;

/**
 * Created by Daniel on 2017-01-21.
 */
public class DatabaseHelper extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "FCM";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_NAME_GROUP + " TEXT)";

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void insertInvite(String invitation) {
        Log.d("Lab4", "Inserting to " + COLUMN_NAME_GROUP);
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME_GROUP, invitation);
        long result = db.insert(TABLE_NAME, null, cv);
        if(result == -1){
            Log.d("Lab4", "Failed");
        }else{
            Log.d("Lab4", "Success");
        }
        db.close();
    }

    public ArrayList<String> getAllInvitations() {

        SQLiteDatabase db = getReadableDatabase();

        // Define which columns we want to use
        String[] projection = {
                _ID,
                COLUMN_NAME_GROUP
        };

        // Create filter (Where clause), case sensitive and * don't work, use null in builder
        String selection = COLUMN_NAME_GROUP + "=?";
        String[] selectionArgs = new String[]  {
                "*"
        };

        // We can define sort order
        String sortOrder =
                COLUMN_NAME_GROUP + " DESC";

        Cursor cursor = db.query(
                TABLE_NAME,                               // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        ArrayList groupNames = new ArrayList<>();
        while(cursor.moveToNext()) {
            String itemId = cursor.getString(
                    cursor.getColumnIndexOrThrow(COLUMN_NAME_GROUP));
            Log.d("Lab4", "Found this: " + itemId);
            groupNames.add(itemId);
        }
        cursor.close();

        return groupNames;

    }

    public static final class InvitationReaderContract {
        private InvitationReaderContract() {}

        public static class Invite implements BaseColumns {
            public static final String TABLE_NAME = "invite";
            public static final String COLUMN_NAME_GROUP = "groupName";
        }
    }
}

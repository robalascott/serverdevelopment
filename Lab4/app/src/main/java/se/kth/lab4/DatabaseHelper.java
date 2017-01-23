package se.kth.lab4;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

import java.io.File;
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
                    COLUMN_NAME_GROUP + " TEXT," +
                    COLUMN_NAME_GROUP_ID + " TEXT," +
                    COLUMN_NAME_SENDER + " TEXT)";

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public boolean doesDataExist(Context context,String name){
        File dbFile = context.getDatabasePath(name);
        return dbFile.exists();
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
    public boolean hasList(){
        SQLiteDatabase db = getReadableDatabase();
        String temp = "SELECT * FROM invite";
        Cursor cursor = db.rawQuery(temp, null);
        if (cursor.moveToFirst()) {
            try{
                Log.i("database",cursor.getString(0));
                return true;
            }catch (NullPointerException e){
                return false;
            }
        }
        cursor.close();
        db.close();
        return false;
    }

    public boolean deleteRow(String value)
    {
        /*Single Row Solution */
        /*Delete using unique id  or row id */
        SQLiteDatabase db = this.getWritableDatabase();
        Boolean bool = db.delete(TABLE_NAME, COLUMN_NAME_GROUP + "='" + value + "';", null) > 0;
        db.close();
        return true;
    }


    public void insertInvite(RemoteMessage remoteMessage) {
        Log.d("Lab4", "Inserting to " + COLUMN_NAME_GROUP);
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME_GROUP, remoteMessage.getData().get("groupname"));
        cv.put(COLUMN_NAME_GROUP_ID, remoteMessage.getData().get("groupid"));
        cv.put(COLUMN_NAME_SENDER, remoteMessage.getData().get("sender"));
        long result = db.insert(TABLE_NAME, null, cv);
        if(result == -1){
            Log.d("Lab4", "Failed");
        }else{
            Log.d("Lab4", "Success");
        }
        db.close();
    }

    public ArrayList<Invitation> getAllInvitations() {

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

        ArrayList groupNames = new ArrayList<Invitation>();
        while(cursor.moveToNext()) {
            String itemId = cursor.getString(
                    cursor.getColumnIndexOrThrow(COLUMN_NAME_GROUP));
            Log.d("Lab4", "Found this: " + itemId);
            //Todo get rest of object values
            Invitation tempObject = new Invitation(itemId,itemId,itemId);
            groupNames.add(tempObject);
        }
        cursor.close();

        return groupNames;

    }

    public int getInvitationCount() {
        SQLiteDatabase db = getReadableDatabase();
        // TODO: Typesafe
        Cursor res = db.rawQuery("SELECT Count(*) FROM " + TABLE_NAME, null);
        res.moveToFirst();
        return res.getInt(0);
    }

    public ArrayList<Invitation> getInvitationLatest(int howMany) {
        SQLiteDatabase db = getReadableDatabase();

        ArrayList<Invitation> invites = new ArrayList<>();

        String[] allColumns = {COLUMN_NAME_GROUP, COLUMN_NAME_SENDER, COLUMN_NAME_GROUP_ID};
        Cursor cursor = db.query(TABLE_NAME, allColumns, null, null, null, null, _ID +" DESC",
                Integer.toString(howMany));

        try {
            while (cursor.moveToNext()) {
                invites.add(new Invitation(
                        cursor.getString(cursor.getColumnIndex(COLUMN_NAME_GROUP)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_NAME_GROUP_ID)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_NAME_SENDER))));
            }
        } finally {
            cursor.close();
        }

        return invites;
    }

    public static final class InvitationReaderContract {
        private InvitationReaderContract() {}

        public static class Invite implements BaseColumns {
            public static final String TABLE_NAME = "invite";
            public static final String COLUMN_NAME_GROUP = "groupName";
            public static final String COLUMN_NAME_GROUP_ID = "groupid";
            public static final String COLUMN_NAME_SENDER = "sender";
        }
    }

}

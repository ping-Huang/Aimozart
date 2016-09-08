package com.aimozart.user.myapplicationlayouttest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class MySQLitePlayListOpenHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "MusicSpots";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "Music";
    private static final String MUS_id = "id";
    private static final String MUS_name = "name";
    private static final String MUS_path = "path";
    private static final String MUS_type = "type";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_NAME + " ( " +
                    MUS_id + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    MUS_name + " TEXT NOT NULL, "+
                    MUS_path + " TEXT NOT NULL, "+
                    MUS_type + " TEXT NOT NULL) ";
    //最後一個參數要加")"

    public MySQLitePlayListOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }


    public void insertIfEmpty() {
        SQLiteDatabase db = getReadableDatabase();
        String[] columns = {
                MUS_id, MUS_name, MUS_path,MUS_type
        };
        //Cursor cursor = db.query(TABLE_NAME, columns,COL_name+"=?",new String[]{"%"+name+"%"},null,null,null);
        String name = "default";
        ContentValues cv = new ContentValues();
        cv.put(MUS_name,name);
        //把name 放入cv container
        long id = db.insert(TABLE_NAME, null, cv);
        //加入table 自動會生成id
        //cursor.close();
    }


    public List<Music> getAllSpots() {

        SQLiteDatabase db = getReadableDatabase();
        String[] columns = {
                MUS_id, MUS_name ,MUS_path,MUS_type
        };
        //Cursor cursor = db.query(TABLE_NAME, columns,);
        Cursor cursor = db.query(TABLE_NAME,null,null,null,null,null,null);
        //Cursor cursor = db.query(TABLE_NAME, columns,COL_name+"=?",new String[]{"%"+name+"%"},null,null,null);
        //COL_name+"=?查詢欄位
        //new String[]{"%"+name+"%"}查詢條件   有加% 是like查詢(有部分符合名字即可)
        List<Music> spotList = new ArrayList<>();
        System.out.println("GetAllSpots : " + cursor.getCount());
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            String path = cursor.getString(2);
            String type = cursor.getString(3);
            Music spot = new Music(id, name,path);
            spot.setType(type);
            spotList.add(spot);

            // System.out.println("Get1Spot : " + name);
        }
        cursor.close();
        // System.out.println("GetSpotList Size : " + spotList.size());
        return spotList;
    }

    public Music findById(int id) {
        SQLiteDatabase db = getWritableDatabase();
        String[] columns = {
                MUS_id, MUS_name ,MUS_path,MUS_type
        };
        String selection = MUS_id + " = ?;";
        String[] selectionArgs = {String.valueOf(id)};
        //Cursor cursor = db.query(TABLE_NAME, columns);
        Cursor cursor = db.query(TABLE_NAME, columns,MUS_id+"=?",new String[]{String.valueOf(id)},null,null,null);
        Music spot = null;
        if (cursor.moveToNext()) {
            String name = cursor.getString(1);
            String path = cursor.getString(2);
            String type = cursor.getString(3);
            spot = new Music(id, name,path,type);
        }
        cursor.close();
        return spot;
    }

    public long insert(Music spot) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MUS_name, spot.getName());
        values.put(MUS_path,spot.getPath());
        values.put(MUS_type,spot.getType());
        return db.insert(TABLE_NAME, null, values);
    }

    public int update(Music spot) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MUS_name, spot.getName());
        String whereClause = MUS_id + " = ?;";
        String[] whereArgs = {Integer.toString(spot.getId())};
        return db.update(TABLE_NAME, values, whereClause, whereArgs);
    }

    public int deleteById(int id) {
        SQLiteDatabase db = getWritableDatabase();
        String whereClause = MUS_id + " = ?;";
        String[] whereArgs = {String.valueOf(id)};
        return db.delete(TABLE_NAME, whereClause, whereArgs);
    }

    public List<Music> getSpots(String g) {

        SQLiteDatabase db = getReadableDatabase();
        String[] columns = {
                MUS_id, MUS_name,MUS_path,MUS_type
        };
        String[] selectionArgs = {g};
        //Cursor cursor = db.query(TABLE_NAME, columns);
        //Cursor cursor = db.query(TABLE_NAME, columns,MUS_name+"=?",new String[]{"%"+g+"%"},null,null,null);
        Cursor cursor = db.query(TABLE_NAME, columns,MUS_name+"=?",new String[]{g},null,null,null);//修該完全比對
        List<Music> spotList = new ArrayList<>();
        //System.out.println("Get " + g + " Spots : " + cursor.getCount());
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            String path = cursor.getString(2);
            String type = cursor.getString(3);
            Music spot = new Music(id, name,path);
            spot.setType(type);
            spotList.add(spot);

            //System.out.println("Get1Spot : " + name);
        }
        cursor.close();
        //System.out.println("GetSpotList Size : " + spotList.size());
        return spotList;
    }

    public List<Music> getOrderSpots(String s) {

        SQLiteDatabase db = getReadableDatabase();
        String[] columns = {
                MUS_id, MUS_name,MUS_path,MUS_type
        };
        //Cursor cursor = db.query(TABLE_NAME, columns);
        Cursor cursor = db.query(TABLE_NAME, columns,MUS_name+"=?",new String[]{"%"+s+"%"},null,null,null);
        List<Music> spotList = new ArrayList<>();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            String path = cursor.getString(2);
            Music spot = new Music(id, name,path);
            spotList.add(spot);
        }
        cursor.close();
        System.out.println("GetSpotList Size : " + spotList.size());
        return spotList;
    }

    public int deleteAll(){
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_NAME, null, null);
    }
}


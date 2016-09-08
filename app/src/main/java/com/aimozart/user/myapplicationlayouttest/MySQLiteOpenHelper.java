package com.aimozart.user.myapplicationlayouttest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "PlayListSpots";
    private static final int DB_VERSION = 1;
    //private static final String TABLE_NAME = "Spot";
    private static final String TABLE_NAME = "PlayListSpots";
    private static final String COL_id = "id";
    private static final String COL_name = "name";
    private static final String COL_music = "music";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_NAME + " ( " +
                    COL_id + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_name + " TEXT NOT NULL, " + COL_music + " TEXT NOT NULL) ";
    //最後一個參數要加")"

    public MySQLiteOpenHelper(Context context) {
        super(context, TABLE_NAME+".db", null, DB_VERSION);//加上.db 副檔名
        if(getSpots("α-alpha").size()==0)
            insert(new Member("α-alpha"));
        if(getSpots("β-beta").size()==0)
            insert(new Member("β-beta"));
        if(getSpots("θ-theta").size()==0)
            insert(new Member("θ-theta"));
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
                COL_id, COL_name
        };
        //Cursor cursor = db.query(TABLE_NAME, columns,COL_name+"=?",new String[]{"%"+name+"%"},null,null,null);
        String name = "default";
        ContentValues cv = new ContentValues();
        cv.put(COL_name, name);
        //把name 放入cv container
        long id = db.insert(TABLE_NAME, null, cv);
        //加入table 自動會生成id
        //cursor.close();
    }


    public List<Member> getAllSpots() {

        SQLiteDatabase db = getReadableDatabase();
        String[] columns = {
                COL_id, COL_name, COL_music
        };
        //Cursor cursor = db.query(TABLE_NAME, columns,);
        Cursor cursor = db.query(TABLE_NAME,null,null,null,null,null,null);
        //Cursor cursor = db.query(TABLE_NAME, columns,COL_name+"=?",new String[]{"%"+name+"%"},null,null,null);
        //COL_name+"=?查詢欄位
        //new String[]{"%"+name+"%"}查詢條件   有加% 是like查詢(有部分符合名字即可)
        List<Member> spotList = new ArrayList<>();
        System.out.println("GetAllSpots : " + cursor.getCount());
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            String music = cursor.getString(2);
            Member spot = new Member(id, name,music);
            if(!name.equals("α-alpha") && !name.equals("β-beta") && !name.equals("θ-theta"))//預設清單不讀入
                spotList.add(spot);

            // System.out.println("Get1Spot : " + name);
        }
        cursor.close();
        // System.out.println("GetSpotList Size : " + spotList.size());
        return spotList;
    }

    public Member findById(int id) {
        SQLiteDatabase db = getWritableDatabase();
        String[] columns = {
                COL_id, COL_name
        };
        String selection = COL_id + " = ?;";
        String[] selectionArgs = {String.valueOf(id)};
        //Cursor cursor = db.query(TABLE_NAME, columns);
        Cursor cursor = db.query(TABLE_NAME, columns, COL_id + "=?", new String[]{String.valueOf(id)}, null, null, null);
        Member spot = null;
        if (cursor.moveToNext()) {
            String name = cursor.getString(1);
            String music = cursor.getString(2);
            spot = new Member(id, name,music);
        }
        cursor.close();
        return spot;
    }

    public long insert(Member spot) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_name, spot.getName());
        values.put(COL_music, spot.getMusic());
        return db.insert(TABLE_NAME, null, values);
    }

    public int update(Member spot) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_name, spot.getName());
        if(spot.getMusic()!=null)
            values.put(COL_music, spot.getMusic());
        String whereClause = COL_name + " = ?;";
        String[] whereArgs = {spot.getName()};
        return db.update(TABLE_NAME, values, whereClause, whereArgs);
    }
    public int rename(Member spot,String oldName) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_name, spot.getName());
        String whereClause = COL_name + " = ?;";
        String[] whereArgs = {oldName};
        return db.update(TABLE_NAME, values, whereClause, whereArgs);
    }

    public int deleteById(int id) {
        SQLiteDatabase db = getWritableDatabase();
        String whereClause = COL_id + " = ?;";
        String[] whereArgs = {String.valueOf(id)};
        return db.delete(TABLE_NAME, whereClause, whereArgs);
    }
    public int deleteByName(String name) {
        SQLiteDatabase db = getWritableDatabase();
        String whereClause = COL_name + " = ?;";
        String[] whereArgs = {name};
        return db.delete(TABLE_NAME, whereClause, whereArgs);
    }

    public List<Member> getSpots(String g) {

        SQLiteDatabase db = getReadableDatabase();
        String[] columns = {
                COL_id, COL_name, COL_music
        };
        String[] selectionArgs = {g};
        //Cursor cursor = db.query(TABLE_NAME, columns);
        //Cursor cursor = db.query(TABLE_NAME, columns,COL_name+"=?",new String[]{"%"+g+"%"},null,null,null);
        Cursor cursor = db.query(TABLE_NAME, columns,COL_name+"=?",new String[]{ g },null,null,null);
        List<Member> spotList = new ArrayList<>();
        //System.out.println("Get " + g + " Spots : " + cursor.getCount());
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(COL_id));
            String name = cursor.getString(cursor.getColumnIndex(COL_name));
            String music = cursor.getString(cursor.getColumnIndex(COL_music));
            Member spot = new Member(id, name,music);
            spotList.add(spot);

            //System.out.println("Get1Spot : " + name);
        }
        cursor.close();
        //System.out.println("GetSpotList Size : " + spotList.size());
        return spotList;
    }

    public List<Member> getOrderSpots(String s) {

        SQLiteDatabase db = getReadableDatabase();
        String[] columns = {
                COL_id, COL_name, COL_music
        };
        //Cursor cursor = db.query(TABLE_NAME, columns);
        Cursor cursor = db.query(TABLE_NAME, columns,COL_name+" LIKE ?",new String[]{"%"+s+"%"},null,null,null);
        List<Member> spotList = new ArrayList<>();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(COL_id));
            String name = cursor.getString(cursor.getColumnIndex(COL_name));
            String music = cursor.getString(cursor.getColumnIndex(COL_music));
            Member spot = new Member(id, name,music);
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
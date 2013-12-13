package com.squad22.fit.provider;

import com.squad22.fit.utils.Constants;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class ProfileProvider extends ContentProvider {
private WeightRecordBaseHelper dbHelper;
    
    @Override
    public boolean onCreate() {
        final Context context = getContext();
        dbHelper = getDatabaseHelper(context);
        return true;
    }

    @Override
    public String getType(Uri uri) {
        // TODO Auto-generated method stub
        return null;
    }
    
    /* Visible for testing */
    protected WeightRecordBaseHelper getDatabaseHelper(final Context context) {
        return WeightRecordBaseHelper.getInstance(context);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        qb.setTables(WeightRecordBaseHelper.PROFILE_TABLE_NAME);
        
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null,
                sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        long rowId = db.insert(WeightRecordBaseHelper.PROFILE_TABLE_NAME, "", values);
        if (rowId > 0) {
            Uri rowUri = ContentUris.appendId(Constants.ProfileUri.buildUpon(),rowId).build();
            notifyChange(rowUri);
            return rowUri;
        }
        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int count = db.update(WeightRecordBaseHelper.PROFILE_TABLE_NAME, values, selection, selectionArgs);
        if (count > 0) {
            notifyChange(Constants.ProfileUri);
        }
        return count;
    }

    /** {@inheritDoc} */
    //当删除全部数据时比较耗时间，所有如果没有设置条件，则先将表drop然后create达到清空表数据的目的
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        int count = 0;
        
        //如果传入的选择条件为空，则使用drop将表删除，然后新建表，达到清空表数据的目的
        if(null == selection){
            
            db.execSQL("DROP TABLE IF EXISTS "+ WeightRecordBaseHelper.PROFILE_TABLE_NAME);
            
            db.execSQL(WeightRecordBaseHelper.PROFILE_TABLE_NAME);
            
            notifyChange(Constants.ProfileUri);
        }else{
            
            count = db.delete(WeightRecordBaseHelper.PROFILE_TABLE_NAME, selection, selectionArgs);
            
            if (count > 0) {
                notifyChange(Constants.ProfileUri);
            }
        }
        return count;
    }
    
    protected void notifyChange(Uri uri) {
        getContext().getContentResolver().notifyChange(uri,null);
    }


}

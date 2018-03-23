/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */

package cn.lrapps.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import cn.lrapps.models.AppInfo;

/**
 * Created by libit on 15/8/26.
 */
public class DataBaseFactory
{
	public static class DBHelper extends SQLiteOpenHelper
	{
		private static final int DATABASE_VERSION = 19;
		private static final String[] TABLES = new String[]{AppInfo.getCreateTableSQL()// App信息表
		};

		DBHelper(Context context)
		{
			super(context, DbConstant.AUTHORITY, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db)
		{
			int count = TABLES.length;
			for (int i = 0; i < count; i++)
			{
				db.execSQL(TABLES[i]);
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
		{
			if (oldVersion < 4)
			{
				db.execSQL("DROP TABLE IF EXISTS " + DbConstant.TABLE_NAME_APP_INFO);
			}
			if (oldVersion < 9)
			{
				db.execSQL("DROP TABLE IF EXISTS " + DbConstant.TABLE_NAME_APP_INFO);
				db.execSQL("DROP TABLE IF EXISTS " + DbConstant.TABLE_NAME_BLACK_APP_INFO);
				db.execSQL("ALTER TABLE " + DbConstant.TABLE_NAME_HIDE_APP_INFO + " ADD " + AppInfo.FIELD_NAME_LABEL + " TEXT");
				db.execSQL("ALTER TABLE " + DbConstant.TABLE_NAME_HIDE_APP_INFO + " ADD " + AppInfo.FIELD_EXIST + " INTEGER NOT NULL DEFAULT 1");
			}
			if (oldVersion < 10)
			{
				db.execSQL("ALTER TABLE appInfo RENAME TO " + DbConstant.TABLE_NAME_APP_INFO);
			}
			if (oldVersion < 12)
			{
				db.execSQL("DROP TABLE IF EXISTS " + DbConstant.TABLE_NAME_BLACK_APP_INFO);
			}
			if (oldVersion < 14)
			{
				db.execSQL("ALTER TABLE " + DbConstant.TABLE_NAME_APP_INFO + " ADD " + AppInfo.FIELD_SORT_NAME + " TEXT AFTER " + AppInfo.FIELD_NAME);
			}
			if (oldVersion < 15)
			{
				db.execSQL("ALTER TABLE " + DbConstant.TABLE_NAME_APP_INFO + " ADD " + AppInfo.FIELD_BLACK + " TEXT AFTER " + AppInfo.FIELD_ENABLED);
			}
			if (oldVersion < 16)
			{
				db.execSQL("DROP TABLE IF EXISTS " + DbConstant.TABLE_NAME_HIDE_APP_INFO);
			}
			if (oldVersion < 17)
			{
				db.execSQL("DROP TABLE IF EXISTS " + DbConstant.TABLE_NAME_BLACK_APP_INFO);
			}
			if (oldVersion < 18)
			{
				db.execSQL("ALTER TABLE " + DbConstant.TABLE_NAME_APP_INFO + " ADD " + AppInfo.FIELD_VERSION_NAME + " TEXT AFTER " + AppInfo.FIELD_LAUNCH_CLASS);
				db.execSQL("ALTER TABLE " + DbConstant.TABLE_NAME_APP_INFO + " ADD " + AppInfo.FIELD_VERSION_CODE + " TEXT AFTER " + AppInfo.FIELD_VERSION_NAME);
			}
			if (oldVersion < 19)
			{
				db.execSQL("ALTER TABLE " + DbConstant.TABLE_NAME_APP_INFO + " ADD " + AppInfo.FIELD_COMMON_USE + " INTEGER NOT NULL DEFAULT 1");
			}
			onCreate(db);
		}
	}
}

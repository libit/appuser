/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.db;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import cn.lrapps.android.ui.MyApplication;
import cn.lrapps.models.AppInfo;
import cn.lrapps.enums.AppBlackStatus;
import cn.lrapps.enums.AppEnableStatus;
import cn.lrapps.enums.AppExistStatus;
import cn.lrapps.enums.AppShowStatus;
import cn.lrapps.enums.SqlOrderType;
import cn.lrapps.enums.StatusType;
import cn.lrapps.utils.ListTools;
import cn.lrapps.utils.LogcatTools;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by libit on 15/8/31.
 */
public class DbAppServiceImpl implements DbAppService
{
	private static final String TAG = DbAppFactory.class.getSimpleName();
	private static final String TABLE_NAME = DbConstant.TABLE_NAME_APP_INFO;

	private ContentResolver getContextResolver()
	{
		Context context = MyApplication.getContext();
		if (context == null)
		{
			return null;
		}
		ContentResolver contentResolver = context.getContentResolver();
		if (contentResolver == null)
		{
			return null;
		}
		return contentResolver;
	}

	private Uri getUri()
	{
		return DbConstant.getTableUri(TABLE_NAME);
	}

	/**
	 * 添加App
	 *
	 * @param appInfo App对象
	 *
	 * @return 成功：true，失败：false
	 */
	@Override
	public boolean add(AppInfo appInfo)
	{
		if (appInfo == null)
		{
			return false;
		}
		ContentResolver contentResolver = getContextResolver();
		if (contentResolver == null)
		{
			return false;
		}
		Uri uri = contentResolver.insert(getUri(), appInfo.getObjectContentValues());
		return uri != null;
	}

	/**
	 * 更新App
	 *
	 * @param appInfo App对象
	 *
	 * @return 成功：true，失败：false
	 */
	@Override
	public boolean update(AppInfo appInfo)
	{
		if (appInfo == null)
		{
			return false;
		}
		ContentResolver contentResolver = getContextResolver();
		if (contentResolver == null)
		{
			return false;
		}
		int rows = contentResolver.update(getUri(), appInfo.getObjectContentValues(), AppInfo.FIELD_PACKAGE_NAME + " = ?", new String[]{appInfo.getPackageName()});
		return rows > 0;
	}

	/**
	 * 添加或更新App
	 *
	 * @param appInfo App对象
	 *
	 * @return 成功：true，失败：false
	 */
	@Override
	public boolean addOrUpdate(AppInfo appInfo)
	{
		if (appInfo == null)
		{
			return false;
		}
		ContentResolver contentResolver = getContextResolver();
		if (contentResolver == null)
		{
			return false;
		}
		ContentValues values = appInfo.getObjectContentValues();
		int rows = contentResolver.update(getUri(), values, AppInfo.FIELD_PACKAGE_NAME + " = ?", new String[]{appInfo.getPackageName()});
		if (rows < 1)
		{
			Uri uri = contentResolver.insert(getUri(), values);
			return uri != null;
		}
		return true;
	}

	/**
	 * 增加或更新App列表
	 *
	 * @param appInfoList App列表
	 *
	 * @return 增加成功的个数
	 */
	@Override
	public int addOrUpdateList(List<AppInfo> appInfoList)
	{
		int count = 0;
		if (ListTools.isNull(appInfoList))
		{
			return count;
		}
		ContentResolver contentResolver = getContextResolver();
		if (contentResolver == null)
		{
			return 0;
		}
		final Uri tableUri = getUri();
		final String where = AppInfo.FIELD_PACKAGE_NAME + " = ?";
		for (AppInfo appInfo : appInfoList)
		{
			try
			{
				LogcatTools.info(TAG, "当前App：" + appInfo.toString());
				ContentValues values = appInfo.getObjectContentValues();
				int rows = contentResolver.update(tableUri, values, where, new String[]{appInfo.getPackageName()});
				if (rows < 1)
				{
					Uri uri = contentResolver.insert(tableUri, values);
					if (uri != null)
						count++;
				}
			}
			catch (Exception e)
			{
				LogcatTools.info(TAG, "出现错误" + e.getMessage());
				e.printStackTrace();
			}
		}
		LogcatTools.info(TAG, "处理完毕");
		return count;
	}

	/**
	 * 删除App
	 *
	 * @param packageName App包名
	 *
	 * @return 成功：true，失败：false
	 */
	@Override
	public boolean delete(String packageName)
	{
		ContentResolver contentResolver = getContextResolver();
		if (contentResolver == null)
		{
			return false;
		}
		int rows = contentResolver.delete(getUri(), AppInfo.FIELD_PACKAGE_NAME + " = ?", new String[]{packageName});
		return rows > 0;
	}

	/**
	 * 删除
	 *
	 * @return 删除条数
	 */
	@Override
	public int deleteNotExistApps()
	{
		ContentResolver contentResolver = getContextResolver();
		if (contentResolver == null)
		{
			return 0;
		}
		int rows = contentResolver.delete(getUri(), AppInfo.FIELD_EXIST + " = ?", new String[]{AppExistStatus.NOT_EXIST.getStatus() + ""});
		return rows;
	}

	/**
	 * 清空App列表
	 */
	@Override
	public int deleteAll()
	{
		ContentResolver contentResolver = getContextResolver();
		if (contentResolver == null)
		{
			return 0;
		}
		int rows = contentResolver.delete(getUri(), null, null);
		LogcatTools.debug(TAG + " deleteAll", "清空数据库条数：" + rows);
		return rows;
	}

	/**
	 * 获取指定的App
	 *
	 * @param packageName App包名
	 *
	 * @return AppInfo
	 */
	@Override
	public AppInfo getAppInfo(String packageName)
	{
		AppInfo appInfo = null;
		ContentResolver contentResolver = getContextResolver();
		if (contentResolver == null)
		{
			return null;
		}
		Cursor cursor = contentResolver.query(getUri(), null, AppInfo.FIELD_PACKAGE_NAME + " = ?", new String[]{packageName}, null);
		if (cursor != null)
		{
			if (cursor.moveToFirst())
			{
				appInfo = AppInfo.getObjectFromDb(cursor);
				if (appInfo != null)
				{
					LogcatTools.debug(TAG + " query", "packageName:" + packageName + ",APP信息：" + appInfo.toString());
				}
			}
			cursor.close();
		}
		return appInfo;
	}

	/**
	 * 获取App列表
	 *
	 * @param enabledStatus 启用状态
	 * @param appType       App类型
	 * @param hideStatus    隐藏状态
	 * @param blackStatus   黑名单状态
	 *
	 * @return
	 */
	@Override
	public List<AppInfo> getAppInfoList(Integer enabledStatus, Integer appType, Integer hideStatus, Integer blackStatus, Integer commonUse)
	{
		return getAppInfoList(enabledStatus, appType, hideStatus, blackStatus, commonUse, AppInfo.FIELD_NAME_LABEL, SqlOrderType.ASC.getType());
	}

	@Override
	public List<AppInfo> getAppInfoList(Integer enabledStatus, Integer appType, Integer hideStatus, Integer blackStatus, Integer commonUse, String orderCol, String orderType)
	{
		List<AppInfo> appInfoList = new ArrayList<>();
		String condition = AppInfo.FIELD_EXIST + " = ?";
		List<String> params = new ArrayList<>();
		params.add(AppExistStatus.EXIST.getStatus() + "");
		if (enabledStatus != null)
		{
			condition += " AND " + AppInfo.FIELD_ENABLED + " = ?";
			params.add(enabledStatus + "");
		}
		if (appType != null)
		{
			condition += " AND " + AppInfo.FIELD_TYPE + " = ?";
			params.add(appType + "");
		}
		if (hideStatus != null)
		{
			condition += " AND " + AppInfo.FIELD_HIDE + " = ?";
			params.add(hideStatus + "");
		}
		if (blackStatus != null)
		{
			condition += " AND " + AppInfo.FIELD_BLACK + " = ?";
			params.add(blackStatus + "");
		}
		if (commonUse != null)
		{
			condition += " AND " + AppInfo.FIELD_COMMON_USE + " = ?";
			params.add(commonUse + "");
		}
		String[] args = null;
		int size = params.size();
		if (size > 0)
		{
			args = new String[size];
			for (int i = 0; i < size; i++)
			{
				args[i] = params.get(i);
			}
		}
		ContentResolver contentResolver = getContextResolver();
		if (contentResolver == null)
		{
			return appInfoList;
		}
		Cursor cursor = contentResolver.query(getUri(), null, condition, args, orderCol + " " + orderType);
		if (cursor != null)
		{
			if (cursor.moveToFirst())
			{
				do
				{
					AppInfo appInfo = AppInfo.getObjectFromDb(cursor);
					if (appInfo != null)
					{
						//						LogcatTools.debug(TAG + " getAppInfoList", "enabledStatus:" + enabledStatus + ",App信息：" + appInfo.toString());
						if (hideStatus == null || (hideStatus == AppShowStatus.SHOW.getStatus() && !appInfo.isHide()) || (hideStatus == AppShowStatus.HIDE.getStatus() && appInfo.isHide()))
						{
							appInfoList.add(appInfo);
						}
					}
				}
				while (cursor.moveToNext());
			}
			cursor.close();
		}
		return appInfoList;
	}

	/**
	 * 获取App列表数量
	 *
	 * @param enabledStatus 启用状态
	 * @param appType       App类型
	 * @param hideStatus    隐藏状态
	 * @param blackStatus   黑名单状态
	 *
	 * @return
	 */
	@Override
	public int getAppInfoListCount(Integer enabledStatus, Integer appType, Integer hideStatus, Integer blackStatus)
	{
		List<String> list = new ArrayList<>();
		String condition = AppInfo.FIELD_EXIST + " = ?";
		List<String> params = new ArrayList<>();
		params.add(AppExistStatus.EXIST.getStatus() + "");
		if (enabledStatus != null)
		{
			condition += " AND " + AppInfo.FIELD_ENABLED + " = ?";
			params.add(enabledStatus + "");
		}
		if (appType != null)
		{
			condition += " AND " + AppInfo.FIELD_TYPE + " = ?";
			params.add(appType + "");
		}
		if (hideStatus != null)
		{
			condition += " AND " + AppInfo.FIELD_HIDE + " = ?";
			params.add(hideStatus + "");
		}
		if (blackStatus != null)
		{
			condition += " AND " + AppInfo.FIELD_BLACK + " = ?";
			params.add(blackStatus + "");
		}
		String[] args = null;
		int size = params.size();
		if (size > 0)
		{
			args = new String[size];
			for (int i = 0; i < size; i++)
			{
				args[i] = params.get(i);
			}
		}
		ContentResolver contentResolver = getContextResolver();
		if (contentResolver == null)
		{
			return 0;
		}
		Cursor cursor = contentResolver.query(getUri(), new String[]{AppInfo.FIELD_PACKAGE_NAME, AppInfo.FIELD_HIDE}, condition, args, AppInfo.FIELD_NAME_LABEL + " ASC");
		if (cursor != null)
		{
			if (cursor.moveToFirst())
			{
				do
				{
					String packageName = cursor.getString(0);
					int isHide = cursor.getInt(1);
					if (packageName != null)
					{
						if (hideStatus == null || (hideStatus == isHide))
						{
							list.add(packageName);
						}
					}
				}
				while (cursor.moveToNext());
			}
			cursor.close();
		}
		return list.size();
	}

	/**
	 * 重置App状态
	 *
	 * @param isEnabled 是否启用
	 * @param isHide    是否隐藏
	 * @param isExist   是否存在
	 *
	 * @return 重置条数
	 */
	@Override
	public int resetAppStatus(Boolean isEnabled, Boolean isHide, Boolean isExist)
	{
		ContentResolver contentResolver = getContextResolver();
		if (contentResolver == null)
		{
			return 0;
		}
		ContentValues contentValues = new ContentValues();
		if (isEnabled != null)
		{
			contentValues.put(AppInfo.FIELD_ENABLED, AppEnableStatus.getAppStatus(isEnabled));
		}
		if (isHide != null)
		{
			contentValues.put(AppInfo.FIELD_HIDE, AppShowStatus.getHideStatus(isHide));
		}
		if (isExist != null)
		{
			contentValues.put(AppInfo.FIELD_EXIST, AppExistStatus.getAppStatus(isExist));
		}
		int rows = contentResolver.update(getUri(), contentValues, null, null);
		return rows;
	}

	/**
	 * 查询状态
	 *
	 * @param packageName
	 * @param fieldName
	 *
	 * @return
	 */
	@Override
	public int getStatus(String packageName, String fieldName)
	{
		ContentResolver contentResolver = getContextResolver();
		if (contentResolver == null)
		{
			return 0;
		}
		int status = 0;
		Cursor cursor = contentResolver.query(getUri(), new String[]{fieldName}, AppInfo.FIELD_PACKAGE_NAME + " = ?", new String[]{packageName}, null);
		if (cursor != null)
		{
			if (cursor.moveToFirst())
			{
				status = cursor.getInt(0);
			}
			cursor.close();
		}
		return status;
	}

	/**
	 * 查询包名是否存在
	 *
	 * @param packageName 包名
	 *
	 * @return
	 */
	@Override
	public boolean isExist(String packageName)
	{
		int existStatus = getStatus(packageName, AppInfo.FIELD_EXIST);
		return AppExistStatus.isExist(existStatus);
	}

	/**
	 * 查询包名是否隐藏
	 *
	 * @param packageName 包名
	 *
	 * @return
	 */
	@Override
	public boolean isHide(String packageName)
	{
		int hideStatus = getStatus(packageName, AppInfo.FIELD_HIDE);
		return AppShowStatus.isHide(hideStatus);
	}

	/**
	 * 查询包名是否黑名单
	 *
	 * @param packageName 包名
	 *
	 * @return
	 */
	@Override
	public boolean isBlack(String packageName)
	{
		int blackStatus = getStatus(packageName, AppInfo.FIELD_BLACK);
		return AppBlackStatus.isBlack(blackStatus);
	}

	/**
	 * 设置App为存在
	 *
	 * @param packageName 应用包名
	 * @param isExist     是否存在
	 *
	 * @return
	 */
	@Override
	public boolean setAppExist(String packageName, boolean isExist)
	{
		ContentResolver contentResolver = getContextResolver();
		if (contentResolver == null)
		{
			return false;
		}
		ContentValues values = new ContentValues();
		values.put(AppInfo.FIELD_EXIST, AppShowStatus.getHideStatus(isExist));
		int rows = contentResolver.update(getUri(), values, AppInfo.FIELD_PACKAGE_NAME + " = ?", new String[]{packageName});
		return rows > 0;
	}

	/**
	 * 设置App为隐藏
	 *
	 * @param packageName 应用包名
	 * @param isHide      是否隐藏
	 *
	 * @return
	 */
	@Override
	public boolean setAppHide(String packageName, boolean isHide)
	{
		ContentResolver contentResolver = getContextResolver();
		if (contentResolver == null)
		{
			return false;
		}
		ContentValues values = new ContentValues();
		values.put(AppInfo.FIELD_HIDE, AppShowStatus.getHideStatus(isHide));
		int rows = contentResolver.update(getUri(), values, AppInfo.FIELD_PACKAGE_NAME + " = ?", new String[]{packageName});
		return rows > 0;
	}

	/**
	 * 设置App为禁用
	 *
	 * @param packageName 应用包名
	 * @param isBlack     是否禁用
	 *
	 * @return
	 */
	@Override
	public boolean setAppBlack(String packageName, boolean isBlack)
	{
		ContentResolver contentResolver = getContextResolver();
		if (contentResolver == null)
		{
			return false;
		}
		ContentValues values = new ContentValues();
		values.put(AppInfo.FIELD_BLACK, AppBlackStatus.getAppStatus(isBlack));
		int rows = contentResolver.update(getUri(), values, AppInfo.FIELD_PACKAGE_NAME + " = ?", new String[]{packageName});
		return rows > 0;
	}

	/**
	 * 设置APP是否为常用
	 *
	 * @param packageName
	 * @param commonUse
	 *
	 * @return
	 */
	@Override
	public boolean setAppCommonUse(String packageName, int commonUse)
	{
		ContentResolver contentResolver = getContextResolver();
		if (contentResolver == null)
		{
			return false;
		}
		ContentValues values = new ContentValues();
		if (commonUse != StatusType.ENABLE.getStatus())
		{
			commonUse = StatusType.DISABLE.getStatus();
		}
		values.put(AppInfo.FIELD_COMMON_USE, commonUse);
		int rows = contentResolver.update(getUri(), values, AppInfo.FIELD_PACKAGE_NAME + " = ?", new String[]{packageName});
		return rows > 0;
	}
}

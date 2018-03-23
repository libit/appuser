/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;

import com.google.gson.annotations.SerializedName;
import cn.lrapps.db.DbConstant;
import cn.lrapps.enums.AppBlackStatus;
import cn.lrapps.enums.AppEnableStatus;
import cn.lrapps.enums.AppExistStatus;
import cn.lrapps.enums.AppShowStatus;
import cn.lrapps.enums.StatusType;
import cn.lrapps.utils.PinyinTools;
import cn.lrapps.utils.StringTools;

import java.util.Comparator;

/**
 * Created by libit on 15/8/19.
 */
public class AppInfo extends DbObject implements Comparator<AppInfo>
{
	public static final String FIELD_UID = "uid";
	public static final String FIELD_NAME = "name";
	public static final String FIELD_SORT_NAME = "sort_name";
	public static final String FIELD_NAME_LABEL = "name_label";
	public static final String FIELD_PACKAGE_NAME = "package_name";
	public static final String FIELD_LAUNCH_CLASS = "launch_class";
	public static final String FIELD_VERSION_NAME = "version_name";
	public static final String FIELD_VERSION_CODE = "version_code";
	//    public static final String FIELD_PHOTO = "photo";
	public static final String FIELD_TYPE = "type";
	public static final String FIELD_ENABLED = "is_enabled";
	public static final String FIELD_BLACK = "is_black";
	public static final String FIELD_HIDE = "is_hide";
	public static final String FIELD_EXIST = "is_exist";
	public static final String FIELD_COMMON_USE = "common_use";
	@SerializedName("id")
	private String id;// 主键
	@SerializedName("uid")
	private String uid;// 程序的用户ID，不同的程序ID可能一样
	@SerializedName("name")
	private String name;// 程序名称
	@SerializedName("sortName")
	private String sortName;// 分类名称
	@SerializedName("nameLabel")
	private String nameLabel;// 程序名称的拼音，用户检索
	@SerializedName("packageName")
	private String packageName;// 包名，唯一标识
	@SerializedName("launchClassName")
	private String launchClassName;// 启动的class
	@SerializedName("versionName")
	private String versionName;// 版本号
	@SerializedName("versionCode")
	private Integer versionCode;// 版本代号
	@SerializedName("photo")
	private Bitmap photo;// 程序图片
	@SerializedName("type")
	private int type;// 程序类型（系统程序或用户程序）
	@SerializedName("isEnabled")
	private boolean isEnabled;// 程序状态（启用或禁用）
	@SerializedName("isBlack")
	private boolean isBlack;// 程序是否是禁用程序
	@SerializedName("isHide")
	private boolean isHide;// 程序是否是隐藏程序
	@SerializedName("isExist")
	private boolean isExist;// 程序是否还存在（当用户卸载后）
	@SerializedName("commonUse")
	private Integer commonUse;// 是否常用程序（用于快速启动支付、分享等场景）

	public AppInfo()
	{
		//        type = ConstValues.USER;
		//        isEnabled = true;
		//        isHide = false;
		isBlack = false;
	}

	public AppInfo(String id, String uid, String name, String sortName, String nameLabel, String packageName, String launchClassName, String versionName, Integer versionCode, Bitmap photo, int type, boolean isEnabled, boolean isBlack, boolean isHide, boolean isExist, Integer commonUse)
	{
		this.id = id;
		this.uid = uid;
		this.name = name;
		this.sortName = sortName;
		this.nameLabel = nameLabel;
		this.packageName = packageName;
		this.launchClassName = launchClassName;
		this.versionName = versionName;
		this.versionCode = versionCode;
		this.photo = photo;
		this.type = type;
		this.isEnabled = isEnabled;
		this.isBlack = isBlack;
		this.isHide = isHide;
		this.isExist = isExist;
		this.commonUse = commonUse;
	}

	/**
	 * 获取创建表的SQL语句
	 *
	 * @return SQL语句
	 */
	public static String getCreateTableSQL()
	{
		// App信息表
		return String.format("CREATE TABLE IF NOT EXISTS %s (%s INTEGER PRIMARY KEY AUTOINCREMENT,%s TEXT NOT NULL,%s TEXT NOT NULL,%s TEXT,%s TEXT NOT NULL,%s TEXT NOT NULL UNIQUE,%s TEXT,%s TEXT,%s INTEGER,%s INTEGER NOT NULL,%s INTEGER NOT NULL,%s INTEGER NOT NULL,%s INTEGER NOT NULL,%s INTEGER NOT NULL,%s INTEGER DEFAULT 1);", DbConstant.TABLE_NAME_APP_INFO, AppInfo.FIELD_ID, AppInfo.FIELD_UID, AppInfo.FIELD_NAME, AppInfo.FIELD_SORT_NAME, AppInfo.FIELD_NAME_LABEL, AppInfo.FIELD_PACKAGE_NAME, AppInfo.FIELD_LAUNCH_CLASS, AppInfo.FIELD_VERSION_NAME, AppInfo.FIELD_VERSION_CODE, AppInfo.FIELD_TYPE, AppInfo.FIELD_ENABLED, AppInfo.FIELD_BLACK, AppInfo.FIELD_HIDE, AppInfo.FIELD_EXIST, AppInfo.FIELD_COMMON_USE);
	}

	/**
	 * 从数据库中取出对象
	 *
	 * @param cursor
	 *
	 * @return
	 */
	public static AppInfo getObjectFromDb(Cursor cursor)
	{
		AppInfo appInfo = new AppInfo();
		appInfo.setId(cursor.getString(cursor.getColumnIndex(FIELD_ID)));
		appInfo.setUid(cursor.getString(cursor.getColumnIndex(FIELD_UID)));
		appInfo.setName(cursor.getString(cursor.getColumnIndex(FIELD_NAME)));
		appInfo.setSortName(cursor.getString(cursor.getColumnIndex(FIELD_SORT_NAME)));
		appInfo.setNameLabel(cursor.getString(cursor.getColumnIndex(FIELD_NAME_LABEL)));
		appInfo.setPackageName(cursor.getString(cursor.getColumnIndex(FIELD_PACKAGE_NAME)));
		appInfo.setLaunchClassName(cursor.getString(cursor.getColumnIndex(FIELD_LAUNCH_CLASS)));
		appInfo.setVersionName(cursor.getString(cursor.getColumnIndex(FIELD_VERSION_NAME)));
		appInfo.setVersionCode(cursor.getInt(cursor.getColumnIndex(FIELD_VERSION_CODE)));
		//        byte[] bs = cursor.getBlob(cursor.getColumnIndex(FIELD_PHOTO));
		//        if (bs != null && bs.length > 0)
		//        {
		//            Bitmap bmp = BitmapFactory.decodeByteArray(bs, 0, bs.length);
		//            appInfo.setPhoto(bmp);
		//        }
		appInfo.setType(cursor.getInt(cursor.getColumnIndex(FIELD_TYPE)));
		appInfo.setIsEnabled(AppEnableStatus.isEnabled(cursor.getInt(cursor.getColumnIndex(FIELD_ENABLED))));
		appInfo.setBlack(AppBlackStatus.isBlack(cursor.getInt(cursor.getColumnIndex(FIELD_BLACK))));
		appInfo.setIsHide(AppShowStatus.isHide(cursor.getInt(cursor.getColumnIndex(FIELD_HIDE))));
		appInfo.setIsExist(AppExistStatus.isExist(cursor.getInt(cursor.getColumnIndex(FIELD_EXIST))));
		appInfo.setCommonUse(cursor.getInt(cursor.getColumnIndex(FIELD_COMMON_USE)));
		return appInfo;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getUid()
	{
		return uid;
	}

	public void setUid(String uid)
	{
		this.uid = uid;
	}

	public String getPackageName()
	{
		return packageName;
	}

	public void setPackageName(String packageName)
	{
		this.packageName = packageName;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getNameLabel()
	{
		return nameLabel;
	}

	public void setNameLabel(String nameLabel)
	{
		this.nameLabel = nameLabel;
	}

	public String getSortName()
	{
		return sortName;
	}

	public void setSortName(String sortName)
	{
		this.sortName = sortName;
	}

	public String getLaunchClassName()
	{
		return launchClassName;
	}

	public void setLaunchClassName(String launchClassName)
	{
		this.launchClassName = launchClassName;
	}

	public String getVersionName()
	{
		return versionName;
	}

	public void setVersionName(String versionName)
	{
		this.versionName = versionName;
	}

	public Integer getVersionCode()
	{
		return versionCode;
	}

	public void setVersionCode(Integer versionCode)
	{
		this.versionCode = versionCode;
	}

	public Bitmap getPhoto()
	{
		return photo;
	}

	public void setPhoto(Bitmap photo)
	{
		this.photo = photo;
	}

	public int getType()
	{
		return type;
	}

	public void setType(int type)
	{
		this.type = type;
	}

	public boolean isEnabled()
	{
		return isEnabled;
	}

	public void setIsEnabled(boolean isEnabled)
	{
		this.isEnabled = isEnabled;
	}

	public boolean isBlack()
	{
		return isBlack;
	}

	public void setBlack(boolean black)
	{
		isBlack = black;
	}

	public boolean isHide()
	{
		return isHide;
	}

	public void setIsHide(boolean isHide)
	{
		this.isHide = isHide;
	}

	public boolean isExist()
	{
		return isExist;
	}

	public void setIsExist(boolean isExist)
	{
		this.isExist = isExist;
	}

	public Integer getCommonUse()
	{
		return commonUse;
	}

	public void setCommonUse(Integer commonUse)
	{
		this.commonUse = commonUse;
	}

	/**
	 * 转换成数据库存储的数据
	 *
	 * @return ContentValues
	 */
	public ContentValues getObjectContentValues()
	{
		ContentValues contentValues = new ContentValues();
		if (id != null)
		{
			contentValues.put(AppInfo.FIELD_ID, id);
		}
		contentValues.put(AppInfo.FIELD_UID, uid);
		contentValues.put(AppInfo.FIELD_NAME, name);
		contentValues.put(AppInfo.FIELD_SORT_NAME, sortName);
		if (StringTools.isNull(nameLabel))
		{
			nameLabel = PinyinTools.Chinese2Pinyin(name);
		}
		contentValues.put(AppInfo.FIELD_NAME_LABEL, nameLabel);
		contentValues.put(AppInfo.FIELD_PACKAGE_NAME, packageName);
		if (!StringTools.isNull(getLaunchClassName()))
		{
			contentValues.put(AppInfo.FIELD_LAUNCH_CLASS, launchClassName);
		}
		contentValues.put(AppInfo.FIELD_VERSION_NAME, versionName);
		contentValues.put(AppInfo.FIELD_VERSION_CODE, versionCode);
		//        if (photo != null)
		//        {
		//            ByteArrayOutputStream os = new ByteArrayOutputStream();
		//            photo.compress(Bitmap.CompressFormat.PNG, 100, os);
		//            contentValues.put(AppInfo.FIELD_PHOTO, os.toByteArray());
		//        }
		contentValues.put(AppInfo.FIELD_TYPE, type);
		contentValues.put(AppInfo.FIELD_ENABLED, AppEnableStatus.getAppStatus(isEnabled));
		contentValues.put(AppInfo.FIELD_BLACK, AppBlackStatus.getAppStatus(isBlack));
		contentValues.put(AppInfo.FIELD_HIDE, AppShowStatus.getHideStatus(isHide));
		contentValues.put(AppInfo.FIELD_EXIST, AppExistStatus.getAppStatus(isExist));
		if (commonUse == null)
		{
			commonUse = StatusType.DISABLE.getStatus();
		}
		contentValues.put(AppInfo.FIELD_COMMON_USE, commonUse);
		return contentValues;
	}

	@Override
	public String toString()
	{
		return String.format("uid:%s,name:%s,sortName:%s,nameLabel:%s,packageName:%s,type:%d,enabled:%d,hide:%d,exist:%d,black:%d.", uid, name, sortName, nameLabel, packageName, type, AppEnableStatus.getAppStatus(isEnabled), AppShowStatus.getHideStatus(isHide), AppExistStatus.getAppStatus(isExist), AppBlackStatus.getAppStatus(isBlack));
	}

	@Override
	public int compare(AppInfo lhs, AppInfo rhs)
	{
		if (lhs == null && rhs == null)
		{
			return 0;
		}
		if (lhs == null)
		{
			return -1;
		}
		if (rhs == null)
		{
			return -1;
		}
		if (lhs == rhs)
		{
			return 0;
		}
		String lName = PinyinTools.Chinese2Pinyin(lhs.getName());
		String rName = PinyinTools.Chinese2Pinyin(rhs.getName());
		return lName.compareToIgnoreCase(rName);
	}
}

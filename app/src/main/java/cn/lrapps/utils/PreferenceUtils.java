/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */

package cn.lrapps.utils;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import cn.lrapps.android.ui.MyApplication;
import cn.lrapps.enums.FloatFuncType;
import cn.lrapps.enums.FloatType;
import cn.lrapps.enums.LogLevel;
import cn.lrapps.enums.StatusType;

import java.util.HashMap;
import java.util.Map;

public class PreferenceUtils
{
	private static final String TAG = PreferenceUtils.class.getName();
	public static final String PREF_USER_ID = "pref_username_key";//账号
	public static final String PREF_SESSION_ID = "pref_session_id_key";//登录Session ID
	public static final String PREF_CRASH_FILE_NAME = "PREF_CRASH_FILE";//崩溃的日志文件名
	public static final String APP_COLUMN_COUNT_PORTRAIT = "app_column_count_portrait";//竖屏显示的程序数量
	public static final String APP_COLUMN_COUNT_LANDSCAPE = "app_column_count_landscape";//横屏显示的程序数量
	public static final String APP_FONT_SIZE = "app_font_size";//列表字体显示大小
	public static final String LOGCAT_LEVEL = "logcat_level";//日志记录级别
	public static final String LOGCAT_AUTO_UPDATE = "logcat_auto_update";//是否自动提交日志
	public static final String IS_ROOT = "is_root";//手机是否已ROOT，当前未生效
	public static final String IS_FIRST_RUN = "is_first_run";//是否第一次运行，以便于做一些初始化工作
	public static final String IS_SHOW_NOTIFICATION = "is_show_notification";//是否显示状态栏
	public static final String RUNNING_BLACK_APP_COUNT = "app_running_black_app_count";//后台运行程序数量，0为不限制，当前未生效
	public static final String IS_ACCEPT_ROLE = "is_accepted_role";//是否接受条款，接受才能使用
	public static final String APP_WIDGET_INFOS = "app_widget_infos";//widget插件的id数组
	public static final String NOTIFICATION_SWITCH_CLICK = "notification_switch_click";//状态栏点击事件切换，已废弃
	public static final String IS_SHOW_HOVER_BALL = "is_show_hover_ball";//是否显示悬浮球
	public static final String HOVER_BALL_X = "hover_ball_x";//悬浮球x坐标
	public static final String HOVER_BALL_Y = "hover_ball_y";//悬浮球y坐标
	public static final String FLOAT_TYPE = "float_type";//悬浮球类型
	public static final String IS_BOOT_START = "is_boot_start";//是否开机启动
	public static final String FLOAT_FUNC = "float_func";//悬浮窗单击功能
	public static final String APP_PADDING_SIZE = "app_padding_size";//应用图标之间的padding
	public static final String SHOW_DISABLE_DIALOG = "show_disable_dialog";//新安装程序显示禁用对话框
	public static final String SHOW_DISABLE_COUNT = "show_disabled_count";//界面显示禁用数量
	public static final String SHOW_ALL_APPLIST = "show_all_applist";//显示全部应用界面
	public static final String SHOW_ENABLE_DISABLE_APPLIST = "show_enable_disable_applist";//显示启用和禁用应用界面
	public static final String FLOAT_SIDE_WIDTH = "float_width_setting";//侧边多少像素可以响应操作
	public static final String IS_DEBUG = "is_debug";//是否调试
	private final static HashMap<String, String> STRING_PREFS = new HashMap<String, String>()
	{
		private static final long serialVersionUID = 1L;

		{
			put(PREF_USER_ID, "");
			put(PREF_SESSION_ID, "");
			put(PREF_CRASH_FILE_NAME, "");
			put(APP_COLUMN_COUNT_PORTRAIT, "5");
			put(APP_COLUMN_COUNT_LANDSCAPE, "8");
			put(APP_FONT_SIZE, "10");
			put(LOGCAT_LEVEL, LogLevel.LEVEL_3.getLevel() + "");
			put(RUNNING_BLACK_APP_COUNT, "4");
			put(APP_WIDGET_INFOS, "");
			put(HOVER_BALL_X, "0");
			put(HOVER_BALL_Y, "0");
			put(FLOAT_TYPE, FloatType.FLOAT_BALL.getType());
			put(NOTIFICATION_SWITCH_CLICK, StatusType.ENABLE.getStatus() + "");
			put(FLOAT_FUNC, FloatFuncType.START.getType());
			put(APP_PADDING_SIZE, "15");
			put(FLOAT_SIDE_WIDTH, "10");
		}
	};
	private final static HashMap<String, Boolean> BOOLEAN_PREFS = new HashMap<String, Boolean>()
	{
		private static final long serialVersionUID = 1L;

		{
			put(LOGCAT_AUTO_UPDATE, true);
			put(IS_ACCEPT_ROLE, false);
			put(IS_ROOT, false);
			put(IS_FIRST_RUN, true);
			put(IS_SHOW_NOTIFICATION, true);
			put(IS_SHOW_HOVER_BALL, false);
			put(IS_BOOT_START, true);
			put(SHOW_DISABLE_DIALOG, false);
			put(SHOW_DISABLE_COUNT, false);
			put(SHOW_ALL_APPLIST, true);
			put(SHOW_ENABLE_DISABLE_APPLIST, false);
			put(IS_DEBUG, true);
		}
	};
	protected static PreferenceUtils instance = null;
	private final SharedPreferences prefs;

	protected PreferenceUtils()
	{
		prefs = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());
		//		prefs=MyApplication.getContext().getSharedPreferences(PreferenceManager.getDefaultSharedPreferencesName(MyApplication.getContext()),Context.MODE_MULTI_PROCESS);
	}

	synchronized public static PreferenceUtils getInstance()
	{
		if (instance == null)
		{
			instance = new PreferenceUtils();
		}
		return instance;
	}

	private static String gPrefStringValue(SharedPreferences aPrefs, String key)
	{
		if (STRING_PREFS.containsKey(key))
		{
			return aPrefs.getString(key, STRING_PREFS.get(key));
		}
		return "";
	}

	private static Boolean gPrefBooleanValue(SharedPreferences aPrefs, String key)
	{
		if (BOOLEAN_PREFS.containsKey(key))
		{
			return aPrefs.getBoolean(key, BOOLEAN_PREFS.get(key));
		}
		return false;
	}

	public String getUserId()
	{
		return getStringValue(PREF_USER_ID);
	}

	public boolean setUserId(String value)
	{
		return setStringValue(PREF_USER_ID, value);
	}

	public String getSessionId()
	{
		return getStringValue(PREF_SESSION_ID);
	}

	public boolean setSessionId(String value)
	{
		return setStringValue(PREF_SESSION_ID, value);
	}

	synchronized public boolean setStringValue(String key, String value)
	{
		Editor editor = prefs.edit();
		editor.putString(key, value);
		return editor.commit();
	}

	public String getStringValue(String key)
	{
		return gPrefStringValue(prefs, key);
	}

	synchronized public void setBooleanValue(String key, Boolean value)
	{
		Editor editor = prefs.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	public Boolean getBooleanValue(String key)
	{
		return gPrefBooleanValue(prefs, key);
	}

	public int getIntegerValue(String key)
	{
		try
		{
			return Integer.parseInt(getStringValue(key));
		}
		catch (NumberFormatException e)
		{
			LogcatTools.error(TAG, "Invalid " + key + " format : expect a int");
		}
		return Integer.parseInt(STRING_PREFS.get(key));
	}

	/**
	 * Set all values to default
	 */
	public void resetAllDefaultValues()
	{
		for (String key : STRING_PREFS.keySet())
		{
			setStringValue(key, STRING_PREFS.get(key));
		}
		for (String key : BOOLEAN_PREFS.keySet())
		{
			setBooleanValue(key, BOOLEAN_PREFS.get(key));
		}
	}

	/**
	 * 备份数据
	 *
	 * @return
	 */
	public String backup()
	{
		Map<String, String> map = new HashMap<>();
		for (String key : STRING_PREFS.keySet())
		{
			map.put(key, getStringValue(key));
		}
		for (String key : BOOLEAN_PREFS.keySet())
		{
			setBooleanValue(key, BOOLEAN_PREFS.get(key));
			map.put(key, getBooleanValue(key).toString());
		}
		return GsonTools.toJson(map);
	}
}

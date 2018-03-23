/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.services;

import cn.lrapps.utils.AppConfig;

/**
 * Created by libit on 16/4/6.
 */
public class ApiConfig
{
	public static final int API_VERSION = 3;
	//	public static final String DEBUG_URL = "http://192.168.168.4:8080/lr_appuser/user";
	public static final String RELEASE_URL = "http://lrapps.cn/lr_appuser/";
	public static final String SUBMIT_BUG = getServerUrl() + "user/ajaxAddClientBugInfo";//BUG日志提交
	public static final String CHECK_UPDATE = getServerUrl() + "user/ajaxGetLastAndroidClientInfo";//检查更新
	public static final String UPLOAD_BUG_FILE = getServerUrl() + "user/ajaxUploadAndroidBugFile";//上传文件
	public static final String SUBMIT_ADVICE = getServerUrl() + "user/ajaxAddAdviceInfo";//提交意见反馈
	public static final String USER_LOGIN = getServerUrl() + "user/ajaxLogin";//用户登录
	public static final String USER_CHANGE_PWD = getServerUrl() + "user/ajaxChangePwd";//用户修改密码
	public static final String USER_REGISTER = getServerUrl() + "user/ajaxRegister";//用户注册
	public static final String GET_USER_INFO = getServerUrl() + "user/ajaxGetMyUserInfo";//获取用户信息
	public static final String UPLOAD_PIC = getServerUrl() + "user/ajaxUploadPic";//上传图片
	public static final String UPDATE_USER_HEADER = getServerUrl() + "user/ajaxUpdateUserHeader";//上传用户头像
	public static final String BACKUP_LIST = getServerUrl() + "user/ajaxGetUserBackupInfoList";//获取用户备份列表
	public static final String GET_BACKUP_CONFIG = getServerUrl() + "user/ajaxGetUserConfigBackupInfo";//获取用户配置备份信息
	public static final String UPDATE_BACKUP_CONFIG = getServerUrl() + "user/ajaxUpdateUserConfigBackupInfo";//用户配置备份
	//	public static final String BACKUP_APPS_LIST = getServerUrl() + "user/ajaxUserToGetBackupAppsList";//获取用户Apps备份列表
	//	public static final String GET_BACKUP_APPS = getServerUrl() + "user/ajaxUserToGetBackupApps";//获取用户Apps备份信息
	public static final String UPDATE_BACKUP_APPS = getServerUrl() + "user/ajaxUpdateUserAppsBackupInfo";//用户Apps备份
	public static final String DELETE_BACKUP_INFO = getServerUrl() + "user/ajaxDeleteUserBackupInfo";//用户删除备份信息
	public static final String USER_SHARE = getServerUrl() + "user/ajaxUserShareApp";//用户分享App
	public static final String USER_GET_AUTH_CODE = getServerUrl() + "user/ajaxGetAuthCode";//获取登录验证码

	public static String getServerUrl()
	{
		if (AppConfig.isDebug())
		{
			return RELEASE_URL;
		}
		else
		{
			return RELEASE_URL;
		}
	}

	// 关于我们页面
	public static String getAboutUrl()
	{
		return getServerUrl() + "about";
	}

	// 更多应用页面
	public static String getMoreAppUrl()
	{
		return getServerUrl() + "moreApp";
	}

	//教程页面
	public static String getTutorialUrl()
	{
		return getServerUrl() + "tutorial";
	}

	//意见反馈页面，已废弃，用原生界面提交意见反馈代替
	public static String getAdviceUrl()
	{
		return getServerUrl() + "pageAdvice";
	}
}

/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */

package cn.lrapps.utils;

/**
 * Created by libit on 15/8/31.
 */
public class ConstValues
{
	public static final String INSTALL_SHORTCUT = "com.android.launcher.action.INSTALL_SHORTCUT";
	public static final String DATA_TITLE = "dialog.layout_title";
	public static final String DATA_CONTENT = "dialog.content";
	public final static String DATA_WEB_TITLE = "data.web.layout_title";
	public final static String DATA_WEB_URL = "data.web.url";
	public final static String DATA_PACKAGE_NAME = "data.package.name";
	public final static String DATA_APP_STATUS = "data.app.status";
	public final static String DATA_ACTION = "data.action";
	public final static String DATA_FONT_URL = "data.font_url";
	// 登录结果
	public final static int REQUEST_LOGIN = 1000;//登录代码
	public final static int REQUEST_LOGIN_USER = 1001;//登录代码，跳转到用户中心
	public final static int RESULT_LOGIN_SUCCESS = 2000;//登录成功代码
	public final static int RESULT_LOGIN_ERROR = 2001;//登录失败代码
	// 注册结果
	public final static int REQUEST_REGISTER = 1100;//注册代码
	public final static int RESULT_REGISTER_SUCCESS = 2002;//注册成功代码
	public final static int RESULT_REGISTER_ERROR = 2003;//注册失败代码
	public static final String DATA_SHOW_SERVER = "data.show_server";//是否显示云端
	// 选取图片
	public static final int IMAGE_PICKER = 1006;
}

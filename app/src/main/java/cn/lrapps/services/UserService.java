/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.services;

import android.content.Context;
import android.content.Intent;

import com.androidquery.callback.AjaxStatus;
import com.lrcall.appuser.R;
import cn.lrapps.enums.UserEvent;
import cn.lrapps.models.ReturnInfo;
import cn.lrapps.utils.GsonTools;
import cn.lrapps.utils.PreferenceUtils;
import cn.lrapps.utils.StringTools;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import cn.lrapps.android.ui.customer.ToastView;
import cn.lrapps.models.UserInfo;

import static android.R.attr.name;

/**
 * 用户服务类，用于操作与用户相关的服务
 * Created by libit on 16/4/6.
 */
public class UserService extends BaseService
{
	public UserService(Context context)
	{
		super(context);
	}

	/**
	 * 检查本地是否已登录
	 *
	 * @return
	 */
	public static boolean isLogin()
	{
		return (!StringTools.isNull(PreferenceUtils.getInstance().getUserId()) && !StringTools.isNull(PreferenceUtils.getInstance().getSessionId()));
	}
	/**
	 * 获取登录验证码
	 *
	 * @param tips
	 * @param needServiceProcess
	 */
	//	public void getAuthCode(String tips, final boolean needServiceProcess)
	//	{
	//		Map<String, Object> params = new HashMap<>();
	//		ajaxStringCallback(ApiConfig.USER_GET_AUTH_CODE, params, true, tips, needServiceProcess);
	//	}

	/**
	 * 用户登录
	 *
	 * @param username           账号
	 * @param password           密码
	 * @param code               验证码
	 * @param tips               等待提示信息
	 * @param needServiceProcess 是否需要服务类处理
	 */
	public void login(String username, String password, String code, String tips, final boolean needServiceProcess)
	{
		Map<String, Object> params = new HashMap<>();
		params.put("userId", username);
		params.put("password", password);
		params.put("code", code);
		ajaxStringCallback(ApiConfig.USER_LOGIN, params, true, tips, needServiceProcess);
	}

	/**
	 * 用户注册
	 *
	 * @param username           账号
	 * @param password           密码
	 * @param nickname           昵称
	 * @param number             手机号码
	 * @param country            国家
	 * @param province           省份
	 * @param city               城市
	 * @param address            地址
	 * @param sex                性别
	 * @param picUrl             图片地址
	 * @param birthday           出生日期
	 * @param remark             备注
	 * @param tips               等待提示信息
	 * @param needServiceProcess 是否需要服务类处理
	 */
	public void register(String username, String password, String nickname, String number, String country, String province, String city, String address, Byte sex, String picUrl, Long birthday, String remark, String tips, final boolean needServiceProcess)
	{
		Map<String, Object> params = new HashMap<>();
		params.put("userId", username);
		params.put("password", password);
		params.put("name", name);
		params.put("nickname", nickname);
		params.put("number", number);
		params.put("country", country);
		params.put("province", province);
		params.put("city", city);
		params.put("address", address);
		params.put("remark", remark);
		params.put("sex", sex);
		params.put("picUrl", picUrl);
		params.put("birthday", birthday);
		ajaxStringCallback(ApiConfig.USER_REGISTER, params, true, tips, needServiceProcess);
	}

	/**
	 * 获取用户信息
	 *
	 * @param tips               等待提示信息
	 * @param needServiceProcess 是否需要服务类处理
	 */
	public void getUserInfo(String tips, final boolean needServiceProcess)
	{
		Map<String, Object> params = new HashMap<>();
		ajaxStringCallback(ApiConfig.GET_USER_INFO, params, true, tips, needServiceProcess);
	}

	/**
	 * 更新用户头像
	 *
	 * @param headerUrl          头像地址
	 * @param tips               等待提示信息
	 * @param needServiceProcess 是否需要服务类处理
	 */
	public void updateUserHeader(String headerUrl, String tips, final boolean needServiceProcess)
	{
		Map<String, Object> params = new HashMap<>();
		params.put("headUrl", headerUrl);
		ajaxStringCallback(ApiConfig.UPDATE_USER_HEADER, params, true, tips, needServiceProcess);
	}

	/**
	 * 注销登录
	 */
	public static void logout()
	{
		PreferenceUtils.getInstance().setSessionId("");
		EventBus.getDefault().post(UserEvent.LOGOUT);
	}

	/**
	 * 分享给好友
	 *
	 * @param tips               等待提示信息
	 * @param needServiceProcess 是否需要服务类处理
	 */
	public void share(String tips, final boolean needServiceProcess)
	{
		Map<String, Object> params = new HashMap<>();
		ajaxStringCallback(ApiConfig.USER_SHARE, params, true, tips, needServiceProcess);
	}

	/**
	 * 用户修改密码
	 *
	 * @param password           旧密码
	 * @param newPassword        新密码
	 * @param tips               等待提示信息
	 * @param needServiceProcess 是否需要服务类处理
	 */
	public void changePwd(String password, String newPassword, String tips, final boolean needServiceProcess)
	{
		Map<String, Object> params = new HashMap<>();
		params.put("password", password);
		params.put("newPassword", newPassword);
		ajaxStringCallback(ApiConfig.USER_CHANGE_PWD, params, true, tips, needServiceProcess);
	}

	@Override
	public void parseData(String url, String result, AjaxStatus status)
	{
		if (url.endsWith(ApiConfig.USER_LOGIN))
		{
			ReturnInfo returnInfo = GsonTools.getReturnInfo(result);
			if (ReturnInfo.isSuccess(returnInfo))
			{
				//登录成功，保存账号和SessionId
				UserInfo userInfo = GsonTools.getReturnObject(returnInfo, UserInfo.class);
				PreferenceUtils.getInstance().setUserId(userInfo.getUserId());
				PreferenceUtils.getInstance().setSessionId(userInfo.getSessionId());
			}
			else
			{
				// 登录失败，清空SessionId
				PreferenceUtils.getInstance().setSessionId("");
			}
		}
		else if (url.endsWith(ApiConfig.USER_REGISTER))
		{
			ReturnInfo returnInfo = GsonTools.getReturnInfo(result);
			if (ReturnInfo.isSuccess(returnInfo))
			{
				//注册成功，保存账号和SessionId
				UserInfo userInfo = GsonTools.getReturnObject(returnInfo, UserInfo.class);
				PreferenceUtils.getInstance().setUserId(userInfo.getUserId());
				PreferenceUtils.getInstance().setSessionId(userInfo.getSessionId());
			}
			else
			{
				// 注册失败，清空SessionId
				PreferenceUtils.getInstance().setSessionId("");
			}
		}
		else if (url.endsWith(ApiConfig.USER_CHANGE_PWD))
		{
			ReturnInfo returnInfo = GsonTools.getReturnInfo(result);
			if (ReturnInfo.isSuccess(returnInfo))
			{
				//修改成功，保存SessionId
				UserInfo userInfo = GsonTools.getReturnObject(returnInfo, UserInfo.class);
				PreferenceUtils.getInstance().setSessionId(userInfo.getSessionId());
			}
		}
		else if (url.endsWith(ApiConfig.GET_USER_INFO))
		{
			ReturnInfo returnInfo = GsonTools.getReturnInfo(result);
			if (!ReturnInfo.isSuccess(returnInfo))
			{
				// 获取失败，清空SessionId
				PreferenceUtils.getInstance().setSessionId("");
			}
		}
		else if (url.endsWith(ApiConfig.USER_SHARE))
		{
			ReturnInfo returnInfo = GsonTools.getReturnInfo(result);
			if (ReturnInfo.isSuccess(returnInfo))
			{
				String msg = returnInfo.getMsg();
				Intent intent = new Intent(Intent.ACTION_SEND); // 启动分享发送到属性
				intent.setType("text/plain"); // 分享发送到数据类型
				intent.putExtra(Intent.EXTRA_SUBJECT, "分享"); // 分享的主题
				intent.putExtra(Intent.EXTRA_TEXT, msg); // 分享的内容
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // 允许intent启动新的activity
				context.startActivity(Intent.createChooser(intent, "来自" + context.getString(R.string.app_name) + "的分享")); // //目标应用选择对话框的
			}
			else
			{
				ToastView.showCenterToast(context, R.drawable.ic_do_fail, "暂时无法分享！");
			}
		}
	}

	@Override
	protected void parseData(String url, File file, AjaxStatus status)
	{
		super.parseData(url, file, status);
		if (url.endsWith("jpg"))
		{
			if (file == null)
			{
			}
		}
	}
}

/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.services;

import android.content.Context;

import com.androidquery.callback.AjaxStatus;
import com.google.gson.reflect.TypeToken;
import cn.lrapps.android.ui.customer.ToastView;
import com.lrcall.appuser.R;
import cn.lrapps.models.UserBackupInfo;
import cn.lrapps.enums.BackupType;
import cn.lrapps.models.ReturnInfo;
import cn.lrapps.utils.CryptoTools;
import cn.lrapps.utils.GsonTools;
import cn.lrapps.utils.PreferenceUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by libit on 16/4/6.
 */
public class BackupService extends BaseService
{
	public BackupService(Context context)
	{
		super(context);
	}

	/**
	 * 用户获取配置备份
	 *
	 * @param name               备份名称
	 * @param tips               等待提示信息
	 * @param needServiceProcess 是否需要服务类处理
	 */
	public void getBackupConfig(String name, String tips, final boolean needServiceProcess)
	{
		Map<String, Object> params = new HashMap<>();
		params.put("name", name);
		ajaxStringCallback(ApiConfig.GET_BACKUP_CONFIG, params, true, tips, needServiceProcess);
	}

	/**
	 * 用户获取Apps备份列表
	 *
	 * @param tips               等待提示信息
	 * @param needServiceProcess 是否需要服务类处理
	 */
	public void getBackupAppsList(String tips, final boolean needServiceProcess)
	{
		Map<String, Object> params = new HashMap<>();
		params.put("dataType", BackupType.ANDROID_APP.getType());
		ajaxStringCallback(ApiConfig.BACKUP_LIST, params, true, tips, needServiceProcess);
	}
	/**
	 * 用户获取Apps备份
	 *
	 * @param id                 备份ID
	 * @param tips               等待提示信息
	 * @param needServiceProcess 是否需要服务类处理
	 */
	//	public void getBackupApps(int id, String tips, final boolean needServiceProcess)
	//	{
	//		Map<String, Object> params = new HashMap<>();
	//		params.put("id", id);
	//		ajaxStringCallback(ApiConfig.GET_BACKUP_APPS, params, true, tips, needServiceProcess);
	//	}

	/**
	 * 用户备份配置
	 *
	 * @param name               备份名称
	 * @param data               备份数据
	 * @param description        描述
	 * @param tips               等待提示信息
	 * @param needServiceProcess 是否需要服务类处理
	 */
	public void updateBackupConfig(String name, String data, String description, String tips, final boolean needServiceProcess)
	{
		Map<String, Object> params = new HashMap<>();
		params.put("name", name);
		params.put("data", data);
		params.put("description", description);
		params.put("signData", CryptoTools.getMD5Str(PreferenceUtils.getInstance().getUserId() + data));
		ajaxStringCallback(ApiConfig.UPDATE_BACKUP_CONFIG, params, true, tips, needServiceProcess);
	}

	/**
	 * 用户备份Apps
	 *
	 * @param name               备份名称
	 * @param data               备份数据
	 * @param description        描述
	 * @param tips               等待提示信息
	 * @param needServiceProcess 是否需要服务类处理
	 */
	public void updateBackupApps(String name, String data, String description, String tips, final boolean needServiceProcess)
	{
		Map<String, Object> params = new HashMap<>();
		params.put("name", name);
		params.put("data", data);
		params.put("description", description);
		params.put("signData", CryptoTools.getMD5Str(PreferenceUtils.getInstance().getUserId() + data));
		ajaxStringCallback(ApiConfig.UPDATE_BACKUP_APPS, params, true, tips, needServiceProcess);
	}

	/**
	 * 用户删除备份Apps
	 *
	 * @param id                 备份ID
	 * @param tips               等待提示信息
	 * @param needServiceProcess 是否需要服务类处理
	 */
	public void deleteBackupApps(int id, String tips, final boolean needServiceProcess)
	{
		Map<String, Object> params = new HashMap<>();
		params.put("id", id);
		ajaxStringCallback(ApiConfig.DELETE_BACKUP_INFO, params, true, tips, needServiceProcess);
	}

	@Override
	public void parseData(String url, String result, AjaxStatus status)
	{
		if (url.endsWith(ApiConfig.UPDATE_BACKUP_CONFIG) || url.endsWith(ApiConfig.UPDATE_BACKUP_APPS))
		{
			ReturnInfo returnInfo = GsonTools.getReturnInfo(result);
			if (ReturnInfo.isSuccess(returnInfo))
			{
				ToastView.showCenterToast(context, R.drawable.ic_done, "备份成功！");
			}
			else
			{
				String msg = result;
				if (returnInfo != null)
				{
					msg = returnInfo.getMsg();
				}
				ToastView.showCenterToast(context, R.drawable.ic_do_fail, "备份失败：" + msg);
			}
		}
		else if (url.endsWith(ApiConfig.GET_BACKUP_CONFIG))
		{
			ReturnInfo returnInfo = GsonTools.getReturnInfo(result);
			UserBackupInfo userBackupInfo = GsonTools.getReturnObjects(returnInfo, UserBackupInfo.class);
			if (ReturnInfo.isSuccess(returnInfo) && userBackupInfo != null)
			{
				//获取成功
				Map map = GsonTools.getObjects(userBackupInfo.getData(), new TypeToken<Map<String, String>>()
				{
				}.getType());
				if (map != null)
				{
					Iterator<String> iterator = map.keySet().iterator();
					while (iterator.hasNext())
					{
						String key = iterator.next();
						String value = (String) map.get(key);
						if (value.equals("true") || value.equals("false"))
						{
							PreferenceUtils.getInstance().setBooleanValue(key, Boolean.valueOf(value));
						}
						else
						{
							PreferenceUtils.getInstance().setStringValue(key, value);
						}
					}
					PreferenceUtils.getInstance().setBooleanValue(PreferenceUtils.IS_FIRST_RUN, false);
					ToastView.showCenterToast(context, R.drawable.ic_done, "同步成功！");
				}
				else
				{
					ToastView.showCenterToast(context, R.drawable.ic_do_fail, "用户未备份！");
				}
			}
			else
			{
				String msg = result;
				if (returnInfo != null)
				{
					msg = returnInfo.getMsg();
				}
				ToastView.showCenterToast(context, R.drawable.ic_do_fail, "同步失败：" + msg);
			}
		}
		else if (url.endsWith(ApiConfig.DELETE_BACKUP_INFO))
		{
			ReturnInfo returnInfo = GsonTools.getReturnInfo(result);
			if (ReturnInfo.isSuccess(returnInfo))
			{
				ToastView.showCenterToast(context, R.drawable.ic_done, "删除备份成功！");
			}
			else
			{
				String msg = result;
				if (returnInfo != null)
				{
					msg = returnInfo.getMsg();
				}
				ToastView.showCenterToast(context, R.drawable.ic_do_fail, "删除备份失败：" + msg);
			}
		}
	}
}

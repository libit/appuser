/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.services;

import android.content.Context;

import com.androidquery.callback.AjaxStatus;
import cn.lrapps.android.ui.customer.ToastView;
import cn.lrapps.android.ui.dialog.DialogCommon;
import com.lrcall.appuser.R;
import cn.lrapps.models.UpdateInfo;
import cn.lrapps.utils.AppConfig;
import cn.lrapps.utils.GsonTools;
import cn.lrapps.utils.PinyinTools;
import cn.lrapps.utils.apptools.AppFactory;
import cn.lrapps.utils.filetools.FileTools;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 更新服务类
 * Created by libit on 16/4/6.
 */
public class UpdateService extends BaseService
{
	public UpdateService(Context context)
	{
		super(context);
	}

	/**
	 * 检查服务器更新
	 *
	 * @param tips               等待提示信息
	 * @param needServiceProcess 是否需要服务类处理
	 */
	public void checkUpdate(String tips, final boolean needServiceProcess)
	{
		Map<String, Object> params = new HashMap<>();
		ajaxStringCallback(ApiConfig.CHECK_UPDATE, params, true, tips, needServiceProcess);
	}

	/**
	 * 下载服务器更新
	 *
	 * @param updateInfo 更新信息
	 */
	public void downLoading(final UpdateInfo updateInfo)
	{
		Map<String, Object> params = new HashMap<>();
		//		ajaxFileCallback(updateInfo.getUrl(), params, context.getString(R.string.downloading));
		File file = FileTools.getFile(AppConfig.getUpdateFolder(), PinyinTools.Chinese2Pinyin(context.getString(R.string.app_name)) + "_" + updateInfo.getVersionCode() + ".apk");
		ajaxDownloadFileCallback(updateInfo.getUrl(), params, file, context.getString(R.string.downloading));
	}

	@Override
	public void parseData(String url, String result, AjaxStatus status)
	{
		if (url.endsWith(ApiConfig.CHECK_UPDATE))
		{
			UpdateInfo updateInfo = GsonTools.getReturnObject(result, UpdateInfo.class);
			if (updateInfo != null && systemTools.getVersionCode() < updateInfo.getVersionCode())
			{
				showUpdataDialog(updateInfo);
			}
			else
			{
				ToastView.showCenterToast(context, "您的版本已是最新版！");
			}
		}
	}

	@Override
	protected void parseData(String url, File file, AjaxStatus status)
	{
		super.parseData(url, file, status);
		if (url.endsWith("apk"))
		{
			if (file == null)
			{
				ToastView.showCenterToast(context, R.drawable.ic_do_fail, "下载升级包失败！");
				return;
			}
			AppFactory.getInstance().installApp(file, false);
		}
	}

	/**
	 * 显示下载确认对话框
	 *
	 * @param updateInfo 更新信息
	 */
	public void showUpdataDialog(final UpdateInfo updateInfo)
	{
		DialogCommon dialog = new DialogCommon(context, new DialogCommon.LibitDialogListener()
		{
			@Override
			public void onOkClick()
			{
				downLoading(updateInfo);
			}

			@Override
			public void onCancelClick()
			{
			}
		}, context.getString(R.string.update_title), "v" + updateInfo.getVersionName() + "版本更新日志：\n" + updateInfo.getDescription() + "\n", true, false);
		//		更新时间：+StringTools.getTime(updateInfo.getAddDateLong())
		dialog.show();
		dialog.setOKString(R.string.yes);
		dialog.setCancelString(R.string.no);
	}
}

/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.services;

import android.content.Context;

import com.androidquery.callback.AjaxStatus;
import cn.lrapps.android.ui.customer.ToastView;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件上传服务类
 * Created by libit on 16/4/6.
 */
public class FileService extends BaseService
{
	private static final String TAG = FileService.class.getSimpleName();

	public FileService(Context context)
	{
		super(context);
	}

	/**
	 * 上传图片
	 *
	 * @param filePath           图片路径
	 * @param sortId             文件夹
	 * @param tips               等待提示信息
	 * @param needServiceProcess 是否需要服务类处理
	 */
	public void uploadPic(String filePath, String sortId, String tips, final boolean needServiceProcess)
	{
		File file = new File(filePath);
		if (!file.exists())
		{
			ToastView.showCenterToast(context, "图片文件不存在！");
			return;
		}
		Map<String, Object> params = new HashMap<>();
		params.put("pic", file);
		params.put("sortId", sortId);
		//		ajaxStringCallback(ApiConfig.UPLOAD_PIC, params);
		ajaxStringCallback(ApiConfig.UPLOAD_PIC, params, true, tips, needServiceProcess);
	}

	//	/**
	//	 * 上传图片
	//	 *
	//	 * @param data               图片数据
	//	 * @param sortId             文件夹
	//	 * @param tips               等待提示信息
	//	 * @param needServiceProcess 是否需要服务类处理
	//	 */
	//	public void uploadPic(byte[] data, String sortId, String tips, final boolean needServiceProcess)
	//	{
	//		Map<String, Object> params = new HashMap<>();
	//		params.put("pic", data);
	//		params.put("sortId", sortId);
	//		ajaxStringCallback(ApiConfig.UPLOAD_PIC, params, true, tips, needServiceProcess);
	//	}
	@Override
	public void parseData(String url, String result, AjaxStatus status)
	{
		if (url.endsWith(ApiConfig.UPLOAD_PIC))
		{
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
				return;
			}
		}
	}
}

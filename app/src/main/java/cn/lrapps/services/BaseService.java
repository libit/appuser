/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.services;

import android.app.ProgressDialog;
import android.content.Context;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import cn.lrapps.android.ui.customer.ToastView;
import cn.lrapps.android.ui.dialog.MyProgressDialog;
import com.lrcall.appuser.R;
import cn.lrapps.utils.AppConfig;
import cn.lrapps.utils.CryptoTools;
import cn.lrapps.utils.GsonTools;
import cn.lrapps.utils.LogcatTools;
import cn.lrapps.utils.PreferenceUtils;
import cn.lrapps.utils.StringTools;
import cn.lrapps.utils.apptools.SystemToolsFactory;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by libit on 16/4/6.
 */
public abstract class BaseService
{
	protected final Context context;
	protected final AQuery aQuery;
	private final Set<IAjaxDataResponse> IAjaxDataResponseList = new HashSet<>();
	protected SystemToolsFactory systemTools = SystemToolsFactory.getInstance();

	public BaseService(Context context)
	{
		super();
		this.context = context;
		aQuery = new AQuery(context);
	}

	// 处理String的返回数据
	protected void parseData(String url, String result, AjaxStatus status)
	{
	}

	//处理File类型的返回数据
	protected void parseData(String url, File file, AjaxStatus status)
	{
	}

	/**
	 * 构建公共参数
	 *
	 * @param params
	 *
	 * @return
	 */
	private Map<String, Object> buildParams(Map<String, Object> params)
	{
		if (params == null)
		{
			params = new HashMap<>();
		}
		if (!params.containsKey("userId"))
		{
			params.put("userId", PreferenceUtils.getInstance().getUserId());
		}
		//		if (!params.containsKey("password"))
		//		{
		//			params.put("password", PreferenceUtils.getInstance().getSessionId());
		//		}
		if (!params.containsKey("agentId"))
		{
			params.put("agentId", AppConfig.getAgent());
		}
		params.put("platform", AppConfig.PLATFORM);
		params.put("deviceName", systemTools.getDeviceName());
		params.put("sysVersion", systemTools.getSysVersion());
		params.put("versionCode", systemTools.getVersionCode() + "");
		String signData = CryptoTools.getSignValue(params.values());
		params.put("sign", signData);
		params.put("v", ApiConfig.API_VERSION);
		params.put("sessionId", PreferenceUtils.getInstance().getSessionId());
		return params;
	}

	/**
	 * ajax异步取结果，返回值为String类型的Gson对象
	 *
	 * @param url                服务器地址
	 * @param params             参数
	 * @param showProgressDialog 是否显示等待对话框
	 * @param tips               提示文字
	 */
	protected void ajaxStringCallback(String url, Map<String, Object> params, boolean showProgressDialog, String tips)
	{
		ajaxStringCallback(url, params, showProgressDialog, tips, true);
	}

	protected void ajaxStringCallback(String url, Map<String, Object> params, boolean showProgressDialog, String tips, final boolean needServiceProcess)
	{
		GsonCallBack<String> cb = new GsonCallBack<String>()
		{
			@Override
			public void callback(String url, String result, AjaxStatus status)
			{
				LogcatTools.debug("ajaxStringCallback", "url: " + url + "  ,result:" + result);
				if (!commomResultError(url, result, status))
				{
					if (needServiceProcess)
					{
						parseData(url, result, status);
					}
					OnDataResponse(url, result, status);
				}
			}
		};
		params = buildParams(params);
		LogcatTools.debug("ajaxStringCallback", "url: " + url + " ,params:" + GsonTools.toJson(params));
		cb.url(url).type(String.class).params(params);
		if (showProgressDialog && !StringTools.isNull(tips))
		{
			MyProgressDialog pd = new MyProgressDialog(context, tips);
			aQuery.progress(pd.mDialog).ajax(cb);
		}
		else
		{
			aQuery.ajax(cb);
		}
	}

	protected void ajaxStringCallback(String url, Map<String, Object> params)
	{
		ajaxStringCallback(url, params, false, null);
	}

	protected void ajaxFileCallback(String url, Map<String, Object> params, String tips)
	{
		GsonCallBack<File> cb = new GsonCallBack<File>()
		{
			@Override
			public void callback(String url, File file, AjaxStatus status)
			{
				LogcatTools.debug("ajaxFileCallback", "url: " + url + " ,result:" + result);
				parseData(url, file, status);
			}
		};
		params = buildParams(params);
		LogcatTools.debug("ajaxStringCallback", "url: " + url + " ,params:" + GsonTools.toJson(params));
		cb.url(url).type(File.class).params(params);
		final ProgressDialog pd = new ProgressDialog(context);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setMessage(tips);
		pd.setCanceledOnTouchOutside(false);
		aQuery.progress(pd).ajax(cb);
	}

	protected void ajaxDownloadFileCallback(String url, Map<String, Object> params, File file, String tips)
	{
		GsonCallBack<File> cb = new GsonCallBack<File>()
		{
			@Override
			public void callback(String url, File file, AjaxStatus status)
			{
				LogcatTools.debug("ajaxFileCallback", "url: " + url + " ,result:" + result);
				parseData(url, file, status);
			}
		};
		params = buildParams(params);
		LogcatTools.debug("ajaxStringCallback", "url: " + url + " ,params:" + GsonTools.toJson(params));
		cb.url(url).type(File.class).params(params);
		final ProgressDialog pd = new ProgressDialog(context);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setMessage(tips);
		pd.setCanceledOnTouchOutside(false);
		//		aQuery.progress(pd).ajax(cb);
		aQuery.progress(pd).download(url, file, cb);
	}

	//添加观察者
	public boolean addDataResponse(IAjaxDataResponse IAjaxDataResponse)
	{
		synchronized (IAjaxDataResponseList)
		{
			if (!IAjaxDataResponseList.contains(IAjaxDataResponse))
			{
				return IAjaxDataResponseList.add(IAjaxDataResponse);
			}
		}
		return true;
	}

	//移除观察者
	public boolean removeDataResponse(IAjaxDataResponse IAjaxDataResponse)
	{
		synchronized (IAjaxDataResponseList)
		{
			if (IAjaxDataResponseList.contains(IAjaxDataResponse))
			{
				return IAjaxDataResponseList.remove(IAjaxDataResponse);
			}
		}
		return true;
	}

	//通知观察者
	protected void OnDataResponse(String url, String result, AjaxStatus status)
	{
		for (IAjaxDataResponse IAjaxDataResponse : IAjaxDataResponseList)
		{
			boolean b = IAjaxDataResponse.onAjaxDataResponse(url, result, status);
			if (b)
			{
				// 如果处理成功，则终止向下执行
				//				break;
			}
		}
	}

	//公共的错误处理方法
	public boolean commomResultError(String url, String result, AjaxStatus status)
	{
		if (StringTools.isNull(result))
		{
			ToastView.showCenterToast(context, R.drawable.ic_do_fail, "网络错误，请检查网络设置！");
			return true;
		}
		//		ReturnInfo returnInfo = GsonTools.getReturnInfo(result);
		//		if (returnInfo == null)
		//		{
		//			ToastView.showCenterToast(context, R.drawable.ic_do_fail, "网络错误，请检查网络设置！");
		//			return true;
		//		}
		return false;
	}
}

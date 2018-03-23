/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.services;

import android.content.Context;

import com.androidquery.callback.AjaxStatus;
import cn.lrapps.models.ErrorInfo;
import cn.lrapps.models.ReturnInfo;
import cn.lrapps.utils.AppConfig;
import cn.lrapps.utils.GsonTools;
import cn.lrapps.utils.LogcatTools;
import cn.lrapps.utils.PreferenceUtils;
import cn.lrapps.utils.StringTools;
import cn.lrapps.utils.apptools.AppFactory;
import cn.lrapps.utils.filetools.FileTools;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by libit on 16/4/6.
 */
public class BugService extends BaseService
{
	private static final String TAG = BugService.class.getSimpleName();

	public BugService(Context context)
	{
		super(context);
	}

	/**
	 * 提交BUG信息
	 */
	public void submitBug()
	{
		String path = PreferenceUtils.getInstance().getStringValue(PreferenceUtils.PREF_CRASH_FILE_NAME);
		if (StringTools.isNull(path))
		{
			return;
		}
		//		String ext = "";
		//		if (path.lastIndexOf(".") > -1)
		//		{
		//			ext = path.substring(path.lastIndexOf(".") + 1);
		//		}
		//		path = "V2323_20170923220340.log";
		File file = new File(FileTools.getAppDir(AppConfig.getLogcatFolder()) + "/" + path);
		if (file.exists())
		{
			Map<String, Object> params = new HashMap<>();
			params.put("upload", file);
			//			params.put("uploadFileFileName", Build.MODEL + "_" + new Random().nextInt() % 10 + "_" + file.getName());//+ Build.DISPLAY + "_"
			//			params.put("uploadFileContentType", ext);
			ajaxStringCallback(ApiConfig.UPLOAD_BUG_FILE, params);
		}
		else
		{
			PreferenceUtils.getInstance().setStringValue(PreferenceUtils.PREF_CRASH_FILE_NAME, "");
		}
		//		submitBug2(file);
	}

	/**
	 * 提交BUG信息
	 */
	public void submitBug2(File file, String url)
	{
		final String bug = FileTools.readFile(file);
		String content = bug;
		if (StringTools.isNull(bug))
		{
			PreferenceUtils.getInstance().setStringValue(PreferenceUtils.PREF_CRASH_FILE_NAME, "");
			OnDataResponse(ApiConfig.SUBMIT_BUG, new ReturnInfo(ErrorInfo.PARAM_ERROR, "日志内容为空！").toString(), null);
			return;
		}
		else
		{
			if (bug.length() > 10240)
			{
				content = bug.substring(bug.length() - 10240, bug.length());
			}
		}
		Map<String, Object> params = new HashMap<>();
		params.put("userId", PreferenceUtils.getInstance().getUserId());
		params.put("content", content);
		params.put("url", url);
		ajaxStringCallback(ApiConfig.SUBMIT_BUG, params);
	}

	@Override
	public void parseData(String url, String result, AjaxStatus status)
	{
		if (url.endsWith(ApiConfig.SUBMIT_BUG))
		{
			ReturnInfo returnInfo = GsonTools.getReturnInfo(result);
			if (ReturnInfo.isSuccess(returnInfo))
			{
				//				ToastView.showCenterToast(context, R.drawable.ic_done, "日志已提交！");
			}
			//			else
			//			{
			//				if (returnInfo != null)
			//				{
			//					ToastView.showCenterToast(context, "日志提交失败：" + returnInfo.getMsg() + "！");
			//				}
			//				else
			//				{
			//					ToastView.showCenterToast(context, "日志提交失败：" + result + "！");
			//				}
			//			}
		}
		else if (url.endsWith(ApiConfig.UPLOAD_BUG_FILE))
		{
			LogcatTools.debug(TAG, "上传日志文件结果：" + result);
			ReturnInfo returnInfo = GsonTools.getReturnInfo(result);
			if (ReturnInfo.isSuccess(returnInfo))
			{
				String path = PreferenceUtils.getInstance().getStringValue(PreferenceUtils.PREF_CRASH_FILE_NAME);
				if (StringTools.isNull(path))
				{
					return;
				}
				File file = new File(FileTools.getAppDir(AppConfig.getLogcatFolder()) + "/" + path);
				submitBug2(file, returnInfo.getMsg());
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
				return;
			}
			AppFactory.getInstance().installApp(file, false);
		}
	}
}

/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */

package cn.lrapps.android.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import cn.lrapps.android.ui.ActivityDialog;
import cn.lrapps.android.ui.ActivityMain;
import cn.lrapps.models.AppInfo;
import cn.lrapps.db.DbAppFactory;
import cn.lrapps.events.AppEvent;
import cn.lrapps.utils.ConstValues;
import cn.lrapps.utils.LogcatTools;
import cn.lrapps.utils.PreferenceUtils;
import cn.lrapps.utils.StringTools;
import cn.lrapps.utils.apptools.AppFactory;

import org.greenrobot.eventbus.EventBus;

import java.util.Arrays;

public class AppStatusReceiver extends BroadcastReceiver
{
	private static final String TAG = AppStatusReceiver.class.getSimpleName();
	private static final String INTENT_APP_INSTALL = "android.intent.action.PACKAGE_ADDED";// 应用安装
	private static final String INTENT_APP_UNINSTALL = "android.intent.action.PACKAGE_REMOVED";//应用卸载

	@Override
	public void onReceive(Context context, Intent intent)
	{
		String action = intent.getAction();
		if (StringTools.isNull(action))
		{
			return;
		}
		if (action.equals(INTENT_APP_INSTALL))// 应用安装
		{
			final String packageName = intent.getDataString().replace("package:", "");
			boolean isReplacing = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false);//是新安装还是替换
			LogcatTools.debug(TAG + " onReceive", "安装应用包名:" + packageName);
			boolean canShowDlg = false;
			AppInfo appInfo = DbAppFactory.getInstance().getAppInfo(packageName);
			if (appInfo != null && !StringTools.isNull(appInfo.getPackageName()))
			{
				//如果是已经安装过的应用则设置为已存在
				AppInfo appInfo1 = AppFactory.getInstance().getAppInfoByPackageName(packageName, false);
				appInfo.setIsExist(true);
				if (appInfo1 != null)
				{
					appInfo.setUid(appInfo1.getUid());
					appInfo.setName(appInfo1.getName());
					appInfo.setNameLabel(appInfo1.getNameLabel());
					appInfo.setVersionName(appInfo1.getVersionName());
					appInfo.setVersionCode(appInfo1.getVersionCode());
				}
				DbAppFactory.getInstance().update(appInfo);
				if (appInfo.isBlack() && !appInfo.isEnabled())//如果是黑名单并且状态为禁用
				{
					AppFactory.getInstance().disableApp(packageName);
				}
				else
				{
					canShowDlg = true;
				}
			}
			else
			{
				//如果是没有安装过的应用则添加到数据库
				appInfo = AppFactory.getInstance().getAppInfoByPackageName(packageName, false);
				if (appInfo != null && !StringTools.isNull(appInfo.getPackageName()))
				{
					DbAppFactory.getInstance().add(appInfo);
				}
				canShowDlg = true;
			}
			EventBus.getDefault().post(new AppEvent(AppEvent.APP_STATUS_CHANGED, Arrays.asList(packageName)));
			if (!isReplacing && canShowDlg)
			{
				if (PreferenceUtils.getInstance().getBooleanValue(PreferenceUtils.SHOW_DISABLE_DIALOG))
				{
					Context c = ActivityMain.getInstance();//MyApplication.getContext();
					if (c != null)
					{
						//				DialogCommon dialogCommon = new DialogCommon(c, new DialogCommon.LibitDialogListener()
						//				{
						//					@Override
						//					public void onOkClick()
						//					{
						//						AppFactory.getInstance().disableApp(packageName);
						//					}
						//
						//					@Override
						//					public void onCancelClick()
						//					{
						//					}
						//				}, "提示", "是否禁用此软件?", true, false, true);
						//				dialogCommon.show();
						Intent intent1 = new Intent(c, ActivityDialog.class);
						intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent1.putExtra(ConstValues.DATA_TITLE, "提示");
						intent1.putExtra(ConstValues.DATA_CONTENT, "是否冻结" + appInfo.getName() + "软件?");
						intent1.putExtra(ConstValues.DATA_PACKAGE_NAME, packageName);
						c.startActivity(intent1);
					}
				}
			}
		}
		else if (action.equals(INTENT_APP_UNINSTALL))//应用卸载
		{
			final String packageName = intent.getDataString().replace("package:", "");
			LogcatTools.debug(TAG + " onReceive", "应用卸载:" + packageName);
			// 设置为不存在
			AppInfo appInfo = DbAppFactory.getInstance().getAppInfo(packageName);
			if (appInfo != null && !StringTools.isNull(appInfo.getPackageName()))
			{
				//				AppInfo appInfo1 = AppFactory.getInstance().getAppInfoByPackageName(packageName, false);
				appInfo.setIsExist(false);
				//				appInfo.setName(appInfo1.getName());
				//				appInfo.setNameLabel(appInfo1.getNameLabel());
				//				appInfo.setVersionName(appInfo1.getVersionName());
				//				appInfo.setVersionCode(appInfo1.getVersionCode());
				//				appInfo.setUid(appInfo1.getUid());
				DbAppFactory.getInstance().update(appInfo);
			}
			EventBus.getDefault().post(new AppEvent(AppEvent.APP_STATUS_CHANGED, Arrays.asList(packageName)));
		}
	}
}

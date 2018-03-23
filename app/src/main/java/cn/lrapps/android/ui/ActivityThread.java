/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.android.ui;

import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.lrcall.appuser.R;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.lrapps.android.ui.dialog.MyProgressDialog;
import cn.lrapps.db.DbAppFactory;
import cn.lrapps.enums.AppBlackStatus;
import cn.lrapps.enums.AppEnableStatus;
import cn.lrapps.enums.StatusType;
import cn.lrapps.events.AppEvent;
import cn.lrapps.events.BackupEvent;
import cn.lrapps.models.AppInfo;
import cn.lrapps.models.BackupInfo;
import cn.lrapps.models.ReturnInfo;
import cn.lrapps.utils.AppConfig;
import cn.lrapps.utils.GsonTools;
import cn.lrapps.utils.LogcatTools;
import cn.lrapps.utils.MyExecutorService;
import cn.lrapps.utils.PreferenceUtils;
import cn.lrapps.utils.StringTools;
import cn.lrapps.utils.apptools.AppFactory;
import cn.lrapps.utils.apptools.SystemToolsFactory;
import cn.lrapps.utils.filetools.FileTools;

/**
 * Created by libit on 15/9/30.
 */
public class ActivityThread
{
	private static final String TAG = ActivityThread.class.getSimpleName();
	public static final int UPDATE_MEMORY_RESULT = 9;
	public final Context context;
	private final MyExecutorService executorService = MyExecutorService.getInstance();
	private boolean runningGetSystemAppInfosThread = false;
	private SystemToolsFactory systemTools = SystemToolsFactory.getInstance();

	public ActivityThread(Context context)
	{
		this.context = context;
	}

	/**
	 * 获取程序列表，然后加入数据库
	 */
	public void getSystemAppInfosThread(final boolean isFirstRun, final boolean showWaiting)
	{
		if (runningGetSystemAppInfosThread)
		{
			return;
		}
		final MyProgressDialog pd = new MyProgressDialog(context, "正在同步...");
		if (showWaiting)
		{
			pd.show();
		}
		runningGetSystemAppInfosThread = true;
		executorService.submitTask(new Runnable()
		{
			@Override
			public void run()
			{
				long startTime = System.currentTimeMillis();
				List<AppInfo> appInfoList = AppFactory.getInstance().syncDBApps(isFirstRun);
				long midTime = System.currentTimeMillis();
				long endTime = System.currentTimeMillis();
				LogcatTools.debug("getSystemAppInfosThread", "total time:" + (endTime - startTime));
				if (showWaiting)
				{
					pd.dismiss();
				}
				if (!isFirstRun)
				{
					for (AppInfo appInfo : appInfoList)
					{
						long t2 = System.currentTimeMillis();
						if (appInfo.isBlack())
						{
							if (appInfo.isEnabled())
							{
								AppFactory.getInstance().disableApp(appInfo.getPackageName());
							}
						}
						else
						{
							if (!appInfo.isEnabled())
							{
								AppFactory.getInstance().enableApp(appInfo.getPackageName(), false);
							}
						}
						LogcatTools.info("getSystemAppInfosThread", GsonTools.toJson(appInfo));
						long t3 = System.currentTimeMillis();
						LogcatTools.debug("getSystemAppInfosThread", "t3-t2:" + (t3 - t2));
						long endTime1 = System.currentTimeMillis();
						LogcatTools.debug("getSystemAppInfosThread", "total time:" + (endTime1 - startTime) + ",frist time:" + (midTime - startTime));
					}
					long t8 = System.currentTimeMillis();
					long t9 = System.currentTimeMillis();
					long t10 = System.currentTimeMillis();
					LogcatTools.debug(TAG, "getSystemAppInfosThread总时间:" + (t10 - startTime) + ",黑名单应用时间:" + (t9 - t8) + ",隐藏应用时间:" + (t10 - t9));
				}
				runningGetSystemAppInfosThread = false;
				EventBus.getDefault().post(new AppEvent(AppEvent.GET_SYSTEM_APP, null));
			}
		});
	}

	/**
	 * 数据备份线程
	 */
	public void backupThread(final String comment)
	{
		final MyProgressDialog pd = new MyProgressDialog(context, "正在备份数据...");
		pd.show();
		executorService.submitTask(new Thread("backupThread")
		{
			@Override
			public void run()
			{
				super.run();
				BackupInfo backupInfo = new BackupInfo();
				backupInfo.setName(context.getString(R.string.app_name));
				backupInfo.setTime(StringTools.getCurrentTime());
				backupInfo.setVersion(systemTools.getVersionName() + "_" + systemTools.getVersionCode());
				backupInfo.setComment(comment);
				List<AppInfo> appInfos = DbAppFactory.getInstance().getAppInfoList(null, null, null, null, null);
				String appJson = GsonTools.toJson(appInfos);
				String result = "";
				backupInfo.setApps(appJson);
				backupInfo.setConfigs(PreferenceUtils.getInstance().backup());
				if (FileTools.writeFile(AppConfig.getBackupFolder(), AppConfig.getBackupFileName(), GsonTools.toJson(backupInfo)))
				{
					result = "数据已备份！";
				}
				else
				{
					result = "数据备份失败！";
				}
				pd.dismiss();
				EventBus.getDefault().post(new BackupEvent(BackupEvent.BACKUP, result));
			}
		});
	}

	/**
	 * 数据恢复线程
	 */
	public void restoreThread(final String fileName)
	{
		final MyProgressDialog pd = new MyProgressDialog(context, "正在恢复数据...");
		pd.show();
		executorService.submitTask(new Thread("restoreThread")
		{
			@Override
			public void run()
			{
				super.run();
				long startTime = System.currentTimeMillis();
				String json = FileTools.readFile(AppConfig.getBackupFolder(), fileName);
				BackupInfo backupInfo = GsonTools.getObject(json, BackupInfo.class);
				if (backupInfo == null)
				{
					EventBus.getDefault().post(new BackupEvent(BackupEvent.RESTORE, "备份数据为空！"));
					pd.dismiss();
					return;
				}
				String appJson = backupInfo.getApps();
				if (StringTools.isNull(appJson))
				{
					EventBus.getDefault().post(new BackupEvent(BackupEvent.RESTORE, "应用数据没有备份！"));
				}
				else
				{
					List<AppInfo> restoreAppInfoList = GsonTools.getObjects(appJson, new TypeToken<List<AppInfo>>()
					{
					}.getType());
					List<String> blackPackageNames = new ArrayList<>();
					if (restoreAppInfoList != null && restoreAppInfoList.size() > 0)
					{
						for (AppInfo appInfo : restoreAppInfoList)
						{
							if (appInfo.isBlack())
							{
								blackPackageNames.add(appInfo.getPackageName());
							}
							if (appInfo.getCommonUse() == null)
							{
								appInfo.setCommonUse(StatusType.DISABLE.getStatus());
							}
						}
					}
					int successCount = 0;
					int failCount = 0;
					List<String> packageNames = new ArrayList<>();
					List<AppInfo> blackAppInfoList = DbAppFactory.getInstance().getAppInfoList(null, null, null, AppBlackStatus.BLACK.getStatus(), null);
					for (AppInfo blackAppInfo : blackAppInfoList)
					{
						if (!blackAppInfo.isEnabled() && !blackPackageNames.contains(blackAppInfo.getPackageName()))
						{
							packageNames.add(blackAppInfo.getPackageName());
						}
					}
					ReturnInfo returnInfo = AppFactory.getInstance().enableApps(packageNames, true);
					String resultStr = "";
					if (packageNames.size() < 1 || ReturnInfo.isSuccess(returnInfo))
					{
						returnInfo = AppFactory.getInstance().disableApps(blackPackageNames);
						if (ReturnInfo.isSuccess(returnInfo))
						{
							for (String packageName : blackPackageNames)
							{
								AppInfo appInfo = DbAppFactory.getInstance().getAppInfo(packageName);
								if (appInfo != null)
								{
									appInfo.setIsEnabled(false);
									DbAppFactory.getInstance().update(appInfo);
									successCount++;
								}
								else
								{
									failCount++;
								}
							}
						}
						if (blackAppInfoList != null && blackAppInfoList.size() > 0)
						{
							resultStr = String.format("共%s个黑名单应用，启用%s个，冻结%s个。", blackAppInfoList.size(), packageNames.size(), successCount);
						}
						else
						{
							resultStr = "黑名单应用数据没有备份！";
						}
					}
					else
					{
						resultStr = "没有root权限！";
					}
					DbAppFactory.getInstance().addOrUpdateList(restoreAppInfoList);
					EventBus.getDefault().post(new BackupEvent(BackupEvent.RESTORE, resultStr));
				}
				String configs = backupInfo.getConfigs();
				if (StringTools.isNull(configs))
				{
					EventBus.getDefault().post(new BackupEvent(BackupEvent.RESTORE, "配置没有备份！"));
				}
				else
				{
					Map map = GsonTools.getObjects(configs, new TypeToken<Map<String, String>>()
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
					}
					EventBus.getDefault().post(new BackupEvent(BackupEvent.RESTORE, "配置恢复成功！"));
				}
				long endTime = System.currentTimeMillis();
				LogcatTools.debug("restoreThread", "total time:" + (endTime - startTime));
				pd.dismiss();
			}
		});
	}

	/**
	 * 数据恢复线程
	 */
	public void restoreServerThread(final BackupInfo backupInfo)
	{
		final MyProgressDialog pd = new MyProgressDialog(context, "正在恢复数据...");
		pd.show();
		executorService.submitTask(new Thread("restoreThread")
		{
			@Override
			public void run()
			{
				super.run();
				long startTime = System.currentTimeMillis();
				if (backupInfo == null)
				{
					return;
				}
				String appJson = backupInfo.getApps();
				if (StringTools.isNull(appJson))
				{
					EventBus.getDefault().post(new BackupEvent(BackupEvent.RESTORE, "应用数据没有备份！"));
				}
				else
				{
					int successCount = 0;
					int failCount = 0;
					List<String> packageNameList = GsonTools.getObjects(appJson, new TypeToken<List<String>>()
					{
					}.getType());
					String resultStr = "";
					if (packageNameList == null || packageNameList.size() < 1)
					{
						resultStr = "应用数据没有备份！";
					}
					else
					{
						//得到所有黑名单程序
						List<String> packageNames = new ArrayList<>();
						List<AppInfo> blackAppInfoList = DbAppFactory.getInstance().getAppInfoList(null, null, null, AppBlackStatus.BLACK.getStatus(), null);
						for (AppInfo blackAppInfo : blackAppInfoList)
						{
							if (!blackAppInfo.isEnabled() && !packageNameList.contains(blackAppInfo.getPackageName()))
							{
								LogcatTools.debug("enableApps", "appInfoList:" + blackAppInfo.getPackageName());
								packageNames.add(blackAppInfo.getPackageName());
							}
						}
						// 将所有禁用程序都启用
						ReturnInfo returnInfo = AppFactory.getInstance().enableApps(packageNames, true);
						if (packageNames.size() < 1 || ReturnInfo.isSuccess(returnInfo))
						{
							// 恢复备份的黑名单
							returnInfo = AppFactory.getInstance().disableApps(packageNameList);
							if (ReturnInfo.isSuccess(returnInfo))
							{
								for (String packageName : packageNameList)
								{
									AppInfo appInfo = DbAppFactory.getInstance().getAppInfo(packageName);
									if (appInfo != null)
									{
										appInfo.setIsEnabled(false);
										DbAppFactory.getInstance().update(appInfo);
										successCount++;
									}
									else
									{
										failCount++;
									}
								}
							}
							resultStr = String.format("共%s个黑名单应用，启用%s个，冻结%s个。", packageNameList.size(), packageNames.size(), successCount);
						}
						else
						{
							resultStr = "没有root权限！";
						}
					}
					EventBus.getDefault().post(new BackupEvent(BackupEvent.RESTORE, resultStr));
				}
				long endTime = System.currentTimeMillis();
				LogcatTools.debug("restoreThread", "total time:" + (endTime - startTime));
				pd.dismiss();
			}
		});
	}

	/**
	 * 关闭所有已打开的黑名单程序
	 */
	public void closeBlackAppsThread(final boolean showDialog)
	{
		final MyProgressDialog pd = new MyProgressDialog(context, "正在关闭已打开的黑名单");
		if (showDialog)
			pd.show();
		executorService.submitTask(new Thread("closeBlackAppsThread")
		{
			@Override
			public void run()
			{
				super.run();
				long startTime = System.currentTimeMillis();
				List<AppInfo> blackAppInfoList = DbAppFactory.getInstance().getAppInfoList(AppEnableStatus.ENABLED.getStatus(), null, null, AppBlackStatus.BLACK.getStatus(), null);
				int successCount = 0;
				for (AppInfo blackAppInfo : blackAppInfoList)
				{
					ReturnInfo returnInfo = AppFactory.getInstance().disableApp(blackAppInfo.getPackageName());
					if (ReturnInfo.isSuccess(returnInfo))
					{
						successCount++;
					}
				}
				long endTime = System.currentTimeMillis();
				LogcatTools.debug("closeBlackAppsThread", "total time:" + (endTime - startTime));
				if (showDialog)
				{
					pd.dismiss();
				}
				int count = blackAppInfoList.size();
				EventBus.getDefault().post(new AppEvent(AppEvent.CLOSE_BLACK_APP, String.format("共%d个应用，成功关闭%d个，失败%d个。", count, successCount, count - successCount), null));
			}
		});
	}

	/**
	 * 清空所有黑名单程序，并将程序的状态都启用
	 */
	public void clearAndEnableAllAppBlackAppsThread()
	{
		final MyProgressDialog pd = new MyProgressDialog(context, "正在清空所有黑名单应用，并将应用的状态都启用...");
		pd.show();
		executorService.submitTask(new Thread("clearAndEnableAllAppBlackAppsThread")
		{
			@Override
			public void run()
			{
				super.run();
				long startTime = System.currentTimeMillis();
				List<AppInfo> blackAppInfoList = DbAppFactory.getInstance().getAppInfoList(null, null, null, AppBlackStatus.BLACK.getStatus(), null);
				int successCount = 0;
				for (AppInfo blackAppInfo : blackAppInfoList)
				{
					ReturnInfo returnInfo = AppFactory.getInstance().enableApp(blackAppInfo.getPackageName(), true);
					if (ReturnInfo.isSuccess(returnInfo))
					{
						successCount++;
					}
				}
				long endTime = System.currentTimeMillis();
				LogcatTools.debug("clearAndEnableAllAppBlackAppsThread", "total time:" + (endTime - startTime));
				pd.dismiss();
				int count = blackAppInfoList.size();
				EventBus.getDefault().post(new AppEvent(AppEvent.CLEAR_AND_ENABLE_BLACK_APP, String.format("共%d个应用，成功启用%d个，失败%d个。", count, successCount, count - successCount), null));
			}
		});
	}

	/**
	 * 清空所有隐藏程序
	 */
	public void clearAllHideAppsThread()
	{
		final MyProgressDialog pd = new MyProgressDialog(context, "正在清空所有隐藏应用...");
		pd.show();
		executorService.submitTask(new Thread("clearAllHideAppsThread")
		{
			@Override
			public void run()
			{
				super.run();
				long startTime = System.currentTimeMillis();
				DbAppFactory.getInstance().resetAppStatus(null, false, null);
				long endTime = System.currentTimeMillis();
				LogcatTools.debug("clearAllHideAppsThread", "total time:" + (endTime - startTime));
				pd.dismiss();
				EventBus.getDefault().post(new AppEvent(AppEvent.CLEAR_HIDE_APP, "清空隐藏应用已完成！", null));
			}
		});
	}

	/**
	 * 清空所有不存在的程序
	 */
	public void clearNotExistAppsThread()
	{
		final MyProgressDialog pd = new MyProgressDialog(context, "正在清空无用数据...");
		pd.show();
		executorService.submitTask(new Thread("clearNotExistAppsThread")
		{
			@Override
			public void run()
			{
				super.run();
				DbAppFactory.getInstance().deleteNotExistApps();
				pd.dismiss();
				EventBus.getDefault().post(new AppEvent(AppEvent.CLEAR_APP, "无用数据已清理干净！", null));
			}
		});
	}
}

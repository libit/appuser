/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.android.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Toast;

import cn.lrapps.android.ui.adapter.AppListAdapter;
import cn.lrapps.models.AppInfo;
import cn.lrapps.db.DbAppFactory;
import cn.lrapps.enums.StatusType;
import cn.lrapps.events.AppEvent;
import cn.lrapps.models.ReturnInfo;
import cn.lrapps.utils.ConstValues;
import cn.lrapps.utils.apptools.AppFactory;

import org.greenrobot.eventbus.EventBus;

import java.util.Arrays;
import java.util.List;

/**
 * Created by libit on 15/9/29.
 */
public abstract class MyBaseFragment extends Fragment
{
	private static final String TAG = MyBaseFragment.class.getSimpleName();
	protected Activity mActivity;
	protected List<AppInfo> mAppInfoList;
	protected AppListAdapter mAppListAdapter;
	protected final Handler mHandler = new Handler();
	protected boolean isInit = false;//视图是否已经初始化

	protected void viewInit(View rootView)
	{
		isInit = true;
	}

	public abstract Activity getAttachedActivity();
	//	public abstract void onAppClicked(AppInfo appInfo);

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser)
	{
		super.setUserVisibleHint(isVisibleToUser);
		//		LogcatTools.debug(TAG, "fragment setUserVisibleHint:" + isVisibleToUser);
		if (isInit)
		{
			if (isVisibleToUser)
			{
				fragmentShow();
			}
			else
			{
				fragmentHide();
			}
		}
	}

	/**
	 * 当fragment隐藏时调用
	 * 注意：第一次调用时必须等待视图已经初始化完成。
	 */
	public void fragmentHide()
	{
	}

	/**
	 * 当fragment显示时调用
	 * 注意：第一次调用时必须等待视图已经初始化完成。
	 */
	public void fragmentShow()
	{
	}

	public void enableApp(AppInfo appInfo, boolean delete)
	{
		if (appInfo == null)
		{
			return;
		}
		if (!appInfo.isEnabled())
		{
			ReturnInfo returnInfo = AppFactory.getInstance().enableApp(appInfo.getPackageName(), delete);
			if (ReturnInfo.isSuccess(returnInfo))
			{
				appInfo.setIsEnabled(true);
				//				String	msg = String.format("启用%s成功。", appInfo.getName());
			}
			else
			{
				String msg = String.format("启用%s失败：%s。", appInfo.getName(), returnInfo.getMsg());
				Toast.makeText(getAttachedActivity(), msg, Toast.LENGTH_LONG).show();
			}
		}
		ReturnInfo returnInfo = AppFactory.getInstance().startApp(this.getContext(), appInfo.getPackageName());
		if (!ReturnInfo.isSuccess(returnInfo))
		{
			Toast.makeText(getAttachedActivity(), appInfo.getName() + "不能启动！", Toast.LENGTH_LONG).show();
		}
		//		onAppClicked(appInfo);
	}

	public void disableApp(AppInfo appInfo)
	{
		if (appInfo == null)
		{
			return;
		}
		if (appInfo.isEnabled())
		{
			ReturnInfo returnInfo = AppFactory.getInstance().disableApp(appInfo.getPackageName());
			String msg = "";
			if (ReturnInfo.isSuccess(returnInfo))
			{
				appInfo.setIsEnabled(false);
				msg = String.format("冻结%s成功。", appInfo.getName());
			}
			else
			{
				msg = String.format("冻结%s失败：%s。", appInfo.getName(), returnInfo.getMsg());
			}
			Toast.makeText(getAttachedActivity(), msg, Toast.LENGTH_LONG).show();
			//			onAppClicked(appInfo);
		}
	}

	public void hideApp(AppInfo appInfo)
	{
		appInfo.setIsHide(true);
		DbAppFactory.getInstance().update(appInfo);
		//		onAppClicked(appInfo);
		EventBus.getDefault().post(new AppEvent(AppEvent.APP_STATUS_CHANGED, Arrays.asList(appInfo.getPackageName())));
	}

	/**
	 * 创建桌面快捷方式
	 *
	 * @param appInfo
	 */
	public void createAppShortcut(AppInfo appInfo)
	{
		Intent intent = new Intent(ConstValues.INSTALL_SHORTCUT);
		intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, appInfo.getName());
		// 是否可以有多个快捷方式的副本，参数如果是true就可以生成多个快捷方式，如果是false就不会重复添加
		intent.putExtra("duplicate", false);
		Intent intent3 = new Intent(MyApplication.getContext(), ActivityLauncher.class);
		intent3.putExtra(ConstValues.DATA_PACKAGE_NAME, appInfo.getPackageName());
		//		Intent intent3 = new Intent(MyApplication.getContext(), AppService.class);
		//		intent3.putExtra(ConstValues.DATA_PACKAGE_NAME, appInfo.getPackageName());
		intent3.putExtra(ConstValues.DATA_APP_STATUS, StatusType.START.getStatus());
		intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent3);
		intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, appInfo.getPhoto());
		getAttachedActivity().sendBroadcast(intent);
		Toast.makeText(getAttachedActivity(), "创建“" + appInfo.getName() + "”快捷方式成功", Toast.LENGTH_LONG).show();
	}
}

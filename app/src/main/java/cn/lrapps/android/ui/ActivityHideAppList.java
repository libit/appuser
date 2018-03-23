/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.android.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.lrcall.appuser.R;

import java.util.List;

import cn.lrapps.android.ui.adapter.AppListAdapter;
import cn.lrapps.db.DbAppFactory;
import cn.lrapps.enums.AppShowStatus;
import cn.lrapps.enums.SqlOrderType;
import cn.lrapps.models.AppInfo;
import cn.lrapps.models.ReturnInfo;
import cn.lrapps.utils.ConstValues;
import cn.lrapps.utils.PreferenceUtils;
import cn.lrapps.utils.apptools.AppFactory;

public class ActivityHideAppList extends MyBaseActivity
{
	private static final String TAG = ActivityHideAppList.class.getSimpleName();
	private final int INIT_RESULT = 0;
	private GridView gvAppList;
	private List<AppInfo> mAppInfoList;
	private AppListAdapter mAppListAdapter;
	private String mOrderCol = AppInfo.FIELD_NAME_LABEL;
	private String mOrderType = SqlOrderType.ASC.getType();
	private final Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			switch (msg.what)
			{
				case INIT_RESULT:
				{
					if (mAppListAdapter == null)
					{
						mAppListAdapter = new AppListAdapter(ActivityHideAppList.this, mAppInfoList);
						gvAppList.setAdapter(mAppListAdapter);
						gvAppList.setOnItemClickListener(new AdapterView.OnItemClickListener()
						{
							@Override
							public void onItemClick(AdapterView<?> parent, View view, int position, long id)
							{
								AppInfo appInfo = mAppInfoList.get(position);
								enableAndStartApp(appInfo, false);
							}
						});
					}
					else
					{
						mAppListAdapter.setData(mAppInfoList);
						mAppListAdapter.notifyDataSetChanged();
					}
					if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
					{
						gvAppList.setNumColumns(PreferenceUtils.getInstance().getIntegerValue(PreferenceUtils.APP_COLUMN_COUNT_PORTRAIT));
					}
					else
					{
						gvAppList.setNumColumns(PreferenceUtils.getInstance().getIntegerValue(PreferenceUtils.APP_COLUMN_COUNT_LANDSCAPE));
					}
					break;
				}
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_list);
		viewInit();
		//		EventBus.getDefault().register(this);
	}

	@Override
	public void onResume()
	{
		super.onResume();
		getAppInfos();
		registerForContextMenu(gvAppList);
	}

//	@Override
//	public void onPause()
//	{
//		//		unregisterForContextMenu(gvAppList);
//		super.onPause();
//	}

//	@Override
//	protected void onDestroy()
//	{
//		if (EventBus.getDefault().isRegistered(this))
//		{
//			EventBus.getDefault().unregister(this);
//		}
//		super.onDestroy();
//	}

	@Override
	protected void viewInit()
	{
		super.viewInit();
		setBackButton();
		gvAppList = (GridView) findViewById(R.id.gvAppList);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.menu_activity_hide_app_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		int id = item.getItemId();
		if (id == R.id.action_sort_by_name_asc)
		{
			mOrderCol = AppInfo.FIELD_NAME_LABEL;
			mOrderType = SqlOrderType.ASC.getType();
			getAppInfos();
			return true;
		}
		else if (id == R.id.action_sort_by_name_desc)
		{
			mOrderCol = AppInfo.FIELD_NAME_LABEL;
			mOrderType = SqlOrderType.DESC.getType();
			getAppInfos();
			return true;
		}
		else if (id == R.id.action_sort_by_enable_status_asc)
		{
			mOrderCol = AppInfo.FIELD_ENABLED;
			mOrderType = SqlOrderType.ASC.getType();
			getAppInfos();
			return true;
		}
		else if (id == R.id.action_sort_by_enable_status_desc)
		{
			mOrderCol = AppInfo.FIELD_ENABLED;
			mOrderType = SqlOrderType.DESC.getType();
			getAppInfos();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	//	@Override
	//	public boolean onOptionsItemSelected(MenuItem item)
	//	{
	//		int id = item.getItemId();
	//		if (id == android.R.id.home)
	//		{
	//			//			finish();
	//			Intent upIntent = NavUtils.getParentActivityIntent(this);
	//			if (NavUtils.shouldUpRecreateTask(this, upIntent))
	//			{
	//				TaskStackBuilder.create(this).addNextIntentWithParentStack(upIntent).startActivities();
	//			}
	//			else
	//			{
	//				upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	//				NavUtils.navigateUpTo(this, upIntent);
	//			}
	//			return true;
	//		}
	//		return super.onOptionsItemSelected(item);
	//	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
	{
		super.onCreateContextMenu(menu, v, menuInfo);
		getMenuInflater().inflate(R.menu.menu_gridview_hide, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		int position = ((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position;
		if (mAppInfoList.size() <= position)
		{
			return super.onContextItemSelected(item);
		}
		AppInfo appInfo = (AppInfo) mAppListAdapter.getItem(position);
		int id = item.getItemId();
		if (id == R.id.action_hide_show_app)
		{
			boolean result = DbAppFactory.getInstance().setAppHide(appInfo.getPackageName(), false);
			if (result)
			{
				getAppInfos();
				//				appInfo.setIsHide(false);
				//				DbAppFactory.getInstance().update(appInfo);
			}
			return true;
		}
		else if (id == R.id.action_hide_enable)
		{
			enableAndStartApp(appInfo, false);
			return true;
		}
		else if (id == R.id.action_hide_disable)
		{
			if (appInfo.isEnabled())
			{
				ReturnInfo returnInfo = AppFactory.getInstance().disableApp(appInfo.getPackageName());
				if (ReturnInfo.isSuccess(returnInfo))
				{
					//					String msg = String.format("禁用%s成功。", appInfo.getName());
					//                    appInfo.setIsEnabled(false);
					//                    DbAppFactory.getInstance().update(appInfo);
				}
				else
				{
					String msg = String.format("冻结%s失败：%s。", appInfo.getName(), returnInfo.getMsg());
					Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
				}
				getAppInfos();
				return true;
			}
		}
		else if (id == R.id.action_disable_enable_and_del)
		{
			enableAndStartApp(appInfo, true);
			return true;
		}
		else if (id == R.id.action_hide_uninstall)
		{
			AppFactory.getInstance().uninstallApp(appInfo.getPackageName(), false);
//			appInfo.setIsExist(false);
//			DbAppFactory.getInstance().update(appInfo);
			return true;
		}
		else if (id == R.id.action_create_desktop_icon)
		{
			appInfo = AppFactory.getInstance().getAppInfoByPackageName(appInfo.getPackageName(), true);
			Intent intent = new Intent(ConstValues.INSTALL_SHORTCUT);
			intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, appInfo.getName());
			// 是否可以有多个快捷方式的副本，参数如果是true就可以生成多个快捷方式，如果是false就不会重复添加
			intent.putExtra("duplicate", false);
			Intent intent3 = new Intent(MyApplication.getContext(), ActivityLauncher.class);
			intent3.putExtra(ConstValues.DATA_PACKAGE_NAME, appInfo.getPackageName());
			intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent3);
			//          intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(getAttachedActivity(), R.drawable.default_app));
			intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, appInfo.getPhoto());
			sendBroadcast(intent);
			Toast.makeText(this, "创建“" + appInfo.getName() + "”快捷方式成功", Toast.LENGTH_LONG).show();
			return true;
		}
		return super.onContextItemSelected(item);
	}

	private void getAppInfos()
	{
		new Thread("getAppInfos")
		{
			@Override
			public void run()
			{
				super.run();
				mAppInfoList = DbAppFactory.getInstance().getAppInfoList(null, null, AppShowStatus.getHideStatus(true), null, null, mOrderCol, mOrderType);
				Message msg = Message.obtain();
				msg.what = INIT_RESULT;
				mHandler.sendMessage(msg);
			}
		}.start();
	}

	private void enableAndStartApp(AppInfo appInfo, boolean delete)
	{
		if (!appInfo.isEnabled())
		{
			ReturnInfo returnInfo = AppFactory.getInstance().enableApp(appInfo.getPackageName(), delete);
			if (!ReturnInfo.isSuccess(returnInfo))
			{
				String msg = String.format("启用%s失败：%s。", appInfo.getName(), returnInfo.getMsg());
				Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
			}
		}
		ReturnInfo returnInfo = AppFactory.getInstance().startApp(this, appInfo.getPackageName());
		if (!ReturnInfo.isSuccess(returnInfo))
		{
			Toast.makeText(this, appInfo.getName() + "不能启动！", Toast.LENGTH_LONG).show();
		}
		getAppInfos();
	}
	//	@Subscribe
	//	public void onEventMainThread(final AppEvent event)
	//	{
	//		mHandler.post(new Thread()
	//		{
	//			@Override
	//			public void run()
	//			{
	//				super.run();
	//				if (event != null)
	//				{
	//					if (event.getType().equalsIgnoreCase(AppEvent.APP_STATUS_CHANGED))
	//					{
	//						if (event.getPackageNameList() != null && event.getPackageNameList().size() > 0)
	//						{
	//							for (String packageName : event.getPackageNameList())
	//							{
	//								AppInfo appInfo = AppFactory.getInstance().getAppInfoByPackageName(packageName, false);
	//								OnAppStatusChanged(appInfo);
	//								LogcatTools.debug(TAG, "onEventMainThread->packageName:" + packageName);
	//							}
	//						}
	//						else
	//						{
	//							OnAppStatusChanged(null);
	//						}
	//					}
	//				}
	//			}
	//		});
	//	}
	//
	//	public boolean OnAppStatusChanged(final AppInfo appInfo)
	//	{
	//		if (appInfo == null || mAppListAdapter == null || mAppInfoList == null)
	//		{
	//			getAppInfos();
	//			return true;
	//		}
	//		boolean isExist = false;
	//		for (AppInfo app : mAppInfoList)
	//		{
	//			if (app.getPackageName().equals(appInfo.getPackageName()))
	//			{
	//				isExist = true;
	//				break;
	//			}
	//		}
	//		if (!isExist)
	//		{
	//			if (appInfo.isEnabled())
	//			{
	//				mAppInfoList.add(appInfo);
	//				mAppListAdapter.setData(mAppInfoList);
	//				mAppListAdapter.notifyDataSetChanged();
	//				return true;
	//			}
	//		}
	//		else
	//		{
	//			if (appInfo.isHide() || !appInfo.isEnabled())
	//			{
	//				mAppListAdapter.remove(appInfo.getUid());
	//				mAppInfoList = mAppListAdapter.getData();
	//				mAppListAdapter.notifyDataSetChanged();
	//				return true;
	//			}
	//		}
	//		return false;
	//	}
}

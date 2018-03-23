/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.android.ui;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import cn.lrapps.android.ui.adapter.AppListAdapter;
import com.lrcall.appuser.R;
import cn.lrapps.models.AppInfo;
import cn.lrapps.db.DbAppFactory;
import cn.lrapps.enums.AppShowStatus;
import cn.lrapps.enums.SqlOrderType;
import cn.lrapps.events.AppEvent;
import cn.lrapps.events.AppSortChanged;
import cn.lrapps.utils.PreferenceUtils;
import cn.lrapps.utils.apptools.AppFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class FragmentAppList extends MyBaseFragment
{
	private static final String TAG = FragmentAppList.class.getSimpleName();
	private static final Integer APP_STATUS = null;
	private final Handler mHandler = new Handler();
	private GridView gvAppList;
	private String mOrderCol = AppInfo.FIELD_NAME_LABEL;
	private String mOrderType = SqlOrderType.ASC.getType();

	public FragmentAppList()
	{
	}

	public static FragmentAppList newInstance()
	{
		return new FragmentAppList();
	}

	@Override
	public Activity getAttachedActivity()
	{
		if (mActivity != null)
		{
			return mActivity;
		}
		else
		{
			return this.getActivity();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.fragment_app_list, container, false);
		viewInit(rootView);
		if (!EventBus.getDefault().isRegistered(this))
		{
			EventBus.getDefault().register(this);
		}
		return rootView;
	}

	@Override
	public void onDestroyView()
	{
		if (EventBus.getDefault().isRegistered(this))
		{
			EventBus.getDefault().unregister(this);
		}
		super.onDestroyView();
	}

	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		this.mActivity = activity;
	}

	@Override
	public void onDetach()
	{
		super.onDetach();
		this.mActivity = null;
	}

	@Override
	public void onResume()
	{
		super.onResume();
		registerForContextMenu(gvAppList);
		getAppInfos();
	}

	@Override
	public void onPause()
	{
		unregisterForContextMenu(gvAppList);
		super.onPause();
	}
	//	@Override
	//	public void fragmentShow()
	//	{
	//		super.fragmentShow();
	//		registerForContextMenu(gvAppList);
	//	}
	//
	//	@Override
	//	public void fragmentHide()
	//	{
	//		unregisterForContextMenu(gvAppList);
	//		super.fragmentHide();
	//	}

	@Override
	protected void viewInit(View rootView)
	{
		gvAppList = (GridView) rootView.findViewById(R.id.gvAppList);
		super.viewInit(rootView);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
	{
		getAttachedActivity().getMenuInflater().inflate(R.menu.menu_gridview_all, menu);
		super.onCreateContextMenu(menu, v, menuInfo);
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
		if (id == R.id.action_all_hide_app)
		{
			hideApp(appInfo);
			return true;
		}
		else if (id == R.id.action_all_disable)
		{
			disableApp(appInfo);
			return true;
		}
		else if (id == R.id.action_all_enable)
		{
			enableApp(appInfo, false);
			return true;
		}
		else if (id == R.id.action_del_black)
		{
			enableApp(appInfo, true);
			return true;
		}
		else if (id == R.id.action_all_enable_and_del)
		{
			enableApp(appInfo, true);
			return true;
		}
		else if (id == R.id.action_all_uninstall)
		{
			AppFactory.getInstance().uninstallApp(appInfo.getPackageName(), false);
			return true;
		}
		else if (id == R.id.action_all_create_desktop_icon)
		{
			appInfo = AppFactory.getInstance().getAppInfoByPackageName(appInfo.getPackageName(), true);
			createAppShortcut(appInfo);
			return true;
		}
		return super.onContextItemSelected(item);
	}

	private void getAppInfos()
	{
		mHandler.post(new Thread("getAppInfos")
		{
			@Override
			public void run()
			{
				super.run();
				mAppInfoList = DbAppFactory.getInstance().getAppInfoList(APP_STATUS, null, AppShowStatus.SHOW.getStatus(), null, null, mOrderCol, mOrderType);
				initData();
			}
		});
	}

	private void initData()
	{
		if (mAppListAdapter == null)
		{
			mAppListAdapter = new AppListAdapter(getAttachedActivity(), mAppInfoList);
			gvAppList.setAdapter(mAppListAdapter);
			gvAppList.setOnItemClickListener(new AdapterView.OnItemClickListener()
			{
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id)
				{
					//					LogcatTools.debug("onItemClick", String.format("position:%d,id:%d,parent:%s,view:%s.", position, id, parent, view));
					AppInfo appInfo = mAppInfoList.get(position);
					enableApp(appInfo, false);
				}
			});
		}
		else
		{
			mAppListAdapter.setData(mAppInfoList);
			mAppListAdapter.notifyDataSetChanged();
		}
		if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
		{
			gvAppList.setNumColumns(PreferenceUtils.getInstance().getIntegerValue(PreferenceUtils.APP_COLUMN_COUNT_PORTRAIT));
		}
		else
		{
			gvAppList.setNumColumns(PreferenceUtils.getInstance().getIntegerValue(PreferenceUtils.APP_COLUMN_COUNT_LANDSCAPE));
		}
	}

	public boolean OnAppStatusChanged(final AppInfo appInfo)
	{
		if (appInfo == null || mAppListAdapter == null || mAppInfoList == null)
		{
			getAppInfos();
			return true;
		}
		boolean isExist = false;
		for (AppInfo app : mAppInfoList)
		{
			if (app.getPackageName().equals(appInfo.getPackageName()))
			{
				isExist = true;
				break;
			}
		}
		if (!isExist)
		{
			if (appInfo.isEnabled())
			{
				mAppInfoList.add(appInfo);
				mAppListAdapter.setData(mAppInfoList);
				mAppListAdapter.notifyDataSetChanged();
				return true;
			}
		}
		else
		{
			if (appInfo.isHide() || !appInfo.isEnabled())
			{
				mAppListAdapter.remove(appInfo.getUid());
				mAppInfoList = mAppListAdapter.getData();
				mAppListAdapter.notifyDataSetChanged();
				return true;
			}
		}
		return false;
	}

	@Subscribe
	public void onEventMainThread(final AppEvent event)
	{
		mHandler.post(new Thread()
		{
			@Override
			public void run()
			{
				super.run();
				if (event != null)
				{
					if (event.getType().equalsIgnoreCase(AppEvent.APP_STATUS_CHANGED))
					{
						if (event.getPackageNameList() != null && event.getPackageNameList().size() != 1)
						{
							for (String packageName : event.getPackageNameList())
							{
								AppInfo appInfo = AppFactory.getInstance().getAppInfoByPackageName(packageName, false);
								OnAppStatusChanged(appInfo);
							}
						}
						else
						{
							OnAppStatusChanged(null);
						}
					}
				}
			}
		});
	}

	@Subscribe
	public void onEventMainThread(final AppSortChanged event)
	{
		mHandler.post(new Thread()
		{
			@Override
			public void run()
			{
				super.run();
				if (event != null)
				{
					if (event == AppSortChanged.NAME_ASC)
					{
						mOrderCol = AppInfo.FIELD_NAME_LABEL;
						mOrderType = SqlOrderType.ASC.getType();
					}
					else if (event == AppSortChanged.NAME_DESC)
					{
						mOrderCol = AppInfo.FIELD_NAME_LABEL;
						mOrderType = SqlOrderType.DESC.getType();
					}
					else if (event == AppSortChanged.ENABLE_STATUS_ASC)
					{
						mOrderCol = AppInfo.FIELD_ENABLED;
						mOrderType = SqlOrderType.ASC.getType();
					}
					else if (event == AppSortChanged.ENABLE_STATUS_DESC)
					{
						mOrderCol = AppInfo.FIELD_ENABLED;
						mOrderType = SqlOrderType.DESC.getType();
					}
					getAppInfos();
				}
			}
		});
	}
}

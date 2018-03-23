/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.android.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import cn.lrapps.android.ui.adapter.ChooseAppListAdapter;
import cn.lrapps.android.ui.dialog.DialogCommon;
import com.lrcall.appuser.R;
import cn.lrapps.models.AppInfo;
import cn.lrapps.db.DbAppFactory;
import cn.lrapps.enums.AppType;
import cn.lrapps.events.AppEvent;
import cn.lrapps.utils.apptools.AppFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 批量安装应用
 */
public class ActivityUnInstallAppBat extends MyBaseActivity implements View.OnClickListener, ChooseAppListAdapter.IChooseAppListAdapterItemClicked
{
	private final int INIT_RESULT = 1110;
	private final int UNINSTALL_RESULT = 1111;
	private ListView lvApps;
	private List<AppInfo> appInfos;
	private ChooseAppListAdapter mChooseAppListAdapter;
	private Integer appType = null;
	private final Map<String, Boolean> statusChangedApps = new HashMap<>();
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
					statusChangedApps.clear();
					mChooseAppListAdapter = new ChooseAppListAdapter(ActivityUnInstallAppBat.this, appInfos, ActivityUnInstallAppBat.this);
					lvApps.setAdapter(mChooseAppListAdapter);
					break;
				}
				case UNINSTALL_RESULT:
				{
					Toast.makeText(ActivityUnInstallAppBat.this, "卸载应用已完成！", Toast.LENGTH_LONG).show();
					getAppInfos();
					break;
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_uninstall_app_bat);
		viewInit();
		if (!EventBus.getDefault().isRegistered(this))
		{
			EventBus.getDefault().register(this);
		}
		getAppInfos();
	}

	@Override
	protected void onDestroy()
	{
		if (EventBus.getDefault().isRegistered(this))
		{
			EventBus.getDefault().unregister(this);
		}
		super.onDestroy();
	}

	@Override
	protected void viewInit()
	{
		super.viewInit();
		setBackButton();
		lvApps = (ListView) findViewById(R.id.list_all);
		findViewById(R.id.btn_uninstall).setOnClickListener(this);
		findViewById(R.id.tv_app).setOnClickListener(this);
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.btn_uninstall:
			{
				DialogCommon dialogCommon = new DialogCommon(this, new DialogCommon.LibitDialogListener()
				{
					@Override
					public void onOkClick()
					{
						String msg = "正在卸载应用(0/" + statusChangedApps.size() + ")...";
						final DialogCommon dialog = new DialogCommon(ActivityUnInstallAppBat.this, null, "提示", msg, false, true, false);
						dialog.show();
						new Thread("uninstallApps")
						{
							@Override
							public void run()
							{
								super.run();
								int count = statusChangedApps.size();
								if (count > 0)
								{
									int index = 0;
									for (String packageName : statusChangedApps.keySet())
									{
										index++;
										AppInfo appInfo = AppFactory.getInstance().getAppInfoByPackageName(packageName, false);
										final String msg = "正在卸载应用“" + appInfo.getName() + "”(" + index + "/" + statusChangedApps.size() + ")...";
										mHandler.post(new Thread("")
										{
											@Override
											public void run()
											{
												super.run();
												dialog.setDialogMessage(msg);
											}
										});
										AppFactory.getInstance().uninstallApp(packageName, true);
									}
								}
								Message msg = Message.obtain();
								msg.what = UNINSTALL_RESULT;
								mHandler.sendMessage(msg);
								dialog.dismiss();
							}
						}.start();
					}

					@Override
					public void onCancelClick()
					{
					}
				}, "提示", "确定要卸载选择的应用吗？", true, false, true);
				dialogCommon.show();
				break;
			}
		}
	}

	private void getAppInfos()
	{
		new Thread("getAppInfos")
		{
			@Override
			public void run()
			{
				super.run();
				appInfos = DbAppFactory.getInstance().getAppInfoList(null, appType, null, null, null);
				Message msg = Message.obtain();
				msg.what = INIT_RESULT;
				mHandler.sendMessage(msg);
			}
		}.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.menu_activity_uninstall_apps, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		int id = item.getItemId();
		if (id == R.id.action_select_all)
		{
			int count = appInfos.size();
			for (int i = 0; i < count; i++)
			{
				mChooseAppListAdapter.addBooleanToMemoryCache(appInfos.get(i).getPackageName(), true);
				statusChangedApps.put(appInfos.get(i).getPackageName(), false);
			}
			mChooseAppListAdapter.notifyDataSetChanged();
			return true;
		}
		else if (id == R.id.action_select_none)
		{
			int count = appInfos.size();
			for (int i = 0; i < count; i++)
			{
				mChooseAppListAdapter.addBooleanToMemoryCache(appInfos.get(i).getPackageName(), false);
				statusChangedApps.put(appInfos.get(i).getPackageName(), true);
			}
			mChooseAppListAdapter.notifyDataSetChanged();
			return true;
		}
		else if (id == R.id.action_system_app)
		{
			appType = AppType.SYSTEM.getType();
			getAppInfos();
			return true;
		}
		else if (id == R.id.action_user_app)
		{
			appType = AppType.USER.getType();
			getAppInfos();
			return true;
		}
		else if (id == R.id.action_all_app)
		{
			appType = null;
			getAppInfos();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCheckedClicked(AppInfo appInfo, boolean isChecked)
	{
		if (appInfo != null)
		{
			statusChangedApps.put(appInfo.getPackageName(), !isChecked);
			mChooseAppListAdapter.addBooleanToMemoryCache(appInfo.getPackageName(), isChecked);
		}
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
					if (event.getType().equalsIgnoreCase(AppEvent.CLEAR_AND_ENABLE_BLACK_APP))
					{
						Toast.makeText(ActivityUnInstallAppBat.this, event.getMsg(), Toast.LENGTH_LONG).show();
					}
				}
			}
		});
	}
}

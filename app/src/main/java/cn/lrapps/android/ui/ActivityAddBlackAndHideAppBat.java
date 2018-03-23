/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.android.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import cn.lrapps.android.ui.adapter.ChooseBlackAppListAdapter;
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
 * 批量添加黑名单和隐藏程序
 */
public class ActivityAddBlackAndHideAppBat extends MyBaseActivity implements View.OnClickListener, ChooseBlackAppListAdapter.IChooseBlackAppListAdapterItemClicked
{
	private static LruCache<String, Boolean> mBlackBooleanMemoryCache;// 缓存
	private static LruCache<String, Boolean> mHideBooleanMemoryCache;// 缓存
	private static final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
	private static final int cacheSize = maxMemory / 8;
	private final int INIT_RESULT = 1110;
	private final int ADD_RESULT = 1111;
	private final int ADD_HIDE_RESULT = 1112;
	private ListView lvApps;
	private List<AppInfo> appInfos;
	private ChooseBlackAppListAdapter appListAdapter;
	private final Map<String, Boolean> statusChangedBlackApps = new HashMap<>();
	private final Map<String, Boolean> statusChangedHideApps = new HashMap<>();
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
					appListAdapter = new ChooseBlackAppListAdapter(ActivityAddBlackAndHideAppBat.this, appInfos, ActivityAddBlackAndHideAppBat.this, ActivityAddBlackAndHideAppBat.this);
					lvApps.setAdapter(appListAdapter);
					break;
				}
				case ADD_RESULT:
				{
					Toast.makeText(ActivityAddBlackAndHideAppBat.this, "添加黑名单应用已完成！", Toast.LENGTH_LONG).show();
					break;
				}
				case ADD_HIDE_RESULT:
				{
					Toast.makeText(ActivityAddBlackAndHideAppBat.this, "添加隐藏应用已完成！", Toast.LENGTH_LONG).show();
					break;
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_black_and_hide_app_bat);
		// 初始化缓存大小
		if (mBlackBooleanMemoryCache == null)
		{
			mBlackBooleanMemoryCache = new LruCache<String, Boolean>(cacheSize)
			{
				@Override
				protected int sizeOf(String key, Boolean value)
				{
					return 2;
				}
			};
		}
		if (mHideBooleanMemoryCache == null)
		{
			mHideBooleanMemoryCache = new LruCache<String, Boolean>(cacheSize)
			{
				@Override
				protected int sizeOf(String key, Boolean value)
				{
					return 2;
				}
			};
		}
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
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.btn_add_black:
			{
				DialogCommon dialogCommon = new DialogCommon(this, new DialogCommon.LibitDialogListener()
				{
					@Override
					public void onOkClick()
					{
						final DialogCommon dialog = new DialogCommon(ActivityAddBlackAndHideAppBat.this, null, "提示", "正在添加黑名单...", false, true, false);
						dialog.show();
						new Thread("addBlackApps")
						{
							@Override
							public void run()
							{
								super.run();
								int count = statusChangedBlackApps.size();
								if (count > 0)
								{
									for (String packageName : statusChangedBlackApps.keySet())
									{
										// 如果将要禁止的App已经禁止，则直接在已禁止的列表中移除
										boolean b = statusChangedBlackApps.get(packageName);
										if (b)
										{
											AppFactory.getInstance().enableApp(packageName, true);
										}
										else
										{
											AppFactory.getInstance().disableApp(packageName);
										}
									}
								}
								Message msg = Message.obtain();
								msg.what = ADD_RESULT;
								mHandler.sendMessage(msg);
								dialog.dismiss();
							}
						}.start();
					}

					@Override
					public void onCancelClick()
					{
					}
				}, "提示", "确定要添加选择的应用到黑名单应用吗？", true, false, true);
				dialogCommon.show();
				break;
			}
			case R.id.btn_add_hide:
			{
				DialogCommon dialogCommon = new DialogCommon(this, new DialogCommon.LibitDialogListener()
				{
					@Override
					public void onOkClick()
					{
						final DialogCommon dialog = new DialogCommon(ActivityAddBlackAndHideAppBat.this, null, "提示", "正在添加隐藏应用...", false, true, false);
						dialog.show();
						new Thread("addHideApps")
						{
							@Override
							public void run()
							{
								super.run();
								int count = statusChangedHideApps.size();
								if (count > 0)
								{
									for (String packageName : statusChangedHideApps.keySet())
									{
										boolean b = statusChangedHideApps.get(packageName);
										DbAppFactory.getInstance().setAppHide(packageName, b);
									}
								}
								Message msg = Message.obtain();
								msg.what = ADD_HIDE_RESULT;
								mHandler.sendMessage(msg);
								dialog.dismiss();
							}
						}.start();
					}

					@Override
					public void onCancelClick()
					{
					}
				}, "提示", "确定要添加选择的应用到隐藏应用吗？", true, false, true);
				dialogCommon.show();
				break;
			}
			//			case R.id.btn_clear_hide:
			//			{
			//				new ActivityThread(this).clearAllHideAppsThread();
			//				break;
			//			}
			//			case R.id.btn_clear_black:
			//			{
			//				new ActivityThread(this).clearAndEnableAllAppBlackAppsThread();
			//				reset();
			//				break;
			//			}
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
				appInfos = DbAppFactory.getInstance().getAppInfoList(null, null, null, null, null);
				Message msg = Message.obtain();
				msg.what = INIT_RESULT;
				mHandler.sendMessage(msg);
			}
		}.start();
	}

	//加入缓存
	public void addBlackBooleanToMemoryCache(String key, Boolean value)
	{
		mBlackBooleanMemoryCache.put(key, value);
	}

	public void addHideBooleanToMemoryCache(String key, Boolean value)
	{
		mHideBooleanMemoryCache.put(key, value);
	}

	// 从缓存中取出
	public Boolean getBlackBooleanFromMemCache(String key)
	{
		return mBlackBooleanMemoryCache.get(key);
	}

	public Boolean getHideBooleanFromMemCache(String key)
	{
		return mHideBooleanMemoryCache.get(key);
	}

	@Override
	protected void viewInit()
	{
		super.viewInit();
		setBackButton();
		lvApps = (ListView) findViewById(R.id.list_all);
		findViewById(R.id.btn_add_hide).setOnClickListener(this);
		findViewById(R.id.btn_add_black).setOnClickListener(this);
		//		findViewById(R.id.btn_clear_hide).setOnClickListener(this);
		//		findViewById(R.id.btn_clear_black).setOnClickListener(this);
		findViewById(R.id.tv_hide).setOnClickListener(this);
		findViewById(R.id.tv_black).setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_activity_add_black_and_hide_app_bat, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent context in AndroidManifest.xml.
		int id = item.getItemId();
		//noinspection SimplifiableIfStatement
		if (id == R.id.action_select_all)
		{
			int count = appInfos.size();
			for (int i = 0; i < count; i++)
			{
				addBlackBooleanToMemoryCache(appInfos.get(i).getPackageName(), true);
				statusChangedBlackApps.put(appInfos.get(i).getPackageName(), false);
				addHideBooleanToMemoryCache(appInfos.get(i).getPackageName(), true);
				statusChangedHideApps.put(appInfos.get(i).getPackageName(), true);
			}
			appListAdapter.notifyDataSetChanged();
			return true;
		}
		else if (id == R.id.action_select_none)
		{
			int count = appInfos.size();
			for (int i = 0; i < count; i++)
			{
				addBlackBooleanToMemoryCache(appInfos.get(i).getPackageName(), false);
				statusChangedBlackApps.put(appInfos.get(i).getPackageName(), true);
				addHideBooleanToMemoryCache(appInfos.get(i).getPackageName(), false);
				statusChangedHideApps.put(appInfos.get(i).getPackageName(), false);
			}
			appListAdapter.notifyDataSetChanged();
			return true;
		}
		else if (id == R.id.action_select_system)
		{
			mBlackBooleanMemoryCache.evictAll();
			mHideBooleanMemoryCache.evictAll();
			new Thread("getAppInfos")
			{
				@Override
				public void run()
				{
					super.run();
					statusChangedBlackApps.clear();
					statusChangedHideApps.clear();
					appInfos = DbAppFactory.getInstance().getAppInfoList(null, AppType.SYSTEM.getType(), null, null, null);
					Message msg = Message.obtain();
					msg.what = INIT_RESULT;
					mHandler.sendMessage(msg);
				}
			}.start();
			return true;
		}
		else if (id == R.id.action_select_user)
		{
			mBlackBooleanMemoryCache.evictAll();
			mHideBooleanMemoryCache.evictAll();
			new Thread("getAppInfos")
			{
				@Override
				public void run()
				{
					super.run();
					statusChangedBlackApps.clear();
					statusChangedHideApps.clear();
					appInfos = DbAppFactory.getInstance().getAppInfoList(null, AppType.USER.getType(), null, null, null);
					Message msg = Message.obtain();
					msg.what = INIT_RESULT;
					mHandler.sendMessage(msg);
				}
			}.start();
			return true;
		}
		else if (id == R.id.action_select_reset)
		{
			reset();
			return true;
		}
		else if (id == R.id.action_clear_hide)
		{
			DialogCommon dialogCommon = new DialogCommon(this, new DialogCommon.LibitDialogListener()
			{
				@Override
				public void onOkClick()
				{
					new ActivityThread(ActivityAddBlackAndHideAppBat.this).clearAllHideAppsThread();
					reset();
				}

				@Override
				public void onCancelClick()
				{
				}
			}, "提示", "确定要清空所有隐藏应用吗？这将使所有应用都显示在主页上。", true, false, true);
			dialogCommon.show();
			return true;
		}
		else if (id == R.id.action_clear_blacks)
		{
			DialogCommon dialogCommon = new DialogCommon(this, new DialogCommon.LibitDialogListener()
			{
				@Override
				public void onOkClick()
				{
					new ActivityThread(ActivityAddBlackAndHideAppBat.this).clearAndEnableAllAppBlackAppsThread();
					reset();
				}

				@Override
				public void onCancelClick()
				{
				}
			}, "提示", "确定要清空所有黑名单应用吗？这将使所有应用都启用。", true, false, true);
			dialogCommon.show();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void reset()
	{
		mBlackBooleanMemoryCache.evictAll();
		mHideBooleanMemoryCache.evictAll();
		statusChangedBlackApps.clear();
		statusChangedHideApps.clear();
		//            mAppListAdapter.notifyDataSetChanged();
		getAppInfos();
	}

	@Override
	public void onBlackCheckedClicked(AppInfo appInfo, boolean isChecked)
	{
		if (appInfo != null)
		{
			statusChangedBlackApps.put(appInfo.getPackageName(), !isChecked);
			addBlackBooleanToMemoryCache(appInfo.getPackageName(), isChecked);
		}
	}

	@Override
	public void onHideCheckedClicked(AppInfo appInfo, boolean isChecked)
	{
		if (appInfo != null)
		{
			statusChangedHideApps.put(appInfo.getPackageName(), isChecked);
			addHideBooleanToMemoryCache(appInfo.getPackageName(), isChecked);
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
						Toast.makeText(ActivityAddBlackAndHideAppBat.this, event.getMsg(), Toast.LENGTH_LONG).show();
						reset();
					}
					else if (event.getType().equalsIgnoreCase(AppEvent.CLEAR_HIDE_APP))
					{
						Toast.makeText(ActivityAddBlackAndHideAppBat.this, event.getMsg(), Toast.LENGTH_LONG).show();
						reset();
					}
				}
			}
		});
	}
}

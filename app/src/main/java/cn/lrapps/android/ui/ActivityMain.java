/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.android.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.callback.AjaxStatus;
import com.external.xlistview.XListView;
import cn.lrapps.android.services.NotificationService;
import cn.lrapps.android.ui.adapter.BaseUserAdapter;
import cn.lrapps.android.ui.adapter.FuncHAdapter;
import cn.lrapps.android.ui.adapter.SectionsPagerAdapter;
import cn.lrapps.android.ui.customer.LayoutSideMain;
import cn.lrapps.android.ui.customer.MyActionBarDrawerToggle;
import cn.lrapps.android.ui.customer.ToastView;
import cn.lrapps.android.ui.dialog.DialogBegin;
import cn.lrapps.android.ui.dialog.DialogCommon;
import cn.lrapps.android.ui.dialog.DialogList;
import com.lrcall.appuser.R;
import cn.lrapps.models.AppInfo;
import cn.lrapps.models.PicInfo;
import cn.lrapps.models.UpdateInfo;
import cn.lrapps.services.ApiConfig;
import cn.lrapps.services.BugService;
import cn.lrapps.services.FileService;
import cn.lrapps.services.IAjaxDataResponse;
import cn.lrapps.services.UpdateService;
import cn.lrapps.services.UserService;
import cn.lrapps.db.DbAppFactory;
import cn.lrapps.enums.AppBlackStatus;
import cn.lrapps.enums.AppEnableStatus;
import cn.lrapps.enums.AppType;
import cn.lrapps.enums.EventTypeLayoutSideMain;
import cn.lrapps.enums.FloatType;
import cn.lrapps.enums.UserEvent;
import cn.lrapps.events.AppEvent;
import cn.lrapps.events.AppListEvent;
import cn.lrapps.events.AppSortChanged;
import cn.lrapps.events.BackupEvent;
import cn.lrapps.models.FuncInfo;
import cn.lrapps.models.MemoryInfo;
import cn.lrapps.models.ReturnInfo;
import cn.lrapps.utils.ConstValues;
import cn.lrapps.utils.GlideImageLoader;
import cn.lrapps.utils.GsonTools;
import cn.lrapps.utils.LogcatTools;
import cn.lrapps.utils.MyExecutorService;
import cn.lrapps.utils.PreferenceUtils;
import cn.lrapps.utils.StringTools;
import cn.lrapps.utils.apptools.AppFactory;
import cn.lrapps.utils.apptools.SystemToolsFactory;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.view.CropImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static cn.lrapps.utils.ConstValues.IMAGE_PICKER;

public class ActivityMain extends MyBaseActivity implements MyActionBarDrawerToggle.ActionBarDrawerToggleStatusChanged, XListView.IXListViewListener, IAjaxDataResponse
{
	private static final String TAG = ActivityMain.class.getSimpleName();
	public static final int GET_RUNNING_APPS_RESULT = 1000;
	public static final int GET_ENABLED_BLACK_APPS_RESULT = 1001;
	private static ActivityMain instance;
	//	public CustomerNotification mCustomerNotification;
	protected int pageIndex = 0;
	private ScheduledExecutorService scheduledExecutorService = null;
	private ScheduledFuture scheduledFuture = null;
	//视图控件
	protected SectionsPagerAdapter mSectionsPagerAdapter;
	protected ViewPager mViewPager;
	private XListView xListView;
	private LayoutSideMain layoutSideMain;
	protected TextView tvAppInfo;
	private DrawerLayout mDrawerLayout;
	private MyBaseFloatWindow mFloatSide;
	public final ActivityThread mActivityThread = new ActivityThread(this);
	private SystemToolsFactory systemTools = SystemToolsFactory.getInstance();
	private UserService mUserService;
	private FileService mFileService;

	public static ActivityMain getInstance()
	{
		return instance;
	}

	public MyBaseFloatWindow getmFloatSide()
	{
		return mFloatSide;
	}

	public void setmFloatSide(MyBaseFloatWindow mFloatSide)
	{
		this.mFloatSide = mFloatSide;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		instance = this;
		mUserService = new UserService(this);
		mUserService.addDataResponse(this);
		mFileService = new FileService(this);
		mFileService.addDataResponse(this);
		viewInit();
		ImagePicker imagePicker = ImagePicker.getInstance();
		imagePicker.setImageLoader(new GlideImageLoader());   //设置图片加载器
		imagePicker.setShowCamera(true);  //显示拍照按钮
		imagePicker.setMultiMode(false);//是否多选
		imagePicker.setCrop(true);        //允许裁剪（单选才有效）
		imagePicker.setSaveRectangle(true); //是否按矩形区域保存
		boolean isFirst = PreferenceUtils.getInstance().getBooleanValue(PreferenceUtils.IS_FIRST_RUN) && DbAppFactory.getInstance().getAppInfoListCount(null, null, null, null) == 0;
		if (isFirst)
		{
			//            startActivity(new Intent(this, ActivityWelcome.class));
			mActivityThread.getSystemAppInfosThread(true, false);
			//            PreferenceUtils.getInstance().setBooleanValue(PreferenceUtils.IS_FIRST_RUN, false);
		}
		else
		{
			//			mActivityThread.getSystemAppInfosThread(false, false);
		}
		if (PreferenceUtils.getInstance().getBooleanValue(PreferenceUtils.LOGCAT_AUTO_UPDATE))
		{
			new BugService(this).submitBug();
		}
		//		mCustomerNotification = new CustomerNotification(this);
		if (!PreferenceUtils.getInstance().getBooleanValue(PreferenceUtils.IS_ACCEPT_ROLE))
		{
			DialogBegin dialogBegin = new DialogBegin(this, new DialogBegin.OnBeginListener()
			{
				@Override
				public void onOkClick(boolean accept)
				{
					if (!accept)
					{
						exit();
					}
					else
					{
						PreferenceUtils.getInstance().setBooleanValue(PreferenceUtils.IS_ACCEPT_ROLE, true);
					}
				}

				@Override
				public void onCancelClick()
				{
					exit();
				}
			});
			dialogBegin.show();
		}
		//		int i = 1 / 0;
		if (PreferenceUtils.getInstance().getBooleanValue(PreferenceUtils.IS_SHOW_HOVER_BALL))
		{
			String type = PreferenceUtils.getInstance().getStringValue(PreferenceUtils.FLOAT_TYPE);
			if (type.equalsIgnoreCase(FloatType.FLOAT_BALL.getType()))
			{
				mFloatSide = new FloatBall(this);
			}
			else if (type.equalsIgnoreCase(FloatType.FLOAT_BOTTOM.getType()))
			{
				mFloatSide = new FloatSideBottom(this);
			}
			else if (type.equalsIgnoreCase(FloatType.FLOAT_RIGHT.getType()))
			{
				mFloatSide = new FloatSideRight(this);
			}
			mFloatSide.showTopWindow();
		}
		if (!EventBus.getDefault().isRegistered(this))
		{
			EventBus.getDefault().register(this);
		}
		final UpdateService updateService = new UpdateService(this);
		updateService.addDataResponse(new IAjaxDataResponse()
		{
			@Override
			public boolean onAjaxDataResponse(String url, String result, AjaxStatus status)
			{
				if (url.endsWith(ApiConfig.CHECK_UPDATE))
				{
					UpdateInfo updateInfo = GsonTools.getReturnObject(result, UpdateInfo.class);
					if (updateInfo != null && systemTools.getVersionCode() < updateInfo.getVersionCode())
					{
						updateService.showUpdataDialog(updateInfo);
					}
					return true;
				}
				return false;
			}
		});
		updateService.checkUpdate(null, false);
		if (PreferenceUtils.getInstance().getBooleanValue(PreferenceUtils.IS_SHOW_NOTIFICATION))
		{
			startService(new Intent(this, NotificationService.class));
		}
		//		getRunningAppsThread();
	}

	synchronized private void updateData()
	{
		if (scheduledExecutorService == null)
		{
			scheduledExecutorService = Executors.newScheduledThreadPool(1);
		}
		else
		{
			if (scheduledFuture != null)
			{
				scheduledFuture.cancel(true);
			}
		}
		scheduledFuture = scheduledExecutorService.scheduleAtFixedRate(new Thread("updateData")
		{
			@Override
			public void run()
			{
				super.run();
				LogcatTools.debug(TAG, "ScheduledFuture,时间:" + StringTools.getCurrentTime());
				mHandler.sendEmptyMessage(ActivityThread.UPDATE_MEMORY_RESULT);
			}
		}, 5, 30, TimeUnit.SECONDS);
	}

	synchronized private void cancelScheduledFuture()
	{
		if (scheduledFuture != null)
		{
			LogcatTools.debug(TAG, "ScheduledFuture,cancelScheduledFuture:" + StringTools.getCurrentTime());
			scheduledFuture.cancel(true);
		}
	}

	synchronized private void stopScheduledFuture()
	{
		LogcatTools.debug(TAG, "ScheduledFuture,stopScheduledFuture:" + StringTools.getCurrentTime());
		if (scheduledFuture != null && !scheduledFuture.isCancelled())
		{
			scheduledFuture.cancel(true);
		}
		if (scheduledExecutorService != null)
		{
			scheduledExecutorService.shutdown();
		}
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		OnAppStatusChanged();
		if (mFloatSide != null)
		{
			mFloatSide.clearTopWindow();
		}
		//		updateData();
	}

	private void setAppInfo()
	{
		if (PreferenceUtils.getInstance().getBooleanValue(PreferenceUtils.SHOW_DISABLE_COUNT))
		{
			findViewById(R.id.layout_app_info).setVisibility(View.VISIBLE);
			tvAppInfo.setText(String.format("用户应用%d个,已冻结%d个。", DbAppFactory.getInstance().getAppInfoListCount(null, AppType.USER.getType(), null, null), DbAppFactory.getInstance().getAppInfoListCount(AppEnableStatus.DISABLED.getStatus(), AppType.USER.getType(), null, null)));
		}
		else
		{
			findViewById(R.id.layout_app_info).setVisibility(View.GONE);
		}
	}

	@Override
	protected void onPause()
	{
		closeDrawerLayout();
		//		cancelScheduledFuture();
		if (PreferenceUtils.getInstance().getBooleanValue(PreferenceUtils.IS_SHOW_HOVER_BALL))
		{
			if (mFloatSide != null)
			{
				mFloatSide.showTopWindow();
			}
		}
//		new Thread("tapclick")
//		{
//			@Override
//			public void run()
//			{
//				super.run();
//				ReturnInfo returnInfo = AppFactory.simulateClick(10, 10);
//				LogcatTools.info("tapclick", GsonTools.toJson(returnInfo));
//			}
//		}.start();
		super.onPause();
	}

	@Override
	protected void onDestroy()
	{
		if (mFloatSide != null)
		{
			mFloatSide.clearTopWindow();
		}
		instance = null;
		//		stopScheduledFuture();
		if (EventBus.getDefault().isRegistered(this))
		{
			EventBus.getDefault().unregister(this);
		}
		stopService(new Intent(this, NotificationService.class));
		super.onDestroy();
	}

	@Override
	public void onBackPressed()
	{
		//		super.onBackPressed();
		if (mFloatSide != null)
		{
			mFloatSide.hideList();
		}
		MyApplication.getInstance().backToHome();
	}

	@Override
	public void onDrawerOpened(View drawerView)
	{
		//		vHead.setVisibility(View.GONE);
		layoutSideMain.refresh();
	}

	@Override
	public void onDrawerClosed(View drawerView)
	{
		//		int position = bannerViewPager.getCurrentItem();
		//		if (position == FIND || position == DIALER || position == USER)
		//		{
		//			vHead.setVisibility(View.VISIBLE);
		//		}
	}

	@Override
	public void onRefresh()
	{
		layoutSideMain.refresh();
		mHandler.sendEmptyMessage(ActivityThread.UPDATE_MEMORY_RESULT);
		xListView.stopRefresh();
	}

	@Override
	public void onLoadMore()
	{
		xListView.stopLoadMore();
	}

	//关闭侧滑
	private void closeDrawerLayout()
	{
		if (mDrawerLayout != null)
		{
			mDrawerLayout.closeDrawers();
		}
	}

	public DrawerLayout getmDrawerLayout()
	{
		return mDrawerLayout;
	}

	@Override
	protected void viewInit()
	{
		List<Fragment> fragmentList = new ArrayList<>();
		String[] titles;
		if (PreferenceUtils.getInstance().getBooleanValue(PreferenceUtils.SHOW_ALL_APPLIST))
		{
			if (PreferenceUtils.getInstance().getBooleanValue(PreferenceUtils.SHOW_ENABLE_DISABLE_APPLIST))
			{
				titles = new String[]{"全部应用", "已放行的应用", "已禁止的应用"};
				fragmentList.add(FragmentAppList.newInstance());
				fragmentList.add(FragmentEnabledAppList.newInstance());
				fragmentList.add(FragmentDisabledAppList.newInstance());
			}
			else
			{
				titles = new String[]{"全部应用"};
				fragmentList.add(FragmentAppList.newInstance());
			}
		}
		else
		{
			titles = new String[]{"已放行的应用", "已禁止的应用"};
			fragmentList.add(FragmentEnabledAppList.newInstance());
			fragmentList.add(FragmentDisabledAppList.newInstance());
		}
		setTitle(titles[0]);
		super.viewInit();
		setSwipeBackEnable(false); //禁止滑动返回
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), fragmentList, titles);
		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener()
		{
			@Override
			public void onPageSelected(int position)
			{
				mToolbar.setTitle(mSectionsPagerAdapter.getPageTitle(position));
				pageIndex = position;
			}
		});
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
		//		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close);
		//		mDrawerToggle.syncState();
		//		mDrawerLayout.addDrawerListener(mDrawerToggle);
		ActionBarDrawerToggle mDrawerToggle = new MyActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close, this);
		mDrawerToggle.syncState();
		mDrawerLayout.addDrawerListener(mDrawerToggle);
		mViewPager.setOffscreenPageLimit(fragmentList.size());
		pageIndex = 0;
		tvAppInfo = (TextView) findViewById(R.id.tv_app_info);
		findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (v.getId() == R.id.iv_close)
				{
					findViewById(R.id.layout_app_info).setVisibility(View.GONE);
				}
			}
		});
		//侧滑布局
		xListView = (XListView) findViewById(R.id.xlist);
		layoutSideMain = new LayoutSideMain(this);
		xListView.setPullRefreshEnable(true);
		xListView.setPullLoadEnable(false);
		xListView.addHeaderView(layoutSideMain);
		xListView.setAdapter(null);
		xListView.setXListViewListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_activity_main, menu);
		//		if (AppFactory.isCompatible(14))
		//		{
		//			MenuItem shareItem = menu.findItem(R.id.action_share);
		//			ShareActionProvider shareActionProvider = (ShareActionProvider) shareItem.getActionProvider();
		//			Intent intent = new Intent(Intent.ACTION_SEND);
		//			intent.setType("image/*");
		//			shareActionProvider.setShareIntent(intent);
		//		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		int id = item.getItemId();
		if (id == R.id.action_refresh)
		{
			DialogCommon dialogCommon = new DialogCommon(this, new DialogCommon.LibitDialogListener()
			{
				@Override
				public void onOkClick()
				{
					mActivityThread.getSystemAppInfosThread(false, true);
				}

				@Override
				public void onCancelClick()
				{
				}
			}, "提示", "确定要同步系统应用和本程序之间的应用状态吗？", true, false, true);
			dialogCommon.show();
			return true;
		}
		else if (id == R.id.action_close_black)
		{
			DialogCommon dialogCommon = new DialogCommon(this, new DialogCommon.LibitDialogListener()
			{
				@Override
				public void onOkClick()
				{
					mActivityThread.closeBlackAppsThread(true);
				}

				@Override
				public void onCancelClick()
				{
				}
			}, "提示", "确定要关闭所有打开的黑名单应用吗？", true, false, true);
			dialogCommon.show();
			return true;
		}
		else if (id == R.id.action_sort)
		{
			List<FuncInfo> funcInfoList = new ArrayList<>();
			funcInfoList.add(new FuncInfo(null, "按名称升序排序"));
			funcInfoList.add(new FuncInfo(null, "按名称降序排序"));
			funcInfoList.add(new FuncInfo(null, "按启用状态升序排序"));
			funcInfoList.add(new FuncInfo(null, "按启用状态降序排序"));
			FuncHAdapter adapter = new FuncHAdapter(this, funcInfoList, null);
			final DialogList dialogList = new DialogList(this, adapter);
			adapter.setiItemClick(new BaseUserAdapter.IItemClick<FuncInfo>()
			{
				@Override
				public void onItemClicked(FuncInfo funcInfo)
				{
					if (funcInfo.getName().equals("按名称升序排序"))
					{
						EventBus.getDefault().post(AppSortChanged.NAME_ASC);
					}
					else if (funcInfo.getName().equals("按名称降序排序"))
					{
						EventBus.getDefault().post(AppSortChanged.NAME_DESC);
					}
					else if (funcInfo.getName().equals("按启用状态升序排序"))
					{
						EventBus.getDefault().post(AppSortChanged.ENABLE_STATUS_ASC);
					}
					else if (funcInfo.getName().equals("按启用状态降序排序"))
					{
						EventBus.getDefault().post(AppSortChanged.ENABLE_STATUS_DESC);
					}
					dialogList.dismiss();
				}
			});
			dialogList.show();
			return true;
		}
		else if (id == R.id.action_hide_apps)
		{
			startActivity(new Intent(this, ActivityHideAppList.class));
			return true;
		}
		else if (id == R.id.action_settings)
		{
			startActivity(new Intent(this, ActivitySettings.class));
			return true;
		}
		else if (id == R.id.action_root)//需ROOT操作的工具箱
		{
			startActivity(new Intent(this, ActivityRootTools.class));
			return true;
		}
		else if (id == R.id.action_black_clear)
		{
			mActivityThread.clearAndEnableAllAppBlackAppsThread();
			return true;
		}
		else if (id == R.id.action_add_bat)
		{
			startActivity(new Intent(this, ActivityAddBlackAndHideAppBat.class));
			return true;
		}
		else if (id == R.id.action_hide_clear)
		{
			mActivityThread.clearAllHideAppsThread();
			return true;
		}
		else if (id == R.id.action_about)
		{
			startActivity(new Intent(this, ActivityAbout.class));
			return true;
		}
		else if (id == R.id.action_update)
		{
			new UpdateService(this).checkUpdate("正在检查更新", true);
			return true;
		}
		else if (id == R.id.action_welcome)
		{
			startActivity(new Intent(this, ActivityWelcome.class));
			return true;
		}
		else if (id == R.id.action_share)
		{
			new UserService(this).share("请稍后...", true);
			return true;
		}
		else if (id == R.id.action_create_desktop_close_all)
		{
			Intent intent = new Intent(ConstValues.INSTALL_SHORTCUT);
			intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "一键冻结");
			// 是否可以有多个快捷方式的副本，参数如果是true就可以生成多个快捷方式，如果是false就不会重复添加
			intent.putExtra("duplicate", false);
			Intent intent3 = new Intent(MyApplication.getContext(), ActivityLauncher.class);
			intent3.putExtra(ConstValues.DATA_PACKAGE_NAME, ActivityLauncher.CLOSE_ALL);
			intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent3);
			intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(this, R.drawable.ic_close));
			sendBroadcast(intent);
			Toast.makeText(this, "创建“一键冻结”快捷方式成功", Toast.LENGTH_LONG).show();
			return true;
		}
		else if (id == R.id.action_exit)
		{
			exit();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	//退出程序
	public void exit()
	{
		finish();
		//		if (mCustomerNotification != null)
		//		{
		//			LogcatTools.debug("exit", "通知栏被关闭");
		//			mCustomerNotification.cancelAll();
		//		}
	}

	public void OnAppStatusChanged()
	{
		setAppInfo();
		getRunningAppsThread();
		getEnabledBlackAppsThread();
	}

	/**
	 * 当前显示的页面索引
	 *
	 * @return
	 */
	public int getPageIndex()
	{
		return pageIndex;
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
						OnAppStatusChanged();
					}
					else if (event.getType().equalsIgnoreCase(AppEvent.GET_RUNNING_APP))
					{
						getRunningAppsThread();
					}
					else if (event.getType().equalsIgnoreCase(AppEvent.GET_ENABLED_BLACK_APP))
					{
						getEnabledBlackAppsThread();
					}
					else if (event.getType().equalsIgnoreCase(AppEvent.GET_SYSTEM_APP))
					{
						if (PreferenceUtils.getInstance().getBooleanValue(PreferenceUtils.IS_FIRST_RUN))
						{
							//                    activityMain.startActivity(new Intent(activityMain, ActivityAddBlackAndHideAppBat.class));
							PreferenceUtils.getInstance().setBooleanValue(PreferenceUtils.IS_FIRST_RUN, false);
						}
						OnAppStatusChanged();
					}
					else if (event.getType().equalsIgnoreCase(AppEvent.CLOSE_BLACK_APP))
					{
						Toast.makeText(ActivityMain.this, event.getMsg(), Toast.LENGTH_LONG).show();
						OnAppStatusChanged();
					}
					else if (event.getType().equalsIgnoreCase(AppEvent.CLEAR_AND_ENABLE_BLACK_APP))
					{
						Toast.makeText(ActivityMain.this, event.getMsg(), Toast.LENGTH_LONG).show();
						OnAppStatusChanged();
					}
					else if (event.getType().equalsIgnoreCase(AppEvent.CLEAR_HIDE_APP))
					{
						Toast.makeText(ActivityMain.this, event.getMsg(), Toast.LENGTH_LONG).show();
						OnAppStatusChanged();
					}
				}
			}
		});
	}

	@Subscribe
	public void onEventMainThread(final BackupEvent event)
	{
		mHandler.post(new Thread()
		{
			@Override
			public void run()
			{
				super.run();
				if (event != null)
				{
					if (event.getType() == BackupEvent.BACKUP)
					{
						Toast.makeText(ActivityMain.this, event.getMsg(), Toast.LENGTH_LONG).show();
					}
					if (event.getType() == BackupEvent.RESTORE)
					{
						Toast.makeText(ActivityMain.this, event.getMsg(), Toast.LENGTH_LONG).show();
						OnAppStatusChanged();
					}
				}
			}
		});
	}

	@Subscribe
	public void onEventMainThread(final EventTypeLayoutSideMain msg)
	{
		runOnUiThread(new Thread()
		{
			@Override
			public void run()
			{
				super.run();
				if (msg != null)
				{
					if (msg.getType().equalsIgnoreCase(EventTypeLayoutSideMain.CLOSE_DRAWER.getType()))
					{
						closeDrawerLayout();
					}
				}
			}
		});
	}

	@Subscribe
	public void onEventMainThread(final AppListEvent appListEvent)
	{
		if (appListEvent != null)
		{
			if (AppListEvent.GET_RUNNING_APP.equals(appListEvent.getType()))
			{
				mHandler.post(new Thread()
				{
					@Override
					public void run()
					{
						super.run();
						mHandler.sendEmptyMessage(ActivityThread.UPDATE_MEMORY_RESULT);
						if (getmFloatSide() != null && getmFloatSide().getLayoutListStatus() == View.VISIBLE)
						{
							List<AppInfo> runningAppInfoList = appListEvent.getAppInfoList();
							if (runningAppInfoList != null && runningAppInfoList.size() > 0)
							{
								for (AppInfo appInfo : runningAppInfoList)
								{
									if (appInfo == null)
									{
										mActivityThread.clearNotExistAppsThread();
										continue;
									}
									boolean isExist = false;
									for (AppInfo existAppInfo : getmFloatSide().mBlackAppInfoList)
									{
										if (existAppInfo != null && existAppInfo.getPackageName().equalsIgnoreCase(appInfo.getPackageName()))
										{
											isExist = true;
											break;
										}
									}
									if (!isExist)
									{
										getmFloatSide().mBlackAppInfoList.add(appInfo);
									}
								}
								getmFloatSide().setAppListAdapter();
							}
						}
					}
				});
			}
		}
	}

	@Subscribe
	public void onEventMainThread(final UserEvent userEvent)
	{
		runOnUiThread(new Thread()
		{
			@Override
			public void run()
			{
				super.run();
				if (userEvent != null)
				{
					if (userEvent.getEvent().equalsIgnoreCase(UserEvent.LOGOUT.getEvent()))
					{
						startActivity(new Intent(ActivityMain.this, ActivityLogin.class));
						finish();
					}
					else if (userEvent.getEvent().equalsIgnoreCase(UserEvent.CHANGE_HEADER.getEvent()))
					{
						ImagePicker imagePicker = ImagePicker.getInstance();
						//						imagePicker.setShowCamera(true);  //显示拍照按钮
						//						imagePicker.setCrop(true);        //允许裁剪（单选才有效）
						imagePicker.setSaveRectangle(false); //是否按矩形区域保存
						imagePicker.setStyle(CropImageView.Style.CIRCLE);  //裁剪框的形状
						imagePicker.setFocusWidth(512);   //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
						imagePicker.setFocusHeight(512);  //裁剪框的高度。单位像素（圆形自动取宽高最小值）
						imagePicker.setOutPutX(512);//保存文件的宽度。单位像素
						imagePicker.setOutPutY(512);//保存文件的高度。单位像素
						startActivityForResult(new Intent(ActivityMain.this, ImageGridActivity.class), IMAGE_PICKER);
					}
				}
			}
		});
	}

	private final Handler mHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
				case ActivityThread.UPDATE_MEMORY_RESULT:
				{
					MemoryInfo memoryInfo = GsonTools.getReturnObject(AppFactory.getInstance().getMemoryInfo(), MemoryInfo.class);
					if (memoryInfo != null)
					{
						String info = String.format("内存剩余/总大小：%dM/%dM", memoryInfo.getUnusedMemory() / (1024 * 1024), memoryInfo.getTotalMemory() / (1024 * 1024));
						layoutSideMain.setTvMemoryInfo(info);
						if (getmFloatSide() != null && getmFloatSide().getLayoutListStatus() == View.VISIBLE)
						{
							getmFloatSide().updateMemoryInfo(info);
						}
					}
					break;
				}
				//				case GET_RUNNING_APPS_RESULT:
				//				{
				//					layoutSideMain.updateRunningApps();
				//					sendEmptyMessage(ActivityThread.UPDATE_MEMORY_RESULT);
				//					//					if (PreferenceUtils.getInstance().getBooleanValue(PreferenceUtils.IS_SHOW_NOTIFICATION))
				//					//					{
				//					//						mCustomerNotification.notifyApps(layoutSideMain.mRunningAppInfoList);
				//					//					}
				//					if (getmFloatSide() != null && getmFloatSide().getLayoutListStatus() == View.VISIBLE)
				//					{
				//						for (AppInfo appInfo : layoutSideMain.mRunningAppInfoList)
				//						{
				//							if (appInfo == null)
				//							{
				//								mActivityThread.clearNotExistAppsThread();
				//								continue;
				//							}
				//							boolean isExist = false;
				//							for (AppInfo existAppInfo : getmFloatSide().mBlackAppInfoList)
				//							{
				//								if (existAppInfo != null && existAppInfo.getPackageName().equalsIgnoreCase(appInfo.getPackageName()))
				//								{
				//									isExist = true;
				//									break;
				//								}
				//							}
				//							if (!isExist)
				//							{
				//								getmFloatSide().mBlackAppInfoList.add(appInfo);
				//							}
				//						}
				//						getmFloatSide().setAppListAdapter();
				//					}
				//					break;
				//				}
				case GET_ENABLED_BLACK_APPS_RESULT:
				{
					layoutSideMain.getEnabledBlackapps();
					if (getmFloatSide() != null && getmFloatSide().getLayoutListStatus() == View.VISIBLE)
					{
						getmFloatSide().mBlackAppInfoList.clear();
						getmFloatSide().mBlackAppInfoList.addAll(layoutSideMain.mBlackAppInfoList);
						getmFloatSide().setAppListAdapter();
					}
					break;
				}
			}
		}
	};
	private final MyExecutorService myExecutorService = MyExecutorService.getInstance();

	/**
	 * 获取已经启用的黑名单App
	 */
	public void getEnabledBlackAppsThread()
	{
		myExecutorService.submitTask(new Thread("getEnabledBlackAppsThread")
		{
			@Override
			public void run()
			{
				super.run();
				List<AppInfo> blackAppInfoList = DbAppFactory.getInstance().getAppInfoList(AppEnableStatus.ENABLED.getStatus(), null, null, AppBlackStatus.BLACK.getStatus(), null);
				layoutSideMain.mBlackAppInfoList.clear();
				if (blackAppInfoList != null && blackAppInfoList.size() > 0)
				{
					for (AppInfo blackAppInfo : blackAppInfoList)
					{
						layoutSideMain.mBlackAppInfoList.add(AppFactory.getInstance().getAppInfoByPackageName(blackAppInfo.getPackageName(), true));
					}
				}
				Message msg = Message.obtain();
				msg.what = GET_ENABLED_BLACK_APPS_RESULT;
				mHandler.sendMessage(msg);
			}
		});
	}

	/**
	 * 获取正在运行的App
	 */
	public void getRunningAppsThread()
	{
		Thread thread = new Thread("getRunningAppsThread")
		{
			@Override
			public void run()
			{
				super.run();
				List<AppInfo> appInfoList = AppFactory.getInstance().getRunningApps();
				Message msg = Message.obtain();
				msg.what = GET_RUNNING_APPS_RESULT;
				mHandler.sendMessage(msg);
				EventBus.getDefault().post(new AppListEvent(AppListEvent.GET_RUNNING_APP, appInfoList));
			}
		};
		thread.start();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == ImagePicker.RESULT_CODE_ITEMS)
		{
			if (data != null && requestCode == IMAGE_PICKER)
			{
				ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
				if (images != null && images.size() > 0)
				{
					ImageItem imageItem = images.get(0);
					mFileService.uploadPic(imageItem.path, "header", "正在上传图片...", true);
				}
			}
			else
			{
				Toast.makeText(this, "没有数据", Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	public boolean onAjaxDataResponse(String url, String result, AjaxStatus status)
	{
		if (url.endsWith(ApiConfig.UPLOAD_PIC))
		{
			ReturnInfo returnInfo = GsonTools.getReturnInfo(result);
			if (ReturnInfo.isSuccess(returnInfo))
			{
				PicInfo picInfo = GsonTools.getReturnObject(returnInfo, PicInfo.class);
				if (picInfo != null)
				{
					mUserService.updateUserHeader(picInfo.getPicUrl(), "正在设置头像，请稍后...", true);
					//					ivPic.setTag(picInfo.getPicUrl());
					//					Picasso.with(this).load(Uri.parse(ApiConfig.getServerPicUrl(picInfo.getPicUrl()))).into(ivPic);
				}
			}
			else
			{
				String msg = "上传图片失败！";
				if (returnInfo != null)
				{
					msg = returnInfo.getMsg();
				}
				ToastView.showCenterToast(this, R.drawable.ic_do_fail, msg);
			}
		}
		else if (url.endsWith(ApiConfig.UPDATE_USER_HEADER))
		{
			ReturnInfo returnInfo = GsonTools.getReturnInfo(result);
			if (ReturnInfo.isSuccess(returnInfo))
			{
				ToastView.showCenterToast(this, R.drawable.ic_done, "设置头像成功！");
				EventBus.getDefault().post(UserEvent.CHANGED_HEADER);
			}
			else
			{
				String msg = "设置头像失败！";
				if (returnInfo != null)
				{
					msg = returnInfo.getMsg();
				}
				ToastView.showCenterToast(this, R.drawable.ic_do_fail, msg);
			}
		}
		return true;
	}
}

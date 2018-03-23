/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.android.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import cn.lrapps.android.ui.dialog.DialogSettingBugLevel;
import com.lrcall.appuser.R;
import cn.lrapps.services.ApiConfig;
import cn.lrapps.services.UpdateService;
import cn.lrapps.services.UserService;
import cn.lrapps.events.AppEvent;
import cn.lrapps.utils.ConstValues;
import cn.lrapps.utils.PreferenceUtils;
import cn.lrapps.utils.apptools.SystemToolsFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class ActivitySettings extends MyBaseActivity implements View.OnClickListener
{
	private static final String TAG = ActivitySettings.class.getSimpleName();
	private TextView tvCurrentVersion;
	private ImageView ivBootStart;
	private Handler mHandler = new Handler();
	private SystemToolsFactory systemTools = SystemToolsFactory.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		viewInit();
		if (!EventBus.getDefault().isRegistered(this))
		{
			EventBus.getDefault().register(this);
		}
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
					if (event.getType().equalsIgnoreCase(AppEvent.CLEAR_APP))
					{
						Toast.makeText(ActivitySettings.this, event.getMsg(), Toast.LENGTH_LONG).show();
					}
				}
			}
		});
	}

	@Override
	public void viewInit()
	{
		super.viewInit();
		setBackButton();
		tvCurrentVersion = (TextView) findViewById(R.id.app_setting_current_version);
		ivBootStart = (ImageView) findViewById(R.id.app_setting_boot_start_value);
		findViewById(R.id.layout_setting_ui).setOnClickListener(this);
		findViewById(R.id.layout_boot_start).setOnClickListener(this);
		findViewById(R.id.layout_limit_running_app).setOnClickListener(this);
		findViewById(R.id.layout_backup).setOnClickListener(this);
		findViewById(R.id.layout_add_black_and_hide_app_bat).setOnClickListener(this);
		findViewById(R.id.layout_add_common_use_app_bat).setOnClickListener(this);
		findViewById(R.id.layout_clear).setOnClickListener(this);
		findViewById(R.id.layout_about).setOnClickListener(this);
		findViewById(R.id.layout_bug_level).setOnClickListener(this);
		findViewById(R.id.layout_update).setOnClickListener(this);
		findViewById(R.id.layout_more_app).setOnClickListener(this);
		findViewById(R.id.layout_create_desktop_close_all).setOnClickListener(this);
		findViewById(R.id.layout_share).setOnClickListener(this);
		tvCurrentVersion.setText(systemTools.getVersionName());
		if (PreferenceUtils.getInstance().getBooleanValue(PreferenceUtils.IS_BOOT_START))
		{
			ivBootStart.setImageResource(R.drawable.btn_checked);
		}
		else
		{
			ivBootStart.setImageResource(R.drawable.btn_nocheck);
		}
		ivBootStart.setOnClickListener(this);
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.layout_setting_ui:
			{
				startActivity(new Intent(this, ActivitySettingsUi.class));
				break;
			}
			case R.id.layout_boot_start:
			case R.id.app_setting_boot_start_value:
			{
				if (PreferenceUtils.getInstance().getBooleanValue(PreferenceUtils.IS_BOOT_START))
				{
					PreferenceUtils.getInstance().setBooleanValue(PreferenceUtils.IS_BOOT_START, false);
					ivBootStart.setImageResource(R.drawable.btn_nocheck);
				}
				else
				{
					PreferenceUtils.getInstance().setBooleanValue(PreferenceUtils.IS_BOOT_START, true);
					ivBootStart.setImageResource(R.drawable.btn_checked);
				}
				break;
			}
			case R.id.layout_backup:
			{
				startActivity(new Intent(this, ActivityBackups.class));
				break;
			}
			case R.id.layout_add_black_and_hide_app_bat:
			{
				startActivity(new Intent(this, ActivityAddBlackAndHideAppBat.class));
				break;
			}
			case R.id.layout_add_common_use_app_bat:
			{
				startActivity(new Intent(this, ActivityAddCommonUseAppBat.class));
				break;
			}
			case R.id.layout_clear:
			{
				new ActivityThread(this).clearNotExistAppsThread();
				break;
			}
			case R.id.layout_about:
			{
				startActivity(new Intent(this, ActivityAbout.class));
				break;
			}
			case R.id.layout_bug_level:
			{
				new DialogSettingBugLevel(this, null).show();
				break;
			}
			case R.id.layout_update:
			{
				UpdateService updateService = new UpdateService(this);
				updateService.checkUpdate("正在检查更新", true);
				break;
			}
			case R.id.layout_more_app:
			{
				ActivityWebView.startWebActivity(this, getString(R.string.action_more_app), ApiConfig.getMoreAppUrl());
				break;
			}
			case R.id.layout_create_desktop_close_all:
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
				break;
			}
			case R.id.layout_share:
			{
				new UserService(this).share("请稍后...", true);
				break;
			}
		}
	}
}

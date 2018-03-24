/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.android.ui;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lrcall.appuser.R;

import org.greenrobot.eventbus.EventBus;

import cn.lrapps.android.services.NotificationService;
import cn.lrapps.android.ui.customer.ToastView;
import cn.lrapps.android.ui.dialog.DialogListenser;
import cn.lrapps.android.ui.dialog.DialogSettingAppPaddingSize;
import cn.lrapps.android.ui.dialog.DialogSettingColumnLandscape;
import cn.lrapps.android.ui.dialog.DialogSettingColumnPortrait;
import cn.lrapps.android.ui.dialog.DialogSettingFloatBall;
import cn.lrapps.android.ui.dialog.DialogSettingFontSize;
import cn.lrapps.android.ui.dialog.DialogSettingShowFragments;
import cn.lrapps.android.ui.dialog.DialogSettingSideWidth;
import cn.lrapps.enums.FloatType;
import cn.lrapps.events.AppEvent;
import cn.lrapps.utils.PreferenceUtils;
import cn.lrapps.utils.apptools.AppFactory;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class ActivitySettingsUi extends MyBaseActivity implements View.OnClickListener
{
	private static final String TAG = ActivitySettingsUi.class.getSimpleName();
	private TextView tvSize2, tvColumnPortaitCount, tvColumnLandscapeCount, tvAppPaddingSize, tvFloatSideWidthValue;
	private ImageView ivShowNotification, ivShowBall, ivShowDisableDialog;
	private Handler mHandler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings_ui);
		viewInit();
	}

	@Override
	public void viewInit()
	{
		super.viewInit();
		setBackButton();
		tvSize2 = (TextView) findViewById(R.id.app_setting_text_size2);
		tvColumnPortaitCount = (TextView) findViewById(R.id.app_setting_column_portrait_count);
		tvColumnLandscapeCount = (TextView) findViewById(R.id.app_setting_column_landscape_count);
		tvAppPaddingSize = (TextView) findViewById(R.id.app_setting_app_padding_size);
		tvFloatSideWidthValue = (TextView) findViewById(R.id.tv_float_side_width_value);
		ivShowNotification = (ImageView) findViewById(R.id.app_setting_show_notification_value);
		ivShowBall = (ImageView) findViewById(R.id.app_setting_show_ball_value);
		ivShowDisableDialog = (ImageView) findViewById(R.id.app_setting_show_disable_dialog_value);
		findViewById(R.id.layout_font_size).setOnClickListener(this);
		findViewById(R.id.layout_column_portrait).setOnClickListener(this);
		findViewById(R.id.layout_column_landscape).setOnClickListener(this);
		findViewById(R.id.layout_app_padding_size).setOnClickListener(this);
		findViewById(R.id.layout_float_side_width).setOnClickListener(this);
		findViewById(R.id.layout_show_notification).setOnClickListener(this);
		findViewById(R.id.layout_show_ball).setOnClickListener(this);
		findViewById(R.id.layout_limit_running_app).setOnClickListener(this);
		findViewById(R.id.layout_show_disable_dialog).setOnClickListener(this);
		findViewById(R.id.layout_show_fragments).setOnClickListener(this);
		if (PreferenceUtils.getInstance().getBooleanValue(PreferenceUtils.IS_SHOW_NOTIFICATION))
		{
			ivShowNotification.setImageResource(R.drawable.btn_checked);
		}
		else
		{
			ivShowNotification.setImageResource(R.drawable.btn_nocheck);
		}
		if (PreferenceUtils.getInstance().getBooleanValue(PreferenceUtils.IS_SHOW_HOVER_BALL))
		{
			ivShowBall.setImageResource(R.drawable.btn_checked);
		}
		else
		{
			ivShowBall.setImageResource(R.drawable.btn_nocheck);
		}
		if (PreferenceUtils.getInstance().getBooleanValue(PreferenceUtils.SHOW_DISABLE_DIALOG))
		{
			ivShowDisableDialog.setImageResource(R.drawable.btn_checked);
		}
		else
		{
			ivShowDisableDialog.setImageResource(R.drawable.btn_nocheck);
		}
		ivShowNotification.setOnClickListener(this);
		ivShowBall.setOnClickListener(this);
		ivShowDisableDialog.setOnClickListener(this);
	}

	private void initData()
	{
		String textSize = PreferenceUtils.getInstance().getStringValue(PreferenceUtils.APP_FONT_SIZE);
		int minSize = DialogSettingFontSize.MIN_SIZE;
		int maxSize = DialogSettingFontSize.MAX_SIZE;
		try
		{
			int size = Integer.parseInt(textSize);
			if (size < minSize + ((maxSize - minSize) / 3))
			{
				tvSize2.setText("小");
			}
			else if (size < minSize + ((maxSize - minSize) * 2 / 3))
			{
				tvSize2.setText("中");
			}
			else
			{
				tvSize2.setText("大");
			}
		}
		catch (NumberFormatException e)
		{
			PreferenceUtils.getInstance().setStringValue(PreferenceUtils.APP_FONT_SIZE, (minSize + ((maxSize - minSize) / 3)) + "");
			tvSize2.setText("小");
		}
		tvColumnPortaitCount.setText(PreferenceUtils.getInstance().getStringValue(PreferenceUtils.APP_COLUMN_COUNT_PORTRAIT));
		tvColumnLandscapeCount.setText(PreferenceUtils.getInstance().getStringValue(PreferenceUtils.APP_COLUMN_COUNT_LANDSCAPE));
		tvAppPaddingSize.setText(PreferenceUtils.getInstance().getStringValue(PreferenceUtils.APP_PADDING_SIZE));
		tvFloatSideWidthValue.setText(PreferenceUtils.getInstance().getStringValue(PreferenceUtils.FLOAT_SIDE_WIDTH));
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		initData();
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.layout_font_size:
			{
				new DialogSettingFontSize(this, new DialogListenser()
				{
					@Override
					public void onOkClick()
					{
						initData();
					}

					@Override
					public void onCancelClick()
					{
					}
				}).show();
				break;
			}
			case R.id.layout_column_portrait:
			{
				new DialogSettingColumnPortrait(this, new DialogListenser()
				{
					@Override
					public void onOkClick()
					{
						initData();
					}

					@Override
					public void onCancelClick()
					{
					}
				}).show();
				break;
			}
			case R.id.layout_column_landscape:
			{
				new DialogSettingColumnLandscape(this, new DialogListenser()
				{
					@Override
					public void onOkClick()
					{
						initData();
					}

					@Override
					public void onCancelClick()
					{
					}
				}).show();
				break;
			}
			case R.id.layout_app_padding_size:
			{
				new DialogSettingAppPaddingSize(this, new DialogListenser()
				{
					@Override
					public void onOkClick()
					{
						initData();
					}

					@Override
					public void onCancelClick()
					{
					}
				}).show();
				break;
			}
			case R.id.layout_float_side_width:
			{
				new DialogSettingSideWidth(this, new DialogListenser()
				{
					@Override
					public void onOkClick()
					{
						initData();
					}

					@Override
					public void onCancelClick()
					{
					}
				}, getString(R.string.pref_title_float_side_width)).show();
				break;
			}
			case R.id.layout_show_notification:
			case R.id.app_setting_show_notification_value:
			{
				if (PreferenceUtils.getInstance().getBooleanValue(PreferenceUtils.IS_SHOW_NOTIFICATION))
				{
					PreferenceUtils.getInstance().setBooleanValue(PreferenceUtils.IS_SHOW_NOTIFICATION, false);
					ivShowNotification.setImageResource(R.drawable.btn_nocheck);
					//					if (ActivityMain.getInstance() != null)
					//					{
					//						ActivityMain.getInstance().mCustomerNotification.cancelAll();
					//						ActivityMain.getInstance().stopService(new Intent(this, NotificationService.class));
					//					}
					stopService(new Intent(this, NotificationService.class));
				}
				else
				{
					PreferenceUtils.getInstance().setBooleanValue(PreferenceUtils.IS_SHOW_NOTIFICATION, true);
					ivShowNotification.setImageResource(R.drawable.btn_checked);
					EventBus.getDefault().post(new AppEvent(AppEvent.GET_RUNNING_APP, null));
					startService(new Intent(this, NotificationService.class));
				}
				break;
			}
			case R.id.layout_show_ball:
			{
				if (AppFactory.isCompatible(23))
				{
					ActivitySettingsUiPermissionsDispatcher.showFloatBallWithPermissionCheck(ActivitySettingsUi.this);
				}
				else
				{
					showFloatBall();
				}
				break;
			}
			case R.id.app_setting_show_ball_value:
			{
				if (AppFactory.isCompatible(23))
				{
					ActivitySettingsUiPermissionsDispatcher.showFloatBallValueWithPermissionCheck(ActivitySettingsUi.this);
				}
				else
				{
					showFloatBallValue();
				}
				break;
			}
			case R.id.layout_show_disable_dialog:
			case R.id.app_setting_show_disable_dialog_value:
			{
				if (PreferenceUtils.getInstance().getBooleanValue(PreferenceUtils.SHOW_DISABLE_DIALOG))
				{
					PreferenceUtils.getInstance().setBooleanValue(PreferenceUtils.SHOW_DISABLE_DIALOG, false);
					ivShowDisableDialog.setImageResource(R.drawable.btn_nocheck);
				}
				else
				{
					PreferenceUtils.getInstance().setBooleanValue(PreferenceUtils.SHOW_DISABLE_DIALOG, true);
					ivShowDisableDialog.setImageResource(R.drawable.btn_checked);
				}
				break;
			}
			case R.id.layout_show_fragments:
			{
				new DialogSettingShowFragments(this, new DialogListenser()
				{
					@Override
					public void onOkClick()
					{
						ToastView.showCenterToast(ActivitySettingsUi.this, "设置成功，重新启动应用后生效！");
						ActivitySettingsUi.this.finish();
						if (ActivityMain.getInstance() != null)
						{
							ActivityMain.getInstance().exit();
						}
					}

					@Override
					public void onCancelClick()
					{
					}
				}).show();
				break;
			}
		}
	}

	@NeedsPermission({Manifest.permission.SYSTEM_ALERT_WINDOW})
	protected void showFloatBall()
	{
		// Already hold the SYSTEM_ALERT_WINDOW permission, do addview or something.     }
		new DialogSettingFloatBall(this, new DialogListenser()
		{
			@Override
			public void onOkClick()
			{
				if (PreferenceUtils.getInstance().getBooleanValue(PreferenceUtils.IS_SHOW_HOVER_BALL))
				{
					ivShowBall.setImageResource(R.drawable.btn_checked);
					if (ActivityMain.getInstance() != null)
					{
						if (ActivityMain.getInstance().getmFloatSide() != null)
						{
							ActivityMain.getInstance().getmFloatSide().clearTopWindow();
						}
						String type = PreferenceUtils.getInstance().getStringValue(PreferenceUtils.FLOAT_TYPE);
						if (type.equalsIgnoreCase(FloatType.FLOAT_BALL.getType()))
						{
							ActivityMain.getInstance().setmFloatSide(new FloatBall(ActivityMain.getInstance()));
						}
						else if (type.equalsIgnoreCase(FloatType.FLOAT_BOTTOM.getType()))
						{
							ActivityMain.getInstance().setmFloatSide(new FloatSideBottom(ActivityMain.getInstance()));
						}
						else if (type.equalsIgnoreCase(FloatType.FLOAT_RIGHT.getType()))
						{
							ActivityMain.getInstance().setmFloatSide(new FloatSideRight(ActivityMain.getInstance()));
						}
						ActivityMain.getInstance().getmFloatSide().showTopWindow();
					}
				}
				else
				{
					ivShowBall.setImageResource(R.drawable.btn_nocheck);
					if (ActivityMain.getInstance() != null)
					{
						if (ActivityMain.getInstance().getmFloatSide() != null)
						{
							ActivityMain.getInstance().getmFloatSide().clearTopWindow();
						}
					}
				}
			}

			@Override
			public void onCancelClick()
			{
			}
		}).show();
	}

	@NeedsPermission({Manifest.permission.SYSTEM_ALERT_WINDOW})
	protected void showFloatBallValue()
	{
		if (PreferenceUtils.getInstance().getBooleanValue(PreferenceUtils.IS_SHOW_HOVER_BALL))
		{
			PreferenceUtils.getInstance().setBooleanValue(PreferenceUtils.IS_SHOW_HOVER_BALL, false);
			ivShowBall.setImageResource(R.drawable.btn_nocheck);
			if (ActivityMain.getInstance() != null)
			{
				if (ActivityMain.getInstance().getmFloatSide() != null)
				{
					ActivityMain.getInstance().getmFloatSide().clearTopWindow();
				}
			}
		}
		else
		{
			PreferenceUtils.getInstance().setBooleanValue(PreferenceUtils.IS_SHOW_HOVER_BALL, true);
			ivShowBall.setImageResource(R.drawable.btn_checked);
			if (ActivityMain.getInstance() != null)
			{
				if (ActivityMain.getInstance().getmFloatSide() != null)
				{
					ActivityMain.getInstance().getmFloatSide().clearTopWindow();
				}
				String type = PreferenceUtils.getInstance().getStringValue(PreferenceUtils.FLOAT_TYPE);
				if (type.equalsIgnoreCase(FloatType.FLOAT_BALL.getType()))
				{
					ActivityMain.getInstance().setmFloatSide(new FloatBall(ActivityMain.getInstance()));
				}
				else if (type.equalsIgnoreCase(FloatType.FLOAT_BOTTOM.getType()))
				{
					ActivityMain.getInstance().setmFloatSide(new FloatSideBottom(ActivityMain.getInstance()));
				}
				else if (type.equalsIgnoreCase(FloatType.FLOAT_RIGHT.getType()))
				{
					ActivityMain.getInstance().setmFloatSide(new FloatSideRight(ActivityMain.getInstance()));
				}
				ActivityMain.getInstance().getmFloatSide().showTopWindow();
			}
		}
	}
}

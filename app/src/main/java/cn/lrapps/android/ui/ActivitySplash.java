/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.android.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;
import android.widget.Toast;

import com.lrcall.appuser.R;

import cn.lrapps.utils.apptools.AppFactory;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.PermissionUtils;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class ActivitySplash extends Activity
{
	private final int CHANGE_TEXT = 1000;
	private final int INIT_RESULT = 1001;
	//	private final String DATA_INIT_RESULT = "data.init.result";
	private View rootView;
	private TextView tvStart;
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
					startActivity(new Intent(ActivitySplash.this, ActivityMain.class));
					finish();
					break;
				}
				case CHANGE_TEXT:
				{
					String text = (String) msg.obj;
					tvStart.setText(text);
					break;
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		rootView = View.inflate(this, R.layout.activity_splash, null);
		setContentView(rootView);
		if (AppFactory.isCompatible(23))
		{
			ActivitySplashPermissionsDispatcher.initViewWithPermissionCheck(this);
		}
		else
		{
			initView2();
		}
		//		new Thread("splash")
		//		{
		//			@Override
		//			public void run()
		//			{
		//				super.run();
		//				String text = getString(R.string.app_name);
		//				final int tm = 250;
		//				if (!StringTools.isNull(text))
		//				{
		//					int size = text.length();
		//					for (int i = 0; i < size; i++)
		//					{
		//						try
		//						{
		//							Thread.sleep(tm);
		//						}
		//						catch (InterruptedException e)
		//						{
		//							e.printStackTrace();
		//						}
		//						finally
		//						{
		//							//							Message msg = Message.obtain();
		//							//							msg.obj = text.substring(0, i + 1);
		//							//							msg.what = CHANGE_TEXT;
		//							//							mHandler.sendMessage(msg);
		//						}
		//					}
		//					try
		//					{
		//						Thread.sleep(tm);
		//					}
		//					catch (InterruptedException e)
		//					{
		//						e.printStackTrace();
		//					}
		//					finally
		//					{
		//						mHandler.sendEmptyMessage(INIT_RESULT);
		//					}
		//				}
		//				else
		//				{
		//					try
		//					{
		//						Thread.sleep(tm * 4);
		//					}
		//					catch (InterruptedException e)
		//					{
		//						e.printStackTrace();
		//					}
		//					finally
		//					{
		//						mHandler.sendEmptyMessage(INIT_RESULT);
		//					}
		//				}
		//			}
		//		}.start();
	}

	@NeedsPermission({Manifest.permission.WRITE_EXTERNAL_STORAGE})
	protected void initView()
	{
		//		ActivitySplashPermissionsDispatcher.initView2WithCheck(this);
		initView2();
	}

	@OnPermissionDenied({Manifest.permission.WRITE_EXTERNAL_STORAGE})
	protected void initViewDenied()
	{
		Toast.makeText(this, "您拒绝了应用所需的权限，应用将不能工作！", Toast.LENGTH_LONG).show();
		finish();
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
	{
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		// NOTE: delegate the permission handling to generated method
		ActivitySplashPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
		if (PermissionUtils.verifyPermissions(grantResults))
		{
			mHandler.sendEmptyMessage(INIT_RESULT);
		}
		else
		{
			initViewDenied();
		}
	}

	//	@NeedsPermission({Manifest.permission.SYSTEM_ALERT_WINDOW})
	//Manifest.permission.SYSTEM_ALERT_WINDOW, Manifest.permission.INSTALL_PACKAGES, Manifest.permission.DELETE_PACKAGES, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS
	protected void initView2()
	{
		tvStart = (TextView) findViewById(R.id.tv_start);
		AlphaAnimation aa = new AlphaAnimation(0.5f, 1.0f);
		aa.setDuration(100);
		rootView.setAnimation(aa);
		aa.setAnimationListener(new Animation.AnimationListener()
		{
			@Override
			public void onAnimationStart(Animation animation)
			{
			}

			@Override
			public void onAnimationRepeat(Animation animation)
			{
			}

			@Override
			public void onAnimationEnd(Animation animation)
			{
				mHandler.sendEmptyMessage(INIT_RESULT);
			}
		});
	}
}

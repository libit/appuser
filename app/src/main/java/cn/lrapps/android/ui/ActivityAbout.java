/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.android.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import cn.lrapps.android.ui.dialog.DialogShowPic;
import com.lrcall.appuser.R;
import cn.lrapps.services.ApiConfig;

public class ActivityAbout extends MyBaseActivity implements View.OnClickListener
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		viewInit();
	}

	@Override
	protected void viewInit()
	{
		super.viewInit();
		setBackButton();
		findViewById(R.id.layout_wechart).setOnClickListener(this);
		findViewById(R.id.layout_qq_group).setOnClickListener(this);
		findViewById(R.id.layout_alipay).setOnClickListener(this);
		findViewById(R.id.layout_tutorial).setOnClickListener(this);
		findViewById(R.id.layout_advice).setOnClickListener(this);
		findViewById(R.id.layout_more_app).setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.menu_activity_about, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		int id = item.getItemId();
		if (id == R.id.action_online)
		{
			ActivityWebView.startWebActivity(this, getString(R.string.action_about), ApiConfig.getAboutUrl());
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.layout_wechart:
			{
				DialogShowPic dialogShowPic = new DialogShowPic(this, null, R.drawable.wechart_qrcode, getString(R.string.wechart));
				dialogShowPic.show();
				break;
			}
			case R.id.layout_qq_group:
			{
				DialogShowPic dialogShowPic = new DialogShowPic(this, null, R.drawable.qq_group_qrcode, getString(R.string.qq_group));
				dialogShowPic.show();
				break;
			}
			case R.id.layout_alipay:
			{
				DialogShowPic dialogShowPic = new DialogShowPic(this, null, R.drawable.alipay_qrcode, getString(R.string.alipay));
				dialogShowPic.show();
				break;
			}
			case R.id.layout_tutorial:
			{
				ActivityWebView.startWebActivity(this, getString(R.string.action_tutorial), ApiConfig.getTutorialUrl());
				break;
			}
			case R.id.layout_advice:
			{
				//				ActivityWebView.startWebActivity(this, getString(R.string.action_advice), ApiConfig.getAdviceUrl());
				//				if (StringTools.isNull(PreferenceUtils.getInstance().getUserId()) || StringTools.isNull(PreferenceUtils.getInstance().getSessionId()))
				//				{
				//					startActivityForResult(new Intent(this, ActivityLogin.class), ConstValues.REQUEST_LOGIN_ADVICE);
				//				}
				//				else
				{
					startActivity(new Intent(this, ActivityAdvice.class));
				}
				break;
			}
			case R.id.layout_more_app:
			{
				ActivityWebView.startWebActivity(this, getString(R.string.action_more_app), ApiConfig.getMoreAppUrl());
				break;
			}
		}
	}
}

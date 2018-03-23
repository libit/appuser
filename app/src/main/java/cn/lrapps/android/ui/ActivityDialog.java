/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.android.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import com.lrcall.appuser.R;
import cn.lrapps.utils.ConstValues;
import cn.lrapps.utils.StringTools;
import cn.lrapps.utils.apptools.AppFactory;

public class ActivityDialog extends Activity implements OnClickListener
{
	private TextView tvTitle, tvMessage;
	private String packageName;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_dialog);
		String title = "", msg = "";
		Bundle bundle = getIntent().getExtras();
		if (bundle != null)
		{
			title = bundle.getString(ConstValues.DATA_TITLE);
			msg = bundle.getString(ConstValues.DATA_CONTENT);
			packageName = bundle.getString(ConstValues.DATA_PACKAGE_NAME);
		}
		initView();
		if (!StringTools.isNull(title))
		{
			tvTitle.setText(title);
		}
		if (!StringTools.isNull(msg))
		{
			tvMessage.setText(msg);
		}
	}

	private void initView()
	{
		tvTitle = (TextView) findViewById(R.id.tv_title);
		tvMessage = (TextView) findViewById(R.id.tv_message);
		findViewById(R.id.dialog_btn_ok).setOnClickListener(this);
		findViewById(R.id.dialog_btn_cancel).setOnClickListener(this);
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.dialog_btn_ok:
			{
				if (!StringTools.isNull(packageName))
				{
					AppFactory.getInstance().disableApp(packageName);
				}
				//				ActivityWebView.startWebActivity(this, tvTitle.getText().toString(), url, true);
				finish();
				break;
			}
			case R.id.dialog_btn_cancel:
			{
				finish();
				break;
			}
		}
	}
}

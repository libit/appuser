/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.android.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.SeekBar;
import android.widget.TextView;

import com.lrcall.appuser.R;
import cn.lrapps.utils.PreferenceUtils;

public class DialogSettingAppPaddingSize extends Dialog implements OnClickListener, SeekBar.OnSeekBarChangeListener
{
	private static final int MAX_SIZE = 30;
	private static final int MIN_SIZE = 0;
	private TextView tvTitle, tvMessage;
	private SeekBar sbSize;
	private int textSize;
	private final DialogListenser listenser;

	public DialogSettingAppPaddingSize(Context context, DialogListenser listenser)
	{
		super(context, R.style.MyDialog);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setCancelable(false);
		this.listenser = listenser;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_setting_column);
		initView();
	}

	private void initView()
	{
		tvTitle = (TextView) findViewById(R.id.tv_dialog_title);
		tvMessage = (TextView) findViewById(R.id.tv_count);
		sbSize = (SeekBar) findViewById(R.id.sb_count);
		tvTitle.setText(R.string.pref_title_app_setting_padding_size);
		sbSize.setOnSeekBarChangeListener(this);
		sbSize.setMax(MAX_SIZE - MIN_SIZE);
		findViewById(R.id.dialog_btn_ok).setOnClickListener(this);
		findViewById(R.id.dialog_btn_cancel).setOnClickListener(this);
		textSize = PreferenceUtils.getInstance().getIntegerValue(PreferenceUtils.APP_PADDING_SIZE);
		int currentCount = textSize - MIN_SIZE;
		sbSize.setProgress(currentCount);
		onProgressChanged(sbSize, currentCount, false);
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.dialog_btn_ok:
			{
				PreferenceUtils.getInstance().setStringValue(PreferenceUtils.APP_PADDING_SIZE, textSize + "");
				if (listenser != null)
				{
					listenser.onOkClick();
				}
				dismiss();
				break;
			}
			case R.id.dialog_btn_cancel:
			{
				if (listenser != null)
				{
					listenser.onCancelClick();
				}
				dismiss();
				break;
			}
		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
	{
		if (seekBar == sbSize)
		{
			textSize = progress + MIN_SIZE;
			tvMessage.setText("当前大小：" + textSize);
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar)
	{
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar)
	{
	}
}

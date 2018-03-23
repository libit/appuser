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

public class DialogSettingFontSize extends Dialog implements OnClickListener, SeekBar.OnSeekBarChangeListener
{
	public static final int MAX_SIZE = 24;
	public static final int MIN_SIZE = 8;
	private TextView tvMessage, tvSample;
	private SeekBar sbSize;
	private int textSize;
	private final DialogListenser listenser;

	public DialogSettingFontSize(Context context, DialogListenser listenser)
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
		setContentView(R.layout.dialog_setting_font_size);
		initView();
	}

	private void initView()
	{
		sbSize = (SeekBar) findViewById(R.id.sb_size);
		tvMessage = (TextView) findViewById(R.id.tv_size);
		tvSample = (TextView) findViewById(R.id.tv_sample);
		sbSize.setOnSeekBarChangeListener(this);
		sbSize.setMax(MAX_SIZE - MIN_SIZE);
		findViewById(R.id.dialog_btn_ok).setOnClickListener(this);
		findViewById(R.id.dialog_btn_cancel).setOnClickListener(this);
		textSize = PreferenceUtils.getInstance().getIntegerValue(PreferenceUtils.APP_FONT_SIZE);
		int currentSize = textSize - MIN_SIZE;
		sbSize.setProgress(currentSize);
		onProgressChanged(sbSize, currentSize, false);
		//        tvMessage.setText("当前大小：" + currentSize);
		//        tvSample.setTextSize(Integer.parseInt(textSize));
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.dialog_btn_ok:
			{
				PreferenceUtils.getInstance().setStringValue(PreferenceUtils.APP_FONT_SIZE, textSize + "");
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
			tvSample.setTextSize(textSize);
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

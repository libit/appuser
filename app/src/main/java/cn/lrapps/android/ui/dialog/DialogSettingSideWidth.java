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

public class DialogSettingSideWidth extends Dialog implements OnClickListener, SeekBar.OnSeekBarChangeListener
{
	private static final int MAX_SIZE = 50;
	private static final int MIN_SIZE = 1;
	private TextView tvTitle, tvMessage;
	private SeekBar sbCount;
	private int textCount;
	private final DialogListenser listenser;
	private String title;

	public DialogSettingSideWidth(Context context, DialogListenser listenser, String title)
	{
		super(context, R.style.MyDialog);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setCancelable(false);
		this.listenser = listenser;
		this.title = title;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_setting_side_padding);
		initView();
	}

	private void initView()
	{
		tvTitle = (TextView) findViewById(R.id.tv_dialog_title);
		tvMessage = (TextView) findViewById(R.id.tv_count);
		sbCount = (SeekBar) findViewById(R.id.sb_count);
		tvTitle.setText(title);
		sbCount.setOnSeekBarChangeListener(this);
		sbCount.setMax(MAX_SIZE - MIN_SIZE);
		findViewById(R.id.dialog_btn_ok).setOnClickListener(this);
		findViewById(R.id.dialog_btn_cancel).setOnClickListener(this);
		textCount = PreferenceUtils.getInstance().getIntegerValue(PreferenceUtils.FLOAT_SIDE_WIDTH);
		int currentCount = textCount - MIN_SIZE;
		sbCount.setProgress(currentCount);
		onProgressChanged(sbCount, currentCount, false);
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.dialog_btn_ok:
			{
				PreferenceUtils.getInstance().setStringValue(PreferenceUtils.FLOAT_SIDE_WIDTH, textCount + "");
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
		if (seekBar == sbCount)
		{
			textCount = progress + MIN_SIZE;
			tvMessage.setText("当前距离：" + textCount);
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

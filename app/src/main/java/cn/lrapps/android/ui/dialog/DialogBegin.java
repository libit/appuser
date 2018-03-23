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
import android.widget.CheckBox;

import com.lrcall.appuser.R;

public class DialogBegin extends Dialog implements OnClickListener
{
	private CheckBox cbAccept;
	private final OnBeginListener listener;

	public DialogBegin(Context context, OnBeginListener l)
	{
		// super(context);
		super(context, R.style.MyDialog);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setCancelable(false);
		listener = l;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_begin);
		initView();
	}

	private void initView()
	{
		cbAccept = (CheckBox) findViewById(R.id.cb_accept);
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
				if (listener != null)
				{
					boolean accept = cbAccept.isChecked();
					listener.onOkClick(accept);
				}
				dismiss();
				break;
			}
			case R.id.dialog_btn_cancel:
			{
				if (listener != null)
				{
					listener.onCancelClick();
				}
				dismiss();
				break;
			}
		}
	}

	public interface OnBeginListener
	{
		void onOkClick(boolean accept);

		void onCancelClick();
	}
}

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
import android.widget.Button;
import android.widget.EditText;

import com.lrcall.appuser.R;
import cn.lrapps.utils.StringTools;
import cn.lrapps.utils.viewtools.DisplayTools;

public class DialogSetDpi extends Dialog implements OnClickListener
{
	private EditText etContent;
	private Button btnOk, btnCancel;
	private final LibitDialogListener listener;

	public DialogSetDpi(Context context, LibitDialogListener l)
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
		setContentView(R.layout.dialog_set_dpi);
		initView();
	}

	private void initView()
	{
		etContent = (EditText) findViewById(R.id.et_content);
		btnOk = (Button) findViewById(R.id.dialog_btn_ok);
		btnOk.setOnClickListener(this);
		btnCancel = (Button) findViewById(R.id.dialog_btn_cancel);
		btnCancel.setOnClickListener(this);
		etContent.setText(DisplayTools.getDpi(getContext()) + "");
		etContent.setSelection(etContent.getText().length());
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
					Integer value = null;
					String content = String.valueOf(etContent.getText());
					if (!StringTools.isNull(content))
					{
						try
						{
							value = Integer.parseInt(content);
						}
						catch (Exception e)
						{
						}
					}
					listener.onOkClick(value);
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

	public interface LibitDialogListener
	{
		void onOkClick(Integer value);

		void onCancelClick();
	}
}

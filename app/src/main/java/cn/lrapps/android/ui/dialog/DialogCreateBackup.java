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
import android.widget.EditText;

import com.lrcall.appuser.R;

public class DialogCreateBackup extends Dialog implements OnClickListener
{
	private EditText etComment;
	private CheckBox cbUpdateServer;
	private final OnCreateBackupListenser listenser;
	private final boolean showCb;

	public DialogCreateBackup(Context context, OnCreateBackupListenser listenser, boolean showCb)
	{
		super(context, R.style.MyDialog);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setCancelable(false);
		this.listenser = listenser;
		this.showCb = showCb;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_create_backup);
		initView();
		if (!showCb)
		{
			cbUpdateServer.setChecked(false);
			cbUpdateServer.setVisibility(View.GONE);
		}
	}

	private void initView()
	{
		etComment = (EditText) findViewById(R.id.et_comment);
		cbUpdateServer = (CheckBox) findViewById(R.id.cb_backup_server);
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
				if (listenser != null)
				{
					listenser.onOkClick(etComment.getText().toString(), cbUpdateServer.isChecked());
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

	public interface OnCreateBackupListenser
	{
		void onOkClick(String comment, boolean updateServer);

		void onCancelClick();
	}
}

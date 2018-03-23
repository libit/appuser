/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.android.ui.dialog;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lrcall.appuser.R;
import cn.lrapps.utils.StringTools;

public class DialogShowPic extends Dialog implements OnClickListener
{
	private int resId;
	private String msg;
	private ImageView ivPic;
	private TextView tvMessage;
	private Button btnOk;
	private final DialogCommon.LibitDialogListener listener;

	public DialogShowPic(Context context, DialogCommon.LibitDialogListener l, int resId, String msg)
	{
		// super(context);
		super(context, R.style.MyDialog);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setCancelable(false);
		listener = l;
		this.resId = resId;
		this.msg = msg;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_show_pic);
		initView();
	}

	private void initView()
	{
		ivPic = (ImageView) findViewById(R.id.iv_pic);
		tvMessage = (TextView) findViewById(R.id.tv_message);
		btnOk = (Button) findViewById(R.id.dialog_btn_ok);
		btnOk.setOnClickListener(this);
		ivPic.setImageResource(resId);
		tvMessage.setText(msg);
		tvMessage.setOnClickListener(this);
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.tv_message:
			{
				String copy = msg;
				int index = msg.indexOf("\n");
				if (index > 0)
				{
					copy = msg.substring(index + 1);
				}
				ClipboardManager cm = (ClipboardManager) this.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
				ClipData mClipData = ClipData.newPlainText(copy, copy);
				cm.setPrimaryClip(mClipData);
				String tips = "";
				if (msg.contains("公众号"))
				{
					tips = "微信公众号已复制到剪贴板。";
				}
				else if (msg.contains("群"))
				{
					tips = "QQ群号已复制到剪贴板。";
				}
				else if (msg.contains("支付宝"))
				{
					tips = "支付宝账号已复制到剪贴板。";
				}
				if (!StringTools.isNull(tips))
				{
					Toast.makeText(this.getContext(), tips, Toast.LENGTH_LONG).show();
				}
				break;
			}
			case R.id.dialog_btn_ok:
			{
				if (listener != null)
				{
					listener.onOkClick();
				}
				dismiss();
				break;
			}
		}
	}
}

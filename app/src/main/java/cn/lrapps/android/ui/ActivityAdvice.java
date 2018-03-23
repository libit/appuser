/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.android.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.androidquery.callback.AjaxStatus;
import cn.lrapps.android.ui.customer.ToastView;
import com.lrcall.appuser.R;
import cn.lrapps.services.AdviceService;
import cn.lrapps.services.ApiConfig;
import cn.lrapps.services.IAjaxDataResponse;
import cn.lrapps.models.ReturnInfo;
import cn.lrapps.utils.GsonTools;
import cn.lrapps.utils.StringTools;

public class ActivityAdvice extends MyBaseActivity implements View.OnClickListener, IAjaxDataResponse
{
	private static final String CONTENT = "content";
	private EditText etNumber, etEmail, etContent;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_advice);
		viewInit();
	}

	@Override
	protected void viewInit()
	{
		super.viewInit();
		setBackButton();
		etNumber = (EditText) findViewById(R.id.et_number);
		etEmail = (EditText) findViewById(R.id.et_email);
		etContent = (EditText) findViewById(R.id.et_content);
		findViewById(R.id.btn_ok).setOnClickListener(this);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		outState.putCharSequence(CONTENT, etContent.getText().toString());
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState)
	{
		String content = savedInstanceState.getString(CONTENT);
		etContent.setText(content);
		super.onRestoreInstanceState(savedInstanceState);
	}
	//	@Override
	//	protected void onPause()
	//	{
	//		super.onPause();
	//		Bundle bundle = new Bundle();
	//		bundle.putCharSequence(CONTENT, etContent.getText().toString());
	//		onSaveInstanceState(bundle);
	//	}
	//
	//	@Override
	//	protected void onResume()
	//	{
	//		super.onResume();
	//		Bundle bundle = new Bundle();
	//		onRestoreInstanceState(bundle);
	//		String content = bundle.getString(CONTENT);
	//		etContent.setText(content);
	//	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.btn_ok:
			{
				String number = etNumber.getText().toString();
				String email = etEmail.getText().toString();
				String content = etContent.getText().toString();
				if (!StringTools.isNull(number) && !StringTools.isChinaMobilePhoneNumber(number))
				{
					ToastView.showCenterToast(this, R.drawable.ic_do_fail, "手机号码格式不正确，请重新输入！");
					etNumber.requestFocus();
					return;
				}
				if (!StringTools.isNull(email) && !StringTools.isEmail(email))
				{
					ToastView.showCenterToast(this, R.drawable.ic_do_fail, "邮箱格式不正确，请重新输入！");
					etEmail.requestFocus();
					return;
				}
				if (StringTools.isNull(content))
				{
					ToastView.showCenterToast(this, R.drawable.ic_do_fail, "请输入您的建议！");
					etContent.requestFocus();
					return;
				}
				AdviceService adviceService = new AdviceService(this);
				adviceService.addDataResponse(this);
				adviceService.submitAdvice(number, email, content, "正在提交...", false);
				break;
			}
		}
	}

	@Override
	public boolean onAjaxDataResponse(String url, String result, AjaxStatus status)
	{
		if (url.endsWith(ApiConfig.SUBMIT_ADVICE))
		{
			ReturnInfo returnInfo = GsonTools.getReturnInfo(result);
			if (ReturnInfo.isSuccess(returnInfo))
			{
				ToastView.showCenterToast(this, R.drawable.ic_done, "意见反馈已提交！");
				etContent.setText("");
			}
			else
			{
				String msg = "意见反馈提交失败：" + result;
				if (returnInfo != null)
				{
					msg = "意见反馈提交失败：" + returnInfo.getMsg();
				}
				ToastView.showCenterToast(this, R.drawable.ic_do_fail, msg);
			}
		}
		return true;
	}
}

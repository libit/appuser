/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.android.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.androidquery.callback.AjaxStatus;
import cn.lrapps.android.ui.customer.ToastView;
import com.lrcall.appuser.R;
import cn.lrapps.services.ApiConfig;
import cn.lrapps.services.IAjaxDataResponse;
import cn.lrapps.services.UserService;
import cn.lrapps.models.ReturnInfo;
import cn.lrapps.utils.ConstValues;
import cn.lrapps.utils.GsonTools;
import cn.lrapps.utils.PreferenceUtils;
import cn.lrapps.utils.StringTools;

public class ActivityLogin extends MyBaseActivity implements View.OnClickListener, IAjaxDataResponse
{
	private EditText etUsername, etPassword, etCode;
	private ImageView ivCode;
	private UserService mUserService;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		mUserService = new UserService(this);
		mUserService.addDataResponse(this);
		viewInit();
		//		mUserService.getAuthCode(null, true);
	}

	@Override
	protected void viewInit()
	{
		super.viewInit();
		setBackButton();
		etUsername = (EditText) findViewById(R.id.et_username);
		etPassword = (EditText) findViewById(R.id.et_password);
		etCode = (EditText) findViewById(R.id.et_code);
		ivCode = (ImageView) findViewById(R.id.iv_code);
		findViewById(R.id.btn_login).setOnClickListener(this);
		findViewById(R.id.btn_register).setOnClickListener(this);
		//		ivCode.setImageURI(Uri.parse("http://192.168.168.4:8080/LR/user/getAuthCode"));
		//		PicService.ajaxGetPic(ivCode, "http://192.168.168.4:8080/LR/user/getAuthCode", 0);
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		etUsername.setText(PreferenceUtils.getInstance().getUserId());
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.btn_login:
			{
				String username = etUsername.getText().toString();
				String password = etPassword.getText().toString();
				String code = etCode.getText().toString();
				if (StringTools.isNull(username))
				{
					ToastView.showCenterToast(this, R.drawable.ic_do_fail, "账号不能为空！");
					etUsername.requestFocus();
					return;
				}
				if (username.length() < 5 || username.length() > 16)
				{
					ToastView.showCenterToast(this, R.drawable.ic_do_fail, "账号位数不正确，请输入5-16位的账号！");
					etUsername.requestFocus();
					return;
				}
				if (StringTools.isNull(password))
				{
					ToastView.showCenterToast(this, R.drawable.ic_do_fail, "密码不能为空！");
					etPassword.requestFocus();
					return;
				}
				if (password.length() < 6 || password.length() > 16)
				{
					ToastView.showCenterToast(this, R.drawable.ic_do_fail, "密码位数不正确，请输入6-16位的密码！");
					etPassword.requestFocus();
					return;
				}
				mUserService.login(username, password, code, "正在登录...", true);
				break;
			}
			case R.id.btn_register:
			{
				startActivityForResult(new Intent(this, ActivityRegister.class), ConstValues.REQUEST_REGISTER);
				break;
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == ConstValues.REQUEST_REGISTER)
		{
			if (resultCode == ConstValues.RESULT_REGISTER_SUCCESS)
			{
				setResult(ConstValues.RESULT_LOGIN_SUCCESS);
				finish();
			}
			else
			{
				setResult(ConstValues.RESULT_LOGIN_ERROR);
				finish();
			}
		}
	}

	@Override
	public boolean onAjaxDataResponse(String url, String result, AjaxStatus status)
	{
		if (url.endsWith(ApiConfig.USER_GET_AUTH_CODE))
		{
		}
		else if (url.endsWith(ApiConfig.USER_LOGIN))
		{
			ReturnInfo returnInfo = GsonTools.getReturnInfo(result);
			if (ReturnInfo.isSuccess(returnInfo))
			{
				ToastView.showCenterToast(this, R.drawable.ic_done, "登录成功");
				setResult(ConstValues.RESULT_LOGIN_SUCCESS);
				finish();
			}
			else
			{
				setResult(ConstValues.RESULT_LOGIN_ERROR);
				String msg = result;
				if (returnInfo != null)
				{
					msg = returnInfo.getMsg();
				}
				ToastView.showCenterToast(this, R.drawable.ic_do_fail, msg);
			}
		}
		return true;
	}
}

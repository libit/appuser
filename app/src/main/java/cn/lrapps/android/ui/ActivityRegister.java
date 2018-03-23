/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.android.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.androidquery.callback.AjaxStatus;
import cn.lrapps.android.ui.customer.ToastView;
import com.lrcall.appuser.R;
import cn.lrapps.services.ApiConfig;
import cn.lrapps.services.IAjaxDataResponse;
import cn.lrapps.services.UserService;
import cn.lrapps.enums.SexType;
import cn.lrapps.models.ReturnInfo;
import cn.lrapps.utils.ConstValues;
import cn.lrapps.utils.GsonTools;
import cn.lrapps.utils.StringTools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityRegister extends MyBaseActivity implements View.OnClickListener, IAjaxDataResponse
{
	private EditText etUsername, etPassword, etNickname, etNumber, etCountry, etProvince, etCity, etAddress;
	private Spinner spinnerSex;
	private ArrayAdapter spinnerSexAdapter;
	private Map<String, Byte> sexMap = new HashMap<>();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		viewInit();
	}

	@Override
	protected void viewInit()
	{
		super.viewInit();
		setBackButton();
		etUsername = (EditText) findViewById(R.id.et_username);
		etPassword = (EditText) findViewById(R.id.et_password);
		etNickname = (EditText) findViewById(R.id.et_nickname);
		etNumber = (EditText) findViewById(R.id.et_number);
		etCountry = (EditText) findViewById(R.id.et_city);
		etProvince = (EditText) findViewById(R.id.et_city);
		etCity = (EditText) findViewById(R.id.et_city);
		etAddress = (EditText) findViewById(R.id.et_address);
		spinnerSex = (Spinner) findViewById(R.id.spinner_sex);
		findViewById(R.id.btn_register).setOnClickListener(this);
		final List<String> sexStringList = new ArrayList<>();
		sexStringList.add("男");
		sexStringList.add("女");
		sexMap.put("男", SexType.MALE.getSex());
		sexMap.put("女", SexType.FEMALE.getSex());
		spinnerSexAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, sexStringList);
		spinnerSexAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerSex.setAdapter(spinnerSexAdapter);
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.btn_register:
			{
				String username = etUsername.getText().toString();
				String password = etPassword.getText().toString();
				String nickname = etNickname.getText().toString();
				String number = etNumber.getText().toString();
				String country = etCountry.getText().toString();
				String province = etProvince.getText().toString();
				String city = etCity.getText().toString();
				String address = etAddress.getText().toString();
				Byte sex = sexMap.get(spinnerSex.getSelectedItem());
				String picUrl = null;
				Long birthday = null;
				String remark = null;
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
				if (StringTools.isNull(nickname))
				{
					ToastView.showCenterToast(this, R.drawable.ic_do_fail, "昵称不能为空！");
					etNickname.requestFocus();
					return;
				}
				if (StringTools.isNull(number))
				{
					ToastView.showCenterToast(this, R.drawable.ic_do_fail, "手机号码不能为空！");
					etNumber.requestFocus();
					return;
				}
				if (!StringTools.isChinaMobilePhoneNumber(number))
				{
					ToastView.showCenterToast(this, R.drawable.ic_do_fail, "手机号码格式不正确！");
					etNumber.requestFocus();
					return;
				}
				UserService userService = new UserService(this);
				userService.addDataResponse(this);
				userService.register(username, password, nickname, number, country, province, city, address, sex, picUrl, birthday, remark, "正在注册...", true);
				break;
			}
		}
	}

	@Override
	public boolean onAjaxDataResponse(String url, String result, AjaxStatus status)
	{
		if (url.endsWith(ApiConfig.USER_REGISTER))
		{
			ReturnInfo returnInfo = GsonTools.getReturnInfo(result);
			if (ReturnInfo.isSuccess(returnInfo))
			{
				ToastView.showCenterToast(this, R.drawable.ic_done, "恭喜您注册成功！");
				setResult(ConstValues.RESULT_REGISTER_SUCCESS);
				finish();
			}
			else
			{
				String msg = "";
				if (returnInfo != null)
				{
					msg = returnInfo.getMsg();
				}
				Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
				ToastView.showCenterToast(this, R.drawable.ic_do_fail, msg);
				//				setResult(ConstValues.RESULT_REGISTER_ERROR);
			}
		}
		return true;
	}
}

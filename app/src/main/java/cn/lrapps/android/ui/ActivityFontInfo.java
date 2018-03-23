/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.android.ui;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import cn.lrapps.android.ui.dialog.DialogCommon;
import cn.lrapps.android.ui.dialog.DialogFileList;
import com.lrcall.appuser.R;
import cn.lrapps.enums.SqlOrderType;
import cn.lrapps.models.ReturnInfo;
import cn.lrapps.utils.ConstValues;
import cn.lrapps.utils.GsonTools;
import cn.lrapps.utils.LogcatTools;
import cn.lrapps.utils.ShellUtils;
import cn.lrapps.utils.StringTools;
import cn.lrapps.utils.apptools.AppFactory;
import cn.lrapps.utils.filetools.FileSizeSortComparator;
import cn.lrapps.utils.filetools.FileTools;

import java.io.File;

import static cn.lrapps.utils.StringTools.getFileNameExt;
import static cn.lrapps.utils.StringTools.getFileNameWithoutExt;
import static cn.lrapps.utils.apptools.AppFactory.loadSystemPermissions;
import static cn.lrapps.utils.apptools.IAppService.convertResultToReturnInfo;

public class ActivityFontInfo extends MyBaseActivity implements View.OnClickListener
{
	private EditText etContent;
	private TextView tvFont12, tvFont16, tvFont20, tvFont24, tvFont28;
	private String mFontPath;
	private final String systemFontFolderPath = "/system/fonts/";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_font_info);
		mFontPath = getIntent().getStringExtra(ConstValues.DATA_FONT_URL);
		viewInit();
		LogcatTools.info("ActivityFontInfo", "mFontPath:" + mFontPath);
	}

	@Override
	protected void viewInit()
	{
		super.viewInit();
		setBackButton();
		etContent = (EditText) findViewById(R.id.et_content);
		tvFont12 = (TextView) findViewById(R.id.tv_font_size12);
		tvFont16 = (TextView) findViewById(R.id.tv_font_size16);
		tvFont20 = (TextView) findViewById(R.id.tv_font_size20);
		tvFont24 = (TextView) findViewById(R.id.tv_font_size24);
		tvFont28 = (TextView) findViewById(R.id.tv_font_size28);
		findViewById(R.id.btn_set).setOnClickListener(this);
		try
		{
			Typeface tf = Typeface.createFromFile(mFontPath);
			tvFont12.setTypeface(tf);
			tvFont16.setTypeface(tf);
			tvFont20.setTypeface(tf);
			tvFont24.setTypeface(tf);
			tvFont28.setTypeface(tf);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		etContent.addTextChangedListener(new TextWatcher()
		{
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after)
			{
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count)
			{
			}

			@Override
			public void afterTextChanged(Editable s)
			{
				String conent = etContent.getText().toString();
				if (!StringTools.isNull(conent))
				{
					tvFont12.setText(conent);
					tvFont16.setText(conent);
					tvFont20.setText(conent);
					tvFont24.setText(conent);
					tvFont28.setText(conent);
				}
			}
		});
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.btn_set:
			{
				DialogCommon dialogCommon = new DialogCommon(this, new DialogCommon.LibitDialogListener()
				{
					@Override
					public void onOkClick()
					{
						File rootFile = new File(systemFontFolderPath);
						DialogFileList dialogFileList = new DialogFileList(ActivityFontInfo.this, rootFile, null, new DialogFileList.IDialogChooseFile()
						{
							@Override
							public void onFileSelected(File file)
							{
								if (file.isDirectory())
								{
									Toast.makeText(ActivityFontInfo.this, "您选择的是目录！", Toast.LENGTH_LONG).show();
								}
								else
								{
									String systemFontPath = file.getPath();
									changeFont(systemFontPath);
								}
							}
						}, new FileSizeSortComparator(SqlOrderType.DESC.getType()));
						dialogFileList.show();
						dialogFileList.setDlgTitle("选择要替换的系统字体");
					}

					@Override
					public void onCancelClick()
					{
					}
				}, "提示", "替换系统字体有可能变砖，如果替换后不能正常显示字体，请重启手机，少数情况可能需要重启2次才能正常显示，确定要继续？（需要替换的字体一般为体积最大的那个）", true, false, true);
				dialogCommon.show();
				break;
			}
		}
	}

	private void changeFont(final String systemFontPath)
	{
		//systemFontPath="/system/fonts/" + fontFileName;
		if (StringTools.isNull(systemFontPath))
		{
			Toast.makeText(ActivityFontInfo.this, "要替换的系统字体路径为空！", Toast.LENGTH_LONG).show();
			return;
		}
		File file = new File(mFontPath);
		if (file.isDirectory())
		{
			Toast.makeText(ActivityFontInfo.this, "您选择的是目录！", Toast.LENGTH_LONG).show();
			return;
		}
		if (!StringTools.getFileNameExt(systemFontPath).toLowerCase().equals(getFileNameExt(file.getPath()).toLowerCase()))
		{
			Toast.makeText(ActivityFontInfo.this, "您选择的文件跟要替换的文件扩展名不一致！", Toast.LENGTH_LONG).show();
			return;
		}
		if (file.getAbsolutePath().contains(" "))
		{
			Toast.makeText(ActivityFontInfo.this, "路径包含空格，请去掉空格后再替换！", Toast.LENGTH_LONG).show();
			return;
		}
		//					String fontFileName = "NotoSansCJK-Regular.ttc";
		//先挂载为可读写
		ReturnInfo returnInfo = loadSystemPermissions();
		if (!ReturnInfo.isSuccess(returnInfo))
		{
			Toast.makeText(ActivityFontInfo.this, returnInfo.getMsg(), Toast.LENGTH_LONG).show();
			return;
		}
		//先备份字体文件
		File systemFontFile = new File(systemFontPath);
		boolean canExe = true;
		if (!systemFontFile.isFile())
		{
			canExe = false;
			Toast.makeText(ActivityFontInfo.this, "系统字体文件不存在！", Toast.LENGTH_LONG).show();
		}
		if (canExe)
		{
			String tmpFilePath = FileTools.getAppDir("tmp/fonts/system") + getFileNameWithoutExt(systemFontPath) + "." + getFileNameExt(systemFontPath);
			//先检查有没有系统字体，如果没有的话则备份系统字体
			File file1 = new File(tmpFilePath);
			if (file1 == null && file1.getParentFile() == null && !file1.getParentFile().exists())
			{
				file1.getParentFile().mkdirs();
			}
			else
			{
				tmpFilePath = FileTools.getAppDir("tmp/fonts/system") + getFileNameWithoutExt(systemFontPath) + "_" + systemFontFile.length() + "." + getFileNameExt(systemFontPath);
			}
			String cmd = "cat " + systemFontPath + " >" + tmpFilePath;
			ShellUtils.CommandResult commandResult = ShellUtils.execCommand(cmd, true, true);///system/fonts/
			LogcatTools.debug("changeFont", "cmd:" + cmd + ",commandResult:" + GsonTools.toJson(commandResult));
			returnInfo = convertResultToReturnInfo(commandResult, "备份替换的系统字体成功。", "备份替换的系统字体失败");
			if (ReturnInfo.isSuccess(returnInfo))
			{
				commandResult = ShellUtils.execCommand("cat " + file.getPath() + " >" + systemFontPath, true, true);//"/system/fonts/" + fontFileName
				returnInfo = convertResultToReturnInfo(commandResult, "替换系统字体成功。", "替换系统字体失败");
				if (ReturnInfo.isSuccess(returnInfo))
				{
					commandResult = ShellUtils.execCommand("chmod 644 " + systemFontPath, true, true);
					returnInfo = convertResultToReturnInfo(commandResult, "修改系统字体文件权限成功。", "修改系统字体文件权限失败");
					if (!ReturnInfo.isSuccess(returnInfo))
					{
						canExe = false;
						Toast.makeText(ActivityFontInfo.this, returnInfo.getMsg(), Toast.LENGTH_LONG).show();
					}
				}
				else
				{
					canExe = false;
					Toast.makeText(ActivityFontInfo.this, returnInfo.getMsg(), Toast.LENGTH_LONG).show();
				}
			}
			else
			{
				canExe = false;
				Toast.makeText(ActivityFontInfo.this, returnInfo.getMsg(), Toast.LENGTH_LONG).show();
			}
		}
		//完事后改回来
		returnInfo = AppFactory.closeSystemPermissions();
		if (!ReturnInfo.isSuccess(returnInfo))
		{
			Toast.makeText(ActivityFontInfo.this, returnInfo.getMsg(), Toast.LENGTH_LONG).show();
			return;
		}
		if (canExe)
		{
			if (ReturnInfo.isSuccess(returnInfo))
			{
				new DialogCommon(ActivityFontInfo.this, new DialogCommon.LibitDialogListener()
				{
					@Override
					public void onOkClick()
					{
						ShellUtils.CommandResult commandResult = ShellUtils.execCommand("reboot", true, true);
						ReturnInfo returnInfo = convertResultToReturnInfo(commandResult, "重启命令执行成功。", "重启命令执行失败");
						Toast.makeText(ActivityFontInfo.this, returnInfo.getMsg(), Toast.LENGTH_LONG).show();
					}

					@Override
					public void onCancelClick()
					{
					}
				}, "提示", "替换字体成功，是否重启手机？", true, false, true).show();
			}
			else
			{
				new DialogCommon(ActivityFontInfo.this, null, "提示", "替换系统字体失败。", true, false, true).show();
			}
		}
	}
}

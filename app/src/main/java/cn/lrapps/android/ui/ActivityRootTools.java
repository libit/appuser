/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.android.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import cn.lrapps.android.ui.adapter.WifiPwdListAdapter;
import cn.lrapps.android.ui.dialog.DialogCommon;
import cn.lrapps.android.ui.dialog.DialogFileList;
import cn.lrapps.android.ui.dialog.DialogSetDpi;
import cn.lrapps.android.ui.dialog.DialogWifiPwdList;
import com.lrcall.appuser.R;
import cn.lrapps.models.WifiInfo;
import cn.lrapps.enums.SqlOrderType;
import cn.lrapps.events.AppEvent;
import cn.lrapps.models.ReturnInfo;
import cn.lrapps.utils.GsonTools;
import cn.lrapps.utils.LogcatTools;
import cn.lrapps.utils.ShellUtils;
import cn.lrapps.utils.StringTools;
import cn.lrapps.utils.apptools.AppFactory;
import cn.lrapps.utils.apptools.SystemToolsFactory;
import cn.lrapps.utils.filetools.FileNameSortComparator;
import cn.lrapps.utils.filetools.FileSizeSortComparator;
import cn.lrapps.utils.filetools.FileTools;
import cn.lrapps.utils.viewtools.DisplayTools;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static cn.lrapps.utils.StringTools.getFileNameExt;
import static cn.lrapps.utils.StringTools.getFileNameWithoutExt;
import static cn.lrapps.utils.apptools.AppFactory.closeSystemPermissions;
import static cn.lrapps.utils.apptools.AppFactory.loadSystemPermissions;
import static cn.lrapps.utils.apptools.IAppService.convertResultToReturnInfo;

public class ActivityRootTools extends MyBaseActivity implements View.OnClickListener
{
	private static final String TAG = ActivityRootTools.class.getSimpleName();
	private TextView tvDpi2, tvDeviceName;
	private Handler mHandler = new Handler();
	private SystemToolsFactory systemTools = SystemToolsFactory.getInstance();
	private final String systemPropPath = "/system/build.prop";
	private final String systemWifiConfigPath = "/data/misc/wifi/wpa_supplicant.conf";
	private final String systemFontFolderPath = "/system/fonts/";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_root_tools);
		viewInit();
		if (!EventBus.getDefault().isRegistered(this))
		{
			EventBus.getDefault().register(this);
		}
	}

	@Override
	protected void onDestroy()
	{
		if (EventBus.getDefault().isRegistered(this))
		{
			EventBus.getDefault().unregister(this);
		}
		super.onDestroy();
	}

	@Subscribe
	public void onEventMainThread(final AppEvent event)
	{
		mHandler.post(new Thread()
		{
			@Override
			public void run()
			{
				super.run();
				if (event != null)
				{
					if (event.getType().equalsIgnoreCase(AppEvent.CLEAR_APP))
					{
						Toast.makeText(ActivityRootTools.this, event.getMsg(), Toast.LENGTH_LONG).show();
					}
				}
			}
		});
	}

	@Override
	public void viewInit()
	{
		super.viewInit();
		setBackButton();
		tvDpi2 = (TextView) findViewById(R.id.app_change_dpi2);
		tvDeviceName = (TextView) findViewById(R.id.app_change_device_name2);
		findViewById(R.id.layout_mount_system).setOnClickListener(this);
		findViewById(R.id.layout_unmount_system).setOnClickListener(this);
		findViewById(R.id.layout_remove_cm_point).setOnClickListener(this);
		findViewById(R.id.layout_change_font).setOnClickListener(this);
		findViewById(R.id.layout_preview_font).setOnClickListener(this);
		findViewById(R.id.layout_change_dpi).setOnClickListener(this);
		findViewById(R.id.layout_view_wifi_pwd).setOnClickListener(this);
		findViewById(R.id.layout_install_app_bat).setOnClickListener(this);
		findViewById(R.id.layout_uninstall_app_bat).setOnClickListener(this);
		String deviceName = systemTools.getDeviceName();
		tvDeviceName.setText(deviceName);
		//		String deviceName = AppFactory.getInstance().getOsName();
		//		if (StringTools.isNull(deviceName) || (!deviceName.contains("CM") && !deviceName.contains("cm") && !deviceName.contains("lineage")))
		//		{
		//			findViewById(R.id.layout_remove_cm_point).setVisibility(View.GONE);
		//		}
		//		String fontFileName = "NotoSansCJK-Regular.ttc";
		//		String tmpFilePath = FileTools.getAppDir("tmp/fonts/system") + "NotoSansCJK-Regular.ttc";
		//		ShellUtils.CommandResult commandResult = ShellUtils.execCommand("cat /system/fonts/" + fontFileName + " >" + tmpFilePath, true, true);
		//		LogcatTools.debug(TAG, "文件复制结果：" + GsonTools.toJson(commandResult));
		//		Typeface typeface = Typeface.createFromFile(tmpFilePath);
		//		typeface.getStyle();
	}

	private void initData()
	{
		tvDpi2.setText(DisplayTools.getDpi(this) + "");
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		initData();
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.layout_mount_system:
			{
				//先挂载为可读写
				ReturnInfo returnInfo = loadSystemPermissions();
				Toast.makeText(ActivityRootTools.this, returnInfo.getMsg(), Toast.LENGTH_LONG).show();
				break;
			}
			case R.id.layout_unmount_system:
			{
				//挂载为只读
				ReturnInfo returnInfo = closeSystemPermissions();
				Toast.makeText(ActivityRootTools.this, returnInfo.getMsg(), Toast.LENGTH_LONG).show();
				break;
			}
			case R.id.layout_remove_cm_point:
			{
				DialogCommon dialogCommon = new DialogCommon(this, new DialogCommon.LibitDialogListener()
				{
					@Override
					public void onOkClick()
					{
						String cmd = "settings put global captive_portal_detection_enabled 0";
						if (AppFactory.isCompatible(25))//N需要以下命令
						{
							cmd = "settings put global captive_portal_mode 0";
						}
						ShellUtils.CommandResult result = ShellUtils.execCommand(cmd, true, true);
						ReturnInfo returnInfo = convertResultToReturnInfo(result, cmd + "执行成功！", cmd + "执行失败");
						new DialogCommon(ActivityRootTools.this, null, "结果", returnInfo.getMsg(), true, false, false).show();
					}

					@Override
					public void onCancelClick()
					{
					}
				}, "提示", "是否去除CM感叹号？", true, false, true);
				dialogCommon.show();
				break;
			}
			case R.id.layout_preview_font:
			{
				startActivity(new Intent(this, ActivityFontManage.class));
				break;
			}
			case R.id.layout_change_font:
			{
				DialogCommon dialogCommon = new DialogCommon(this, new DialogCommon.LibitDialogListener()
				{
					@Override
					public void onOkClick()
					{
						//先挂载为可读写
						//						ReturnInfo returnInfo = loadSystemPermissions();
						//						if (!ReturnInfo.isSuccess(returnInfo))
						//						{
						//							Toast.makeText(ActivityRootTools.this, returnInfo.getMsg(), Toast.LENGTH_LONG).show();
						//							changeFont("/system/fonts/NotoSansCJK-Regular.ttc");
						//							return;
						//						}
						File rootFile = new File(systemFontFolderPath);
						DialogFileList dialogFileList = new DialogFileList(ActivityRootTools.this, rootFile, null, new DialogFileList.IDialogChooseFile()
						{
							@Override
							public void onFileSelected(File file)
							{
								if (file.isDirectory())
								{
									Toast.makeText(ActivityRootTools.this, "您选择的是目录！", Toast.LENGTH_LONG).show();
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
			case R.id.layout_change_dpi:
			{
				new DialogSetDpi(this, new DialogSetDpi.LibitDialogListener()
				{
					@Override
					public void onOkClick(Integer value)
					{
						if (value != null)
						{
							if (value < 200 || value > 600)
							{
								Toast.makeText(ActivityRootTools.this, "请输入200~600之间的值！", Toast.LENGTH_LONG).show();
								return;
							}
							String tmpFilePath = FileTools.getAppDir("tmp") + "build.prop";
							File tmpFile = new File(tmpFilePath);
							if (tmpFile == null && tmpFile.getParentFile() == null && !tmpFile.getParentFile().exists())
							{
								tmpFile.getParentFile().mkdirs();
							}
							ShellUtils.CommandResult commandResult = ShellUtils.execCommand("cat " + systemPropPath + " >" + tmpFilePath, true, true);
							ReturnInfo returnInfo = convertResultToReturnInfo(commandResult, "读取系统配置成功。", "读取系统配置失败。");
							if (!ReturnInfo.isSuccess(returnInfo))
							{
								Toast.makeText(ActivityRootTools.this, returnInfo.getMsg(), Toast.LENGTH_LONG).show();
								return;
							}
							//修改dpi
							String fileContent = FileTools.readFile(tmpFile);
							String src = "ro.sf.lcd_density=";
							int index = fileContent.indexOf(src);
							if (index > 0)
							{
								fileContent = fileContent.substring(0, index) + "ro.sf.lcd_density=" + value + fileContent.substring(index + src.length() + 3);
							}
							else
							{
								fileContent += "\nro.sf.lcd_density=" + value;
							}
							FileTools.writeFile(tmpFile, fileContent);
							//先挂载为可读写
							returnInfo = loadSystemPermissions();
							if (!ReturnInfo.isSuccess(returnInfo))
							{
								Toast.makeText(ActivityRootTools.this, returnInfo.getMsg(), Toast.LENGTH_LONG).show();
								return;
							}
							boolean isOk = false;
							commandResult = ShellUtils.execCommand("cat " + tmpFilePath + " >" + systemPropPath, true, true);
							returnInfo = convertResultToReturnInfo(commandResult, "复制文件到系统文件成功。", "复制文件到系统文件失败");
							if (ReturnInfo.isSuccess(returnInfo))
							{
								commandResult = ShellUtils.execCommand("chmod 644 " + systemPropPath, true, true);
								returnInfo = convertResultToReturnInfo(commandResult, "修改系统文件权限成功。", "修改系统文件权限失败");
								if (ReturnInfo.isSuccess(returnInfo))
								{
									isOk = true;
								}
								else
								{
									Toast.makeText(ActivityRootTools.this, returnInfo.getMsg(), Toast.LENGTH_LONG).show();
								}
							}
							else
							{
								Toast.makeText(ActivityRootTools.this, returnInfo.getMsg(), Toast.LENGTH_LONG).show();
							}
							//完事后改回来
							returnInfo = closeSystemPermissions();
							if (!ReturnInfo.isSuccess(returnInfo))
							{
								Toast.makeText(ActivityRootTools.this, returnInfo.getMsg(), Toast.LENGTH_LONG).show();
								return;
							}
							if (isOk)
							{
								new DialogCommon(ActivityRootTools.this, new DialogCommon.LibitDialogListener()
								{
									@Override
									public void onOkClick()
									{
										ShellUtils.CommandResult commandResult = ShellUtils.execCommand("reboot", true, true);
										LogcatTools.debug(TAG, "结果：" + GsonTools.toJson(commandResult));
									}

									@Override
									public void onCancelClick()
									{
									}
								}, "提示", "修改DPI成功，是否重启手机？", true, false, true).show();
							}
							else
							{
								new DialogCommon(ActivityRootTools.this, null, "提示", "修改DPI失败。", true, false, true).show();
							}
						}
					}

					@Override
					public void onCancelClick()
					{
					}
				}).show();
				break;
			}
			case R.id.layout_view_wifi_pwd:
			{
				String tmpFilePath = FileTools.getAppDir("tmp") + "wpa_supplicant.conf";
				File tmpFile = new File(tmpFilePath);
				if (tmpFile == null && tmpFile.getParentFile() == null && !tmpFile.getParentFile().exists())
				{
					tmpFile.getParentFile().mkdirs();
				}
				ShellUtils.CommandResult commandResult = ShellUtils.execCommand("cat " + systemWifiConfigPath + " >" + tmpFilePath, true, true);
				ReturnInfo returnInfo = convertResultToReturnInfo(commandResult, "读取WIFI信息成功。", "读取WIFI信息失败");
				if (!ReturnInfo.isSuccess(returnInfo))
				{
					Toast.makeText(ActivityRootTools.this, returnInfo.getMsg(), Toast.LENGTH_LONG).show();
					return;
				}
				if (tmpFile != null)
				{
					String fileContent = FileTools.readFile(tmpFile);
					String tmpOldFilePath = FileTools.getAppDir("tmp") + "wpa_supplicant.info";
					File oldFile = new File(tmpOldFilePath);
					if (oldFile == null && oldFile.getParentFile() == null && !oldFile.getParentFile().exists())
					{
						oldFile.getParentFile().mkdirs();
					}
					if (!StringTools.isNull(fileContent))
					{
						String oldFileContent = FileTools.readFile(oldFile);
						List<WifiInfo> wifiInfoList = GsonTools.getObjects(oldFileContent, new TypeToken<List<WifiInfo>>()
						{
						}.getType());
						if (wifiInfoList == null)
						{
							wifiInfoList = new ArrayList<>();
						}
						String network = StringTools.getValue(fileContent, "network=", "}");
						while (!StringTools.isNull(network))
						{
							String ssid = StringTools.getValue(network, "ssid=\"", "\"");
							String scanSsid = StringTools.getValue(network, "scan_ssid=", "\n");
							String bssid = StringTools.getValue(network, "bssid=", "\n");
							String psk = StringTools.getValue(network, "psk=\"", "\"");
							String keyMgmt = StringTools.getValue(network, "key_mgmt=", "\n");
							String priority = StringTools.getValue(network, "priority=", "\n");
							String disabled = StringTools.getValue(network, "disabled=", "\n");
							String idStr = StringTools.getValue(network, "id_str=", "\n");
							WifiInfo wifiInfo = new WifiInfo(ssid, scanSsid, bssid, psk, keyMgmt, priority, disabled, idStr, System.currentTimeMillis());
							boolean isExist = false;
							for (WifiInfo wifiInfo1 : wifiInfoList)
							{
								if (wifiInfo1.getSsid().equals(wifiInfo.getSsid()) && wifiInfo1.getPsk().equals(wifiInfo.getPsk()))
								{
									isExist = true;
								}
							}
							if (!isExist)
							{
								wifiInfoList.add(wifiInfo);
							}
							fileContent = fileContent.substring(fileContent.indexOf("network=") + 5);
							network = StringTools.getValue(fileContent, "network=", "}");
						}
						FileTools.writeFile(oldFile, GsonTools.toJson(wifiInfoList));
						DialogWifiPwdList dialogList = new DialogWifiPwdList(ActivityRootTools.this, new WifiPwdListAdapter(ActivityRootTools.this, wifiInfoList));
						dialogList.show();
					}
				}
				break;
			}
			case R.id.layout_install_app_bat:
			{
				startActivity(new Intent(this, ActivityInstallAppBat.class));
				break;
			}
			case R.id.layout_uninstall_app_bat:
			{
				startActivity(new Intent(this, ActivityUnInstallAppBat.class));
				break;
			}
		}
	}

	private void changeFont(final String systemFontPath)
	{
		//systemFontPath="/system/fonts/" + fontFileName;
		if (StringTools.isNull(systemFontPath))
		{
			Toast.makeText(ActivityRootTools.this, "要替换的系统字体路径为空！", Toast.LENGTH_LONG).show();
			return;
		}
		File rootFile = new File(FileTools.getUserDir(""));
		DialogFileList dialogFileList = new DialogFileList(this, rootFile, null, new DialogFileList.IDialogChooseFile()
		{
			@Override
			public void onFileSelected(File file)
			{
				if (file.isDirectory())
				{
					Toast.makeText(ActivityRootTools.this, "您选择的是目录！", Toast.LENGTH_LONG).show();
					return;
				}
				if (!StringTools.getFileNameExt(systemFontPath).toLowerCase().equals(StringTools.getFileNameExt(file.getPath()).toLowerCase()))
				{
					Toast.makeText(ActivityRootTools.this, "您选择的文件跟要替换的文件扩展名不一致！", Toast.LENGTH_LONG).show();
					return;
				}
				if (file.getAbsolutePath().contains(" "))
				{
					Toast.makeText(ActivityRootTools.this, "路径包含空格，请去掉空格后再替换！", Toast.LENGTH_LONG).show();
					return;
				}
				//					String fontFileName = "NotoSansCJK-Regular.ttc";
				//先挂载为可读写
				ReturnInfo returnInfo = loadSystemPermissions();
				if (!ReturnInfo.isSuccess(returnInfo))
				{
					Toast.makeText(ActivityRootTools.this, returnInfo.getMsg(), Toast.LENGTH_LONG).show();
					return;
				}
				//先备份字体文件
				File systemFontFile = new File(systemFontPath);
				boolean canExe = true;
				if (!systemFontFile.isFile())
				{
					canExe = false;
					Toast.makeText(ActivityRootTools.this, "系统字体文件不存在！", Toast.LENGTH_LONG).show();
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
								Toast.makeText(ActivityRootTools.this, returnInfo.getMsg(), Toast.LENGTH_LONG).show();
							}
						}
						else
						{
							canExe = false;
							Toast.makeText(ActivityRootTools.this, returnInfo.getMsg(), Toast.LENGTH_LONG).show();
						}
					}
					else
					{
						canExe = false;
						Toast.makeText(ActivityRootTools.this, returnInfo.getMsg(), Toast.LENGTH_LONG).show();
					}
				}
				//完事后改回来
				returnInfo = AppFactory.closeSystemPermissions();
				if (!ReturnInfo.isSuccess(returnInfo))
				{
					Toast.makeText(ActivityRootTools.this, returnInfo.getMsg(), Toast.LENGTH_LONG).show();
					return;
				}
				if (canExe)
				{
					if (ReturnInfo.isSuccess(returnInfo))
					{
						new DialogCommon(ActivityRootTools.this, new DialogCommon.LibitDialogListener()
						{
							@Override
							public void onOkClick()
							{
								ShellUtils.CommandResult commandResult = ShellUtils.execCommand("reboot", true, true);
								ReturnInfo returnInfo = convertResultToReturnInfo(commandResult, "重启命令执行成功。", "重启命令执行失败");
								Toast.makeText(ActivityRootTools.this, returnInfo.getMsg(), Toast.LENGTH_LONG).show();
							}

							@Override
							public void onCancelClick()
							{
							}
						}, "提示", "替换字体成功，是否重启手机？", true, false, true).show();
					}
					else
					{
						new DialogCommon(ActivityRootTools.this, null, "提示", "替换系统字体失败。", true, false, true).show();
					}
				}
			}
		}, new FileNameSortComparator(SqlOrderType.ASC.getType()));
		dialogFileList.show();
		dialogFileList.setDlgTitle("选择您的字体文件");
	}
}

/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.android.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import cn.lrapps.android.ui.adapter.ChooseAppListAdapter;
import cn.lrapps.android.ui.dialog.DialogCommon;
import cn.lrapps.android.ui.dialog.DialogFileList;
import com.lrcall.appuser.R;
import cn.lrapps.models.AppInfo;
import cn.lrapps.utils.LogcatTools;
import cn.lrapps.utils.StringTools;
import cn.lrapps.utils.apptools.AppFactory;
import cn.lrapps.utils.filetools.FileTools;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 批量安装应用
 */
public class ActivityInstallAppBat extends MyBaseActivity implements View.OnClickListener, ChooseAppListAdapter.IChooseAppListAdapterItemClicked
{
	private final int INIT_RESULT = 1110;
	private final int INSTALL_RESULT = 1111;
	private final int DEL_RESULT = 1112;
	private ListView lvApps;
	private TextView tvPath;
	private List<AppInfo> appInfos;
	private String scanPath = "";
	private boolean isExiting = false;
	private ChooseAppListAdapter mChooseAppListAdapter;
	private final Map<String, Boolean> statusChangedApps = new HashMap<>();
	private final Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			switch (msg.what)
			{
				case INIT_RESULT:
				{
					//					tvPath.setVisibility(View.GONE);
					//					statusChangedApps.clear();
					mChooseAppListAdapter = new ChooseAppListAdapter(ActivityInstallAppBat.this, appInfos, ActivityInstallAppBat.this);
					lvApps.setAdapter(mChooseAppListAdapter);
					break;
				}
				case INSTALL_RESULT:
				{
					Toast.makeText(ActivityInstallAppBat.this, "安装应用已完成！", Toast.LENGTH_LONG).show();
					break;
				}
				case DEL_RESULT:
				{
					Toast.makeText(ActivityInstallAppBat.this, "删除安装包已完成！", Toast.LENGTH_LONG).show();
					getAppInfos();
					break;
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_install_app_bat);
		viewInit();
		//		EventBus.getDefault().register(this);
		getAppInfos();
	}

	@Override
	protected void onDestroy()
	{
		//		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}

	@Override
	public void onBackPressed()
	{
		super.onBackPressed();
		isExiting = true;
	}

	@Override
	protected void viewInit()
	{
		super.viewInit();
		setBackButton();
		lvApps = (ListView) findViewById(R.id.list_all);
		tvPath = (TextView) findViewById(R.id.tv_path);
		findViewById(R.id.btn_install).setOnClickListener(this);
		findViewById(R.id.btn_del).setOnClickListener(this);
		findViewById(R.id.tv_app).setOnClickListener(this);
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.btn_install:
			{
				DialogCommon dialogCommon = new DialogCommon(this, new DialogCommon.LibitDialogListener()
				{
					@Override
					public void onOkClick()
					{
						String msg = "正在安装应用(0/" + statusChangedApps.size() + ")...";
						final DialogCommon dialog = new DialogCommon(ActivityInstallAppBat.this, null, "提示", msg, false, true, false);
						dialog.show();
						new Thread("installApps")
						{
							@Override
							public void run()
							{
								super.run();
								int count = statusChangedApps.size();
								if (count > 0)
								{
									int index = 0;
									for (String filePath : statusChangedApps.keySet())
									{
										index++;
										AppInfo appInfo = AppFactory.getInstance().getApkInfo(filePath);
										final String msg = "正在安装应用“" + appInfo.getName() + "”(" + index + "/" + statusChangedApps.size() + ")...";
										mHandler.post(new Thread("")
										{
											@Override
											public void run()
											{
												super.run();
												dialog.setDialogMessage(msg);
											}
										});
										AppFactory.getInstance().installApp(new File(filePath), true);
									}
								}
								Message msg = Message.obtain();
								msg.what = INSTALL_RESULT;
								mHandler.sendMessage(msg);
								dialog.dismiss();
							}
						}.start();
					}

					@Override
					public void onCancelClick()
					{
					}
				}, "提示", "确定要安装选择的应用吗？", true, false, true);
				dialogCommon.show();
				break;
			}
			case R.id.btn_del:
			{
				DialogCommon dialogCommon = new DialogCommon(this, new DialogCommon.LibitDialogListener()
				{
					@Override
					public void onOkClick()
					{
						String msg = "正在删除安装包(0/" + statusChangedApps.size() + ")...";
						final DialogCommon dialog = new DialogCommon(ActivityInstallAppBat.this, null, "提示", msg, false, true, false);
						dialog.show();
						new Thread("installApps")
						{
							@Override
							public void run()
							{
								super.run();
								int count = statusChangedApps.size();
								if (count > 0)
								{
									int index = 0;
									for (String filePath : statusChangedApps.keySet())
									{
										index++;
										final AppInfo appInfo = AppFactory.getInstance().getApkInfo(filePath);
										final String msg = "正在删除安装包“" + appInfo.getName() + "”(" + index + "/" + statusChangedApps.size() + ")...";
										mHandler.post(new Thread("")
										{
											@Override
											public void run()
											{
												super.run();
												dialog.setDialogMessage(msg);
											}
										});
										File file = new File(filePath);
										if (!file.delete())
										{
											mHandler.post(new Thread("")
											{
												@Override
												public void run()
												{
													super.run();
													Toast.makeText(ActivityInstallAppBat.this, "安装删除安装包“" + appInfo.getName() + "”失败！", Toast.LENGTH_LONG).show();
												}
											});
										}
									}
								}
								Message msg = Message.obtain();
								msg.what = DEL_RESULT;
								mHandler.sendMessage(msg);
								dialog.dismiss();
							}
						}.start();
					}

					@Override
					public void onCancelClick()
					{
					}
				}, "提示", "确定要删除选择的安装包吗？", true, false, true);
				dialogCommon.show();
				break;
			}
		}
	}

	private void getAppInfos()
	{
		new Thread("getAppInfos")
		{
			@Override
			public void run()
			{
				super.run();
				mHandler.post(new Thread("")
				{
					@Override
					public void run()
					{
						super.run();
						tvPath.setVisibility(View.VISIBLE);
					}
				});
				//				appInfos = DbAppFactory.getInstance().getAppInfoList(null, null, null, null);
				if (appInfos == null)
				{
					appInfos = new ArrayList<>();
				}
				else
				{
					appInfos.clear();
				}
				statusChangedApps.clear();
				Message msg = Message.obtain();
				msg.what = INIT_RESULT;
				mHandler.sendMessage(msg);
				if (StringTools.isNull(scanPath))
				{
					scanPath = FileTools.getUserDir(scanPath);
				}
				getFiles(scanPath, ".apk");
				mHandler.post(new Thread("")
				{
					@Override
					public void run()
					{
						super.run();
						tvPath.setVisibility(View.GONE);
					}
				});
			}
		}.start();
	}

	public boolean getFiles(String dirPath, String ext)
	{
		File file = new File(dirPath);
		if (file.isDirectory())
		{
			String[] strs = file.list();
			for (String str : strs)
			{
				if (isExiting)
				{
					return false;
				}
				final File subFile = new File(dirPath + "/" + str);
				LogcatTools.debug("getFiles", "str:" + dirPath + "/" + str + ",subFile:" + subFile.getName());
				mHandler.post(new Thread("getFiles")
				{
					@Override
					public void run()
					{
						super.run();
						tvPath.setText(subFile.getAbsolutePath() + "/" + subFile.getName());
					}
				});
				if (!subFile.isDirectory())
				{
					if (subFile.getName().toLowerCase().endsWith(ext.toLowerCase()))
					{
						AppInfo appInfo = AppFactory.getInstance().getApkInfo(subFile);
						if (appInfo != null)
						{
							appInfos.add(appInfo);
							mHandler.post(new Thread("getFiles")
							{
								@Override
								public void run()
								{
									super.run();
									mChooseAppListAdapter.notifyDataSetChanged();
								}
							});
						}
					}
				}
				else
				{
					getFiles(dirPath + "/" + str, ext);
				}
			}
		}
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.menu_activity_install_apps, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		int id = item.getItemId();
		if (id == R.id.action_select_all)
		{
			int count = appInfos.size();
			for (int i = 0; i < count; i++)
			{
				mChooseAppListAdapter.addBooleanToMemoryCache(appInfos.get(i).getPackageName(), true);
				statusChangedApps.put(appInfos.get(i).getId(), false);
			}
			mChooseAppListAdapter.notifyDataSetChanged();
			return true;
		}
		else if (id == R.id.action_select_none)
		{
			int count = appInfos.size();
			for (int i = 0; i < count; i++)
			{
				mChooseAppListAdapter.addBooleanToMemoryCache(appInfos.get(i).getPackageName(), false);
				statusChangedApps.put(appInfos.get(i).getId(), true);
			}
			mChooseAppListAdapter.notifyDataSetChanged();
			return true;
		}
		else if (id == R.id.action_select_path)
		{
			File rootFile = new File(FileTools.getUserDir(""));
			DialogFileList dialogFileList = new DialogFileList(this, rootFile, null, new DialogFileList.IDialogChooseFile()
			{
				@Override
				public void onFileSelected(File file)
				{
					if (file.isDirectory())
					{
						scanPath = file.getPath();
					}
					else
					{
						scanPath = file.getParent();
					}
					//					LogcatTools.debug("dialogFileList", "scanPath:" + scanPath);
					getAppInfos();
				}
			}, null);
			dialogFileList.show();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCheckedClicked(AppInfo appInfo, boolean isChecked)
	{
		if (appInfo != null)
		{
			statusChangedApps.put(appInfo.getId(), !isChecked);
			mChooseAppListAdapter.addBooleanToMemoryCache(appInfo.getPackageName(), isChecked);
		}
	}
	//	@Subscribe
	//	public void onEventMainThread(final AppEvent event)
	//	{
	//		mHandler.post(new Thread()
	//		{
	//			@Override
	//			public void run()
	//			{
	//				super.run();
	//				if (event != null)
	//				{
	//					if (event.getType().equalsIgnoreCase(AppEvent.CLEAR_AND_ENABLE_BLACK_APP))
	//					{
	//						Toast.makeText(ActivityInstallAppBat.this, event.getMsg(), Toast.LENGTH_LONG).show();
	//					}
	//				}
	//			}
	//		});
	//	}
}

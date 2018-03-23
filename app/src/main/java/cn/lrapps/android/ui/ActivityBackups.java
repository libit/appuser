/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.android.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.callback.AjaxStatus;
import com.google.gson.reflect.TypeToken;
import com.lrcall.appuser.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.lrapps.android.ui.adapter.LocalBackupAdapter;
import cn.lrapps.android.ui.adapter.ServerBackupAdapter;
import cn.lrapps.android.ui.dialog.DialogCreateBackup;
import cn.lrapps.db.DbAppFactory;
import cn.lrapps.events.BackupEvent;
import cn.lrapps.events.ViewChanged;
import cn.lrapps.models.AppInfo;
import cn.lrapps.models.BackupInfo;
import cn.lrapps.models.TableData;
import cn.lrapps.models.UserBackupInfo;
import cn.lrapps.services.ApiConfig;
import cn.lrapps.services.BackupService;
import cn.lrapps.services.IAjaxDataResponse;
import cn.lrapps.utils.AppConfig;
import cn.lrapps.utils.ConstValues;
import cn.lrapps.utils.GsonTools;
import cn.lrapps.utils.LogcatTools;
import cn.lrapps.utils.StringTools;
import cn.lrapps.utils.apptools.SystemToolsFactory;
import cn.lrapps.utils.filetools.FileTools;
import cn.lrapps.utils.viewtools.ViewHeightCalTools;

public class ActivityBackups extends MyBaseActivity implements View.OnClickListener, IAjaxDataResponse
{
	private static final String TAG = ActivityBackups.class.getSimpleName();
	private static final int DELETE_RESULT = 111;
	private TextView tvLocalName, tvServerName;
	private ListView lvLocalBackups, lvServerBackups;
	private final List<BackupInfo> localBackupInfoList = new ArrayList<>();
	private BackupService backupService;
	private boolean showServer;
	private SystemToolsFactory systemTools = SystemToolsFactory.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_backups);
		showServer = getIntent().getBooleanExtra(ConstValues.DATA_SHOW_SERVER, false);
		viewInit();
		initData();
		if (showServer)
		{
			backupService = new BackupService(this);
			backupService.addDataResponse(this);
			backupService.getBackupAppsList("正在获取列表...", false);
		}
		else
		{
			tvServerName.setVisibility(View.GONE);
			lvServerBackups.setVisibility(View.GONE);
			findViewById(R.id.v_line1).setVisibility(View.GONE);
			findViewById(R.id.v_line2).setVisibility(View.GONE);
		}
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
	public void onEventMainThread(final ViewChanged viewChanged)
	{
		mHandler.post(new Thread()
		{
			@Override
			public void run()
			{
				super.run();
				if (viewChanged != null)
				{
					if (viewChanged == ViewChanged.LOCAL_BACKUP_VIEW_CAHNGED)
					{
						LogcatTools.debug(TAG, "onEventMainThread:更新本地备份View！");
						updateView();
						//			ViewGroup.LayoutParams layoutParams = lvLocalBackups.getLayoutParams();
						//			layoutParams.height = layoutParams.height + 100;
						//			lvLocalBackups.setLayoutParams(layoutParams);
						//			mHandler.sendEmptyMessage(LOCAL_VIEW_CHANGED);
					}
					else if (viewChanged == ViewChanged.SERVER_BACKUP_VIEW_CAHNGED)
					{
						updateView();
					}
				}
			}
		});
	}

	@Subscribe
	public void onEventMainThread(final BackupEvent event)
	{
		mHandler.post(new Thread()
		{
			@Override
			public void run()
			{
				super.run();
				if (event != null)
				{
					if (event.getType() == BackupEvent.BACKUP)
					{
						initData();
						Toast.makeText(ActivityBackups.this, event.getMsg(), Toast.LENGTH_LONG).show();
					}
					if (event.getType() == BackupEvent.RESTORE)
					{
						Toast.makeText(ActivityBackups.this, event.getMsg(), Toast.LENGTH_LONG).show();
					}
				}
			}
		});
	}

	@Override
	protected void viewInit()
	{
		super.viewInit();
		setBackButton();
		tvLocalName = (TextView) findViewById(R.id.tv_local_name);
		tvServerName = (TextView) findViewById(R.id.tv_server_name);
		lvLocalBackups = (ListView) findViewById(R.id.list_backups);
		lvServerBackups = (ListView) findViewById(R.id.list_server_backups);
		findViewById(R.id.btn_add_backup).setOnClickListener(this);
	}

	private void initData()
	{
		String dir = FileTools.getAppDir(AppConfig.getBackupFolder());
		if (StringTools.isNull(dir))
		{
			return;
		}
		File directory = new File(dir);
		if (directory.isDirectory())
		{
			File[] files = directory.listFiles();
			if (files == null)
			{
				return;
			}
			int count = files.length;
			List<File> saveFiles = new ArrayList<>();
			for (int i = 0; i < count; i++)
			{
				File file = files[i];
				if (file.isFile())
				{
					int size = saveFiles.size();
					saveFiles.add(size, file);
					for (int j = size; j > 0; j--)
					{
						try
						{
							String fileName1 = saveFiles.get(j).getName();
							long date1 = Long.parseLong(fileName1.substring(fileName1.lastIndexOf("_") + 1, fileName1.lastIndexOf(".")));
							String fileName2 = saveFiles.get(j - 1).getName();
							long date2 = Long.parseLong(fileName2.substring(fileName2.lastIndexOf("_") + 1, fileName2.lastIndexOf(".")));
							if (date1 > date2)
							{
								File tmp = saveFiles.get(j - 1);
								saveFiles.set(j - 1, saveFiles.get(j));
								saveFiles.set(j, tmp);
							}
							else
							{
								break;
							}
						}
						catch (Exception e)
						{
							saveFiles.remove(j);
						}
					}
				}
			}
			localBackupInfoList.clear();
			int size = saveFiles.size();
			for (int i = size - 1; i >= 0; i--)
			{
				File file = saveFiles.get(i);
				if (file.isFile())
				{
					String content = FileTools.readFile(AppConfig.getBackupFolder(), file.getName());
					BackupInfo backupInfo = GsonTools.getObject(content, BackupInfo.class);
					if (backupInfo != null)
					{
						backupInfo.setFileName(file.getName());
						localBackupInfoList.add(backupInfo);
					}
				}
			}
		}
		lvLocalBackups.setAdapter(new LocalBackupAdapter(this, localBackupInfoList, new LocalBackupAdapter.ILocalBackupAdapterItemClicked()
		{
			@Override
			public void onRestoreClicked(BackupInfo backupInfo)
			{
				if (backupInfo != null)
				{
					new ActivityThread(ActivityBackups.this).restoreThread(backupInfo.getFileName());
				}
			}

			@Override
			public void onDeleteClicked(BackupInfo backupInfo)
			{
				if (backupInfo != null)
				{
					String fileName = backupInfo.getFileName();
					File file = FileTools.getFile(AppConfig.getBackupFolder(), fileName);
					if (file != null && file.exists() && file.isFile())
					{
						boolean b = file.delete();
						initData();
						Message msg = Message.obtain();
						msg.what = DELETE_RESULT;
						msg.obj = b ? "删除成功！" : "删除失败！";
						mHandler.sendMessage(msg);
					}
				}
			}
		}));
		tvLocalName.setText("本地备份(" + localBackupInfoList.size() + ")");
		updateView();
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.btn_add_backup:
			{
				new DialogCreateBackup(this, new DialogCreateBackup.OnCreateBackupListenser()
				{
					@Override
					public void onOkClick(String comment, boolean updateServer)
					{
						new ActivityThread(ActivityBackups.this).backupThread(comment);
						if (updateServer)
						{
							BackupInfo backupInfo = new BackupInfo();
							backupInfo.setTime(StringTools.getCurrentTime());
							backupInfo.setVersion(systemTools.getVersionName() + "_" + systemTools.getVersionCode());
							List<AppInfo> blackAppInfos = DbAppFactory.getInstance().getAppInfoList(null, null, null, null, null);
							List<String> packageNamgeList = new ArrayList<>();
							for (AppInfo appInfo : blackAppInfos)
							{
								packageNamgeList.add(appInfo.getPackageName());
							}
							String appJson = GsonTools.toJson(packageNamgeList);
							backupInfo.setApps(appJson);
							String name = systemTools.getVersionCode() + "";
							String data = GsonTools.toJson(backupInfo);
							String description = comment;
							if (StringTools.isNull(comment))
							{
								description = "安卓V" + systemTools.getVersionName() + "应用备份";
							}
							backupService.updateBackupApps(name, data, description, "正在备份...", true);
						}
					}

					@Override
					public void onCancelClick()
					{
					}
				}, showServer).show();
				break;
			}
		}
	}

	@Override
	public boolean onAjaxDataResponse(String url, String result, AjaxStatus status)
	{
		if (url.endsWith(ApiConfig.BACKUP_LIST))
		{
			List<UserBackupInfo> userBackupInfoList = null;
			TableData tableData = GsonTools.getObject(result, TableData.class);
			if (tableData != null)
			{
				userBackupInfoList = GsonTools.getObjects(GsonTools.toJson(tableData.getData()), new TypeToken<List<UserBackupInfo>>()
				{
				}.getType());
			}
			if (userBackupInfoList != null)
			{
				lvServerBackups.setAdapter(new ServerBackupAdapter(this, userBackupInfoList, new ServerBackupAdapter.IServerBackupAdapterItemClicked()
				{
					@Override
					public void onRestoreClicked(UserBackupInfo userBackupInfo)
					{
						if (userBackupInfo != null)
						{
							final BackupInfo backupInfo = GsonTools.getObject(userBackupInfo.getData(), BackupInfo.class);
							new ActivityThread(ActivityBackups.this).restoreServerThread(backupInfo);
						}
					}

					@Override
					public void onDeleteClicked(UserBackupInfo userBackupInfo)
					{
						if (userBackupInfo != null)
						{
							backupService.deleteBackupApps(userBackupInfo.getId(), "正在删除备份...", true);
						}
					}
				}));
				tvServerName.setText("云端备份(" + userBackupInfoList.size() + ")");
				updateView();
			}
			else
			{
				tvServerName.setText("云端备份(0)");
			}
		}
		else if (url.endsWith(ApiConfig.UPDATE_BACKUP_APPS) || url.endsWith(ApiConfig.DELETE_BACKUP_INFO))
		{
			backupService.getBackupAppsList("正在获取列表...", false);
		}
		return true;
	}

	private final Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			switch (msg.what)
			{
				case DELETE_RESULT:
				{
					initData();
					String result = (String) msg.obj;
					Toast.makeText(ActivityBackups.this, result, Toast.LENGTH_LONG).show();
					break;
				}
			}
		}
	};

	private void updateView()
	{
		ViewHeightCalTools.setListViewHeight(lvLocalBackups, false);
		ViewHeightCalTools.setListViewHeight(lvServerBackups, false);
	}
}

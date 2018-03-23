/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.android.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.callback.AjaxStatus;
import com.external.xlistview.XListView;
import cn.lrapps.android.ui.customer.ToastView;
import cn.lrapps.android.ui.dialog.DialogList;
import com.lrcall.appuser.R;
import cn.lrapps.models.PicInfo;
import cn.lrapps.models.UserInfo;
import cn.lrapps.services.ApiConfig;
import cn.lrapps.services.BackupService;
import cn.lrapps.services.FileService;
import cn.lrapps.services.IAjaxDataResponse;
import cn.lrapps.services.UserService;
import cn.lrapps.enums.UserEvent;
import cn.lrapps.models.ReturnInfo;
import cn.lrapps.utils.ConstValues;
import cn.lrapps.utils.GsonTools;
import cn.lrapps.utils.PreferenceUtils;
import cn.lrapps.utils.StringTools;
import cn.lrapps.utils.apptools.SystemToolsFactory;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import static cn.lrapps.utils.ConstValues.IMAGE_PICKER;

public class ActivityUser extends MyBaseActivity implements View.OnClickListener, XListView.IXListViewListener, IAjaxDataResponse
{
	private static final String TAG = ActivityUser.class.getSimpleName();
	private XListView xListView;
	private View headView;
	private TextView tvName, tvSubscribe;
	private UserService mUserService;
	private BackupService mBackupService;
	private SystemToolsFactory systemTools = SystemToolsFactory.getInstance();
	private FileService mFileService;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user);
		viewInit();
		mUserService = new UserService(this);
		mUserService.addDataResponse(this);
		mBackupService = new BackupService(this);
		mBackupService.addDataResponse(this);
		mFileService = new FileService(this);
		mFileService.addDataResponse(this);
		mUserService.getUserInfo(null, true);
	}

	@Override
	protected void viewInit()
	{
		super.viewInit();
		setBackButton();
		colorChange(R.drawable.bg_user);
		xListView = (XListView) findViewById(R.id.xlist);
		headView = LayoutInflater.from(this).inflate(R.layout.activity_user_head, null);
		xListView.setPullRefreshEnable(true);
		xListView.setPullLoadEnable(false);
		xListView.addHeaderView(headView);
		xListView.setAdapter(null);
		xListView.setXListViewListener(this);
		findViewById(R.id.iv_photo).setOnClickListener(this);
		findViewById(R.id.layout_sync_apps).setOnClickListener(this);
		findViewById(R.id.layout_sync_config).setOnClickListener(this);
		findViewById(R.id.layout_change_password).setOnClickListener(this);
		findViewById(R.id.layout_share).setOnClickListener(this);
		findViewById(R.id.btn_logout).setOnClickListener(this);
		tvName = (TextView) findViewById(R.id.tv_name);
		tvSubscribe = (TextView) findViewById(R.id.tv_subscribe);
	}

	@Override
	public void onRefresh()
	{
		mUserService.getUserInfo(null, true);
	}

	@Override
	public void onLoadMore()
	{
		//		LogcatTools.debug(TAG, "onLoadMore:加载更多");
		new Handler().postDelayed(new Runnable()
		{
			@Override
			public void run()
			{
				xListView.stopLoadMore();
			}
		}, 2000);
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.iv_photo:
			{
				if (!UserService.isLogin())
				{
					startActivityForResult(new Intent(this, ActivityLogin.class), ConstValues.REQUEST_LOGIN_USER);
				}
				else
				{
					EventBus.getDefault().post(UserEvent.CHANGE_HEADER);
				}
				break;
			}
			case R.id.layout_sync_apps:
			{
				if (!UserService.isLogin())
				{
					startActivityForResult(new Intent(this, ActivityLogin.class), ConstValues.REQUEST_LOGIN_USER);
				}
				else
				{
					Intent intent = new Intent(this, ActivityBackups.class);
					intent.putExtra(ConstValues.DATA_SHOW_SERVER, true);
					startActivity(intent);
				}
				break;
			}
			case R.id.layout_sync_config:
			{
				if (!UserService.isLogin())
				{
					startActivityForResult(new Intent(this, ActivityLogin.class), ConstValues.REQUEST_LOGIN_USER);
				}
				else
				{
					SyncConfigListAdapter syncConfigListAdapter = new SyncConfigListAdapter(this);
					DialogList dialogList = new DialogList(this, syncConfigListAdapter);
					syncConfigListAdapter.setDialog(dialogList);
					dialogList.show();
				}
				break;
			}
			case R.id.layout_change_password:
			{
				if (!UserService.isLogin())
				{
					startActivityForResult(new Intent(this, ActivityLogin.class), ConstValues.REQUEST_LOGIN_USER);
				}
				else
				{
					startActivity(new Intent(this, ActivityChangePwd.class));
				}
				break;
			}
			case R.id.layout_share:
			{
				mUserService.share("请稍后...", true);
				break;
			}
			case R.id.btn_logout:
			{
				mUserService.logout();
				finish();
				break;
			}
		}
	}

	private void backupConfig()
	{
		String name = systemTools.getVersionCode() + "";
		String data = PreferenceUtils.getInstance().backup();
		String description = "安卓V" + systemTools.getVersionName() + "配置备份";
		mBackupService.updateBackupConfig(name, data, description, "正在备份...", true);
	}

	private void downloadConfig()
	{
		String name = systemTools.getVersionCode() + "";
		mBackupService.getBackupConfig(name, "正在同步...", true);
	}

	class SyncConfigListAdapter extends BaseAdapter
	{
		private final Context context;
		private Dialog dialog;
		private final List<String> functions = new ArrayList<>();

		public SyncConfigListAdapter(Context context)
		{
			super();
			this.context = context;
			functions.add("上传配置");
			functions.add("下载配置");
		}

		public void setDialog(Dialog dialog)
		{
			this.dialog = dialog;
		}

		@Override
		public int getCount()
		{
			return functions.size();
		}

		@Override
		public Object getItem(int position)
		{
			return functions.get(position);
		}

		@Override
		public long getItemId(int position)
		{
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			convertView = LayoutInflater.from(context).inflate(R.layout.item_func, null);
			final ImageView ivIcon = (ImageView) convertView.findViewById(R.id.iv_icon);
			final TextView tvName = (TextView) convertView.findViewById(R.id.tv_name);
			ivIcon.setVisibility(View.GONE);
			tvName.setText(functions.get(position));
			convertView.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					if (tvName.getText().toString().contains("上传"))
					{
						backupConfig();
						dialog.dismiss();
					}
					else if (tvName.getText().toString().contains("下载"))
					{
						downloadConfig();
						dialog.dismiss();
					}
				}
			});
			return convertView;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == ConstValues.REQUEST_LOGIN_USER)
		{
			if (resultCode == ConstValues.RESULT_LOGIN_SUCCESS)
			{
				mUserService.getUserInfo(null, true);
			}
			else
			{
				finish();
			}
		}
		else if (resultCode == ImagePicker.RESULT_CODE_ITEMS)
		{
			if (data != null && requestCode == IMAGE_PICKER)
			{
				ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
				if (images != null && images.size() > 0)
				{
					ImageItem imageItem = images.get(0);
					mFileService.uploadPic(imageItem.path, "header", "正在上传图片...", true);
				}
			}
			else
			{
				Toast.makeText(this, "没有数据", Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	public boolean onAjaxDataResponse(String url, String result, AjaxStatus status)
	{
		xListView.stopRefresh();
		xListView.stopLoadMore();
		if (url.endsWith(ApiConfig.GET_USER_INFO))
		{
			UserInfo userInfo = GsonTools.getReturnObject(result, UserInfo.class);
			if (userInfo != null)
			{
				tvName.setText(userInfo.getNickname());
				tvSubscribe.setText(StringTools.getTime(userInfo.getAddDateLong()));
			}
			else
			{
				startActivityForResult(new Intent(this, ActivityLogin.class), ConstValues.REQUEST_LOGIN_USER);
			}
		}
		else if (url.endsWith(ApiConfig.UPLOAD_PIC))
		{
			ReturnInfo returnInfo = GsonTools.getReturnInfo(result);
			if (ReturnInfo.isSuccess(returnInfo))
			{
				PicInfo picInfo = GsonTools.getReturnObject(returnInfo, PicInfo.class);
				if (picInfo != null)
				{
					mUserService.updateUserHeader(picInfo.getPicUrl(), "正在设置头像，请稍后...", true);
					//					ivPic.setTag(picInfo.getPicUrl());
					//					Picasso.with(this).load(Uri.parse(ApiConfig.getServerPicUrl(picInfo.getPicUrl()))).into(ivPic);
				}
			}
			else
			{
				String msg = "上传图片失败！";
				if (returnInfo != null)
				{
					msg = returnInfo.getMsg();
				}
				ToastView.showCenterToast(this, R.drawable.ic_do_fail, msg);
			}
		}
		else if (url.endsWith(ApiConfig.UPDATE_USER_HEADER))
		{
			ReturnInfo returnInfo = GsonTools.getReturnInfo(result);
			if (ReturnInfo.isSuccess(returnInfo))
			{
				ToastView.showCenterToast(this, R.drawable.ic_done, "设置头像成功！");
				EventBus.getDefault().post(UserEvent.CHANGED_HEADER);
			}
			else
			{
				String msg = "设置头像失败！";
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

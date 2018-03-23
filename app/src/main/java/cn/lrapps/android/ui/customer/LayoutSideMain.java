/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.android.ui.customer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import cn.lrapps.android.ui.ActivityLogin;
import cn.lrapps.android.ui.ActivityUser;
import cn.lrapps.android.ui.adapter.AppListAdapter;
import cn.lrapps.android.ui.adapter.RunningAppListAdapter;
import com.lrcall.appuser.R;
import cn.lrapps.models.AppInfo;
import cn.lrapps.services.UserService;
import cn.lrapps.enums.EventTypeLayoutSideMain;
import cn.lrapps.enums.UserEvent;
import cn.lrapps.events.AppListEvent;
import cn.lrapps.models.ReturnInfo;
import cn.lrapps.utils.PreferenceUtils;
import cn.lrapps.utils.apptools.AppFactory;
import cn.lrapps.utils.viewtools.ViewHeightCalTools;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by libit on 16/6/29.
 */
public class LayoutSideMain extends LinearLayout implements View.OnClickListener
{
	private final Activity mActivity;
	private final Context mContext;
	private View rootView, vUser;
	protected GridView gvBlackAppList;
	public final List<AppInfo> mBlackAppInfoList = new ArrayList<>();
	protected TextView tvMemoryInfo;
	protected ListView mListViewRunning;
	//	public List<AppInfo> mRunningAppInfoList;
	private TextView tvName;
	private ImageView ivPhoto;
	private int colNum = 4;

	public LayoutSideMain(Activity context)
	{
		super(context);
		this.mActivity = context;
		this.mContext = context;
		viewInit();
	}

	public LayoutSideMain(Activity context, AttributeSet attrs)
	{
		super(context, attrs);
		this.mActivity = context;
		this.mContext = context;
		viewInit();
	}

	@Override
	protected void onAttachedToWindow()
	{
		super.onAttachedToWindow();
		if (!EventBus.getDefault().isRegistered(this))
		{
			EventBus.getDefault().register(this);
		}
	}

	@Override
	protected void onDetachedFromWindow()
	{
		if (EventBus.getDefault().isRegistered(this))
		{
			EventBus.getDefault().unregister(this);
		}
		super.onDetachedFromWindow();
	}

	private void viewInit()
	{
		rootView = LayoutInflater.from(mContext).inflate(R.layout.layout_side_main, null);
		gvBlackAppList = (GridView) rootView.findViewById(R.id.gv_black_apps);
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
		{
			colNum = PreferenceUtils.getInstance().getIntegerValue(PreferenceUtils.APP_COLUMN_COUNT_PORTRAIT) - 1;
		}
		else
		{
			colNum = PreferenceUtils.getInstance().getIntegerValue(PreferenceUtils.APP_COLUMN_COUNT_LANDSCAPE) - 1;
		}
		gvBlackAppList.setNumColumns(colNum);
		gvBlackAppList.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				AppInfo appInfo = mBlackAppInfoList.get(position);
				disableApp(appInfo);
			}
		});
		gvBlackAppList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
		{
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
			{
				AppInfo appInfo = mBlackAppInfoList.get(position);
				startApp(appInfo);
				return true;
			}
		});
		tvMemoryInfo = (TextView) rootView.findViewById(R.id.tv_memory_info);
		mListViewRunning = (ListView) rootView.findViewById(R.id.list_running);
		vUser = rootView.findViewById(R.id.layout_user);
		tvName = (TextView) rootView.findViewById(R.id.tv_name);
		ivPhoto = (ImageView) rootView.findViewById(R.id.iv_photo);
		vUser.setOnClickListener(this);
		ivPhoto.setOnClickListener(this);
		this.addView(rootView);
		refresh();
	}

	public void refresh()
	{
		if (UserService.isLogin())
		{
			tvName.setText(PreferenceUtils.getInstance().getUserId());
			//			vUser.setVisibility(VISIBLE);
		}
		else
		{
			tvName.setText("访客");
			//			vUser.setVisibility(GONE);
		}
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.layout_user:
			{
				EventBus.getDefault().post(EventTypeLayoutSideMain.CLOSE_DRAWER);
				if (!UserService.isLogin())
				{
					mContext.startActivity(new Intent(mContext, ActivityLogin.class));
				}
				else
				{
					mContext.startActivity(new Intent(mContext, ActivityUser.class));
				}
				break;
			}
			case R.id.iv_photo:
			{
				EventBus.getDefault().post(EventTypeLayoutSideMain.CLOSE_DRAWER);
				if (!UserService.isLogin())
				{
					UserService.logout();
				}
				else
				{
					EventBus.getDefault().post(UserEvent.CHANGE_HEADER);
				}
				break;
			}
		}
	}

	private void startApp(AppInfo appInfo)
	{
		ReturnInfo returnInfo = AppFactory.getInstance().startApp(mContext, appInfo.getPackageName());
		if (!ReturnInfo.isSuccess(returnInfo))
		{
			Toast.makeText(mContext, appInfo.getName() + "不能启动！", Toast.LENGTH_LONG).show();
		}
	}

	private void disableApp(AppInfo appInfo)
	{
		ReturnInfo returnInfo = AppFactory.getInstance().disableApp(appInfo.getPackageName());
		String msg;
		if (ReturnInfo.isSuccess(returnInfo))
		{
			msg = String.format("冻结%s成功。", appInfo.getName());
		}
		else
		{
			msg = String.format("冻结%s失败：%s。", appInfo.getName(), returnInfo.getMsg());
		}
		Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
	}

	public void setTvMemoryInfo(String info)
	{
		tvMemoryInfo.setText(info);
	}

	public void updateRunningApps(List<AppInfo> runningAppInfoList)
	{
		RunningAppListAdapter adapter = new RunningAppListAdapter(mContext, runningAppInfoList);
		mListViewRunning.setAdapter(adapter);
		ViewHeightCalTools.setListViewHeight(mListViewRunning, true);
	}

	public void getEnabledBlackapps()
	{
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
		{
			colNum = PreferenceUtils.getInstance().getIntegerValue(PreferenceUtils.APP_COLUMN_COUNT_PORTRAIT) - 1;
		}
		else
		{
			colNum = PreferenceUtils.getInstance().getIntegerValue(PreferenceUtils.APP_COLUMN_COUNT_LANDSCAPE) - 1;
		}
		gvBlackAppList.setNumColumns(colNum);
		AppListAdapter adapter = new AppListAdapter(mContext, mBlackAppInfoList);
		gvBlackAppList.setAdapter(adapter);
		ViewHeightCalTools.setGridViewHeight(gvBlackAppList, colNum, true);
	}

	private final Handler mHandler = new Handler();

	@Subscribe
	public void onEventMainThread(final AppListEvent appListEvent)
	{
		if (appListEvent != null)
		{
			if (AppListEvent.GET_RUNNING_APP.equals(appListEvent.getType()))
			{
				mHandler.post(new Thread()
				{
					@Override
					public void run()
					{
						super.run();
						updateRunningApps(appListEvent.getAppInfoList());
					}
				});
			}
		}
	}
}

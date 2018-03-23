/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.android.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.lrcall.appuser.R;
import cn.lrapps.models.AppInfo;
import cn.lrapps.events.AppEvent;
import cn.lrapps.models.ReturnInfo;
import cn.lrapps.utils.LogcatTools;
import cn.lrapps.utils.apptools.AppFactory;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by libit on 15/9/30.
 */
public class RunningAppListAdapter extends BaseUserAdapter<AppInfo>
{
	public RunningAppListAdapter(Context context, List<AppInfo> runningAppList)
	{
		super(context, runningAppList);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		convertView = LayoutInflater.from(context).inflate(R.layout.item_app_detail, null);
		ImageView ivHead = (ImageView) convertView.findViewById(R.id.iv_head);
		final AppInfo appInfo = list.get(position);
		Bitmap bmp = appInfo.getPhoto();
		if (bmp != null)
		{
			ivHead.setImageBitmap(bmp);
		}
		convertView.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				AppFactory.getInstance().killApp(appInfo.getPackageName());
				LogcatTools.debug("killProcess", "name:" + appInfo.getName());
				Toast.makeText(context, appInfo.getName() + "已关闭", Toast.LENGTH_SHORT).show();
				EventBus.getDefault().post(new AppEvent(AppEvent.GET_RUNNING_APP, null));
			}
		});
		convertView.findViewById(R.id.btn_disable).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				//				new Thread("")
				//				{
				//					@Override
				//					public void run()
				//					{
				//						super.run();
				//						ReturnInfo returnInfo = AppFactory.getInstance().disableApp(appInfo.getPackageName());
				//						if (ReturnInfo.isSuccess(returnInfo))
				//						{
				//							appInfo.setIsEnabled(false);
				//							//					EventBus.getDefault().post(new AppEvent(AppEvent.APP_STATUS_DISABLED, Arrays.asList(appInfo.getPackageName())));
				//						}
				//					}
				//				}.start();
				ReturnInfo returnInfo = AppFactory.getInstance().disableApp(appInfo.getPackageName());
				if (ReturnInfo.isSuccess(returnInfo))
				{
					appInfo.setIsEnabled(false);
					//					EventBus.getDefault().post(new AppEvent(AppEvent.APP_STATUS_DISABLED, Arrays.asList(appInfo.getPackageName())));
				}
			}
		});
		ivHead.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				LogcatTools.debug("setOnClickListener", "packageName:" + appInfo.getPackageName());
				ReturnInfo returnInfo = AppFactory.getInstance().startApp(context, appInfo.getPackageName());
				if (!ReturnInfo.isSuccess(returnInfo))
				{
					Toast.makeText(context, appInfo.getName() + "不能启动！", Toast.LENGTH_LONG).show();
				}
			}
		});
		return convertView;
	}
}

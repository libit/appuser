/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.android.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lrcall.appuser.R;
import cn.lrapps.models.AppInfo;
import cn.lrapps.utils.PreferenceUtils;
import cn.lrapps.utils.apptools.AppFactory;
import cn.lrapps.utils.viewtools.DisplayTools;

import java.util.List;

import static com.squareup.picasso.Picasso.with;

/**
 * Created by libit on 15/9/30.
 */
public class FloatAppListAdapter extends BaseUserAdapter<AppInfo>
{
	public FloatAppListAdapter(Context context, List<AppInfo> appInfoList)
	{
		super(context, appInfoList);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		convertView = LayoutInflater.from(context).inflate(R.layout.item_float_app_detail, null);
		ImageView ivHead = (ImageView) convertView.findViewById(R.id.iv_head);
		TextView tvName = (TextView) convertView.findViewById(R.id.tv_name);
		tvName.setTextSize(TypedValue.COMPLEX_UNIT_SP, PreferenceUtils.getInstance().getIntegerValue(PreferenceUtils.APP_FONT_SIZE));
		ivHead.setPadding(DisplayTools.dip2px(context, PreferenceUtils.getInstance().getIntegerValue(PreferenceUtils.APP_PADDING_SIZE) / 2), 0, DisplayTools.dip2px(context, PreferenceUtils.getInstance().getIntegerValue(PreferenceUtils.APP_PADDING_SIZE) / 2), 0);
		final AppInfo appInfo = list.get(position);
		Bitmap bmp = appInfo.getPhoto();
		if (bmp != null)
		{
			ivHead.setImageBitmap(bmp);
		}
		else
		{
			Drawable drawable = AppFactory.getInstance().getAppIconByPackageName(appInfo.getPackageName());
			if (drawable != null)
			{
				with(context).load(Uri.EMPTY).placeholder(drawable).error(drawable).into(ivHead);
			}
		}
		tvName.setText(appInfo.getName());
		//		convertView.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener()
		//		{
		//			@Override
		//			public void onClick(View v)
		//			{
		//				AppFactory.getInstance().killApp(appInfo.getPackageName());
		//				LogcatTools.debug("killProcess", "name:" + appInfo.getName());
		//				ActivityMain activityMain = ActivityMain.getInstance();
		//				if (activityMain != null)
		//				{
		//					Toast.makeText(context, appInfo.getName() + "已关闭", Toast.LENGTH_SHORT).show();
		//					activityMain.mActivityMainThread.getRunningAppsThread();
		//					activityMain.getmFloatSide().refreshAppList();
		//				}
		//			}
		//		});
		//		convertView.setOnLongClickListener(new View.OnLongClickListener()
		//		{
		//			@Override
		//			public boolean onLongClick(View v)
		//			{
		//				ShellUtils.CommandResult result = AppFactory.getInstance().disableApp(appInfo.getPackageName());
		//				if (result.result == 0)
		//				{
		//					appInfo.setIsEnabled(false);
		//					ActivityMain activityMain = ActivityMain.getInstance();
		//					if (activityMain != null)
		//					{
		//						activityMain.OnAppStatusChanged(appInfo);
		//					}
		//				}
		//				return true;
		//			}
		//		});
		//		ivHead.setOnClickListener(new View.OnClickListener()
		//		{
		//			@Override
		//			public void onClick(View v)
		//			{
		//				LogcatTools.debug("setOnClickListener", "packageName:" + appInfo.getPackageName());
		//				boolean isBoot = AppFactory.getInstance().startApp(appInfo.getPackageName());
		//				if (!isBoot)
		//				{
		//					Toast.makeText(context, appInfo.getName() + "不能启动！", Toast.LENGTH_LONG).show();
		//				}
		//			}
		//		});
		return convertView;
	}
}

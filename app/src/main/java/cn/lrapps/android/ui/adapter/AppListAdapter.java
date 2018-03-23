/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.android.ui.adapter;

import android.content.Context;
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
 * Created by libit on 15/8/29.
 */
public class AppListAdapter extends BaseUserAdapter<AppInfo>
{
	public AppListAdapter(Context context, List<AppInfo> appInfoList)
	{
		super(context, appInfoList);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		AppViewHolder viewHolder = null;
		if (convertView != null)
		{
			viewHolder = (AppViewHolder) convertView.getTag();
		}
		if (viewHolder == null)
		{
			convertView = LayoutInflater.from(context).inflate(R.layout.item_app, null);
			viewHolder = new AppViewHolder();
			viewHolder.viewInit(convertView);
			convertView.setTag(viewHolder);
		}
		else
		{
			viewHolder.clear();
		}
		viewHolder.tvName.setTextSize(TypedValue.COMPLEX_UNIT_SP, PreferenceUtils.getInstance().getIntegerValue(PreferenceUtils.APP_FONT_SIZE));
		viewHolder.ivHead.setPadding(DisplayTools.dip2px(context, PreferenceUtils.getInstance().getIntegerValue(PreferenceUtils.APP_PADDING_SIZE)), 0, DisplayTools.dip2px(context, PreferenceUtils.getInstance().getIntegerValue(PreferenceUtils.APP_PADDING_SIZE)), 0);
		if (list != null && list.size() > 0 && list.size() > position)
		{
			AppInfo appInfo = list.get(position);
			if (appInfo != null)
			{
				if (!appInfo.isEnabled())
				{
					viewHolder.ivTag.setVisibility(View.VISIBLE);
				}
				else
				{
					viewHolder.ivTag.setVisibility(View.GONE);
				}
				viewHolder.tvName.setText(appInfo.getName());
				//				loadBitmap(appInfo.getPackageName(), viewHolder.ivHead);
				Drawable drawable = AppFactory.getInstance().getAppIconByPackageName(appInfo.getPackageName());
				if (drawable != null)
				{
					with(context).load(Uri.EMPTY).placeholder(drawable).error(drawable).into(viewHolder.ivHead);
				}
			}
		}
		return convertView;
	}

	public List<AppInfo> getData()
	{
		return list;
	}

	public void setData(List<AppInfo> appInfos)
	{
		list = appInfos;
	}

	public boolean remove(String uid)
	{
		if (list != null)
		{
			for (AppInfo appInfo : list)
			{
				if (appInfo.getUid().equals(uid))
				{
					list.remove(appInfo);
					return true;
				}
			}
		}
		return false;
	}

	public static class AppViewHolder
	{
		public ImageView ivHead;
		public TextView tvName;
		public View ivTag;

		public void viewInit(View convertView)
		{
			ivHead = (ImageView) convertView.findViewById(R.id.iv_head);
			tvName = (TextView) convertView.findViewById(R.id.tv_label);
			ivTag = convertView.findViewById(R.id.iv_tag);
		}

		public void clear()
		{
			ivHead.setImageBitmap(null);
			tvName.setText("");
		}
	}
}
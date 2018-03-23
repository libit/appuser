/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.android.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import cn.lrapps.android.ui.ActivityAddBlackAndHideAppBat;
import com.lrcall.appuser.R;
import cn.lrapps.models.AppInfo;
import cn.lrapps.enums.AppType;
import cn.lrapps.utils.apptools.AppFactory;

import java.util.List;

import static com.squareup.picasso.Picasso.with;

/**
 * Created by libit on 16/5/9.
 */
public class ChooseBlackAppListAdapter extends BaseUserAdapter<AppInfo>
{
	public interface IChooseBlackAppListAdapterItemClicked
	{
		void onBlackCheckedClicked(final AppInfo appInfo, boolean isChecked);

		void onHideCheckedClicked(final AppInfo appInfo, boolean isChecked);
	}

	private final IChooseBlackAppListAdapterItemClicked chooseBlackAppListAdapterItemClicked;
	private final ActivityAddBlackAndHideAppBat activityAddBlackAndHideAppBat;

	public ChooseBlackAppListAdapter(Context context, List<AppInfo> appInfoList, ActivityAddBlackAndHideAppBat activityAddBlackAndHideAppBat, IChooseBlackAppListAdapterItemClicked chooseBlackAppListAdapterItemClicked)
	{
		super(context, appInfoList);
		this.activityAddBlackAndHideAppBat = activityAddBlackAndHideAppBat;
		this.chooseBlackAppListAdapterItemClicked = chooseBlackAppListAdapterItemClicked;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		convertView = LayoutInflater.from(context).inflate(R.layout.item_choose_black_app, null);
		ImageView ivHead = (ImageView) convertView.findViewById(R.id.iv_head);
		TextView tvName = (TextView) convertView.findViewById(R.id.tv_name);
		TextView tvPackageName = (TextView) convertView.findViewById(R.id.tv_package_name);
		TextView tvType = (TextView) convertView.findViewById(R.id.tv_type);
		TextView tvTips = (TextView) convertView.findViewById(R.id.tv_tips);
		CheckBox cbBlack = (CheckBox) convertView.findViewById(R.id.cb_black);
		CheckBox cbHide = (CheckBox) convertView.findViewById(R.id.cb_hide);
		final AppInfo appInfo = list.get(position);
		tvName.setText(appInfo.getName());
		tvPackageName.setText("包名：" + appInfo.getPackageName());
		tvType.setText(appInfo.getType() == AppType.SYSTEM.getType() ? "类型：系统应用" : "类型：用户应用");
		//		loadBitmap(appInfo.getPackageName(), ivHead);
		Drawable drawable = AppFactory.getInstance().getAppIconByPackageName(appInfo.getPackageName());
		if (drawable != null)
		{
			with(context).load(Uri.EMPTY).placeholder(drawable).error(drawable).into(ivHead);
		}
		Boolean b = activityAddBlackAndHideAppBat.getBlackBooleanFromMemCache(appInfo.getPackageName());
		if (b != null)
		{
			cbBlack.setChecked(b);
		}
		else
		{
			cbBlack.setChecked(!appInfo.isEnabled());
		}
		Boolean bHide = activityAddBlackAndHideAppBat.getHideBooleanFromMemCache(appInfo.getPackageName());
		if (bHide != null)
		{
			cbHide.setChecked(bHide);
		}
		else
		{
			cbHide.setChecked(appInfo.isHide());
		}
		cbBlack.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				if (chooseBlackAppListAdapterItemClicked != null)
				{
					chooseBlackAppListAdapterItemClicked.onBlackCheckedClicked(appInfo, isChecked);
				}
			}
		});
		cbHide.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				if (chooseBlackAppListAdapterItemClicked != null)
				{
					chooseBlackAppListAdapterItemClicked.onHideCheckedClicked(appInfo, isChecked);
				}
			}
		});
		tvTips.setText("");
		return convertView;
	}
}

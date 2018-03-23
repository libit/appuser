/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.android.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.lrcall.appuser.R;
import cn.lrapps.models.AppInfo;
import cn.lrapps.enums.AppType;
import cn.lrapps.enums.StatusType;
import cn.lrapps.utils.apptools.AppFactory;

import java.util.List;

import static com.squareup.picasso.Picasso.with;

/**
 * Created by libit on 16/5/9.
 */
public class ChooseCommonUseAppListAdapter extends BaseUserAdapter<AppInfo>
{
	public interface IChooseCommonUseAppListAdapterItemClicked
	{
		void onCheckedClicked(final AppInfo appInfo, boolean isChecked);
	}

	public static LruCache<String, Boolean> mBooleanMemoryCache;// 缓存
	private static final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
	private static final int cacheSize = maxMemory / 8;
	private final IChooseCommonUseAppListAdapterItemClicked iChooseCommonUseAppListAdapterItemClicked;

	public ChooseCommonUseAppListAdapter(Context context, List<AppInfo> appInfoList, IChooseCommonUseAppListAdapterItemClicked iChooseCommonUseAppListAdapterItemClicked)
	{
		super(context, appInfoList);
		this.iChooseCommonUseAppListAdapterItemClicked = iChooseCommonUseAppListAdapterItemClicked;
		// 初始化缓存大小
		if (mBooleanMemoryCache == null)
		{
			mBooleanMemoryCache = new LruCache<String, Boolean>(cacheSize)
			{
				@Override
				protected int sizeOf(String key, Boolean value)
				{
					return 2;
				}
			};
		}
		else
		{
			mBooleanMemoryCache.evictAll();
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		convertView = LayoutInflater.from(context).inflate(R.layout.item_choose_app, null);
		ImageView ivHead = (ImageView) convertView.findViewById(R.id.iv_head);
		TextView tvName = (TextView) convertView.findViewById(R.id.tv_name);
		TextView tvPackageName = (TextView) convertView.findViewById(R.id.tv_package_name);
		TextView tvType = (TextView) convertView.findViewById(R.id.tv_type);
		TextView tvTips = (TextView) convertView.findViewById(R.id.tv_tips);
		CheckBox cbBlack = (CheckBox) convertView.findViewById(R.id.cb_app);
		final AppInfo appInfo = list.get(position);
		tvName.setText(appInfo.getName());
		tvPackageName.setText("包名：" + appInfo.getPackageName());
		tvType.setText(appInfo.getType() == AppType.SYSTEM.getType() ? "类型：系统应用" : "类型：用户应用");
		Drawable drawable = AppFactory.getInstance().getAppIconByPackageName(appInfo.getPackageName());
		if (drawable != null)
		{
			with(context).load(Uri.EMPTY).placeholder(drawable).error(drawable).into(ivHead);
		}
		Boolean b = getBooleanFromMemCache(appInfo.getPackageName());
		if (b == null)
		{
			b = appInfo.getCommonUse() != null && appInfo.getCommonUse() == StatusType.ENABLE.getStatus();
			//			b = false;
		}
		cbBlack.setChecked(b);
		cbBlack.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				if (iChooseCommonUseAppListAdapterItemClicked != null)
				{
					iChooseCommonUseAppListAdapterItemClicked.onCheckedClicked(appInfo, isChecked);
				}
			}
		});
		tvTips.setText("");
		return convertView;
	}

	//加入缓存
	public void addBooleanToMemoryCache(String key, Boolean value)
	{
		mBooleanMemoryCache.put(key, value);
	}

	// 从缓存中取出
	public Boolean getBooleanFromMemCache(String key)
	{
		return mBooleanMemoryCache.get(key);
	}
}

/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.android.ui.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lrcall.appuser.R;
import cn.lrapps.models.FontInfo;

import java.util.List;

/**
 * Created by libit on 15/8/29.
 */
public class FontInfoAdapter extends BaseUserAdapter<FontInfo>
{
	private final IItemClick iItemClick;

	public FontInfoAdapter(Context context, List<FontInfo> fontInfoList, IItemClick iItemClick)
	{
		super(context, fontInfoList);
		this.iItemClick = iItemClick;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		WxSendMsgLogInfoViewHolder viewHolder = null;
		if (convertView != null)
		{
			viewHolder = (WxSendMsgLogInfoViewHolder) convertView.getTag();
		}
		if (viewHolder == null)
		{
			convertView = LayoutInflater.from(context).inflate(R.layout.item_font_info, null);
			viewHolder = new WxSendMsgLogInfoViewHolder();
			viewHolder.viewInit(convertView);
			convertView.setTag(viewHolder);
		}
		else
		{
			viewHolder.clear();
		}
		if (list != null && list.size() > 0 && list.size() > position)
		{
			final FontInfo fontInfo = list.get(position);
			if (fontInfo != null)
			{
				viewHolder.tvName.setText(fontInfo.getName());
				viewHolder.tvUrl.setText("字体路径：" + fontInfo.getUrl());
				String size = "";
				if (fontInfo.getSize() > 1024 * 1024)
				{
					size = String.format("%d MB", fontInfo.getSize() / (1024 * 1024));
				}
				else if (fontInfo.getSize() > 1024)
				{
					size = String.format("%d KB", fontInfo.getSize() / (1024));
				}
				viewHolder.tvFontSize.setText("字体大小：" + size);
				try
				{
					Typeface tf = Typeface.createFromFile(fontInfo.getUrl());
					viewHolder.tvPreview1.setTypeface(tf);
					viewHolder.tvPreview2.setTypeface(tf);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				if (iItemClick != null)
				{
					convertView.setOnClickListener(new View.OnClickListener()
					{
						@Override
						public void onClick(View v)
						{
							iItemClick.onItemClicked(v, fontInfo);
						}
					});
				}
			}
		}
		return convertView;
	}

	public interface IItemClick
	{
		void onItemClicked(View v, FontInfo fontInfo);
	}

	public static class WxSendMsgLogInfoViewHolder
	{
		public TextView tvName;
		public TextView tvUrl;
		public TextView tvFontSize;
		public TextView tvPreview1;
		public TextView tvPreview2;

		public void viewInit(View convertView)
		{
			tvName = (TextView) convertView.findViewById(R.id.tv_name);
			tvUrl = (TextView) convertView.findViewById(R.id.tv_url);
			tvFontSize = (TextView) convertView.findViewById(R.id.tv_font_size);
			tvPreview1 = (TextView) convertView.findViewById(R.id.tv_preview1);
			tvPreview2 = (TextView) convertView.findViewById(R.id.tv_preview2);
		}

		public void clear()
		{
			tvName.setText("");
			tvUrl.setText("");
			tvFontSize.setText("");
			//			tvPreview1.setText("");
			//			tvPreview2.setText("");
			tvPreview1.setTypeface(null);
			tvPreview2.setTypeface(null);
		}
	}
}
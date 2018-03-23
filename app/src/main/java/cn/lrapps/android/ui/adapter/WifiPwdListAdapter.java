/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.android.ui.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.lrcall.appuser.R;
import cn.lrapps.models.WifiInfo;

import java.util.List;

/**
 * Created by libit on 15/8/29.
 */
public class WifiPwdListAdapter extends BaseUserAdapter<WifiInfo>
{
	public WifiPwdListAdapter(Context context, List<WifiInfo> wifiInfoList)
	{
		super(context, wifiInfoList);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		WifiViewHolder viewHolder = null;
		if (convertView != null)
		{
			viewHolder = (WifiViewHolder) convertView.getTag();
		}
		if (viewHolder == null)
		{
			convertView = LayoutInflater.from(context).inflate(R.layout.item_wifi_pwd, null);
			viewHolder = new WifiViewHolder();
			viewHolder.viewInit(convertView);
			convertView.setTag(viewHolder);
		}
		else
		{
			viewHolder.clear();
		}
		final WifiInfo wifiInfo = list.get(position);
		if (wifiInfo != null)
		{
			viewHolder.tvName.setText("WIFI名称：" + wifiInfo.getSsid());
			viewHolder.tvPwd.setText("WIFI密码：" + wifiInfo.getPsk());
			viewHolder.tvMgmt.setText("加密方式：" + wifiInfo.getKeyMgmt());
			viewHolder.btnCopy.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
					ClipData mClipData = ClipData.newPlainText(wifiInfo.getPsk(), wifiInfo.getPsk());
					cm.setPrimaryClip(mClipData);
					Toast.makeText(context, "密码已复制到剪贴板，分享给您的小伙伴吧。", Toast.LENGTH_LONG).show();
				}
			});
		}
		return convertView;
	}

	public static class WifiViewHolder
	{
		public TextView tvName;
		public TextView tvPwd;
		public TextView tvMgmt;
		public Button btnCopy;

		public void viewInit(View convertView)
		{
			tvName = (TextView) convertView.findViewById(R.id.tv_name);
			tvPwd = (TextView) convertView.findViewById(R.id.tv_pwd);
			tvMgmt = (TextView) convertView.findViewById(R.id.tv_mgmt);
			btnCopy = (Button) convertView.findViewById(R.id.btn_copy);
		}

		public void clear()
		{
			tvName.setText("");
			tvPwd.setText("");
			tvMgmt.setText("");
		}
	}
}
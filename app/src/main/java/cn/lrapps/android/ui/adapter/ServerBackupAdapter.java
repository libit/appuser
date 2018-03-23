/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.android.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lrcall.appuser.R;
import cn.lrapps.models.BackupInfo;
import cn.lrapps.models.UserBackupInfo;
import cn.lrapps.events.ViewChanged;
import cn.lrapps.utils.GsonTools;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by libit on 16/5/9.
 */
public class ServerBackupAdapter extends BaseUserAdapter<UserBackupInfo>
{
	public interface IServerBackupAdapterItemClicked
	{
		void onRestoreClicked(UserBackupInfo userBackupInfo);

		void onDeleteClicked(UserBackupInfo userBackupInfo);
	}

	private final List<View> views = new ArrayList<>();
	private final IServerBackupAdapterItemClicked serverBackupAdapterItemClicked;

	public ServerBackupAdapter(Context context, List<UserBackupInfo> list, IServerBackupAdapterItemClicked serverBackupAdapterItemClicked)
	{
		super(context, list);
		this.serverBackupAdapterItemClicked = serverBackupAdapterItemClicked;
		views.clear();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		convertView = LayoutInflater.from(context).inflate(R.layout.item_backup, null);
		final TextView tvName = (TextView) convertView.findViewById(R.id.tv_name);
		final TextView tvComment = (TextView) convertView.findViewById(R.id.tv_comment);
		final TextView tvTime = (TextView) convertView.findViewById(R.id.tv_time);
		final View btns = convertView.findViewById(R.id.layout_btns);
		final UserBackupInfo userBackupInfo = list.get(position);
		tvComment.setText(userBackupInfo.getDescription());
		final BackupInfo backupInfo = GsonTools.getObject(userBackupInfo.getData(), BackupInfo.class);
		if (backupInfo != null)
		{
			tvName.setText(context.getString(R.string.app_name) + "_" + backupInfo.getVersion() + "版备份");
			tvTime.setText("备份时间：" + backupInfo.getTime());
			View.OnClickListener listenser = new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					switch (v.getId())
					{
						case R.id.btn_extends:
						case R.id.layout_item:
						{
							int size = views.size();
							for (int i = 0; i < size; i++)
							{
								if (views.get(i).findViewById(R.id.layout_btns) != btns)
								{
									views.get(i).findViewById(R.id.layout_btns).setVisibility(View.GONE);
								}
								else
								{
									if (btns.getVisibility() == View.VISIBLE)
									{
										btns.setVisibility(View.GONE);
									}
									else
									{
										btns.setVisibility(View.VISIBLE);
									}
								}
							}
							EventBus.getDefault().post(ViewChanged.SERVER_BACKUP_VIEW_CAHNGED);
							break;
						}
						case R.id.btn_restore:
						{
							if (serverBackupAdapterItemClicked != null)
							{
								serverBackupAdapterItemClicked.onRestoreClicked(userBackupInfo);
							}
							break;
						}
						case R.id.btn_delete:
						{
							if (serverBackupAdapterItemClicked != null)
							{
								serverBackupAdapterItemClicked.onDeleteClicked(userBackupInfo);
							}
							break;
						}
					}
				}
			};
			convertView.findViewById(R.id.layout_item).setOnClickListener(listenser);
			convertView.findViewById(R.id.btn_extends).setOnClickListener(listenser);
			convertView.findViewById(R.id.btn_restore).setOnClickListener(listenser);
			convertView.findViewById(R.id.btn_delete).setOnClickListener(listenser);
		}
		if (!views.contains(convertView))
		{
			views.add(convertView);
		}
		return convertView;
	}
}

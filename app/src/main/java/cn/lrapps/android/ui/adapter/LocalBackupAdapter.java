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
import cn.lrapps.events.ViewChanged;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by libit on 16/5/9.
 */
public class LocalBackupAdapter extends BaseUserAdapter<BackupInfo>
{
	public interface ILocalBackupAdapterItemClicked
	{
		void onRestoreClicked(BackupInfo backupInfo);

		void onDeleteClicked(BackupInfo backupInfo);
	}

	private final List<View> views = new ArrayList<>();
	private final ILocalBackupAdapterItemClicked localBackupAdapterItemClicked;

	public LocalBackupAdapter(Context context, List<BackupInfo> list, ILocalBackupAdapterItemClicked localBackupAdapterItemClicked)
	{
		super(context, list);
		this.localBackupAdapterItemClicked = localBackupAdapterItemClicked;
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
		final BackupInfo backupInfo = list.get(position);
		tvName.setText(backupInfo.getName() + "_" + backupInfo.getVersion() + "版备份");
		tvComment.setText(backupInfo.getComment());
		tvTime.setText("备份时间：" + backupInfo.getTime());
		//		btns.setVisibility(View.GONE);
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
						//						EventBus.getDefault().post(ViewChanged.LOCAL_BACKUP_VIEW_CAHNGED);
						EventBus.getDefault().postSticky(ViewChanged.LOCAL_BACKUP_VIEW_CAHNGED);
						break;
					}
					case R.id.btn_restore:
					{
						if (localBackupAdapterItemClicked != null)
						{
							localBackupAdapterItemClicked.onRestoreClicked(backupInfo);
						}
						break;
					}
					case R.id.btn_delete:
					{
						if (localBackupAdapterItemClicked != null)
						{
							localBackupAdapterItemClicked.onDeleteClicked(backupInfo);
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
		if (!views.contains(convertView))
		{
			views.add(convertView);
		}
		return convertView;
	}
}

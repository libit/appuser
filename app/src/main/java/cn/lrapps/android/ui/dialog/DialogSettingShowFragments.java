/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.android.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.lrcall.appuser.R;
import cn.lrapps.utils.PreferenceUtils;

public class DialogSettingShowFragments extends Dialog implements OnClickListener
{
	private ListView lvFragments;
	private final DialogListenser listenser;
	private String[] fragments;
	private Boolean[] values;

	public DialogSettingShowFragments(Context context, DialogListenser listenser)
	{
		super(context, R.style.MyDialog);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setCancelable(false);
		this.listenser = listenser;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_setting_show_fragments);
		fragments = new String[]{"全部应用", "启用和冻结应用"};
		values = new Boolean[]{PreferenceUtils.getInstance().getBooleanValue(PreferenceUtils.SHOW_ALL_APPLIST), PreferenceUtils.getInstance().getBooleanValue(PreferenceUtils.SHOW_ENABLE_DISABLE_APPLIST)};
		initView();
	}

	private void initView()
	{
		lvFragments = (ListView) findViewById(R.id.list_fragments);
		findViewById(R.id.dialog_btn_ok).setOnClickListener(this);
		findViewById(R.id.dialog_btn_cancel).setOnClickListener(this);
		lvFragments.setAdapter(new ListTypes(this.getContext()));
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.dialog_btn_ok:
			{
				PreferenceUtils.getInstance().setBooleanValue(PreferenceUtils.SHOW_ALL_APPLIST, values[0]);
				PreferenceUtils.getInstance().setBooleanValue(PreferenceUtils.SHOW_ENABLE_DISABLE_APPLIST, values[1]);
				if (listenser != null)
				{
					listenser.onOkClick();
				}
				dismiss();
				break;
			}
			case R.id.dialog_btn_cancel:
			{
				if (listenser != null)
				{
					listenser.onCancelClick();
				}
				dismiss();
				break;
			}
		}
	}

	class ListTypes extends BaseAdapter
	{
		private final Context context;

		public ListTypes(Context context)
		{
			this.context = context;
		}

		@Override
		public int getCount()
		{
			if (fragments != null)
				return fragments.length;
			return 0;
		}

		@Override
		public Object getItem(int position)
		{
			if (fragments != null)
				return fragments[position];
			return null;
		}

		@Override
		public long getItemId(int position)
		{
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent)
		{
			convertView = LayoutInflater.from(context).inflate(R.layout.item_fragments, null);
			final TextView tvName = (TextView) convertView.findViewById(R.id.tv_name);
			final CheckBox cbShow = (CheckBox) convertView.findViewById(R.id.cb_show);
			tvName.setText(fragments[position]);
			cbShow.setChecked(values[position]);
			View.OnClickListener listenser = new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					switch (v.getId())
					{
						case R.id.layout_item:
						{
							if (cbShow.isChecked())
							{
								cbShow.setChecked(false);
							}
							else
							{
								cbShow.setChecked(true);
							}
							break;
						}
					}
				}
			};
			convertView.findViewById(R.id.layout_item).setOnClickListener(listenser);
			cbShow.setOnClickListener(listenser);
			cbShow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
			{
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
				{
					values[position] = isChecked;
				}
			});
			return convertView;
		}
	}
}

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
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.lrcall.appuser.R;
import cn.lrapps.enums.FloatType;
import cn.lrapps.utils.PreferenceUtils;

import java.util.ArrayList;
import java.util.List;

public class DialogSettingFloatBall extends Dialog implements OnClickListener
{
	private ListView lvFloatType;
	private CheckBox cbFloat;
	private String type = "";
	private List<View> views;
	private final DialogListenser listenser;
	private List<FloatType> typeList;

	public DialogSettingFloatBall(Context context, DialogListenser listenser)
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
		setContentView(R.layout.dialog_setting_float_ball);
		views = new ArrayList<>();
		typeList = new ArrayList<>();
		typeList.add(FloatType.FLOAT_BALL);
		typeList.add(FloatType.FLOAT_BOTTOM);
		typeList.add(FloatType.FLOAT_RIGHT);
		initView();
	}

	private void initView()
	{
		lvFloatType = (ListView) findViewById(R.id.list_types);
		cbFloat = (CheckBox) findViewById(R.id.cb_float);
		findViewById(R.id.dialog_btn_ok).setOnClickListener(this);
		findViewById(R.id.dialog_btn_cancel).setOnClickListener(this);
		type = PreferenceUtils.getInstance().getStringValue(PreferenceUtils.FLOAT_TYPE);
		lvFloatType.setAdapter(new ListTypes(this.getContext()));
		cbFloat.setChecked(PreferenceUtils.getInstance().getBooleanValue(PreferenceUtils.IS_SHOW_HOVER_BALL));
		if (views != null && views.size() > 0)
		{
			int size = views.size();
			for (int i = 0; i < size; i++)
			{
				//                final TextView tvName = (TextView) views.get(i).findViewById(R.id.tv_name);
				//                ((RadioButton) views.get(i).findViewById(R.id.rb_level)).setChecked(tvName.getText().toString().equals(type));
			}
		}
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.dialog_btn_ok:
			{
				PreferenceUtils.getInstance().setBooleanValue(PreferenceUtils.IS_SHOW_HOVER_BALL, cbFloat.isChecked());
				PreferenceUtils.getInstance().setStringValue(PreferenceUtils.FLOAT_TYPE, type);
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
			if (typeList != null)
				return typeList.size();
			return 0;
		}

		@Override
		public Object getItem(int position)
		{
			if (typeList != null)
				return typeList.get(position);
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
			convertView = LayoutInflater.from(context).inflate(R.layout.item_float_type, null);
			final TextView tvName = (TextView) convertView.findViewById(R.id.tv_name);
			final TextView tvDescription = (TextView) convertView.findViewById(R.id.tv_description);
			final RadioButton rbType = (RadioButton) convertView.findViewById(R.id.rb_type);
			tvName.setText(typeList.get(position).getType());
			tvDescription.setText(typeList.get(position).getDesc());
			rbType.setChecked(typeList.get(position).getType().equals(type));
			View.OnClickListener listenser = new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					switch (v.getId())
					{
						case R.id.rb_type:
						case R.id.layout_item:
						{
							type = typeList.get(position).getType();
							int size = views.size();
							for (int i = 0; i < size; i++)
							{
								((RadioButton) views.get(i).findViewById(R.id.rb_type)).setChecked(false);
							}
							rbType.setChecked(true);
							break;
						}
					}
				}
			};
			convertView.findViewById(R.id.layout_item).setOnClickListener(listenser);
			convertView.findViewById(R.id.rb_type).setOnClickListener(listenser);
			if (!views.contains(convertView))
			{
				views.add(convertView);
			}
			return convertView;
		}
	}
}

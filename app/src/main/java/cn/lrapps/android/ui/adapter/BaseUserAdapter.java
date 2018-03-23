/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.android.ui.adapter;

import android.content.Context;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by libit on 16/4/30.
 */
public abstract class BaseUserAdapter<T> extends BaseAdapter
{
	protected final Context context;
	protected List<T> list;

	public BaseUserAdapter(Context context, List<T> list)
	{
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount()
	{
		return list != null ? list.size() : 0;
	}

	@Override
	public Object getItem(int position)
	{
		return list != null ? list.get(position) : null;
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	public interface IItemClick<T>
	{
		void onItemClicked(final T t);
	}
}

/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.android.ui;

import com.external.xlistview.XListView;

public abstract class MyBasePageActivity extends MyBaseActivity implements XListView.IXListViewListener
{
	protected static final int PAGE_SIZE = 10;//每页获取多少数据
	private static final String TAG = MyBasePageActivity.class.getSimpleName();
	protected int mDataStart = 0;//开始位置
	protected int mDataTotal = 0;//总记录数
	protected XListView xListView;
	//	protected boolean isRefresh = false;//是否刷新,这里是为了保证数据刷新时如果少于原先的个数会FC的问题

	/**
	 * 获取每页加载的数据数量
	 *
	 * @return
	 */
	protected int getPageSize()
	{
		return PAGE_SIZE;
	}

	/**
	 * 重置初始位置
	 */
	protected void resetDataStart()
	{
		mDataStart = 0;
	}

	/**
	 * 获取上一页数据
	 */
	protected void goPreviousPage()
	{
		mDataStart = mDataStart - getPageSize();
		if (mDataStart < 0)
		{
			mDataStart = 0;
		}
	}

	/**
	 * 跳转到下一页
	 */
	protected void goNextPage()
	{
		mDataStart = mDataStart + getPageSize();
		if (mDataTotal > 0)
		{
			if (mDataStart > mDataTotal)
			{
				mDataStart = mDataTotal + 1;
			}
		}
	}

	/**
	 * 刷新数据
	 */
	abstract public void refreshData();

	/**
	 * 加载更多数据
	 */
	abstract public void loadMoreData();

	@Override
	public void onRefresh()
	{
		xListView.setPullLoadEnable(false);
		resetDataStart();
		refreshData();
	}

	@Override
	public void onLoadMore()
	{
		goNextPage();
		loadMoreData();
	}
}

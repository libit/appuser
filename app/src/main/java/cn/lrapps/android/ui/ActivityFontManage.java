/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.android.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.external.xlistview.XListView;
import com.lrcall.appuser.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.lrapps.android.ui.adapter.FontInfoAdapter;
import cn.lrapps.models.FontInfo;
import cn.lrapps.utils.ConstValues;
import cn.lrapps.utils.LogcatTools;
import cn.lrapps.utils.filetools.FileTools;

public class ActivityFontManage extends MyBasePageActivity implements View.OnClickListener
{
	private static final String TAG = ActivityFontManage.class.getSimpleName();
	private View layoutList, layoutNoData;
	private TextView tvTips;
	private List<FontInfo> mFontInfoList = new ArrayList<>();
	private FontInfoAdapter mFontInfoAdapter;
	private Handler mHandler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_font_manage);
		viewInit();
		onRefresh();
	}

	@Override
	protected void viewInit()
	{
		super.viewInit();
		setBackButton();
		layoutList = findViewById(R.id.layout_list);
		layoutNoData = findViewById(R.id.layout_no_data);
		xListView = (XListView) findViewById(R.id.xlist);
		xListView.setPullRefreshEnable(true);
		xListView.setPullLoadEnable(false);
		xListView.setXListViewListener(this);
		tvTips = (TextView) findViewById(R.id.tv_tips);
	}

	@Override
	public void refreshData()
	{
		mFontInfoList.clear();
		if (mFontInfoAdapter != null)
		{
			mFontInfoAdapter.notifyDataSetChanged();
		}
		mFontInfoAdapter = null;
		loadMoreData();
	}

	@Override
	public void loadMoreData()
	{
		final List<FontInfo> fontInfoList = new ArrayList<>();
		new Thread("load")
		{
			@Override
			public void run()
			{
				super.run();
				mFontInfoList.clear();
				search(FileTools.getUserDir(""), fontInfoList);
				mHandler.post(new Runnable()
				{
					@Override
					public void run()
					{
						refreshFontInfoList(fontInfoList);
					}
				});
			}
		}.start();
	}

	@Override
	public void onRefresh()
	{
		super.onRefresh();
		tvTips.setText("正在搜索字体...");
		tvTips.setVisibility(View.VISIBLE);
	}

	private void search(final String fileDir, List<FontInfo> fontInfoList)
	{
		LogcatTools.info("search", "搜索的路径：" + fileDir);
		mHandler.post(new Runnable()
		{
			@Override
			public void run()
			{
				tvTips.setText("搜索路径：" + fileDir);
			}
		});
		File file = new File(fileDir);
		if (file.exists())
		{
			if (file.isDirectory())
			{
				String[] list = file.list();
				for (String fileName : list)
				{
					File subFile = new File(fileDir + "/" + fileName);
					if (subFile.isDirectory())
					{
						search(subFile.getAbsolutePath(), fontInfoList);
					}
					else
					{
						String path = subFile.getPath();
						if (path.endsWith("ttf") || path.endsWith("TTF") || path.endsWith("ttc") || path.endsWith("TTC"))
						{
							fontInfoList.add(new FontInfo(subFile.getName(), "", subFile.getAbsolutePath(), subFile.length()));
						}
					}
				}
			}
		}
	}

	synchronized private void refreshFontInfoList(List<FontInfo> fontInfoList)
	{
		tvTips.setVisibility(View.GONE);
		xListView.stopRefresh();
		xListView.setPullLoadEnable(false);
		if (fontInfoList == null || fontInfoList.size() < 1)
		{
			if (mFontInfoList.size() < 1)
			{
				layoutList.setVisibility(View.GONE);
				layoutNoData.setVisibility(View.VISIBLE);
			}
			return;
		}
		layoutList.setVisibility(View.VISIBLE);
		layoutNoData.setVisibility(View.GONE);
		//		xListView.setPullLoadEnable(fontInfoList.size() >= getPageSize());
		for (FontInfo fontInfo : fontInfoList)
		{
			mFontInfoList.add(fontInfo);
		}
		if (mFontInfoAdapter == null)
		{
			FontInfoAdapter.IItemClick iItemClick = new FontInfoAdapter.IItemClick()
			{
				@Override
				public void onItemClicked(View v, final FontInfo fontInfo)
				{
					if (fontInfo != null)
					{
						Intent intent = new Intent(ActivityFontManage.this, ActivityFontInfo.class);
						intent.putExtra(ConstValues.DATA_FONT_URL, fontInfo.getUrl());
						ActivityFontManage.this.startActivity(intent);
					}
				}
			};
			mFontInfoAdapter = new FontInfoAdapter(this, mFontInfoList, iItemClick);
			xListView.setAdapter(mFontInfoAdapter);
		}
		else
		{
			mFontInfoAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.menu_activity_font_manage, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		int id = item.getItemId();
		if (id == R.id.action_refresh)
		{
			onRefresh();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}

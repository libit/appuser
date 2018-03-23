/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.android.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by libit on 16/7/14.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter
{
	private List<Fragment> fragmentList;
	private String[] titles;

	public SectionsPagerAdapter(FragmentManager fm, List<Fragment> fragmentList, String[] titles)
	{
		super(fm);
		this.fragmentList = fragmentList;
		this.titles = titles;
	}

	@Override
	public Fragment getItem(int position)
	{
		if (position >= 0 && position < fragmentList.size())
		{
			return fragmentList.get(position);
		}
		else
		{
			return null;
		}
	}

	@Override
	public int getCount()
	{
		return fragmentList != null ? fragmentList.size() : 0;
	}

	@Override
	public CharSequence getPageTitle(int position)
	{
		return titles[position];
	}
}

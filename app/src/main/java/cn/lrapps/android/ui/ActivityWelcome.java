/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.android.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;

import com.lrcall.appuser.R;

import java.util.ArrayList;
import java.util.List;

public class ActivityWelcome extends ActionBarActivity
{
	protected SectionsPagerAdapter mSectionsPagerAdapter;
	protected ViewPager mViewPager;
	List<FragmentWelcome> mFragmentWelcomes;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		initView();
	}

	public void initView()
	{
		mFragmentWelcomes = new ArrayList<>();
		mFragmentWelcomes.add(new FragmentWelcome().newInstance(R.drawable.welcome, false));
		mFragmentWelcomes.add(new FragmentWelcome().newInstance(R.drawable.default_app, false));
		mFragmentWelcomes.add(new FragmentWelcome().newInstance(R.drawable.item_bg, false));
		mFragmentWelcomes.add(new FragmentWelcome().newInstance(R.drawable.image_progress, true));
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener()
		{
			@Override
			public void onPageSelected(int position)
			{
			}
		});
	}

	public class SectionsPagerAdapter extends FragmentPagerAdapter
	{
		public SectionsPagerAdapter(FragmentManager fm)
		{
			super(fm);
		}

		@Override
		public Fragment getItem(int position)
		{
			if (position < mFragmentWelcomes.size())
			{
				return mFragmentWelcomes.get(position);
			}
			else
			{
				return null;
			}
		}

		@Override
		public int getCount()
		{
			return mFragmentWelcomes.size();
		}

		@Override
		public CharSequence getPageTitle(int position)
		{
			return "";
		}
	}
}

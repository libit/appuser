/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.android.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.lrcall.appuser.R;

/**
 * A simple {@link Fragment} subclass.
 * to handle interaction events.
 * Use the {@link FragmentWelcome#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentWelcome extends Fragment
{
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String ARG_IMAGE_RES = "ARG_IMAGE_RES";
	private static final String ARG_IS_LAST = "ARG_IS_LAST";
	private int mImageRes;
	private boolean mIsLast;
	private View rootView;
	private Button btnStart;
	private ImageView imageView;

	public FragmentWelcome()
	{
		// Required empty public constructor
	}

	public static FragmentWelcome newInstance(int imageRes, boolean isLast)
	{
		FragmentWelcome fragment = new FragmentWelcome();
		Bundle args = new Bundle();
		args.putInt(ARG_IMAGE_RES, imageRes);
		args.putBoolean(ARG_IS_LAST, isLast);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		if (getArguments() != null)
		{
			mImageRes = getArguments().getInt(ARG_IMAGE_RES);
			mIsLast = getArguments().getBoolean(ARG_IS_LAST);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// Inflate the layout for this fragment
		rootView = inflater.inflate(R.layout.fragment_welcome, container, false);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		initView();
		imageView.setImageResource(mImageRes);
		if (mIsLast)
		{
			btnStart.setVisibility(View.VISIBLE);
			btnStart.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					getActivity().finish();
				}
			});
		}
	}

	public void initView()
	{
		imageView = (ImageView) rootView.findViewById(R.id.iv_bg);
		btnStart = (Button) rootView.findViewById(R.id.btn_start);
	}
}

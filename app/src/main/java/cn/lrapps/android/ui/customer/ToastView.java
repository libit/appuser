/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.android.ui.customer;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lrcall.appuser.R;
import cn.lrapps.utils.viewtools.DisplayTools;

import java.util.Timer;
import java.util.TimerTask;

public class ToastView
{
	public static Toast toast;
	private int time;
	private Timer timer;
	private View view;
	private TextView t;
	private ImageView ivIcon;

	public ToastView(Context context, String text)
	{
		viewInit(context);
		t.setText(text);
		if (toast != null)
		{
			toast.cancel();
		}
		toast = new Toast(context);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(view);
	}

	public ToastView(Context context, int text)
	{
		viewInit(context);
		t.setText(text);
		if (toast != null)
		{
			toast.cancel();
		}
		toast = new Toast(context);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(view);
	}

	public ToastView(Context context, int imgRes, String text)
	{
		viewInit(context);
		ivIcon.setImageResource(imgRes);
		t.setText(text);
		if (toast != null)
		{
			toast.cancel();
		}
		toast = new Toast(context);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(view);
	}

	public ToastView(Context context, int imgRes, int text)
	{
		viewInit(context);
		ivIcon.setImageResource(imgRes);
		t.setText(text);
		if (toast != null)
		{
			toast.cancel();
		}
		toast = new Toast(context);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(view);
	}

	private void viewInit(Context context)
	{
		view = LayoutInflater.from(context).inflate(R.layout.toast_view, null);
		View layoutBg = view.findViewById(R.id.layout_bg);
		ViewGroup.LayoutParams layoutParams = layoutBg.getLayoutParams();
		layoutParams.width = DisplayTools.getWindowWidth(context) * 2 / 5;
		layoutParams.height = layoutParams.width;
		view.setLayoutParams(layoutParams);
		t = (TextView) view.findViewById(R.id.toast_text);
		ivIcon = (ImageView) view.findViewById(R.id.iv_icon);
	}

	public static void cancel()
	{
		if (toast != null)
		{
			toast.cancel();
		}
	}

	public static void showCenterToast(Context context, String tips)
	{
		ToastView toast = new ToastView(context, tips);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

	public static void showCenterToast(Context context, int imgRes, String tips)
	{
		ToastView toast = new ToastView(context, imgRes, tips);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

	//设置toast显示位置
	public void setGravity(int gravity, int xOffset, int yOffset)
	{
		//toast.setGravity(Gravity.CENTER, 0, 0); //居中显示
		toast.setGravity(gravity, xOffset, yOffset);
	}

	//设置toast显示时间
	public void setDuration(int duration)
	{
		toast.setDuration(duration);
	}

	//设置toast显示时间(自定义时间)
	public void setLongTime(int duration)
	{
		//toast.setDuration(duration);
		time = duration;
		timer = new Timer();
		timer.schedule(new TimerTask()
		{
			@Override
			public void run()
			{
				if (time - 1000 >= 0)
				{
					show();
					time = time - 1000;
				}
				else
				{
					timer.cancel();
				}
			}
		}, 0, 1000);
	}

	public void show()
	{
		toast.show();
	}
}

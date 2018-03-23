/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */

package cn.lrapps.utils;

import android.os.Build;
import android.util.Log;

import cn.lrapps.enums.LogLevel;
import cn.lrapps.utils.apptools.SystemToolsFactory;
import cn.lrapps.utils.filetools.FileTools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * 日志记录类
 */
public class LogcatTools implements Thread.UncaughtExceptionHandler
{
	private static final String TAG = LogcatTools.class.getSimpleName();
	private static LogcatTools instance = null;
	private Thread.UncaughtExceptionHandler mDefaultHandler;// 系统默认的UncaughtException处理类
	private LogDumper mLogDumper = null;
	private final int mPId;
	private String mFileName;

	private LogcatTools()
	{
		init();
		mPId = android.os.Process.myPid();
	}

	public static LogcatTools getInstance()
	{
		if (instance == null)
		{
			instance = new LogcatTools();
		}
		return instance;
	}

	public static void debug(String tag, String msg)
	{
		if (AppConfig.isDebug())
		{
			Log.d(tag, msg);
		}
	}

	public static void info(String tag, String msg)
	{
		//		if (AppConfig.isDebug())
		{
			Log.d(tag, msg);
		}
	}

	public static void error(String tag, String msg)
	{
		//		if (AppConfig.isDebug())
		{
			Log.e(tag, msg);
		}
	}

	private static String getFileName()
	{
		return "V" + SystemToolsFactory.getInstance().getVersionCode() + "_" + StringTools.getCurrentTimeNum() + ".log";
	}

	/**
	 * 初始化目录
	 */
	private void init()
	{
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();// 获取系统默认的UncaughtException处理器
		Thread.setDefaultUncaughtExceptionHandler(this);// 设置该CrashHandler为程序的默认处理器
	}

	/**
	 * 当UncaughtException发生时会转入该重写的方法来处理
	 */
	public void uncaughtException(Thread thread, Throwable ex)
	{
		if (!handleException(ex) && mDefaultHandler != null)
		{
			// 如果自定义的没有处理则让系统默认的异常处理器来处理
			mDefaultHandler.uncaughtException(thread, ex);
		}
	}

	/**
	 * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
	 *
	 * @param ex 异常信息
	 *
	 * @return true 如果处理了该异常信息;否则返回false.
	 */
	public boolean handleException(Throwable ex)
	{
		if (ex == null)
		{
			return false;
		}
		// 保存出错日志
		saveCrashFile(ex);
		//        android.os.Process.killProcess(android.os.Process.myPid());
		//        new DialogCommon(context, null, "提示", "哦哦，出错了！", true, false, false).show();
		//        Toast.makeText(context, "哦哦，出错了！", Toast.LENGTH_LONG).show();
		return false;
	}

	public void start()
	{
		if (mLogDumper == null)
		{
			mLogDumper = new LogDumper(String.valueOf(mPId));
		}
		if (!mLogDumper.isAlive())
		{
			mLogDumper.start();
		}
		//只保留最近3个log日志
		String dir = FileTools.getAppDir(AppConfig.getLogcatFolder());
		if (StringTools.isNull(dir))
		{
			LogcatTools.error(TAG, "日志目录为空！");
			return;
		}
		File directory = new File(dir);
		if (directory != null && directory.isDirectory())
		{
			File[] files = directory.listFiles();
			if (files == null)
			{
				return;
			}
			int count = files.length;
			final int MAX_SAVE = 5;
			if (count > MAX_SAVE)
			{
				List<File> saveFiles = new ArrayList<>();
				for (int i = 0; i < count; i++)
				{
					saveFiles.add(i, files[i]);
					for (int j = i; j > 0; j--)
					{
						try
						{
							File file1 = saveFiles.get(j);
							File file2 = saveFiles.get(j - 1);
							long date1 = file1.lastModified();
							long date2 = file2.lastModified();
							if (date1 > date2)
							{
								saveFiles.set(j - 1, file1);
								saveFiles.set(j, file2);
							}
							else
							{
								break;
							}
						}
						catch (Exception e)
						{
							saveFiles.remove(i);
						}
					}
				}
				LogcatTools.debug("saveFiles", "saveFiles:" + GsonTools.toJson(saveFiles));
				for (int i = saveFiles.size() - 1; i >= MAX_SAVE; i--)
				{
					if (saveFiles.get(i).isFile())
					{
						LogcatTools.debug("saveFiles", "delete:" + GsonTools.toJson(saveFiles.get(i)));
						saveFiles.get(i).delete();
					}
				}
			}
		}
	}

	public void stop()
	{
		if (mLogDumper != null)
		{
			mLogDumper.stopLogs();
			mLogDumper = null;
		}
		LogcatTools.debug(TAG + "stop", "停止记录日志！");
	}

	/**
	 * 保存当前记录的日志为出错日志
	 */
	public void saveCrashFile(Throwable ex)
	{
		if (!StringTools.isNull(mFileName))
		{
			PreferenceUtils.getInstance().setStringValue(PreferenceUtils.PREF_CRASH_FILE_NAME, mFileName);
			try
			{
				File file = FileTools.getFile(AppConfig.getLogcatFolder(), mFileName + ".err");
				PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
				pw.println("================================================================");
				pw.println(String.format("应用出错时间：%s。", StringTools.getCurrentTime()));
				pw.println(String.format("安卓系统版本：%s_%d。", Build.VERSION.RELEASE, Build.VERSION.SDK_INT));
				pw.println(String.format("  手机制造商：%s，品牌：%s，型号：%s。", Build.MANUFACTURER, Build.BRAND, Build.MODEL));
				pw.println(String.format("  处理器架构：%s。", Build.CPU_ABI));
				ex.printStackTrace(pw);
				pw.close();
				//提交日志
				//				new BugService(MyApplication.getContext()).uploadLogFile(null, true);
			}
			catch (Exception e)
			{
				e.printStackTrace();
				Log.e(TAG, "保存异常信息到文件失败！");
			}
		}
	}

	// 抓取日志，并写入文件
	private class LogDumper extends Thread
	{
		String cmds = null;
		private Process logcatProc;
		private BufferedReader mReader = null;
		private boolean mRunning = true;
		private final String mPID;
		private FileOutputStream out = null;

		public LogDumper(String pid)
		{
			mPID = pid;
			/**
			 * 日志等级：*:v , *:d , *:w , *:e , *:f , *:s 显示当前mPID程序的 E和W等级的日志.
			 */
			int level = PreferenceUtils.getInstance().getIntegerValue(PreferenceUtils.LOGCAT_LEVEL);
			if (level == LogLevel.LEVEL_0.getLevel())
			{
				cmds = "";
			}
			else
			{
				if (level == LogLevel.LEVEL_1.getLevel())
				{
					cmds = "logcat *:e | grep \"(" + mPID + ")\"";
				}
				else if (level == LogLevel.LEVEL_2.getLevel())
				{
					cmds = "logcat *:e *:w | grep \"(" + mPID + ")\"";
				}
				else if (level == LogLevel.LEVEL_3.getLevel())
				{
					cmds = "logcat *:e *:w *:i | grep \"(" + mPID + ")\"";
				}
				else if (level == LogLevel.LEVEL_4.getLevel())
				{
					cmds = "logcat *:e *:w *:i *:d | grep \"(" + mPID + ")\"";
				}
				else if (level == LogLevel.LEVEL_5.getLevel())
				{
					cmds = "logcat | grep \"(" + mPID + ")\"";
				}
				try
				{
					mFileName = getFileName();
					out = new FileOutputStream(FileTools.getFile(AppConfig.getLogcatFolder(), mFileName));
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}

		public void stopLogs()
		{
			mRunning = false;
		}

		@Override
		public void run()
		{
			try
			{
				if (!StringTools.isNull(cmds))
				{
					logcatProc = Runtime.getRuntime().exec(cmds);
					mReader = new BufferedReader(new InputStreamReader(logcatProc.getInputStream()), 10240);
					String line;
					while (mRunning && (line = mReader.readLine()) != null)
					{
						if (!mRunning)
						{
							break;
						}
						if (line.length() == 0)
						{
							continue;
						}
						if (out != null && line.contains(mPID))
						{
							out.write((StringTools.getCurrentTime() + "  " + line + "\n").getBytes());
						}
					}
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			finally
			{
				if (logcatProc != null)
				{
					logcatProc.destroy();
					logcatProc = null;
				}
				if (mReader != null)
				{
					try
					{
						mReader.close();
						mReader = null;
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
				if (out != null)
				{
					try
					{
						out.close();
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
					out = null;
				}
			}
		}
	}
}

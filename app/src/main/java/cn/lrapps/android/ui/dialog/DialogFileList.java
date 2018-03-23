/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.android.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

import cn.lrapps.android.ui.adapter.FileListAdapter;
import com.lrcall.appuser.R;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DialogFileList extends Dialog implements FileListAdapter.IFileListAdapter, View.OnClickListener
{
	private TextView tvTitle;
	private ListView listView;
	private FileListAdapter adapter;
	private File mRootFile, mCurrentDir;
	private final FilenameFilter filenameFilter;
	private final IDialogChooseFile dialogChooseFile;
	private final List<File> fileList = new ArrayList<>();
	private Comparator<File> fileComparator;

	public interface IDialogChooseFile
	{
		//已选择文件
		void onFileSelected(File file);
		//取消选择
		//		void onCancelled();
	}

	public DialogFileList(Context context, File rootFile, FilenameFilter filenameFilter, IDialogChooseFile dialogChooseFile, Comparator<File> fileComparator)
	{
		super(context, R.style.MyDialog);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		//		setCancelable(false);
		this.mRootFile = rootFile;
		this.mCurrentDir = rootFile;
		this.filenameFilter = filenameFilter;
		this.dialogChooseFile = dialogChooseFile;
		this.fileComparator = fileComparator;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_file_list);
		initView();
		initData(mRootFile);
	}

	private void initView()
	{
		tvTitle = (TextView) findViewById(R.id.tv_title);
		listView = (ListView) findViewById(R.id.list_view);
		listView.setAdapter(adapter);
		findViewById(R.id.dialog_btn_ok).setOnClickListener(this);
		findViewById(R.id.dialog_btn_cancel).setOnClickListener(this);
	}

	public void setDlgTitle(String title)
	{
		tvTitle.setText(title);
	}

	//初始化数据
	private void initData(File rootFile)
	{
		File[] files = null;
		if (filenameFilter == null)
		{
			files = rootFile.listFiles();
		}
		else
		{
			files = rootFile.listFiles(filenameFilter);
		}
		fileList.clear();
		//文件夹所在的位置
		int index = 0;
		if (files != null && files.length > 0)
		{
			for (File file : files)
			{
				if (file.isDirectory())
				{
					fileList.add(index++, file);
				}
				else
				{
					fileList.add(file);
				}
			}
			if (fileComparator != null)
			{
				Collections.sort(fileList, fileComparator);
			}
		}
		if (rootFile.getParentFile() != null)
		{
			fileList.add(0, rootFile.getParentFile());
		}
		else
		{
			fileList.add(0, rootFile);
		}
		if (adapter == null)
		{
			adapter = new FileListAdapter(getContext(), fileList, this);
			listView.setAdapter(adapter);
		}
		else
		{
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onFileSelected(File file)
	{
		if (file.isDirectory())
		{
			//			ToastView.showCenterToast(getContext(), "选择文件夹：" + file.getName());
			mCurrentDir = file;
			initData(file);
		}
		else
		{
			//			ToastView.showCenterToast(getContext(), "选择文件：" + file.getName());
			if (dialogChooseFile != null)
			{
				dialogChooseFile.onFileSelected(file);
			}
			dismiss();
		}
	}

	@Override
	public void onParentSelected(File file)
	{
		//		ToastView.showCenterToast(getContext(), "选择上一层：" + file.getName());
		//		rootFile = file;
		mCurrentDir = file;
		initData(file);
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.dialog_btn_ok:
			{
				if (dialogChooseFile != null)
				{
					dialogChooseFile.onFileSelected(mCurrentDir);
				}
				dismiss();
				break;
			}
			case R.id.dialog_btn_cancel:
			{
				dismiss();
				break;
			}
		}
	}
}

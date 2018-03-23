/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */

package cn.lrapps.db;

/**
 * Created by libit on 15/8/31.
 */
public class DbAppFactory
{
	private static final String TAG = DbAppFactory.class.getSimpleName();
	private static final String TABLE_NAME = DbConstant.TABLE_NAME_APP_INFO;
	private static DbAppService instance;

	protected DbAppFactory()
	{
	}

	public static DbAppService getInstance()
	{
		if (instance == null)
		{
			synchronized (DbAppService.class)
			{
				if (instance == null)
				{
					instance = new DbAppServiceImpl();
				}
			}
		}
		return instance;
	}
}

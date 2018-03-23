/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.db;

import cn.lrapps.models.AppInfo;

import java.util.List;

/**
 * Created by libit on 15/8/31.
 */
public interface DbAppService
{
	/**
	 * 添加App
	 *
	 * @param appInfo App对象
	 *
	 * @return 成功：true，失败：false
	 */
	boolean add(AppInfo appInfo);

	/**
	 * 更新App
	 *
	 * @param appInfo App对象
	 *
	 * @return 成功：true，失败：false
	 */
	boolean update(AppInfo appInfo);

	/**
	 * 添加或更新App
	 *
	 * @param appInfo App对象
	 *
	 * @return 成功：true，失败：false
	 */
	boolean addOrUpdate(AppInfo appInfo);

	/**
	 * 增加或更新App列表
	 *
	 * @param appInfoList App列表
	 *
	 * @return 增加成功的个数
	 */
	int addOrUpdateList(List<AppInfo> appInfoList);

	/**
	 * 删除App
	 *
	 * @param packageName App包名
	 *
	 * @return 成功：true，失败：false
	 */
	boolean delete(String packageName);

	/**
	 * 删除
	 *
	 * @return 删除条数
	 */
	int deleteNotExistApps();

	/**
	 * 清空App列表
	 */
	int deleteAll();

	/**
	 * 获取指定的App
	 *
	 * @param packageName App包名
	 *
	 * @return AppInfo
	 */
	AppInfo getAppInfo(String packageName);

	/**
	 * 获取App列表<br>
	 * 获取的都是存在的app
	 *
	 * @param enabledStatus 启用状态
	 * @param appType       App类型
	 * @param hideStatus    隐藏状态
	 * @param blackStatus   黑名单状态
	 *
	 * @return
	 */
	List<AppInfo> getAppInfoList(Integer enabledStatus, Integer appType, Integer hideStatus, Integer blackStatus, Integer commonUse);

	List<AppInfo> getAppInfoList(Integer enabledStatus, Integer appType, Integer hideStatus, Integer blackStatus, Integer commonUse, String orderCol, String orderType);

	/**
	 * 获取App列表数量
	 *
	 * @param enabledStatus 启用状态
	 * @param appType       App类型
	 * @param hideStatus    隐藏状态
	 * @param blackStatus   黑名单状态
	 *
	 * @return
	 */
	int getAppInfoListCount(Integer enabledStatus, Integer appType, Integer hideStatus, Integer blackStatus);

	/**
	 * 重置App状态
	 *
	 * @param isEnabled 是否启用
	 * @param isHide    是否隐藏
	 * @param isExist   是否存在
	 *
	 * @return 重置条数
	 */
	int resetAppStatus(Boolean isEnabled, Boolean isHide, Boolean isExist);

	/**
	 * 查询状态
	 *
	 * @param packageName
	 * @param fieldName
	 *
	 * @return
	 */
	int getStatus(String packageName, String fieldName);

	/**
	 * 查询包名是否存在
	 *
	 * @param packageName 包名
	 *
	 * @return
	 */
	boolean isExist(String packageName);

	/**
	 * 查询包名是否隐藏
	 *
	 * @param packageName 包名
	 *
	 * @return
	 */
	boolean isHide(String packageName);

	/**
	 * 查询包名是否黑名单
	 *
	 * @param packageName 包名
	 *
	 * @return
	 */
	boolean isBlack(String packageName);

	/**
	 * 设置App为存在
	 *
	 * @param packageName 应用包名
	 * @param isExist     是否存在
	 *
	 * @return
	 */
	boolean setAppExist(String packageName, boolean isExist);

	/**
	 * 设置App为隐藏
	 *
	 * @param packageName 应用包名
	 * @param isHide      是否隐藏
	 *
	 * @return
	 */
	boolean setAppHide(String packageName, boolean isHide);

	/**
	 * 设置App为禁用
	 *
	 * @param packageName 应用包名
	 * @param isBlack     是否禁用
	 *
	 * @return
	 */
	boolean setAppBlack(String packageName, boolean isBlack);

	/**
	 * 设置APP是否为常用
	 *
	 * @param packageName
	 * @param commonUse
	 *
	 * @return
	 */
	boolean setAppCommonUse(String packageName, int commonUse);
}

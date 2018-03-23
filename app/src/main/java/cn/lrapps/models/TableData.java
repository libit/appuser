/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by libit on 16/7/14.
 */
public class TableData
{
	@SerializedName("draw")
	private long draw;
	@SerializedName("start")
	private long start;
	@SerializedName("recordsTotal")
	private long recordsTotal;
	@SerializedName("recordsFiltered")
	private long recordsFiltered;
	@SerializedName("data")
	private List data;

	public TableData()
	{
		super();
	}

	public TableData(long draw, long start, long recordsTotal, long recordsFiltered, List data)
	{
		// recordsTotal 和 recordsFiltered数量应该相等
		super();
		this.draw = draw;
		this.start = start;
		this.recordsTotal = recordsTotal;
		this.recordsFiltered = recordsTotal;
		this.data = data;
	}

	public long getDraw()
	{
		return draw;
	}

	public void setDraw(long draw)
	{
		this.draw = draw;
	}

	public long getStart()
	{
		return start;
	}

	public void setStart(long start)
	{
		this.start = start;
	}

	public long getRecordsTotal()
	{
		return recordsTotal;
	}

	public void setRecordsTotal(long recordsTotal)
	{
		this.recordsTotal = recordsTotal;
	}

	public long getRecordsFiltered()
	{
		return recordsFiltered;
	}

	public void setRecordsFiltered(long recordsFiltered)
	{
		this.recordsFiltered = recordsFiltered;
	}

	public List getData()
	{
		return data;
	}

	public void setData(List data)
	{
		this.data = data;
	}
}

package com.bornfire.entity;

import java.util.List;

public class DataTableCBS {
	private int draw;
    private int start;
    private long recordsTotal;
    private long recordsFiltered;
    private List<TranCimCBSTable> data;
	public int getDraw() {
		return draw;
	}
	public void setDraw(int draw) {
		this.draw = draw;
	}
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public long getRecordsTotal() {
		return recordsTotal;
	}
	public void setRecordsTotal(long recordsTotal) {
		this.recordsTotal = recordsTotal;
	}
	public long getRecordsFiltered() {
		return recordsFiltered;
	}
	public void setRecordsFiltered(long recordsFiltered) {
		this.recordsFiltered = recordsFiltered;
	}
	public List<TranCimCBSTable> getData() {
		return data;
	}
	public void setData(List<TranCimCBSTable> data) {
		this.data = data;
	}
}

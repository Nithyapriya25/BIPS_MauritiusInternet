package com.bornfire.entity;

import java.util.List;

public class DataTable {
	private int draw;
    private int start;
    private long recordsTotal;
    private long recordsFiltered;
    private List<TransationMonitorPojo> data;
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
	public List<TransationMonitorPojo> getData() {
		return data;
	}
	public void setData(List<TransationMonitorPojo> data) {
		this.data = data;
	}
	
}

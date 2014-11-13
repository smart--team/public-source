package com.keyue.qlm.util;

import java.util.HashMap;
import java.util.List;

public class PageUtil {
	private int pageindex=1;
	private int totalcount=-1;
	private int pagesize=10;
	private List<HashMap<String, Object>> hashMaps;
	public int getPageindex() {
		return pageindex;
	}
	public void setPageindex(int pageindex) {
		this.pageindex = pageindex;
	}
	public int getTotalcount() {
		return totalcount;
	}
	public void setTotalcount(int totalcount) {
		this.totalcount = totalcount;
	}
	public List<HashMap<String, Object>> getHashMaps() {
		return hashMaps;
	}
	public void setHashMaps(List<HashMap<String, Object>> hashMaps) {
		this.hashMaps = hashMaps;
	}
	public int getPagesize() {
		return pagesize;
	}
	public void setPagesize(int pagesize) {
		this.pagesize = pagesize;
	}
	
	
	

}

package com.beike.page; 

import com.beike.util.Constant;

/**
 * Title : 	Pager
 * <p/>
 * Description	: 保存分页信息类
 * <p/>
 * CopyRight : CopyRight (c) 2011
 * </P>
 * Company : Sinobo
 * </P>
 * JDK Version Used	: JDK 5.0 +
 * <p/>
 * Modification History		:
 * <p/>
 * <pre>NO.    Date    Modified By    Why & What is modified</pre>
 * <pre>1     2011-5-23   lvjx			Created<pre>
 * <p/>
 *
 * @author  lvjx
 * @version 1.0.0.2011-5-23  
 */
public class Pager {
	private int totalRows=0; // 总行数

	private int pageSize=Constant.PAGE_SIZE; // 每页显示的行数

	private int currentPage=1; // 当前页号

	private int totalPages=1; // 总页数

	private int startRow=0; // 当前页在数据库中的起始行

	private String linkUrl; // 要跳转的URL

	public Pager() {
	}

	public Pager(int _totalRows, int _pageSize) {
		totalRows = _totalRows;
		pageSize = _pageSize;
		totalPages = totalRows / pageSize;
		int mod = totalRows % pageSize;
		if (mod > 0) {
			totalPages++;
		}
		currentPage = 1;
		startRow = 0;
		
	}
	public Pager(int currentPage,int _totalRows, int _pageSize)
	{
		 int totalPages1 = _totalRows / _pageSize;
		 int mod1 = _totalRows % _pageSize;
		 
		totalRows = _totalRows;
		pageSize = _pageSize;
			
		if (mod1 > 0) {
			totalPages1++;
		}
		if(currentPage>totalPages1){
			currentPage=currentPage-1;
		}
		if (currentPage==0) {
			this.setStart(1);
		}else{
			this.setStart(currentPage);
		} 
		

		totalPages = totalRows / pageSize;
		int mod = totalRows % pageSize;
		if (mod > 0) {
			totalPages++;
		}else{
			if(currentPage==0){
				currentPage = 1;
			}
		}
			
		
	}



	/**
	 * 
	 * 
	 * @param currentPage
	 */

	public void setStart(int currentPage) {
		this.currentPage = currentPage;
		startRow = (currentPage - 1) * pageSize;
	}

	public int getTotalRows() {
		return totalRows;
	}

	public void setTotalRows(int totalRows) {
		this.totalRows = totalRows;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getCurrentPage() {
		if(currentPage==0)
		{
			currentPage=1;
		}
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {

		this.currentPage = currentPage;
	}

	public int getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

	public int getStartRow() {
		return startRow;
	}

	public void setStartRow(int startRow) {
		this.startRow = startRow;
	}

	public String getLinkUrl() {
		return linkUrl;
	}

	public void setLinkUrl(String linkUrl) {
		this.linkUrl = linkUrl;
	}
}

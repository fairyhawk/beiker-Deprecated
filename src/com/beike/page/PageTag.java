package com.beike.page;

import java.io.IOException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * Title : 	PageTag
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
@SuppressWarnings("serial")
public class PageTag extends TagSupport {
	//private static final long serialVersionUID = 1L;

	private Pager pager;

	@SuppressWarnings("static-access")
	public int doStartTag() {

		try {
			JspWriter out = pageContext.getOut();
//			out.print("<form  id='pageForm' action='" + pager.getLinkUrl()+ "'method='post' >");
			if(pager.getTotalRows()>0 &&(pager.getTotalPages()<=1 )){
				out.print("<div class='product_list_page'>");
				out.print("<span class='disabled'>共" + pager.getTotalRows()+"条</span>");
				out.print("</div>");
			}
			if(pager.getTotalPages()>1){
				out.print("<div class='product_list_page'>");
				out.print("<span class='disabled'>共" + pager.getTotalRows()+"条</span>");
				if (pager.getCurrentPage() == 1) {
					out.print("<span class='disabled'>&lt; 首页</span>");
				}
				if (pager.getCurrentPage() != 1) {
					out.print("<a href='javascript:gopage(1);'>&lt; 首页</a>");
					out.print("<a href='javascript:gopage("+(pager.getCurrentPage() - 1)+"); '>&lt; 上一页</a>");
				}

				for (int i = pager.getCurrentPage() - 3; i <= pager
						.getCurrentPage() + 3; i++) {
					if (i <= 0 || i > pager.getTotalPages()) {
						continue;
					}
					if (i == pager.getCurrentPage()) {
						out.print("<span class='current'>"+i+"</span>");
					} else {
						out.print("<a href='javascript:gopage("+i+");'>"+i+"</a>");
					}
				}
				
				if(pager.getCurrentPage()==pager.getTotalPages()){
					out.print("<span class='disabled'>末页 &gt;</span>");
				}
				
				if (pager.getCurrentPage() != pager.getTotalPages()&& pager.getTotalPages() != 0) {
					out.print("<a href='javascript:gopage("+(pager.getCurrentPage() + 1)+");'>下一页 &gt;</a>");
					out.print("<a href='javascript:gopage("+pager.getTotalPages()+");'>末页 &gt;</a>");
				}
//				out.print("</form>");
				out.print("</div>");
				out.flush();
			}
			
		

		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return super.SKIP_BODY;
	}

	public int doEndTag() {
		return EVAL_PAGE;
	}

	public void setPager(Pager pager) {
		this.pager = pager;
	}

	public Pager getPager() {
		return pager;
	}

	public void release() {
		super.release();
	}
}

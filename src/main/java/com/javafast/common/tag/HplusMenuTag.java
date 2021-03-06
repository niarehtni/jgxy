package com.javafast.common.tag;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.javafast.common.config.Global;
import com.javafast.common.utils.SpringContextHolder;
import com.javafast.modules.sys.entity.Menu;
import com.javafast.modules.sys.utils.UserUtils;

/**
 * H+主题下菜单标签
 * @author JavaFast
 */
public class HplusMenuTag extends TagSupport {
	
	private static final long serialVersionUID = 1L;
	protected Menu menu;//菜单Map
	
	

	public Menu getMenu() {
		return menu;
	}

	public void setMenu(Menu menu) {
		this.menu = menu;
	}

	public int doStartTag() throws JspTagException {
		return EVAL_PAGE;
	}

	public int doEndTag() throws JspTagException {
		try {
			JspWriter out = this.pageContext.getOut();
			String menu = (String) this.pageContext.getSession().getAttribute("menu");
			if(menu!=null){
				out.print(menu);
			}else{
				menu=end().toString();
				out.print(menu);
				
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return EVAL_PAGE;
	}

	public StringBuffer end() {
		StringBuffer sb = new StringBuffer();
		sb.append(getChildOfTree(menu,0, UserUtils.getMenuList()));
		return sb;
		
	}
	
	private static String getChildOfTree(Menu parent, int level, List<Menu> menuList) {
		StringBuffer menuString = new StringBuffer();
		String href = "";
		if (!parent.hasPermisson())
			return "";
		if (level > 0) {//level 为0是功能菜单

			menuString.append("<li class=\"mm-dropdown\" >");

			ServletContext context = SpringContextHolder
					.getBean(ServletContext.class);
			if (parent.getHref() != null && parent.getHref().length() > 0) {
				
				
				if (parent.getHref().startsWith("http://")) {// 如果是互联网资源
					href =  parent.getHref();
				} else if(parent.getHref().endsWith(".html")&&!parent.getHref().endsWith("ckfinder.html")){//如果是静态资源并且不是ckfinder.html，直接访问不加adminPath
					href = context.getContextPath() + parent.getHref();
				}else{
					href = context.getContextPath() + Global.getAdminPath()
					+ parent.getHref();
				}
			}
		}


		if ((parent.getHref() == null || parent.getHref().trim().equals("")) && parent.getIsShow().equals("1")) {//如果是父节点且显示
			if (level > 0) {
			menuString
					.append("<a href=\""
							+ href
							+ "\"><i class=\"fa "+parent.getIcon()+"\"></i> <span class=\"mm-text\">"
							+ parent.getName()
							+ "</span></a>");
			}
			if (level == 1) {
				menuString.append("<ul>");
			} else if (level == 2) {
				menuString.append("<ul>");
			} else if (level == 3) {
				menuString.append("<ul>");
			} else if (level == 4) {
				menuString.append("<ul>");
			}
			for (Menu child : menuList) {
				if (child.getParentId().equals(parent.getId())&&child.getIsShow().equals("1")) {
					menuString.append(getChildOfTree(child, level + 1, menuList));
				}
			}
			if (level > 0) {
			menuString.append("</ul>");
			}
		} else {
			menuString.append("<li><a class=\"J_menuItem\" target=\"mainFrame\"  href=\"" + href
					+ "\" ><i class=\"fa "+parent.getIcon()+"\"></i> <span class=\"mm-text\">"+parent.getName()+"</span></a></li>");
		}
		if (level > 0) {
			menuString.append("</li>");
		}

		return menuString.toString();
	}
	
	

}

package org.sagebionetworks.bridge.webapp.jsp;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;

public class DataTableTag extends SimpleTagSupport {

	private static final Logger logger = LogManager.getLogger(DataTableTag.class.getName());
	
	private TagBuilder tb = new TagBuilder();
	
	private SimpleDateFormat formatter = new SimpleDateFormat("MMMM dd, YYYY");
	
	private PropertyUtilsBean pub = new PropertyUtilsBean();
	
	private String formId;
	private String itemId;
	private String action;
	private Collection<Object> items;
	private String caption;
	private boolean selectable;
	
	private List<DataTableColumnTag> columns = Lists.newArrayList();
	private List<DataTableButtonTag> globalButtons = Lists.newArrayList();
	private List<DataTableButtonTag> buttons = Lists.newArrayList();
	
	public void setFormId(String formId) {
		this.formId = formId;
	}
	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public void setItems(Collection<Object> items) {
		this.items = items;
	}
	public void setCaption(String caption) {
		this.caption = caption;
	}
	public void addColumn(DataTableColumnTag column) {
		this.columns.add(column);
	}
	public void addButton(DataTableButtonTag button) {
		if (this.columns.size() > 0) {
			this.buttons.add(button);	
		} else {
			this.globalButtons.add(button);
		}
	}
	
	@Override
	public void doTag() throws JspException, IOException {
		boolean noData = (this.items == null || this.items.size() == 0);
		
		if (getJspBody() != null) {
			getJspBody().invoke(null);
		}
		// No buttons related to items in the table, so selectability.
		this.selectable = (this.buttons.size() > 0);
		createPreTableSection();

		tb.startTag("form", "role", "role", "method", "post", "action", getContextPath() + this.action);
		if (this.formId != null) {
			tb.addAttribute("id", this.formId);	
		}
		
		tb.startTag("table");
		if (this.selectable) {
			tb.addStyleClass("table", "table-hover", "table-selectable");	
		} else {
			tb.addStyleClass("table", "table-hover");
		}
		tb.fullTag("caption", this.caption);
		createTableHead(noData);
		if (noData) {
			createEmptyTableBody();
		} else {
			createTableBody();
			createTableFooter();
		}
		tb.endTag("table");
		tb.endTag("form");
		getJspContext().getOut().write(tb.toString());
	}
	
	protected void createPreTableSection() {
		tb.startTag("div", "class", "table-buttons");
		createGlobalTableButtons();
		// Pagination goes here
		tb.endTag("div");
	}
	protected void createGlobalTableButtons() {
		tb.startTag("div");
		for (DataTableButtonTag button : globalButtons) {
			tb.startTag("a");
			tb.addAttribute("id", button.getId());
			tb.addAttribute("href", getContextPath() + button.getAction());
			tb.addStyleClass("btn", "btn-sm", "btn-"+button.getType());
			if (StringUtils.isNotBlank(button.getIcon())) {
				tb.startTag("span");
				tb.addStyleClass("glyphicon", "glyphicon-"+button.getIcon());
				tb.append(""); // force non-empty closing tag.
				tb.endTag("span");
				tb.append(" ");
			}
			tb.append(button.getLabel());
			tb.endTag("a");
		}
		tb.endTag("div");
	}
	protected void createEmptyTableBody() {
		tb.startTag("tbody");
		tb.startTag("tr");
		tb.startTag("td", "class", "empty");
		tb.addAttribute("colspan", Integer.toString(this.columns.size()));
		tb.append("There are currently no "+this.caption.toLowerCase()+".");
		tb.endTag("td");
		tb.endTag("tr");
		tb.endTag("tbody");
	}	
	protected void createTableBody() {
		tb.startTag("tbody");
		for (Object object : items) {
			createRow(object);
		}
		tb.endTag("tbody");
	}
	protected void createRow(Object object) {
		try {
			tb.startTag("tr");
			String objectId = BeanUtils.getProperty(object, this.itemId);
			addCheckboxIfSelectable(objectId);
			for (DataTableColumnTag column : columns) {
				tb.startTag("td");
				if (column.getClassName() != null) {
					tb.addAttribute("class", column.getClassName());
				}
				if (column.getIcon() != null) {
					tb.startTag("span", "class", "glyphicon glyphicon-"+column.getIcon());
					tb.append(""); // force non-empty closing tag.
					tb.endTag("span");
					tb.append(" ");
				}
				Object value = getColumnValue(column, object);
				if (StringUtils.isNotBlank(column.getLink())) {
					// TODO: Figure out how to do this correctly so there can be any expression here.
					String output = column.getLink().replace("{id}", objectId);
					
					tb.startTag("a", "href", getContextPath() + output);
					tb.append(value);
					tb.endTag("a");
				} else {
					tb.append(value);	
				}
				tb.endTag("td");
			}
			tb.endTag("tr");
		} catch(Exception e) {
			logger.error(e);
		}
	}
	protected Object getColumnValue(DataTableColumnTag column, Object object) {
		Object value = "";
		if (column.getStatic() != null) {
			value = column.getStatic();
		} else if (".".equals(column.getField())) {
			value = column.getField();
		} else {
			try {
				value = pub.getProperty(object, column.getField());
			} catch (Exception e) {
				logger.error(e);
			}
		}
		if (value instanceof Date) {
			value = formatter.format(value);
		}
		return value;
	}
	
	protected void addCheckboxIfSelectable(String objectId) {
		if (this.selectable) {
			tb.startTag("td");
			tb.startTag("input", "type", "checkbox", "name", "rowSelect", "title", "Select Row", "value", objectId);
			tb.endTag("input");
			tb.endTag("td");
		}
	}
	protected void createTableHead(boolean noData) {
		tb.startTag("thead");
		tb.startTag("tr");
		if (this.selectable && !noData) {
			tb.startTag("th", "class", "checkrow");
			tb.endTag("th");
		}
		for (DataTableColumnTag column : columns) {
			tb.fullTag("th" , column.getLabel());
		}
		tb.endTag("tr");
		tb.endTag("thead");
	}
	protected void createTableFooter() {
		if (this.selectable) {
			tb.startTag("tfoot");
			tb.startTag("tr");
			tb.startTag("td");
			tb.startTag("input", "type", "checkbox", "name", "masterSelect", "title", "Select All Rows");
			tb.endTag("input");
			tb.endTag("td");
			tb.startTag("td");
			tb.addAttribute("colspan", Integer.toString(this.columns.size()));
			
			for (DataTableButtonTag button : buttons) {
				tb.startTag("button", "type", "submit");
				tb.addAttribute("id", button.getId());
				tb.addAttribute("name", button.getAction());
				tb.addAttribute("value", button.getAction());
				tb.addStyleClass("btn", "btn-xs", "disabled", "btn-"+button.getType());
				if (StringUtils.isNotBlank(button.getIcon())) {
					tb.startTag("span");
					tb.addStyleClass("glyphicon", "glyphicon-"+button.getIcon());
					tb.append(""); // force non-empty closing tag.
					tb.endTag("span");
					tb.append(" ");
				}
				tb.append(button.getLabel());
				tb.endTag("button");
			}		
			
			tb.endTag("td");
			tb.endTag("tr");
			tb.endTag("tfoot");
		}
	}
	private String getContextPath() {
		PageContext pageContext = (PageContext)getJspContext();
		return pageContext.getServletContext().getContextPath();
	}
}

package org.sagebionetworks.bridge.webapp.jsp;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sagebionetworks.bridge.model.data.ParticipantDataRow;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataValue;
import org.sagebionetworks.bridge.webapp.specs.FormElement;
import org.sagebionetworks.bridge.webapp.specs.ParticipantDataUtils;
import org.sagebionetworks.bridge.webapp.specs.Specification;

import com.google.common.collect.Lists;

public class SpecificationDataTableTag extends SimpleTagSupport {
	
	private static final Logger logger = LogManager.getLogger(SpecificationDataTableTag.class.getName());
	
	private TagBuilder tb = new TagBuilder();

	private String formId;
	private String action;
	private Specification specification;
	private SpecificationDataTableColumnTag column;
	private List<ParticipantDataRow> items;
	private String caption;
	private boolean selectable;

	private List<DataTableButtonTag> globalButtons = Lists.newArrayList();
	private List<DataTableButtonTag> buttons = Lists.newArrayList();

	public void setFormId(String formId) {
		this.formId = formId;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public void setItems(List<ParticipantDataRow> items) {
		this.items = items;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}
	
	public void setSpecificationColumn(SpecificationDataTableColumnTag column) {
		this.column = column;
	}
	
	public void setSpecification(Specification spec) {
		this.specification = spec;
	}

	public void addButton(DataTableButtonTag button) {
		if (this.column != null) {
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
			// Adding hover causes visual problems with the data table when it collapses for mobile. So don't add it.
			tb.addStyleClass("table", "table-selectable");
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
			tb.addStyleClass("btn", "btn-sm", "btn-" + button.getType());
			if (StringUtils.isNotBlank(button.getIcon())) {
				tb.startTag("span");
				tb.addStyleClass("glyphicon", "glyphicon-" + button.getIcon());
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
		tb.addAttribute("colspan", Integer.toString(this.specification.getTableFields().size()));
		tb.append("There are currently no " + this.caption.toLowerCase() + ".");
		tb.endTag("td");
		tb.endTag("tr");
		tb.endTag("tbody");
	}

	protected void createTableBody() {
		tb.startTag("tbody", "class", "dataRows");
		for (int i = 0; i < items.size(); i++) {
			createRow(items.get(i), i);
		}
		tb.endTag("tbody");
	}

	protected void createRow(ParticipantDataRow row, int index) {
		try {
			tb.startTag("tr");
			addCheckboxIfSelectable(row.getRowId());
			
			for (Map.Entry<String,FormElement> specRow : specification.getTableFields().entrySet()) {
				String fieldName = specRow.getKey();
				FormElement element = specRow.getValue();
				tb.startTag("td");
				String value = getColumnValue(element, row.getData().get(fieldName));
				if (StringUtils.isNotBlank(column.getLink())) {
					String output = column.getLink().replace("{id}", row.getRowId().toString());
					tb.startTag("a", "href", getContextPath() + output);
					tb.append(value);
					tb.endTag("a");
				} else {
					tb.append(value);
				}
				tb.endTag("td");
			}
			tb.endTag("tr");
		} catch (Exception e) {
			logger.error(e);
		}
	}

	protected String getColumnValue(FormElement element, ParticipantDataValue pdv) {
		String value = ParticipantDataUtils.getOneValue(element.getStringConverter().convert(pdv));
		if (value == null) {
			value = "";
		}
		return value;
	}

	protected void addCheckboxIfSelectable(Long rowId) {
		if (this.selectable) {
			tb.startTag("td");
			tb.startTag("input", "type", "checkbox", "name", "rowSelect", "title", "Select Row", "value", rowId.toString());
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
		for (FormElement element : specification.getTableFields().values()) {
			tb.fullTag("th", element.getLabel());
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
			tb.addAttribute("colspan", Integer.toString(specification.getTableFields().size()));

			for (DataTableButtonTag button : buttons) {
				tb.startTag("button", "type", "submit");
				tb.addAttribute("id", button.getId());
				tb.addAttribute("name", button.getAction());
				tb.addAttribute("value", button.getAction());
				tb.addStyleClass("btn", "btn-xs", "disabled", "btn-" + button.getType());
				if (button.getConfirm() != null) {
					tb.addAttribute("data-confirm", button.getConfirm());
				}
				if (StringUtils.isNotBlank(button.getIcon())) {
					tb.startTag("span");
					tb.addStyleClass("glyphicon", "glyphicon-" + button.getIcon());
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
		PageContext pageContext = (PageContext) getJspContext();
		return pageContext.getServletContext().getContextPath();
	}
	
}

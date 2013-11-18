package org.sagebionetworks.bridge.webapp.jsp;

import java.util.Stack;

import org.apache.commons.lang3.StringUtils;

/**
 * Garden variety <code>StringBuilder</code> optimized for creating XML-compliant mark-up, useful 
 * for creating tags.
 */
public class TagBuilder {
	
	private StringBuilder sb;
	private boolean withinTag;
	private Stack<String> tagStack;

	public TagBuilder() {
		sb = new StringBuilder();
		tagStack = new Stack<String>();
		withinTag = false;
	}

	public TagBuilder startTag(String tagName, String... atts) {
		if (tagName != null) {
			if (withinTag) {
				sb.append(">");
			}
			tagStack.push(tagName);
			withinTag = true;
			sb.append("<").append(tagName);
			
			if (atts != null) {
				for (int i=0; i < atts.length; i+=2) {
					this.addAttribute(atts[i], atts[i+1]);
				}
			}
		}
		return this;
	}
	
	public void fullTag(String tagName, String content) {
		this.startTag(tagName).append(content).endTag(tagName);
	}

	public void addAttribute(String localName, String value) {
		if (localName == null) {
			throw new IllegalArgumentException("Attribute name cannot be null.");
		}
		if (value == null || !withinTag) {
			return;
		}
		sb.append(" ").append(localName).append("=\"").append(value).append("\"");
	}

	public void addStyleClass(String... styleClasses) {
		if (styleClasses != null & styleClasses.length > 0) {
			addAttribute("class", StringUtils.join(styleClasses, " "));
		}
	}

	public TagBuilder append(boolean b) {
		close();
		sb.append(b);
		return this;
	}

	public TagBuilder append(char c) {
		close();
		sb.append(c);
		return this;
	}

	public TagBuilder append(char[] str) {
		close();
		sb.append(str);
		return this;
	}

	public TagBuilder append(char[] str, int offset, int len) {
		close();
		sb.append(str, offset, len);
		return this;
	}

	public TagBuilder append(CharSequence s) {
		if (s != null) {
			close();
			sb.append(s);
		}
		return this;
	}

	public TagBuilder append(double d) {
		close();
		sb.append(d);
		return this;
	}

	public TagBuilder append(float f) {
		close();
		sb.append(f);
		return this;
	}

	public TagBuilder append(int i) {
		close();
		sb.append(i);
		return this;
	}

	public TagBuilder append(long lng) {
		close();
		sb.append(lng);
		return this;
	}

	public TagBuilder append(Object obj) {
		if (obj != null) {
			close();
			sb.append(obj);
		}
		return this;
	}

	public TagBuilder append(String str) {
		if (str != null) {
			close();
			sb.append(str);
		}
		return this;
	}

	public TagBuilder append(StringBuffer sb2) {
		if (sb2 != null) {
			close();
			sb.append(sb2);
		}
		return this;
	}
/*
	public void endTag() {
		if (tagStack.size() == 0) {
			return;
		}
		if (withinTag) {
			tagStack.pop();
			sb.append(" />");
		} else {
			sb.append("</").append((String) tagStack.pop()).append(">");
		}
		withinTag = false;
	}
*/
    
    public void endTag(String endTag) {
        if (tagStack.size() == 0 || endTag == null) {
            return;
        }
        if (!endTag.equals(tagStack.peek())) {
            throw new RuntimeException("End tag for: " + endTag + ", when stack is on: " + tagStack.peek());
        }
        if (withinTag) {
            tagStack.pop();
            sb.append(" />");
        } else {
            sb.append("</").append((String) tagStack.pop()).append(">");
        }
        withinTag = false;
    }
	
	public int length() {
        if (tagStack.size() == 0) {
            return sb.length();
        } 
        TagBuilder copy = this.copy();
        copy.toString();
        return copy.length();
	}

	public String toString() {
		while (tagStack.size() > 0) {
			endTag((String)tagStack.peek());
		}
		return sb.toString();
	}

	private void close() {
		if (withinTag) {
			sb.append(">");
		}
		withinTag = false;
	}
	
	@SuppressWarnings("unchecked")
	private TagBuilder copy() {
		TagBuilder tb = new TagBuilder();
		tb.sb = new StringBuilder(this.sb.toString());
		tb.tagStack = (Stack<String>)this.tagStack.clone();
		tb.withinTag = this.withinTag;
		return tb;
	}
}

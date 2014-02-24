package org.sagebionetworks.bridge.webapp.jsp;

import java.io.IOException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Vector;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.jstl.core.Config;
import javax.servlet.jsp.jstl.fmt.LocalizationContext;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.taglibs.standard.resources.Resources;
import org.apache.taglibs.standard.tag.common.core.Util;
import org.apache.taglibs.standard.tag.common.fmt.BundleSupport;
import org.apache.taglibs.standard.tag.common.fmt.RequestEncodingSupport;
import org.apache.taglibs.standard.tag.common.fmt.SetLocaleSupport;
import org.apache.taglibs.standard.tag.common.fmt.TimeZoneSupport;

@SuppressWarnings("serial")
public class FormatDateTag extends TagSupport {

	// *********************************************************************
	// Private constants

	private static final String DATE = "date";
	private static final String TIME = "time";
	private static final String DATETIME = "both";

	// *********************************************************************
	// Protected state

	protected Date value; // 'value' attribute
	protected String type; // 'type' attribute
	protected String pattern; // 'pattern' attribute
	protected Object timeZone; // 'timeZone' attribute
	protected String dateStyle; // 'dateStyle' attribute
	protected String timeStyle; // 'timeStyle' attribute

	// *********************************************************************
	// Private state

	private String var; // 'var' attribute
	private int scope; // 'scope' attribute

	// *********************************************************************
	// Constructor and initialization

	public FormatDateTag() {
		super();
		init();
	}

	private void init() {
		type = dateStyle = timeStyle = null;
		pattern = var = null;
		value = null;
		timeZone = null;
		scope = PageContext.PAGE_SCOPE;
	}

	// *********************************************************************
	// Accessor methods

	// 'value' attribute
	public void setValue(Long value) throws JspTagException {
		this.value = (value == null || value == 0L) ? null : new Date(value.longValue());
	}

	// 'type' attribute
	public void setType(String type) throws JspTagException {
		this.type = type;
	}

	// 'dateStyle' attribute
	public void setDateStyle(String dateStyle) throws JspTagException {
		this.dateStyle = dateStyle;
	}

	// 'timeStyle' attribute
	public void setTimeStyle(String timeStyle) throws JspTagException {
		this.timeStyle = timeStyle;
	}

	// 'pattern' attribute
	public void setPattern(String pattern) throws JspTagException {
		this.pattern = pattern;
	}

	// 'timeZone' attribute
	public void setTimeZone(Object timeZone) throws JspTagException {
		this.timeZone = timeZone;
	}

	// *********************************************************************
	// Tag attributes known at translation time

	public void setVar(String var) {
		this.var = var;
	}

	public void setScope(String scope) {
		this.scope = Util.getScope(scope);
	}

	// *********************************************************************
	// Tag logic

	/*
	 * Formats the given date and time.
	 */
	public int doEndTag() throws JspException {

		String formatted = null;

		if (value == null) {
			if (var != null) {
				pageContext.removeAttribute(var, scope);
			}
			return EVAL_PAGE;
		}

		// Create formatter
		Locale locale = getFormattingLocale(pageContext, this, true, DateFormat.getAvailableLocales());

		if (locale != null) {
			DateFormat formatter = createFormatter(locale);

			// Apply pattern, if present
			if (pattern != null) {
				try {
					((SimpleDateFormat) formatter).applyPattern(pattern);
				} catch (ClassCastException cce) {
					formatter = new SimpleDateFormat(pattern, locale);
				}
			}

			// Set time zone
			TimeZone tz = null;
			if ((timeZone instanceof String) && ((String) timeZone).equals("")) {
				timeZone = null;
			}
			if (timeZone != null) {
				if (timeZone instanceof String) {
					tz = TimeZone.getTimeZone((String) timeZone);
				} else if (timeZone instanceof TimeZone) {
					tz = (TimeZone) timeZone;
				} else {
					throw new JspTagException(Resources.getMessage("FORMAT_DATE_BAD_TIMEZONE"));
				}
			} else {
				tz = getTimeZone(pageContext, this);
			}
			if (tz != null) {
				formatter.setTimeZone(tz);
			}
			formatted = formatter.format(value);
		} else {
			// no formatting locale available, use Date.toString()
			formatted = value.toString();
		}

		if (var != null) {
			pageContext.setAttribute(var, formatted, scope);
		} else {
			try {
				pageContext.getOut().print(formatted);
			} catch (IOException ioe) {
				throw new JspTagException(ioe.toString(), ioe);
			}
		}

		return EVAL_PAGE;
	}

	// Releases any resources we may have (or inherit)
	public void release() {
		init();
	}

	// *********************************************************************
	// Private utility methods

	private DateFormat createFormatter(Locale loc) throws JspException {
		DateFormat formatter = null;

		if ((type == null) || DATE.equalsIgnoreCase(type)) {
			formatter = DateFormat.getDateInstance(Util.getStyle(dateStyle, "FORMAT_DATE_INVALID_DATE_STYLE"), loc);
		} else if (TIME.equalsIgnoreCase(type)) {
			formatter = DateFormat.getTimeInstance(Util.getStyle(timeStyle, "FORMAT_DATE_INVALID_TIME_STYLE"), loc);
		} else if (DATETIME.equalsIgnoreCase(type)) {
			formatter = DateFormat.getDateTimeInstance(Util.getStyle(dateStyle, "FORMAT_DATE_INVALID_DATE_STYLE"),
					Util.getStyle(timeStyle, "FORMAT_DATE_INVALID_TIME_STYLE"), loc);
		} else {
			throw new JspException(Resources.getMessage("FORMAT_DATE_INVALID_TYPE", type));
		}

		return formatter;
	}

	// *********************************************************************
	// Package-scoped utility methods

	/*
	 * Stores the given locale in the response object of the given page context, and stores the locale's associated
	 * charset in the javax.servlet.jsp.jstl.fmt.request.charset session attribute, which may be used by the
	 * <requestEncoding> action in a page invoked by a form included in the response to set the request charset to the
	 * same as the response charset (this makes it possible for the container to decode the form parameter values
	 * properly, since browsers typically encode form field values using the response's charset).
	 * 
	 * @param pageContext the page context whose response object is assigned the given locale
	 * 
	 * @param locale the response locale
	 */
	static final String REQUEST_CHAR_SET = "javax.servlet.jsp.jstl.fmt.request.charset";

	static void setResponseLocale(PageContext pc, Locale locale) {
		// set response locale
		ServletResponse response = pc.getResponse();
		response.setLocale(locale);

		// get response character encoding and store it in session attribute
		if (pc.getSession() != null) {
			try {
				pc.setAttribute(REQUEST_CHAR_SET, response.getCharacterEncoding(), PageContext.SESSION_SCOPE);
			} catch (IllegalStateException ex) {
			} // invalidated session ignored
		}
	}

	/*
	 * Returns the formatting locale to use with the given formatting action in the given page.
	 * 
	 * @param pc The page context containing the formatting action
	 * 
	 * @param fromTag The formatting action
	 * 
	 * @param format <tt>true</tt> if the formatting action is of type <formatXXX> (as opposed to <parseXXX>), and
	 * <tt>false</tt> otherwise (if set to <tt>true</tt>, the formatting locale that is returned by this method is used
	 * to set the response locale).
	 * 
	 * @param avail the array of available locales
	 * 
	 * @return the formatting locale to use
	 */
	static Locale getFormattingLocale(PageContext pc, Tag fromTag, boolean format, Locale[] avail) {

		LocalizationContext locCtxt = null;

		// Get formatting locale from enclosing <fmt:bundle>
		Tag parent = findAncestorWithClass(fromTag, BundleSupport.class);
		if (parent != null) {
			/*
			 * use locale from localization context established by parent <fmt:bundle> action, unless that locale is
			 * null
			 */
			locCtxt = ((BundleSupport) parent).getLocalizationContext();
			if (locCtxt.getLocale() != null) {
				if (format) {
					setResponseLocale(pc, locCtxt.getLocale());
				}
				return locCtxt.getLocale();
			}
		}

		// Use locale from default I18N localization context, unless it is null
		if ((locCtxt = BundleSupport.getLocalizationContext(pc)) != null) {
			if (locCtxt.getLocale() != null) {
				if (format) {
					setResponseLocale(pc, locCtxt.getLocale());
				}
				return locCtxt.getLocale();
			}
		}

		/*
		 * Establish formatting locale by comparing the preferred locales (in order of preference) against the available
		 * formatting locales, and determining the best matching locale.
		 */
		Locale match = null;
		Locale pref = getLocale(pc, Config.FMT_LOCALE);
		if (pref != null) {
			// Preferred locale is application-based
			match = findFormattingMatch(pref, avail);
		} else {
			// Preferred locales are browser-based
			match = findFormattingMatch(pc, avail);
		}
		if (match == null) {
			// Use fallback locale.
			pref = getLocale(pc, Config.FMT_FALLBACK_LOCALE);
			if (pref != null) {
				match = findFormattingMatch(pref, avail);
			}
		}
		if (format && (match != null)) {
			setResponseLocale(pc, match);
		}

		return match;
	}

	/**
	 * Setup the available formatting locales that will be used by getFormattingLocale(PageContext).
	 */
	static Locale[] availableFormattingLocales;
	static {
		Locale[] dateLocales = DateFormat.getAvailableLocales();
		Locale[] numberLocales = NumberFormat.getAvailableLocales();
		Vector vec = new Vector(dateLocales.length);
		for (int i = 0; i < dateLocales.length; i++) {
			for (int j = 0; j < numberLocales.length; j++) {
				if (dateLocales[i].equals(numberLocales[j])) {
					vec.add(dateLocales[i]);
					break;
				}
			}
		}
		availableFormattingLocales = new Locale[vec.size()];
		availableFormattingLocales = (Locale[]) vec.toArray(availableFormattingLocales);
		/*
		 * for (int i=0; i<availableFormattingLocales.length; i++) { System.out.println("AvailableLocale[" + i + "] " +
		 * availableFormattingLocales[i]); }
		 */
	}

	/*
	 * Returns the formatting locale to use when <fmt:message> is used with a locale-less localization context.
	 * 
	 * @param pc The page context containing the formatting action
	 * 
	 * @return the formatting locale to use
	 */
	static Locale getFormattingLocale(PageContext pc) {
		/*
		 * Establish formatting locale by comparing the preferred locales (in order of preference) against the available
		 * formatting locales, and determining the best matching locale.
		 */
		Locale match = null;
		Locale pref = getLocale(pc, Config.FMT_LOCALE);
		if (pref != null) {
			// Preferred locale is application-based
			match = findFormattingMatch(pref, availableFormattingLocales);
		} else {
			// Preferred locales are browser-based
			match = findFormattingMatch(pc, availableFormattingLocales);
		}
		if (match == null) {
			// Use fallback locale.
			pref = getLocale(pc, Config.FMT_FALLBACK_LOCALE);
			if (pref != null) {
				match = findFormattingMatch(pref, availableFormattingLocales);
			}
		}
		if (match != null) {
			setResponseLocale(pc, match);
		}

		return match;
	}

	/*
	 * Returns the locale specified by the named scoped attribute or context configuration parameter.
	 * 
	 * <p> The named scoped attribute is searched in the page, request, session (if valid), and application scope(s) (in
	 * this order). If no such attribute exists in any of the scopes, the locale is taken from the named context
	 * configuration parameter.
	 * 
	 * @param pageContext the page in which to search for the named scoped attribute or context configuration parameter
	 * 
	 * @param name the name of the scoped attribute or context configuration parameter
	 * 
	 * @return the locale specified by the named scoped attribute or context configuration parameter, or <tt>null</tt>
	 * if no scoped attribute or configuration parameter with the given name exists
	 */
	static Locale getLocale(PageContext pageContext, String name) {
		Locale loc = null;

		Object obj = Config.find(pageContext, name);
		if (obj != null) {
			if (obj instanceof Locale) {
				loc = (Locale) obj;
			} else {
				loc = SetLocaleSupport.parseLocale((String) obj);
			}
		}

		return loc;
	}

	// *********************************************************************
	// Private utility methods

	/*
	 * Determines the client's preferred locales from the request, and compares each of the locales (in order of
	 * preference) against the available locales in order to determine the best matching locale.
	 * 
	 * @param pageContext Page containing the formatting action
	 * 
	 * @param avail Available formatting locales
	 * 
	 * @return Best matching locale, or <tt>null</tt> if no match was found
	 */
	private static Locale findFormattingMatch(PageContext pageContext, Locale[] avail) {
		Locale match = null;
		for (Enumeration enum_ = Util.getRequestLocales((HttpServletRequest) pageContext.getRequest()); enum_.hasMoreElements();) {
			Locale locale = (Locale) enum_.nextElement();
			match = findFormattingMatch(locale, avail);
			if (match != null) {
				break;
			}
		}

		return match;
	}

	/*
	 * Returns the best match between the given preferred locale and the given available locales.
	 * 
	 * The best match is given as the first available locale that exactly matches the given preferred locale
	 * ("exact match"). If no exact match exists, the best match is given to an available locale that meets the
	 * following criteria (in order of priority): - available locale's variant is empty and exact match for both
	 * language and country - available locale's variant and country are empty, and exact match for language.
	 * 
	 * @param pref the preferred locale
	 * 
	 * @param avail the available formatting locales
	 * 
	 * @return Available locale that best matches the given preferred locale, or <tt>null</tt> if no match exists
	 */
	private static Locale findFormattingMatch(Locale pref, Locale[] avail) {
		Locale match = null;
		boolean langAndCountryMatch = false;
		for (int i = 0; i < avail.length; i++) {
			if (pref.equals(avail[i])) {
				// Exact match
				match = avail[i];
				break;
			} else if (!"".equals(pref.getVariant()) && "".equals(avail[i].getVariant()) && pref.getLanguage().equals(avail[i].getLanguage())
					&& pref.getCountry().equals(avail[i].getCountry())) {
				// Language and country match; different variant
				match = avail[i];
				langAndCountryMatch = true;
			} else if (!langAndCountryMatch && pref.getLanguage().equals(avail[i].getLanguage()) && ("".equals(avail[i].getCountry()))) {
				// Language match
				if (match == null) {
					match = avail[i];
				}
			}
		}
		return match;
	}

	// *********************************************************************
	// Package-scoped utility methods

	/*
	 * Determines and returns the time zone to be used by the given action.
	 * 
	 * <p> If the given action is nested inside a &lt;timeZone&gt; action, the time zone is taken from the enclosing
	 * &lt;timeZone&gt; action.
	 * 
	 * <p> Otherwise, the time zone configuration setting <tt>javax.servlet.jsp.jstl.core.Config.FMT_TIME_ZONE</tt> is
	 * used.
	 * 
	 * @param pageContext the page containing the action for which the time zone needs to be determined
	 * 
	 * @param fromTag the action for which the time zone needs to be determined
	 * 
	 * @return the time zone, or <tt>null</tt> if the given action is not nested inside a &lt;timeZone&gt; action and no
	 * time zone configuration setting exists
	 */
	static TimeZone getTimeZone(PageContext pc, Tag fromTag) {
		TimeZone tz = null;

		Tag t = findAncestorWithClass(fromTag, TimeZoneSupport.class);
		if (t != null) {
			// use time zone from parent <timeZone> tag
			TimeZoneSupport parent = (TimeZoneSupport) t;
			tz = parent.getTimeZone();
		} else {
			// get time zone from configuration setting
			Object obj = Config.find(pc, Config.FMT_TIME_ZONE);
			if (obj != null) {
				if (obj instanceof TimeZone) {
					tz = (TimeZone) obj;
				} else {
					tz = TimeZone.getTimeZone((String) obj);
				}
			}
		}

		return tz;
	}
}

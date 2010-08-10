/**
 * 
 */
package com.scamall.manager.servlet;

import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import com.scamall.manager.Singleton;
import com.vaadin.Application;
import com.vaadin.terminal.gwt.server.AbstractApplicationServlet;
import com.vaadin.terminal.gwt.server.ApplicationServlet;
import com.vaadin.terminal.gwt.server.SystemMessageException;
import com.vaadin.ui.Window;

/**
 * @author Alejandro Serrano
 * 
 */
public class ScamallServlet extends ApplicationServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2837798550836303341L;

	public ScamallServlet() {
		super();
	}

	private List<String> getWidgetsets() {
		ArrayList<String> list = new ArrayList<String>();
		// list.add("com.vaadin.terminal.gwt.DefaultWidgetSet");
		list.add("com.scamall.ui.flowplayer.FlowplayerWidgetset");
		return list;
	}

	@Override
	protected void writeAjaxPageHtmlVaadinScripts(Window window,
			String themeName, Application application, BufferedWriter page,
			String appUrl, String themeUri, String appId,
			HttpServletRequest request) throws ServletException, IOException {

		String widgetsetBasePath = getStaticFilesLocation(request);

		// Get system messages
		Application.SystemMessages systemMessages = null;
		try {
			systemMessages = getSystemMessages();
		} catch (SystemMessageException e) {
			// failing to get the system messages is always a problem
			throw new ServletException("CommunicationError!", e);
		}

		page.write("<script type=\"text/javascript\">\n");
		page.write("//<![CDATA[\n");
		page.write("if(!vaadin || !vaadin.vaadinConfigurations) {\n "
				+ "if(!vaadin) { var vaadin = {}} \n"
				+ "vaadin.vaadinConfigurations = {};\n"
				+ "if (!vaadin.themesLoaded) { vaadin.themesLoaded = {}; }\n");
		if (!isProductionMode()) {
			page.write("vaadin.debug = true;\n");
		}
		page.write("document.write('<iframe tabIndex=\"-1\" id=\"__gwt_historyFrame\" "
				+ "style=\"position:absolute;width:0;height:0;border:0;overflow:"
				+ "hidden;\" src=\"javascript:false\"></iframe>');\n");
		for (String widgetset : getWidgetsets()) {
			final String widgetsetFilePath = widgetsetBasePath + "/"
					+ WIDGETSET_DIRECTORY_PATH + widgetset + "/" + widgetset
					+ ".nocache.js?" + new Date().getTime();
			page.write("document.write(\"<script language='javascript' src='"
					+ widgetsetFilePath + "'><\\/script>\");\n}\n");
		}

		page.write("vaadin.vaadinConfigurations[\"" + appId + "\"] = {");
		page.write("appUri:'" + appUrl + "', ");

		String pathInfo = getRequestPathInfo(request);
		if (pathInfo == null) {
			pathInfo = "/";
		}

		page.write("pathInfo: '" + pathInfo + "', ");
		if (window != application.getMainWindow()) {
			page.write("windowName: '" + window.getName() + "', ");
		}
		page.write("themeUri:");
		page.write(themeUri != null ? "'" + themeUri + "'" : "null");
		page.write(", versionInfo : {vaadinVersion:\"");
		page.write(VERSION);
		page.write("\",applicationVersion:\"");
		page.write(application.getVersion());
		page.write("\"}");
		if (systemMessages != null) {
			// Write the CommunicationError -message to client
			String caption = systemMessages.getCommunicationErrorCaption();
			if (caption != null) {
				caption = "\"" + caption + "\"";
			}
			String message = systemMessages.getCommunicationErrorMessage();
			if (message != null) {
				message = "\"" + message + "\"";
			}
			String url = systemMessages.getCommunicationErrorURL();
			if (url != null) {
				url = "\"" + url + "\"";
			}

			page.write(",\"comErrMsg\": {" + "\"caption\":" + caption + ","
					+ "\"message\" : " + message + "," + "\"url\" : " + url
					+ "}");
		}
		page.write("};\n//]]>\n</script>\n");

		if (themeName != null) {
			// Custom theme's stylesheet, load only once, in different
			// script
			// tag to be dominate styles injected by widget
			// set
			page.write("<script type=\"text/javascript\">\n");
			page.write("//<![CDATA[\n");
			page.write("if(!vaadin.themesLoaded['" + themeName + "']) {\n");
			page.write("var stylesheet = document.createElement('link');\n");
			page.write("stylesheet.setAttribute('rel', 'stylesheet');\n");
			page.write("stylesheet.setAttribute('type', 'text/css');\n");
			page.write("stylesheet.setAttribute('href', '" + themeUri
					+ "/styles.css');\n");
			page.write("document.getElementsByTagName('head')[0].appendChild(stylesheet);\n");
			page.write("vaadin.themesLoaded['" + themeName + "'] = true;\n}\n");
			page.write("//]]>\n</script>\n");
		}

		// Warn if the widgetset has not been loaded after 15 seconds on
		// inactivity
		for (String widgetset : getWidgetsets()) {
			final String widgetsetFilePath = widgetsetBasePath + "/"
					+ WIDGETSET_DIRECTORY_PATH + widgetset + "/" + widgetset
					+ ".nocache.js?" + new Date().getTime();

			page.write("<script type=\"text/javascript\">\n");
			page.write("//<![CDATA[\n");
			page.write("setTimeout('if (typeof "
					+ widgetset.replace('.', '_')
					+ " == \"undefined\") {alert(\"Failed to load the widgetset: "
					+ widgetsetFilePath + "\")};',15000);\n"
					+ "//]]>\n</script>\n");
		}
	}

	String getRequestPathInfo(HttpServletRequest request) {
		try {
			Class<AbstractApplicationServlet> klass = AbstractApplicationServlet.class;
			Method m = klass.getMethod("getRequestPathInfo",
					HttpServletRequest.class);
			m.setAccessible(true);
			return (String) m.invoke(this, request);
		} catch (Exception e) {
			return null;
		}
	}
	
	@Override
	protected ClassLoader getClassLoader() throws ServletException {
		return Singleton.getLoader().getLibraryClassLoader();
	}
}

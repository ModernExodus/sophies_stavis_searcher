package com.john.utils;

import static java.lang.String.format;

/**
 * A naive builder for constructing stringified HTML. No validations
 * are performed so it is the caller's responsibility to ensure the HTML
 * they are building is valid.
 */
public class HtmlBuilder {
	
	private final StringBuilder html;
	
	private HtmlBuilder() {
		html = new StringBuilder(startTag("html"));
	}
	
	public HtmlBuilder addElement(String tagName) {
		html.append(startTag(tagName)).append(endTag(tagName));
		return this;
	}
	
	public HtmlBuilder addElement(String tagName, String content) {
		html.append(startTag(tagName)).append(content).append(endTag(tagName));
		return this;
	}
	
	public HtmlBuilder openTag(String tagName) {
		html.append(startTag(tagName));
		return this;
	}
	
	public HtmlBuilder closeTag(String tagName) {
		html.append(endTag(tagName));
		return this;
	}
	
	public HtmlBuilder addEmptyElement(String tagName) {
		html.append(emptyTag(tagName));
		return this;
	}
	
	public String build() {
		html.append(endTag("html"));
		return html.toString();
	}
	
	public static HtmlBuilder newBuilder() {
		return new HtmlBuilder();
	}
	
	private static String startTag(String tagName) {
		return format("<%s>", tagName);
	}
	
	private static String endTag(String tagName) {
		return format("</%s>", tagName);
	}
	
	private static String emptyTag(String tagName) {
		return format("<%s />");
	}
}

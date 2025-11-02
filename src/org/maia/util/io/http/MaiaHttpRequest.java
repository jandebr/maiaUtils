package org.maia.util.io.http;

import java.net.URL;

public class MaiaHttpRequest implements MaiaHttpMethods, MaiaHttpHeaderNames {

	private String method;

	private URL url;

	private MaiaHttpHeaders headers;

	private MaiaHttpRequest(String method, URL url) {
		this.method = method;
		this.url = url;
		this.headers = new MaiaHttpHeaders();
	}

	public static MaiaHttpRequest createGetRequest(URL url) {
		return new MaiaHttpRequest(HTTP_GET, url);
	}

	public void setUserAgent(String userAgent) {
		getHeaders().setValue(HTTP_HEADERNAME_USER_AGENT, userAgent);
	}

	public String getMethod() {
		return method;
	}

	public URL getUrl() {
		return url;
	}

	public MaiaHttpHeaders getHeaders() {
		return headers;
	}

}
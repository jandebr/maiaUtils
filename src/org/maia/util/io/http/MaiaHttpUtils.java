package org.maia.util.io.http;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

public class MaiaHttpUtils {

	private MaiaHttpUtils() {
	}

	public static String formatUrl(URL url) {
		return url.toExternalForm();
	}

	public static URL parseUrl(String url) {
		try {
			return URI.create(url).toURL();
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static URL appendUrlQueryString(URL url, String key, String value) {
		String urlStr = formatUrl(url);
		StringBuilder sb = new StringBuilder(urlStr.length() + key.length() + value.length() + 2);
		sb.append(urlStr);
		if (url.getQuery() == null) {
			sb.append('?');
		} else {
			sb.append('&');
		}
		sb.append(key);
		sb.append('=');
		sb.append(value);
		return parseUrl(sb.toString());
	}

}
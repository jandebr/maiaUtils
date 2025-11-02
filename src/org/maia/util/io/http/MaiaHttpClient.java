package org.maia.util.io.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import org.maia.util.GenericListenerList;

public class MaiaHttpClient {

	public static long defaultConnectTimeoutMillis = 2000L;

	public static long defaultReadTimeoutMillis = 10000L;

	public static boolean defaultDisableSslHostnameVerification = false;

	private static HostnameVerifier noHostnameVerifier = new NoHostnameVerifier();

	public static GenericListenerList<MaiaHttpClientListener> defaultListeners = new GenericListenerList<MaiaHttpClientListener>();

	private long connectTimeoutMillis;

	private long readTimeoutMillis;

	private boolean disableSslHostnameVerification;

	private GenericListenerList<MaiaHttpClientListener> listeners = new GenericListenerList<MaiaHttpClientListener>();

	private MaiaHttpClient() {
		setConnectTimeoutMillis(defaultConnectTimeoutMillis);
		setReadTimeoutMillis(defaultReadTimeoutMillis);
		setDisableSslHostnameVerification(defaultDisableSslHostnameVerification);
		for (MaiaHttpClientListener listener : getDefaultListeners()) {
			addListener(listener);
		}
	}

	public static MaiaHttpClient createHttpClient() {
		return new MaiaHttpClient();
	}

	public static void addDefaultListener(MaiaHttpClientListener listener) {
		getDefaultListeners().addListener(listener);
	}

	public static void removeDefaultListener(MaiaHttpClientListener listener) {
		getDefaultListeners().removeListener(listener);
	}

	public void addListener(MaiaHttpClientListener listener) {
		getListeners().addListener(listener);
	}

	public void removeListener(MaiaHttpClientListener listener) {
		getListeners().removeListener(listener);
	}

	public MaiaHttpResponse makeRequest(MaiaHttpRequest request) throws MaiaHttpException {
		URL url = request.getUrl();
		try {
			URLConnection cnx = url.openConnection();
			if (cnx instanceof HttpURLConnection) {
				HttpURLConnection httpCnx = (HttpURLConnection) cnx;
				fireHttpRequest(request);
				initConnection(request, httpCnx);
				MaiaHttpResponse response = new MaiaHttpResponseImpl(httpCnx);
				fireHttpResponse(request, response);
				return response;
			} else {
				throw new MaiaHttpException("Not an HTTP(S) connection to " + url.toExternalForm());
			}
		} catch (IOException e) {
			MaiaHttpException failure = new MaiaHttpException("Failed to make a request to " + url.toExternalForm(), e);
			fireHttpRequestFailed(request, failure);
			throw failure;
		}
	}

	protected void initConnection(MaiaHttpRequest request, HttpURLConnection cnx) throws IOException {
		if (cnx instanceof HttpsURLConnection) {
			initSsl(request, (HttpsURLConnection) cnx);
		}
		cnx.setRequestMethod(request.getMethod());
		cnx.setAllowUserInteraction(false);
		cnx.setConnectTimeout((int) getConnectTimeoutMillis());
		cnx.setReadTimeout((int) getReadTimeoutMillis());
		cnx.setUseCaches(false);
		for (String headerName : request.getHeaders().getHeaderNames()) {
			String headerValue = request.getHeaders().getValue(headerName);
			cnx.setRequestProperty(headerName, headerValue);
		}
		cnx.connect();
	}

	protected void initSsl(MaiaHttpRequest request, HttpsURLConnection cnx) throws IOException {
		if (isDisableSslHostnameVerification()) {
			cnx.setHostnameVerifier(noHostnameVerifier);
		}
	}

	protected void fireHttpRequest(MaiaHttpRequest request) {
		for (MaiaHttpClientListener listener : getListeners()) {
			listener.notifyHttpRequest(this, request);
		}
	}

	protected void fireHttpResponse(MaiaHttpRequest request, MaiaHttpResponse response) {
		for (MaiaHttpClientListener listener : getListeners()) {
			listener.notifyHttpResponse(this, request, response);
		}
	}

	protected void fireHttpRequestFailed(MaiaHttpRequest request, MaiaHttpException failure) {
		for (MaiaHttpClientListener listener : getListeners()) {
			listener.notifyHttpRequestFailed(this, request, failure);
		}
	}

	public long getConnectTimeoutMillis() {
		return connectTimeoutMillis;
	}

	public void setConnectTimeoutMillis(long millis) {
		this.connectTimeoutMillis = millis;
	}

	public long getReadTimeoutMillis() {
		return readTimeoutMillis;
	}

	public void setReadTimeoutMillis(long millis) {
		this.readTimeoutMillis = millis;
	}

	public boolean isDisableSslHostnameVerification() {
		return disableSslHostnameVerification;
	}

	public void setDisableSslHostnameVerification(boolean disable) {
		this.disableSslHostnameVerification = disable;
	}

	private GenericListenerList<MaiaHttpClientListener> getListeners() {
		return listeners;
	}

	private static GenericListenerList<MaiaHttpClientListener> getDefaultListeners() {
		return defaultListeners;
	}

	private static class NoHostnameVerifier implements HostnameVerifier {

		public NoHostnameVerifier() {
		}

		@Override
		public boolean verify(String hostname, SSLSession session) {
			return true; // accepted
		}

	}

	private static class MaiaHttpResponseImpl extends MaiaHttpResponse {

		private HttpURLConnection connection;

		private InputStream openedInputStream;

		public MaiaHttpResponseImpl(HttpURLConnection connection) throws IOException {
			this.connection = connection;
			this.connection.getResponseCode(); // force response communication
		}

		@Override
		public int getStatusCode() {
			try {
				return getConnection().getResponseCode();
			} catch (IOException e) {
				// not expected, already fetched in constructor
				return 0;
			}
		}

		@Override
		public int getContentLength() {
			return getConnection().getContentLength();
		}

		@Override
		public MaiaHttpHeaders getHeaders() {
			MaiaHttpHeaders headers = new MaiaHttpHeaders();
			Map<String, List<String>> map = getConnection().getHeaderFields();
			for (String headerName : map.keySet()) {
				for (String headerValue : map.get(headerName)) {
					headers.addValue(headerName, headerValue);
				}
			}
			return headers;
		}

		@Override
		public synchronized InputStream openInputStream() throws MaiaHttpException {
			if (getOpenedInputStream() != null) {
				throw new MaiaHttpException("Input stream already open");
			} else {
				try {
					InputStream is = getConnection().getInputStream();
					setOpenedInputStream(is);
					return is;
				} catch (IOException e) {
					throw new MaiaHttpException("Failed to open input stream", e);
				}
			}
		}

		@Override
		public synchronized void dispose() {
			try {
				InputStream is = getOpenedInputStream();
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
				// ignore
			} finally {
				setOpenedInputStream(null);
				getConnection().disconnect();
			}
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder(32);
			sb.append(getStatusCode());
			String msg = getStatusMessage();
			if (msg != null) {
				sb.append(' ');
				sb.append(msg);
			}
			sb.append(" (").append(getContentLength()).append(" bytes body)");
			return sb.toString();
		}

		private String getStatusMessage() {
			try {
				return getConnection().getResponseMessage();
			} catch (IOException e) {
				return null;
			}
		}

		private HttpURLConnection getConnection() {
			return connection;
		}

		private InputStream getOpenedInputStream() {
			return openedInputStream;
		}

		private void setOpenedInputStream(InputStream is) {
			this.openedInputStream = is;
		}

	}

}
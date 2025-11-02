package org.maia.util.io.http;

import org.maia.util.GenericListener;

public interface MaiaHttpClientListener extends GenericListener {

	void notifyHttpRequest(MaiaHttpClient client, MaiaHttpRequest request);

	void notifyHttpResponse(MaiaHttpClient client, MaiaHttpRequest request, MaiaHttpResponse response);

	void notifyHttpRequestFailed(MaiaHttpClient client, MaiaHttpRequest request, MaiaHttpException failure);

}
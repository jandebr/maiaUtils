package org.maia.util.io.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public abstract class MaiaHttpResponse {

	protected MaiaHttpResponse() {
	}

	public abstract int getStatusCode();

	public abstract int getContentLength();

	public abstract MaiaHttpHeaders getHeaders();

	public abstract InputStream openInputStream() throws MaiaHttpException;

	public abstract void dispose();

	public CharSequence readBodyAsText() throws MaiaHttpException {
		return readBodyAsText(Charset.defaultCharset());
	}

	public CharSequence readBodyAsText(Charset charset) throws MaiaHttpException {
		InputStream is = openInputStream();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, charset));
			String line = null;
			StringBuilder sb = new StringBuilder(Math.max(getContentLength(), 2048));
			boolean firstLine = true;
			while ((line = reader.readLine()) != null) {
				if (!firstLine)
					sb.append('\n');
				sb.append(line);
				firstLine = false;
			}
			reader.close();
			return sb;
		} catch (IOException e) {
			throw new MaiaHttpException("Failed to read response body as text", e);
		}
	}

}
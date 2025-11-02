package org.maia.util.io.http;

@SuppressWarnings("serial")
public class MaiaHttpException extends Exception {

	public MaiaHttpException() {
	}

	public MaiaHttpException(String message) {
		super(message);
	}

	public MaiaHttpException(Throwable cause) {
		super(cause);
	}

	public MaiaHttpException(String message, Throwable cause) {
		super(message, cause);
	}

}

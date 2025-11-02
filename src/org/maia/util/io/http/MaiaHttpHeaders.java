package org.maia.util.io.http;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

public class MaiaHttpHeaders {

	private Map<String, List<String>> headerValuesMap;

	public MaiaHttpHeaders() {
		this.headerValuesMap = new HashMap<String, List<String>>();
	}

	public synchronized void setValue(String headerName, String headerValue) {
		List<String> values = new Vector<String>(1);
		values.add(headerValue);
		getHeaderValuesMap().put(headerName, values);
	}

	public synchronized void addValue(String headerName, String headerValue) {
		List<String> values = getHeaderValuesMap().get(headerName);
		if (values != null) {
			values.add(headerValue);
		} else {
			setValue(headerName, headerValue);
		}
	}

	public synchronized List<String> getValues(String headerName) {
		List<String> values = getHeaderValuesMap().get(headerName);
		if (values != null) {
			return values;
		} else {
			return Collections.emptyList();
		}
	}

	public synchronized String getValue(String headerName) {
		List<String> values = getValues(headerName);
		if (!values.isEmpty()) {
			return values.get(0);
		} else {
			return null;
		}
	}

	public Set<String> getHeaderNames() {
		return getHeaderValuesMap().keySet();
	}

	private Map<String, List<String>> getHeaderValuesMap() {
		return headerValuesMap;
	}

}
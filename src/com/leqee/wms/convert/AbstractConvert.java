package com.leqee.wms.convert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractConvert<S, T> implements Convert<S, T> {

	
	@Override
	public List<T> covertToTargetEntity(List<S> ses  ) {
		if (ses == null || ses.isEmpty()) {
			return Collections.emptyList();
		}

		List<T> ts = new ArrayList<T>(ses.size());
		for (S s : ses) {
			T t = covertToTargetEntity(s);
			if (t != null) {
				ts.add(t);
			}
		}

		return ts;
	}

	@Override
	public List<S> covertToSourceEntity(List<T> ts) {
		if (ts == null || ts.isEmpty()) {
			return Collections.emptyList();
		}

		List<S> ses = new ArrayList<S>(ts.size());
		for (T t : ts) {
			S s = covertToSourceEntity(t);
			if (s != null) {
				ses.add(s);
			}
		}

		return ses;
	}

}

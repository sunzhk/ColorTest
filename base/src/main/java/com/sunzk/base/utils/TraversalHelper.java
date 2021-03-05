package com.sunzk.base.utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public abstract class TraversalHelper<T, R> {
	
	protected abstract List<T> listChild(T parent);
	
	protected abstract boolean checkNeed(T node);
	
	protected abstract R convert(T node);
	
	public void traversal(T root) {
		LinkedList<T> parents = new LinkedList<>();
		ArrayList<T> result = new ArrayList<>();
		
		parents.add(root);
		
		if (checkNeed(root)) {
			result.add(root);
		}
		
		T node;
		while (!parents.isEmpty()) {
			node = parents.removeFirst();
			List<T> childList = listChild(node);
			parents.addAll(childList);
		}
	}
	
}

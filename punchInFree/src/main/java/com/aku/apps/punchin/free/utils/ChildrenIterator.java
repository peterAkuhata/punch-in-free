package com.aku.apps.punchin.free.utils;

import java.util.ArrayList;
import java.util.Iterator;

import android.view.View;
import android.view.ViewGroup;

public class ChildrenIterator<V extends View> implements Iterator<V> {

	ArrayList<V> list;

	private int i;

	public ChildrenIterator(ViewGroup vg) {

		super();

		if (vg == null) {

			throw new RuntimeException(
					"ChildrenIterator needs a ViewGroup != null to find its children");

		}

		init();

		findChildrenAndAddToList(vg, list);

	}

	private void init() {


       list = new ArrayList<V>();


       i = 0;        


    }

	@Override
	public boolean hasNext() {

		return (i < list.size());

	}

	@Override
	public V next() {

		return list.get(i++);

	}

	@Override
	public void remove() {

		list.remove(i);

	}

	@SuppressWarnings("unchecked")
	private void findChildrenAndAddToList(final ViewGroup root,
			final ArrayList<V> list) {

		for (int i = 0; i < root.getChildCount(); i++) {

			V v = (V) root.getChildAt(i);

			list.add(v);

			if (v instanceof ViewGroup) {

				findChildrenAndAddToList((ViewGroup) v, list);

			}

		}

	}

}
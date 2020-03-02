package com.tenchael.metrics.extension.reporter.logger;

import com.tenchael.metrics.extension.common.utils.Validator;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Tenchael on 2020/3/2.
 */
public class PageableHolder<T> implements Iterable<List<T>> {
	public static final int DEFAULT_PAGE_SIZE = 10;
	public static final int DEFAULT_PAGE_INDEX = 0;

	private final List<T> list;
	private final int pageSize;
	private final int pages;
	private int current;

	public PageableHolder(List<T> list) {
		this(list, DEFAULT_PAGE_SIZE);
	}

	public PageableHolder(List<T> list, int pageSize) {
		Validator.notNull(list, "list can not be null");
		Validator.isTrue(pageSize > 0, "pageSize must be positive");
		this.list = list;
		this.pageSize = pageSize;
		this.current = DEFAULT_PAGE_INDEX;

		if (list.size() % pageSize == 0) {
			pages = list.size() / pageSize;
		} else {
			pages = (list.size() / pageSize) + 1;
		}
	}

	public void reset() {
		this.current = DEFAULT_PAGE_INDEX;
	}


	@Override
	public Iterator<List<T>> iterator() {
		return new Iterator<List<T>>() {

			@Override
			public boolean hasNext() {
				return current < pages;
			}

			@Override
			public List<T> next() {
				int start = current * pageSize;
				int end = (current + 1) * pageSize;
				end = (end > list.size()) ? list.size() : end;
				current++;
				return list.subList(start, end);
			}
		};
	}
}

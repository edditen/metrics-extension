package com.tenchael.jmx.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Dict {

	private Map<String, Map<String, Map<String, Object>>> dict = new HashMap<>();

	public void put(String key1, String key2, String key3, Object val) {
		if (dict == null) {
			dict = new HashMap<>();
		}
		Map<String, Map<String, Object>> m1 = dict.get(key1);
		if (m1 == null) {
			dict.putIfAbsent(key1, new HashMap<>());
			m1 = dict.get(key1);
		}
		Map<String, Object> m2 = m1.get(key2);
		if (m2 == null) {
			m1.putIfAbsent(key2, new HashMap<>());
			m2 = m1.get(key2);
		}


		m2.put(key3, val);

	}

	public Map<String, Map<String, Map<String, Object>>> getMap() {
		return this.dict;
	}

	public List<Map<String, Map<String, Object>>> getList() {
		List<Map<String, Map<String, Object>>> list = new ArrayList<>();
		for (Map.Entry<String, Map<String, Map<String, Object>>> entry : getMap().entrySet()) {
			Map<String, Map<String, Object>> item = new HashMap<>();
			item.putAll(entry.getValue());
			Map<String, Object> name = new HashMap<>();
			name.put("Value", entry.getKey());
			item.put("Name", name);
			list.add(item);
		}
		return list;
	}


}

/*
 * Copyright (c) 2007 SK C&C. All rights reserved.
 *
 * This software is the confidential and proprietary information of SK C&C.
 * You shall not disclose such Confidential Information and shall use it
 * only in accordance with the terms of the license agreement you entered into
 * with SK C&C.
 */

package com.elasticsearch.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import com.github.wnameless.json.flattener.JsonFlattener;

/**
 * JSON 관련 Utility
 * @author wychang
 *
 */
public final class JsonUtil {
	private static final transient Log LOG = LogFactory.getLog(JsonUtil.class);

	
	/**
	 * String type의 JSON data를 Map type으로 변환
	 * nested json을 dot notation으로 flatten 시킴.
	 * https://github.com/wnameless/json-flattener
	 * @param jsonString
	 */
	public static Map<String,Object> jsonString2mapJSonFlattner(String jsonString) {
		Map<String,Object> map = new HashMap<String,Object>();
		try {
			// convert JSON string to Map
			map = JsonFlattener.flattenAsMap(jsonString);
		} catch(Exception e) {
			if(LOG.isErrorEnabled())
				LOG.error("jsonString2mapJSonFlattner Exception: " + e.toString());
			e.printStackTrace();
		}
		return map;
	}
	
	/**
	 * String type의 JSON data를 Map type으로 변환
	 * @param jsonString
	 */
	public static Map<String,Object> jsonString2map(String jsonString) {
		Map<String,Object> map = new HashMap<String,Object>();
		try {
			//brace로 시작과 끝에 감싸져있지 않으면 강제로 넣어준다.
			if(!jsonString.startsWith("{")) {
				jsonString = "{" + jsonString + "}";
			}
			JSONObject jsonObj = new JSONObject(jsonString);
			map = jsonObject2map(jsonObj);
		} catch(Exception e) {
			if(LOG.isErrorEnabled())
				LOG.error("string2json Exception: " + e.toString());
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * JSONObject type을 map으로 변환
	 * @param jsonObj
	 * @return
	 */
	private static Map<String,Object> jsonObject2map(JSONObject jsonObj) {
		Map<String,Object> map = new HashMap<String,Object>();
		String key;
		Object value;
		try {
			Iterator<String> jsonKeys = jsonObj.keys();
			while(jsonKeys.hasNext()) {
				key = jsonKeys.next();
				value = jsonObj.get(key);
				if(value instanceof JSONObject) {
					value = jsonObject2map((JSONObject)value);
				} else if(value instanceof JSONArray) {
					value = jsonArray2list((JSONArray)value);
				}
				map.put(key, value);
			}
		} catch(Exception e) {
			if(LOG.isErrorEnabled())
				LOG.error("jsonObject2map Exception: " + e.toString());
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * JSONArray type을 list로 변환
	 * @param jsonArray
	 * @return
	 */
	private static List<Object> jsonArray2list(JSONArray jsonArray) {
		List<Object> list = new ArrayList<Object>();
		Object value;
		try {
			for(int i=0; i<jsonArray.length(); i++) {
				value = jsonArray.get(i);
				if(value instanceof JSONObject) {
					value = jsonObject2map((JSONObject)value);
				} else if(value instanceof JSONArray) {
					value = jsonArray2list((JSONArray)value);
				}
				list.add(value);
			}
		} catch(Exception e) {
			if(LOG.isErrorEnabled())
				LOG.error("jsonObject2list Exception: " + e.toString());
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * map의 value가 map인 경우를 key이름에 dot(.)을 추가해서 flat하게 변경
	 * @param sourceMap
	 * @param prefix
	 * @return
	 */
	public static Map<String,Object> treeMap2FlatMap(Map<String,Object> sourceMap, String prefix) {
		Map<String,Object> flatMap = new HashMap<String,Object>();
		Map<String,Object> tempMap = new HashMap<String,Object>();
		String key;
		Object value;
		String flatMapKey;
		try {
			Iterator<String> keys = sourceMap.keySet().iterator();
			while(keys.hasNext()) {
				key = keys.next();
				value = sourceMap.get(key);
				if(prefix.isEmpty()) { //key name을 dot(.) 구분자로 누적시킴
					flatMapKey = key;
				} else {
					flatMapKey = prefix + "." + key;
				}
				if(value instanceof Map) {
					tempMap = treeMap2FlatMap((Map<String,Object>)value, flatMapKey);
					flatMap.putAll(tempMap);
				} else if(value instanceof List) {
					//TODO: Map 내 List에 대한 처리는 고민중. 일단 그대로 남겨둠.
					//DO Nothing...
				} else {
					flatMap.put(flatMapKey, value);
				}
			}
		} catch(Exception e) {
			if(LOG.isErrorEnabled())
				LOG.error("treeMap2FlatMap Exception: " + e.toString());
			e.printStackTrace();
		}
		return flatMap;
	}
}

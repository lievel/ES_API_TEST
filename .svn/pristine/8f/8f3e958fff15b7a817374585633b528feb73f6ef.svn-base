package com.elasticsearch.aggregation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;

import com.elasticsearch.querybuilder.QueryBuilderUtil;
import com.elasticsearch.service.ESService;

public class AlarmMasAggregation {
	
	public static List<Map<String, Object>> getAggregation(String fromDate, String toDate, String condition1, String condition2, 
			List<String> siteList, List<String> sitebldList, List<String> sitebldflrList ) throws Exception{
		
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		
		RangeQueryBuilder dateRangeBuilder = QueryBuilderUtil.getDateTimeRangeQueryBuilder("alm_time", "yyyy-MM-dd HH:mm:ss", fromDate, toDate);
		BoolQueryBuilder validAreaQueryBuilder = QueryBuilderUtil.getValidAreaQueryBuilder(siteList, sitebldList, sitebldflrList);
		
		//검색 연산
		BoolQueryBuilder searchQuery = QueryBuilders.boolQuery();
		searchQuery.must(dateRangeBuilder);
		searchQuery.must(validAreaQueryBuilder);
		
        //집계 연산
        AggregationBuilder aggregation = AggregationBuilders
                .terms("Agg_Condition1_Count")
                .field(condition1 + ".keyword")
                .subAggregation(AggregationBuilders
                        .terms("Agg_Condition2_Count")
                        .field(condition2 + ".keyword"));
		
        SearchResponse response = ESService.ESServiceRequest(searchQuery, aggregation);
        
	     // Make resultList
	     for (Terms.Bucket entry1 : ((Terms) response.getAggregations().get("Agg_Condition1_Count")).getBuckets()) {
	    	 
	    	 String seq_no = (String) entry1.getKey();
	    	 
	    	 for (Terms.Bucket entry2 : ((Terms) entry1.getAggregations().get("Agg_Condition2_Count")).getBuckets()){
	    		 Map<String, Object> map  = new HashMap<String, Object>();
	    		 map.put("SEQ_NO", seq_no);
	    		 map.put("ALM_MAS_ID", entry2.getKey());
	    		 map.put("CNT", entry2.getDocCount());
	    		 
	    		 resultList.add(map);
	    	 }
	               
	     }
        
        System.out.println(response.toString());
		
		return resultList;
	}

}
